import Map.MapData;
import Map.Portal;
import Player.Player;
import Player.Player1.Player1;
import Player.Player2.Player2;
import Sound.AudioPlayer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Image backgroundImage;
    private int mapWidth, mapHeight;

    private Player player1, player2; // player1이 자신 player2는 상대방
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
    private CopyOnWriteArrayList<MapData> maps = MapData.getMaps();
    private int currentMapIndex =0;
    private int opponentMapIndex = -1;
    private CopyOnWriteArrayList<Rectangle> ground;
    private AudioPlayer audioPlayer; // 배경음악 플레이어


    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private static final int REFERENCE_WIDTH = 1400;  // 기준이 되는 창 너비
    private static final int REFERENCE_HEIGHT = 800;  // 기준이 되는 창 높이
    private int cameraX = 0;
    private int cameraY = 0;
    private static final int VIEWPORT_WIDTH = 800;  // 화면에 보이는 영역 너비
    private static final int VIEWPORT_HEIGHT = 600; // 화면에 보이는 영역 높이

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

        if (playerID == 1) {
            player1 = new Player1(playerStartX, playerStartY); // 1번 클라이언트는 Player1
            player2 = new Player2(playerStartX + 100, playerStartY); // 상대방은 Player2
        } else {
            player1 = new Player2(playerStartX, playerStartY); // 2번 클라이언트는 Player2
            player2 = new Player1(playerStartX + 100, playerStartY); // 상대방은 Player1
        }

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


        // 배경음악 초기화
        audioPlayer = new AudioPlayer();
        playCurrentMapMusic();
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

                    if (id != playerID) { // 상대방 플레이어 데이터
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
        Graphics2D g2d = (Graphics2D) g;

        // 현재 패널 크기 가져오기
        int currentWidth = getWidth();
        int currentHeight = getHeight();

        // 스케일 팩터 계산
        scaleX = (double) currentWidth / REFERENCE_WIDTH;
        scaleY = (double) currentHeight / REFERENCE_HEIGHT;

        // 스케일 변환 적용
        g2d.scale(scaleX, scaleY);

        // 현재 맵 데이터 가져오기
        MapData currentMap = getCurrentMap();

        // 배경 색상 채우기
        g2d.setColor(currentMap.getBackgroundColor());
        g2d.fillRect(0, 0, REFERENCE_WIDTH, REFERENCE_HEIGHT);

        // 배경 이미지 그리기
        g2d.drawImage(backgroundImage, 0, currentMap.getBackgroundYOffset(),
                REFERENCE_WIDTH, REFERENCE_HEIGHT, this);

        // 지형 그리기
        g2d.setColor(Color.GREEN);
        for (Rectangle rect : currentMap.getTerrain()) {
            g2d.fillRect(
                    rect.x,
                    rect.y,
                    rect.width,
                    rect.height
            );
        }

        // 포탈 그리기
        for (Portal portal : currentMap.getPortals()) {
            portal.draw(g2d, this);
        }

        // 플레이어 그리기
        player1.draw(g2d, this);
        if (opponentConnected && opponentMapIndex == currentMapIndex) {
            player2.draw(g2d, this);
        }

        // UI 요소 그리기
        drawUI(g2d);
    }

    private void updateCamera() {
        // 플레이어가 화면 중앙에 오도록 카메라 위치 계산
        cameraX = player1.getX() - VIEWPORT_WIDTH / 2;
        cameraY = player1.getY() - VIEWPORT_HEIGHT / 2;

        // 카메라가 맵 경계를 벗어나지 않도록 제한
        cameraX = Math.max(0, Math.min(cameraX, mapWidth - VIEWPORT_WIDTH));
        cameraY = Math.max(0, Math.min(cameraY, mapHeight - VIEWPORT_HEIGHT));
    }

    private void drawUI(Graphics2D g2d) {
        // 상태바 그리기
        int stateBarHeight = stateBarImage.getHeight(null);
        g2d.drawImage(stateBarImage, 0, REFERENCE_HEIGHT - stateBarHeight,
                REFERENCE_WIDTH, stateBarHeight, this);

        // 버튼 그리기
        int buttonSpacing = 10;
        int buttonWidth = tradeButtonImage.getWidth(null);
        int buttonHeight = tradeButtonImage.getHeight(null);
        int buttonStartX = REFERENCE_WIDTH - (buttonWidth * 3 + buttonSpacing * 2);
        int buttonY = REFERENCE_HEIGHT - stateBarHeight +
                (stateBarHeight - buttonHeight) / 2;

        g2d.drawImage(tradeButtonImage, buttonStartX, buttonY, this);
        g2d.drawImage(shortcutButtonImage,
                buttonStartX + buttonWidth + buttonSpacing, buttonY, this);
        g2d.drawImage(shopButtonImage,
                buttonStartX + 2 * (buttonWidth + buttonSpacing), buttonY, this);
    }

    // 마우스 클릭 위치 변환을 위한 메서드
    private Point transformPoint(Point p) {
        return new Point(
                (int)(p.x / scaleX),
                (int)(p.y / scaleY)
        );
    }

    // Player 클래스의 update 메서드에서 사용할 스케일 적용 메서드
    private Rectangle scaleRectangle(Rectangle rect) {
        return new Rectangle(
                (int)(rect.x * scaleX),
                (int)(rect.y * scaleY),
                (int)(rect.width * scaleX),
                (int)(rect.height * scaleY)
        );
    }


    //키입력, 화면그리기
    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 6;

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

        // Q 키에 특수 동작
        if (pressedKeys.contains(KeyEvent.VK_Q)) {
//            player1.performSpecialAction();
        }

        // 포탈 위에 있는 경우 맵 전환
        if (pressedKeys.contains(KeyEvent.VK_UP)) {
            Portal portal = getPortalOnPlayer(player1);

            if (portal != null) {
                nextMap(portal);
            }
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

    //플레이어가 포탈위에 있는지 판단하는 메서드
    // 포탈을 통해 다음 맵으로 이동
    private void nextMap(Portal portal) {

        currentMapIndex = portal.getNextMapIndex(); // 포탈에 설정된 다음 맵 인덱스 가져오기
        MapData currentMap = getCurrentMap();

        

        // 배경 이미지 업데이트
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage();

        // 플레이어 위치 초기화
        player1.setPosition(portal.getSpawnX(), portal.getSpawnY());

        // 상대방도 같은 맵으로 업데이트
        opponentMapIndex = currentMapIndex;

        // 새로운 배경음악 재생
        playCurrentMapMusic();



        // 화면 갱신
        repaint();
    }

    // 플레이어와 충돌한 포탈 반환 (없으면 null)
    private Portal getPortalOnPlayer(Player player) {
        for (Portal portal : getCurrentMap().getPortals()) {
            Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            if (playerBounds.intersects(portal.getBounds())) {
                return portal; // 충돌한 포탈 반환
            }
        }
        return null; // 충돌한 포탈이 없으면 null 반환
    }



    private MapData getCurrentMap() {
        return maps.get(currentMapIndex);
    }

    private void playCurrentMapMusic() {
        String musicPath = getCurrentMap().getBackgroundMusicPath();
        audioPlayer.play(musicPath);
    }


    public static void main(String[] args) {
        try {
            int playerID = Integer.parseInt(args[0]);
            JFrame frame = new JFrame("2-Player Game");
            Client gamePanel = new Client(playerID, "localhost", 5000);

            frame.add(gamePanel);
            frame.setPreferredSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            gamePanel.requestFocusInWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
