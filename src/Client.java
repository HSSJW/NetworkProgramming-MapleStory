package Player;

import Map.MapData;
import Map.Portal;
import Player.Player1.Player1;
import Player.Player2.Player2;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Image backgroundImage;
    private int mapWidth, mapHeight;

    private Player player1, player2; // 두 플레이어
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private int playerID; // 클라이언트의 플레이어 ID
    private boolean opponentConnected = false;

    // UI 관련
    private Image stateBarImage;
    private Image tradeButtonImage;
    private Image shortcutButtonImage;
    private Image shopButtonImage;

    //맵관련
    private CopyOnWriteArrayList<MapData> maps = MapleServer.Copy;
    private int currentMapIndex = 0;
    private int opponentMapIndex = -1;

    private CopyOnWriteArrayList<Rectangle> ground;

    // 키 입력 상태를 저장하는 Set
    private Set<Integer> pressedKeys;

    // Client.java 내 Player 초기화 코드 수정
    public Client(int playerID, String host, int port) throws IOException {
        this.playerID = playerID;



        // 첫 번째 맵 설정
        MapData currentMap = getCurrentMap();
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage();
        mapWidth = backgroundImage.getWidth(null);
        mapHeight = backgroundImage.getHeight(null);

        // 소켓 연결
        socket = new Socket(host, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        // 서버에서 데이터를 수신하는 스레드 시작
        new Thread(this::receiveUpdates).start();

        // 지형 초기화
        ground = new CopyOnWriteArrayList<>(currentMap.getTerrain());

        // 플레이어 초기화 - 첫 번째 지형(Rectangle) 위에 배치
        Rectangle firstGround = ground.get(0); // 첫 번째 지형
        int playerStartX = firstGround.x + 50; // 지형의 시작점 + 여유 공간
        int playerStartY = firstGround.y - 195; // 지형 상단 - 플레이어 높이

        int player2Id;

        if(playerID == 1){
            player2Id = 2;
        }
        else
            player2Id = 1;

        player1 = new Player1(playerStartX, playerStartY); // Player 초기화
        player2 = new Player2(playerStartX + 100, playerStartY); // Player2 초기화

        // 상태바 및 버튼 이미지 로드
        stateBarImage = new ImageIcon("images/ui/statebar.png").getImage();
        tradeButtonImage = new ImageIcon("images/ui/tradebutton.png").getImage();
        shortcutButtonImage = new ImageIcon("images/ui/shortcutbutton.png").getImage();
        shopButtonImage = new ImageIcon("images/ui/shopbutton.png").getImage();

        // 상태바 이미지 크기 조정
        stateBarImage = stateBarImage.getScaledInstance(mapWidth, stateBarImage.getHeight(null), Image.SCALE_SMOOTH);

        // 키 상태 초기화
        pressedKeys = new HashSet<>();

        // 타이머 설정
        timer = new Timer(16, this);
        timer.start();

        // 키 이벤트 리스너 추가
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }


    private void receiveUpdates() {
        try {
            while (true) {
                String message = input.readUTF();
                if (message.startsWith("MOVE")) {
                    String[] data = message.split(",");
                    int id = Integer.parseInt(data[1]);
                    int mapIndex = Integer.parseInt(data[2]);
                    int x = Integer.parseInt(data[3]);
                    int y = Integer.parseInt(data[4]);
                    String state = data[5]; // 상태 데이터 수신

                    if (id != playerID) { // 상대방 플레이어
                        if (player2 != null) {
                            player2.setPosition(x, y);
                            player2.setState(state); // 상태 반영
                            opponentMapIndex = mapIndex;
                            opponentConnected = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendPosition() {
        try {
            output.writeUTF("MOVE," + playerID + "," + currentMapIndex + "," + player1.getX() + "," + player1.getY() + "," + player1.getCurrentState());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 현재 창 크기 가져오기
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // 배경 이미지 원본 크기 가져오기
        int bgWidth = backgroundImage.getWidth(null);
        int bgHeight = backgroundImage.getHeight(null);

        // 배경 이미지를 창 크기에 맞게 확장 (여백 없이 채우기)
        double panelAspect = (double) panelWidth / panelHeight;
        double bgAspect = (double) bgWidth / bgHeight;

        int newBgWidth, newBgHeight;

        if (panelAspect > bgAspect) {
            // 창의 가로가 더 넓은 경우: 높이를 맞추고 가로를 확장
            newBgHeight = panelHeight;
            newBgWidth = (int) (bgWidth * ((double) panelHeight / bgHeight));
        } else {
            // 창의 세로가 더 긴 경우: 가로를 맞추고 높이를 확장
            newBgWidth = panelWidth;
            newBgHeight = (int) (bgHeight * ((double) panelWidth / bgWidth));
        }

        // 배경 이미지를 중앙에 맞추어 그리기
        int x = (panelWidth - newBgWidth) / 2;
        int y = (panelHeight - newBgHeight) / 2 + 130;
        g.drawImage(backgroundImage, x, y, newBgWidth, newBgHeight, this);

        // 현재 맵 데이터 가져오기
        MapData currentMap = getCurrentMap();

        // 지형 그리기
        g.setColor(Color.GREEN);
        for (Rectangle rect : currentMap.getTerrain()) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // 포탈 그리기
        for (Portal portal : currentMap.getPortals()) {
            portal.draw(g, this);
        }

        // 플레이어 그리기
        player1.draw(g, this);
        if (opponentConnected && opponentMapIndex == currentMapIndex) {
            player2.draw(g, this);
        }

        // 상태바 그리기 - 가로 길이를 창 크기에 맞추기
        int stateBarWidth = panelWidth; // 창 크기에 맞춘 가로 길이
        int stateBarHeight = stateBarImage.getHeight(null);
        int stateBarX = 0;
        int stateBarY = panelHeight - stateBarHeight;
        g.drawImage(stateBarImage, stateBarX, stateBarY, stateBarWidth, stateBarHeight, this);

        // 버튼 그리기
        int buttonSpacing = 10;
        int buttonWidth = tradeButtonImage.getWidth(null);
        int buttonHeight = tradeButtonImage.getHeight(null);
        int buttonStartX = stateBarX + stateBarWidth - (buttonWidth * 3 + buttonSpacing * 2);
        int buttonY = stateBarY + (stateBarHeight - buttonHeight) / 2;

        g.drawImage(tradeButtonImage, buttonStartX, buttonY, this);
        g.drawImage(shortcutButtonImage, buttonStartX + buttonWidth + buttonSpacing, buttonY, this);
        g.drawImage(shopButtonImage, buttonStartX + 2 * (buttonWidth + buttonSpacing), buttonY, this);
    }




    //키입력, 화면그리기
    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 5;

        // 키 상태에 따라 동작 수행
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            player1.moveLeft(speed);
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            player1.moveRight(speed);
        }
        if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
            player1.jump();
        }

        if (pressedKeys.contains(KeyEvent.VK_UP) && isOnPortal(player1)) {
            nextMap(); // 포탈 위에서 위쪽 방향키 누르면 다음 맵으로 이동
        }

        // 현재 맵 객체를 그대로 전달
        MapData currentMap = getCurrentMap();
        player1.update(currentMap, mapWidth, mapHeight);
        sendPosition();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 사용하지 않음
    }

    private boolean isOnPortal(Player player) {
        for (Portal portal : getCurrentMap().getPortals()) {
            if (player.getX() + player.getWidth() > portal.getX() &&
                    player.getX() < portal.getX() + portal.getWidth() &&
                    player.getY() + player.getHeight() > portal.getY() &&
                    player.getY() < portal.getY() + portal.getHeight()) {
                return true;
            }
        }
        return false;
    }

    private void nextMap() {
        currentMapIndex = (currentMapIndex + 1) % maps.size();
        MapData currentMap = getCurrentMap();
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage();
        player1.setPosition(100, 300);
        opponentMapIndex = currentMapIndex;
        sendPosition();
        repaint();
    }

    private MapData getCurrentMap() {
        return maps.get(currentMapIndex);
    }




    public static void main(String[] args) {
        try {
            int playerID = Integer.parseInt(args[0]);
            JFrame frame = new JFrame("2-Player Game");
            Client gamePanel = new Client(playerID, "localhost", 5000);

//            int mapWidth = gamePanel.mapWidth;
//            int mapHeight = gamePanel.mapHeight;

            int mapWidth = 1000;
            int mapHeight = 500;

            frame.add(gamePanel);
            frame.pack();
            Insets insets = frame.getInsets();
            frame.setSize(mapWidth + insets.left + insets.right, mapHeight + insets.top + insets.bottom);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            gamePanel.requestFocusInWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
