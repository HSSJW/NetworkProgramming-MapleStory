import Map.MapData;
import Map.Portal;
import Monster.*;
import Player.Player;
import Player.Player1.Player1;
import Player.Player2.Player2;
import Sound.AudioPlayer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client extends JPanel implements ActionListener, KeyListener {
    private Timer timer;


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
    private Image baseImage; // 배경이미지
    private Image backgroundImage; //지형 이미지
    private int mapWidth, mapHeight;

    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private static final int REFERENCE_WIDTH = 1400;  // 기준이 되는 창 너비
    private static final int REFERENCE_HEIGHT = 800;  // 기준이 되는 창 높이
    private int cameraX = 0;
    private int cameraY = 0;
    private static final int VIEWPORT_WIDTH = 800;  // 화면에 보이는 영역 너비
    private static final int VIEWPORT_HEIGHT = 600; // 화면에 보이는 영역 높이

    //몬스터 관련
    private MonsterManager monsterManager;
    private boolean isAttacking = false;
    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN = 500; // 0.5초 공격 쿨다운


    // 키 입력 상태를 저장하는 Set
    private Set<Integer> pressedKeys;

    // Client.java 내 Player 초기화 코드 수정
    public Client(int playerID, String host, int port) throws IOException {
        this.playerID = playerID;
        this.monsterManager = new MonsterManager(); //몬스터 매니저 초기화
        monsterManager.initializeMonsters(currentMapIndex);


        // 첫 번째 맵 설정
        MapData currentMap = getCurrentMap();
        baseImage = new ImageIcon(currentMap.getBaseImagePath()).getImage(); //배경이미지 초기화
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage(); //지형이미지 초기화
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
            byte[] buffer = new byte[4096];
            StringBuilder messageBuilder = new StringBuilder();

            while (true) {
                String message = input.readUTF();


                // 메시지 시작 부분 확인
                if (message.contains("MONSTER_UPDATE")) {
                    // MONSTER_UPDATE 부분부터 시작하도록 자르기
                    int startIndex = message.indexOf("MONSTER_UPDATE");
                    message = message.substring(startIndex);
                }

                if (message.startsWith("MOVE")) {
                    handleMoveMessage(message);
                } else if (message.startsWith("MONSTER_UPDATE")) {
                    handleMonsterUpdate(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleMoveMessage(String message) {
        try {
            String[] data = message.split(",");
            int id = Integer.parseInt(data[1]);
            int mapIndex = Integer.parseInt(data[2]);
            int x = Integer.parseInt(data[3]);
            int y = Integer.parseInt(data[4]);
            String state = data[5];

            if (id != playerID) {
                if (player2 != null) {
                    player2.setPosition(x, y);
                    player2.setState(state);
                    opponentMapIndex = mapIndex;
                    opponentConnected = true;
                }
            }
            repaint();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void handleMonsterUpdate(String message) {
        try {
            String monsterData = message.substring("MONSTER_UPDATE,".length());
            updateMonsterStates(monsterData);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void updateMonsterStates(String monsterData) {
        try {
            String[] monsters = monsterData.split(";");
            // monsterManager의 monsters 리스트 크기 갱신
            CopyOnWriteArrayList<Monster> currentMonsters = monsterManager.getMonsters();

            // 각 몬스터 데이터에 대해
            for (int i = 0; i < monsters.length; i++) {
                String[] data = monsters[i].split(",");

                // 현재 인덱스가 몬스터 리스트 범위를 벗어나면 건너뛰기
                if (i >= currentMonsters.size()) {

                    continue;
                }

                Monster monster = currentMonsters.get(i);

                // 몬스터 상태 업데이트
                monster.setPosition(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                monster.setHp(Integer.parseInt(data[2]));
                monster.setState(data[3]);
                monster.setFacingRight(Boolean.parseBoolean(data[4]));
                monster.setAlive(Boolean.parseBoolean(data[5]));
            }


            repaint();
        } catch (Exception e) {
            System.err.println("Error updating monster states: " + e.getMessage());
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

        // 렌더링 품질 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 현재 상태 저장
        AffineTransform originalTransform = g2d.getTransform();
        Composite originalComposite = g2d.getComposite();

        // 현재 패널 크기 가져오기
        int currentWidth = getWidth();
        int currentHeight = getHeight();

        // 스케일 팩터 계산
        scaleX = (double) currentWidth / REFERENCE_WIDTH;
        scaleY = (double) currentHeight / REFERENCE_HEIGHT;

        // 스케일 변환 적용
        g2d.scale(scaleX, scaleY);

        // 배경 이미지 그리기
        g2d.drawImage(baseImage, 0, 0, REFERENCE_WIDTH, REFERENCE_HEIGHT, this);

        // 현재 맵 데이터 가져오기
        MapData currentMap = getCurrentMap();

        // 배경 이미지 그리기
        g2d.drawImage(backgroundImage, 0, currentMap.getBackgroundYOffset(),
                REFERENCE_WIDTH, REFERENCE_HEIGHT, this);

        // 지형 그리기
        g2d.setColor(Color.GREEN);
        for (Rectangle rect : currentMap.getTerrain()) {
            g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        // 포탈 그리기
        for (Portal portal : currentMap.getPortals()) {
            portal.draw(g2d, this);
        }

        // 몬스터 그리기
        for (Monster monster : monsterManager.getMonsters()) {
            if (monster.isAlive()) {
                monster.paintMonster(g2d, this);
            }
        }

        // 플레이어와 스킬 그리기
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // 디버깅용 - 플레이어 위치에 사각형 그리기
        g2d.setColor(Color.YELLOW);
        g2d.drawRect(player1.getX(), player1.getY(), player1.getWidth(), player1.getHeight());

        player1.draw(g2d, this);
        if (opponentConnected && opponentMapIndex == currentMapIndex) {
            player2.draw(g2d, this);
        }

        // UI 요소 그리기
        drawUI(g2d);

        // 원래 상태로 복원
        g2d.setTransform(originalTransform);
        g2d.setComposite(originalComposite);
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

        // 스킬 업데이트 추가
        player1.updateSkills();  // Player 클래스에 이 메서드 추가 필요
        if (player2 != null) {
            player2.updateSkills();
        }

        sendPosition();
        repaint();
    }

    // 공격 처리 메소드 추가
    private void attackMonster() {
        // 공격 범위 설정 (플레이어 앞쪽 30픽셀)
        Rectangle attackBox = new Rectangle(
                player1.getX() + (player1.getCurrentState().equals("right") ? player1.getWidth() : -30),
                player1.getY(),
                30,
                player1.getHeight()
        );

        // 몬스터와의 충돌 체크
        for (int i = 0; i < monsterManager.getMonsters().size(); i++) {
            Monster monster = monsterManager.getMonsters().get(i);
            if (monster.isAlive()) {
                Rectangle monsterBox = new Rectangle(
                        monster.getX(),
                        monster.getY(),
                        monster.getWidth(),
                        monster.getHeight()
                );

                if (attackBox.intersects(monsterBox)) {
                    try {
                        output.writeUTF("HIT_MONSTER," + i + ",20"); // 20은 공격력
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());

        // 스킬 키 입력 처리
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
                player1.useQSkill();
                sendSkillUse("Q");
                break;
            case KeyEvent.VK_W:
                player1.useWSkill();
                sendSkillUse("W");
                break;
            case KeyEvent.VK_E:
                player1.useESkill();
                sendSkillUse("E");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 사용하지 않음
    }

    // 스킬 사용 메시지 전송
    private void sendSkillUse(String skillType) {
        try {
            String message = String.format("SKILL,%d,%s,%d,%d,%b",
                    playerID,
                    skillType,
                    player1.getX(),
                    player1.getY(),
                    player1.isFacingRight()
            );
            output.writeUTF(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // 스킬 메시지 수신 처리
    private void handleSkillMessage(String message) {
        try {
            String[] data = message.split(",");
            int id = Integer.parseInt(data[1]);
            String skillType = data[2];
            int x = Integer.parseInt(data[3]);
            int y = Integer.parseInt(data[4]);
            boolean facingRight = Boolean.parseBoolean(data[5]);

            // 상대방의 스킬 사용 처리
            if (id != playerID) {
                switch (skillType) {
                    case "Q":
                        player2.useQSkill();
                        break;
                    case "W":
                        player2.useWSkill();
                        break;
                    case "E":
                        player2.useESkill();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //플레이어가 포탈위에 있는지 판단하는 메서드
    // 포탈을 통해 다음 맵으로 이동
    private void nextMap(Portal portal) {
        currentMapIndex = portal.getNextMapIndex();
        MapData currentMap = getCurrentMap();

        baseImage = new ImageIcon(currentMap.getBaseImagePath()).getImage();
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage();

        // 플레이어 위치 초기화
        player1.setPosition(portal.getSpawnX(), portal.getSpawnY());

        // 상대방도 같은 맵으로 업데이트
        opponentMapIndex = currentMapIndex;

        // 새로운 배경음악 재생
        playCurrentMapMusic();

        // 서버에 맵 변경 알림
        try {
            output.writeUTF("MAP_CHANGE," + currentMapIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //몬스터 갱신
        monsterManager.initializeMonsters(currentMapIndex);

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