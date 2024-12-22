import Map.MapData;
import Map.Portal;
import Monster.*;
import Player.Player;
import Player.Player1.Player1;
import Player.Player2.Player2;
import Player.Skills.Skill;
import Sound.AudioPlayer;
import UI.EndingScreen;


import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.Socket;
import java.util.*;
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

    private static final int VIEWPORT_WIDTH = 800;  // 화면에 보이는 영역 너비
    private static final int VIEWPORT_HEIGHT = 600; // 화면에 보이는 영역 높이

    //몬스터 관련
    private MonsterManager monsterManager;
    private boolean isAttacking = false;
    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN = 500; // 0.5초 공격 쿨다운

    //채팅기능 관련
    private Image chatBoxImage;  // 채팅창 이미지
    private boolean isChatting = false;  // 채팅 입력 중인지 여부
    private StringBuilder currentChat = new StringBuilder();  // 현재 입력 중인 채팅
    private long chatBlinkTimer = 0;  // 커서 깜빡임용 타이머
    private boolean showCursor = true;  // 커서 표시 여부
    private String displayMessage = "";  // 말풍선에 표시할 메시지
    private long messageDisplayTime = 0;  // 메시지 표시 시작 시간
    private static final long MESSAGE_DURATION = 4000;  // 메시지 표시 지속 시간 (4초)
    private Rectangle chatBoxBounds;  // 채팅창의 클릭 가능 영역
    private boolean isOpponentMessage = false;
    //채팅기능 관련

    //엔딩화면 관련 변수
    private EndingScreen endingScreen;  // EndingScreen 인스턴스 추가
    private Map<Integer, MonsterManager> mapMonsterManagers = new HashMap<>();
    //엔딩화면 관련


    // 키 입력 상태를 저장하는 Set
    private Set<Integer> pressedKeys;

    // Client.java 내 Player 초기화 코드 수정
    public Client(int playerID, String host, int port) throws IOException {
        this.playerID = playerID;
        // 각 맵에 대한 MonsterManager 초기화
        for (int i = 0; i < maps.size(); i++) {
            MonsterManager manager = new MonsterManager();
            manager.initializeMonsters(i);
            mapMonsterManagers.put(i, manager);
        }

        //현재 위치한 맵에대한 몬스터 매니저
        this.monsterManager = mapMonsterManagers.get(currentMapIndex);
//        monsterManager.initializeMonsters(currentMapIndex);

        // EndingScreen 초기화
        endingScreen = new EndingScreen(REFERENCE_WIDTH, REFERENCE_HEIGHT);

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


        //채팅창 이미지 초기화 및 크기조절
        chatBoxImage = new ImageIcon("images/ui/chatting.png").getImage();
        // 채팅창 이미지 크기 조정 (원본 비율 유지하면서 높이 기준으로 조정)
        double ratio = (double)chatBoxImage.getWidth(null) / chatBoxImage.getHeight(null);
        int newHeight = 30; // 상태바보다 살짝 작게
        int newWidth = (int)(newHeight * ratio) + 700;  // 기존 비율에서 100픽셀 추가
        chatBoxImage = chatBoxImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

// 채팅창 위치 조정 (상태바 바로 위에 위치)
        chatBoxBounds = new Rectangle(
                0,
                REFERENCE_HEIGHT - stateBarImage.getHeight(null) - newHeight - 40,
                newWidth,
                newHeight
        );
        addMouseListener(new ChatMouseListener());

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


    // Client.java의 receiveUpdates() 메서드 수정
    private void receiveUpdates() {
        try {
            while (true) {
                String message = input.readUTF();

                if (message.startsWith("MOVE")) {
                    handleMoveMessage(message);
                } else if (message.startsWith("MONSTER_UPDATE")) {
                    handleMonsterUpdate(message);
                } else if (message.startsWith("SKILL")) {
                    handleSkillMessage(message);
                } else if (message.startsWith("CHAT")) {
                    handleChatMessage(message);
                } else if (message.equals("GAME_END")) {
                    endGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleChatMessage(String message) {
        String[] parts = message.split(",");
        int senderId = Integer.parseInt(parts[1]);
        String chatMessage = parts[2];

        // 상대방의 메시지인 경우에만 처리
        if (senderId != playerID) {
            displayMessage = chatMessage;
            messageDisplayTime = System.currentTimeMillis();
            isOpponentMessage = true;  // 상대방 메시지임을 표시
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

        if (endingScreen.isActive()) {
            endingScreen.draw(g2d, scaleX, scaleY);
            return;
        }


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

        // 채팅 UI 그리기
        drawChat(g2d);

    }




    //키입력, 화면그리기
    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 6;

        if (endingScreen.isActive()) {
            return;
        }

        // 모든 맵의 몬스터가 죽었는지 확인
        boolean allMapsClear = true;
        for (int i = 0; i < maps.size(); i++) {
            MonsterManager manager = mapMonsterManagers.get(i);
            boolean isMapClear = manager.areAllMapMonstersDead(i);


            if (!isMapClear) {
                allMapsClear = false;
                break;
            }
        }

        if (allMapsClear) {

            endGame();
            return;
        }

        // 키 상태에 따라 동작 수행
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            player1.moveLeft(speed);
        }

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

        // 스킬 몬스터 충돌 체크
        checkSkillMonsterCollisions();

        // 몬스터 업데이트
        for (Monster monster : monsterManager.getMonsters()) {
            if (!monster.isAlive() &&
                    System.currentTimeMillis() - monster.getDeathTime() >= Monster.DEATH_ANIMATION_DURATION) {
                // 몬스터가 완전히 사라진 후의 처리를 여기에 추가할 수 있습니다
                // 예: 경험치 획득, 아이템 드롭 등
            }
        }

        sendPosition();
        repaint();
    }
    // endGame 메소드 수정
    private void endGame() {
        endingScreen.activate();
        audioPlayer.stop();

        // 서버에 게임 종료 알림
        try {
            output.writeUTF("GAME_END," + playerID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // restartGame 메소드 수정
    private void restartGame() {
        endingScreen.deactivate();
        currentMapIndex = 0;

        // 맵 초기화
        MapData currentMap = getCurrentMap();
        baseImage = new ImageIcon(currentMap.getBaseImagePath()).getImage();
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage();

        // 플레이어 위치 초기화
        Rectangle firstGround = currentMap.getTerrain().get(0);
        player1.setPosition(firstGround.x + 50, firstGround.y - 195);

        // 몬스터 초기화
        monsterManager.initializeMonsters(currentMapIndex);

        // 배경음악 재생
        playCurrentMapMusic();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (endingScreen.isActive() && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        if (isChatting) {
            handleChatKeyPress(e);
        } else {
            // 기존의 게임 키 입력 처리
            pressedKeys.add(e.getKeyCode());

            switch (e.getKeyCode()) {
                case KeyEvent.VK_Q:
                    if (player1.getQSkill().canUse()) {
                        player1.useQSkill();
                        sendSkillUse("Q");
                    }
                    break;
                case KeyEvent.VK_W:
                    if (player1.getWSkill().canUse()) {
                        player1.useWSkill();
                        sendSkillUse("W");
                    }
                    break;
                case KeyEvent.VK_E:
                    if (player1.getESkill().canUse()) {
                        player1.useESkill();
                        sendSkillUse("E");
                    }
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }


    // 스킬 사용 메시지 전송
    private void sendSkillUse(String skillType) {
        try {
            String message = String.format("SKILL,%d,%s,%d,%d,%b,%s",
                    playerID,
                    skillType,
                    player1.getX(),
                    player1.getY(),
                    player1.isFacingRight(),
                    player1.getCurrentState()
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
            String state = data[6];

            // 상대방의 스킬 사용을 처리
            if (id != playerID && player2 != null) {
                player2.setPosition(x, y);
                player2.setState(state);
                // facingRight 설정 추가
                if (facingRight) {
                    if (!player2.isFacingRight()) {
                        player2.moveRight(0); // 방향만 바꾸기 위해 속도 0으로 설정
                    }
                } else {
                    if (player2.isFacingRight()) {
                        player2.moveLeft(0); // 방향만 바꾸기 위해 속도 0으로 설정
                    }
                }

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

        monsterManager = mapMonsterManagers.get(currentMapIndex);

        baseImage = new ImageIcon(currentMap.getBaseImagePath()).getImage();
        backgroundImage = new ImageIcon(currentMap.getBackgroundImagePath()).getImage();

        // 플레이어 위치 초기화
        player1.setPosition(portal.getSpawnX(), portal.getSpawnY());

        // 상대방도 같은 맵으로 업데이트
//        opponentMapIndex = currentMapIndex;

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

    private void checkSkillMonsterCollisions() {
        Skill currentSkill = null;
        if (player1.getQSkill().isActive()) currentSkill = player1.getQSkill();
        else if (player1.getWSkill().isActive()) currentSkill = player1.getWSkill();
        else if (player1.getESkill().isActive()) currentSkill = player1.getESkill();

        if (currentSkill == null || currentSkill.getHitbox() == null) return;

        Rectangle skillHitbox = currentSkill.getHitbox();
        MonsterManager currentManager = mapMonsterManagers.get(currentMapIndex);
        CopyOnWriteArrayList<Monster> monsters = currentManager.getMonsters();

        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            if (monster.isAlive() && monster.hitbox.intersects(skillHitbox)) {
                try {
                    output.writeUTF("HIT_MONSTER," + i + "," + currentSkill.getDamage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (isChatting) {
            // 엔터키와 백스페이스는 제외 (이는 keyPressed에서 처리)
            if (e.getKeyChar() != KeyEvent.VK_ENTER && e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                currentChat.append(e.getKeyChar());
            }
        }
    }

    private void handleChatKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (currentChat.length() > 0) {
                displayMessage = currentChat.toString();
                messageDisplayTime = System.currentTimeMillis();
                sendChatMessage(displayMessage);
            }
            isChatting = false;
            currentChat.setLength(0);
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && currentChat.length() > 0) {
            currentChat.deleteCharAt(currentChat.length() - 1);
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            isChatting = false;
            currentChat.setLength(0);
        }
        // 여기서 문자 추가하는 부분 제거 (keyTyped에서 처리)
    }

    // 마우스 클릭 이벤트 처리를 위한 내부 클래스
    private class ChatMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickPoint = new Point((int)(e.getX() / scaleX), (int)(e.getY() / scaleY));
            if (chatBoxBounds.contains(clickPoint)) {
                isChatting = true;
                chatBlinkTimer = System.currentTimeMillis();
            }
        }
    }

    // paintComponent 메서드에 추가할 채팅 UI 그리기 코드
    private void drawChat(Graphics2D g2d) {
        // 채팅창 그리기
        g2d.drawImage(chatBoxImage, chatBoxBounds.x, chatBoxBounds.y,
                chatBoxBounds.width, chatBoxBounds.height, null);

        // 입력 중인 텍스트와 커서 그리기
        if (isChatting) {
            g2d.setColor(Color.BLACK);
            String displayText = currentChat.toString();
            if (System.currentTimeMillis() - chatBlinkTimer > 500) {
                showCursor = !showCursor;
                chatBlinkTimer = System.currentTimeMillis();
            }
            if (showCursor) {
                displayText += "|";
            }
            g2d.drawString(displayText, chatBoxBounds.x + 200,
                    chatBoxBounds.y + chatBoxBounds.height - 10);
        }

        // 말풍선 메시지 그리기
        if (!displayMessage.isEmpty() &&
                System.currentTimeMillis() - messageDisplayTime < MESSAGE_DURATION) {
            if (isOpponentMessage) {
                // 상대방 메시지는 player2 위에 그리기
                drawChatBubble(g2d, displayMessage, player2.getX() + player2.getWidth() / 2,
                        player2.getY() - 20);
            } else {
                // 자신의 메시지는 player1 위에 그리기
                drawChatBubble(g2d, displayMessage, player1.getX() + player1.getWidth() / 2,
                        player1.getY() - 20);
            }
        }
    }

    private void drawChatBubble(Graphics2D g2d, String message, int x, int y) {
        int maxCharsPerLine = 15; // 한 줄당 최대 글자 수
        ArrayList<String> lines = splitString(message, maxCharsPerLine);

        FontMetrics fm = g2d.getFontMetrics();
        int padding = 10;

        int maxLineWidth = 0;
        for (String line : lines) {
            int lineWidth = fm.stringWidth(line);
            maxLineWidth = Math.max(maxLineWidth, lineWidth);
        }

        int bubbleWidth = maxLineWidth + padding * 2;
        int bubbleHeight = (fm.getHeight() * lines.size()) + padding;

        // 말풍선 배경
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(x - bubbleWidth / 2, y - bubbleHeight,
                bubbleWidth, bubbleHeight, 10, 10);

        // 말풍선 테두리
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x - bubbleWidth / 2, y - bubbleHeight,
                bubbleWidth, bubbleHeight, 10, 10);

        // 각 줄의 메시지 텍스트 그리기
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineWidth = fm.stringWidth(line);
            int lineY = y - bubbleHeight + fm.getAscent() + (i * fm.getHeight()) + (padding / 2);
            g2d.drawString(line, x - lineWidth / 2, lineY);
        }
    }

    private ArrayList<String> splitString(String text, int maxCharsPerLine) {
        ArrayList<String> lines = new ArrayList<>();

        while (text.length() > maxCharsPerLine) {
            int endIndex = maxCharsPerLine;
            String line = text.substring(0, endIndex);
            lines.add(line);
            text = text.substring(endIndex);
        }

        if (!text.isEmpty()) {
            lines.add(text);
        }

        return lines;
    }

    // 서버로 채팅 메시지 전송하는 메서드
    private void sendChatMessage(String message) {
        try {
            output.writeUTF("CHAT," + playerID + "," + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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