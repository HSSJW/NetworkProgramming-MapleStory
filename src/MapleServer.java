import Map.MapData;
import Monster.*;

import javax.swing.Timer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapleServer {
    private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;
    private boolean isRunning = true;

    // 맵 관련
    private Map<ClientHandler, Integer> clientMaps = new HashMap<>();
    private Map<Integer, MonsterManager> mapMonsterManagers = new HashMap<>();
    private Timer monsterUpdateTimer;
    private int currentMapIndex = 0;

    public MapleServer(int port) {
        this.port = port;

        // 각 맵에 대한 MonsterManager 초기화
        for (int i = 0; i < MapData.getMaps().size(); i++) {
            MonsterManager manager = new MonsterManager();
            manager.initializeMonsters(i);
            mapMonsterManagers.put(i, manager);
        }

        // 몬스터 업데이트 타이머 수정
        monsterUpdateTimer = new Timer(16, e -> {
            // 클라이언트 존재 여부와 상관없이 모든 맵의 몬스터 업데이트
            for (Map.Entry<Integer, MonsterManager> entry : mapMonsterManagers.entrySet()) {
                int mapIndex = entry.getKey();
                MonsterManager manager = entry.getValue();
                MapData mapData = MapData.getMaps().get(mapIndex);

                for (Monster monster : manager.getMonsters()) {
                    if (monster.isAlive()) {
                        monster.update(mapData);
                    }
                }

                // 클라이언트가 있는 경우에만 상태 전송
                if (!clients.isEmpty()) {
                    String monsterState = createMonsterStateMessage(mapIndex, manager);
                    broadcastMonsterStateToMap(mapIndex, monsterState);
                }
            }
        });
    }

    private void broadcastMonsterStateToMap(int mapIndex, String monsterState) {
        for (Map.Entry<ClientHandler, Integer> entry : clientMaps.entrySet()) {
            ClientHandler client = entry.getKey();
            int clientMapIndex = entry.getValue();
            if (clientMapIndex == mapIndex) {
                client.send(monsterState);
            }
        }
    }

    private String createMonsterStateMessage(int mapIndex, MonsterManager manager) {
        StringBuilder message = new StringBuilder("MONSTER_UPDATE,");
        for (Monster monster : manager.getMonsters()) {
            message.append(String.format("%d,%d,%d,%s,%b,%b;",
                    monster.getX(),
                    monster.getY(),
                    monster.getHp(),
                    monster.getCurrentState(),
                    monster.isFacingRight(),
                    monster.isAlive()
            ));
        }
        if (message.length() > 0 && message.charAt(message.length() - 1) == ';') {
            message.setLength(message.length() - 1);
        }
        return message.toString();
    }

    private synchronized void broadcastMonsterState() {
        try {
            for (Map.Entry<ClientHandler, Integer> entry : clientMaps.entrySet()) {
                ClientHandler client = entry.getKey();
                int mapIndex = entry.getValue();
                MonsterManager manager = mapMonsterManagers.get(mapIndex);
                String monsterState = createMonsterStateMessage(mapIndex, manager);
                client.send(monsterState);
            }
        } catch (Exception e) {
            System.out.println("Error broadcasting monster state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        // 몬스터 업데이트 타이머 시작
        monsterUpdateTimer.start();

        // 클라이언트 접속을 처리하는 스레드 시작
        new Thread(() -> {
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clients.size() < 2) {  // 최대 2명까지만 접속 허용
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        clients.add(clientHandler);

                        // 새로운 클라이언트 접속 시 맵 0의 몬스터 상태 전송
                        MonsterManager initialManager = mapMonsterManagers.get(0);
                        String monsterState = createMonsterStateMessage(0, initialManager);
                        clientHandler.send(monsterState);

                        new Thread(clientHandler).start();
                        System.out.println("New client connected. Total clients: " + clients.size());
                    } else {
                        clientSocket.close();  // 추가 접속 거부
                    }
                } catch (IOException e) {
                    if (isRunning) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
        monsterUpdateTimer.stop();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void handleMapChange(String data) {
        try {
            String[] parts = data.split(",");
            currentMapIndex = Integer.parseInt(parts[1]);

            // 해당 맵의 MonsterManager 사용
            MonsterManager manager = mapMonsterManagers.get(currentMapIndex);
            String monsterState = createMonsterStateMessage(currentMapIndex, manager);
            broadcastMonsterStateToMap(currentMapIndex, monsterState);
        } catch (Exception e) {
            System.out.println("Error handling map change: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private DataOutputStream output;
        private boolean isConnected = true;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.output = new DataOutputStream(socket.getOutputStream());
        }

        public void run() {
            try (DataInputStream input = new DataInputStream(clientSocket.getInputStream())) {
                clientMaps.put(this, 0);

                while (isConnected) {
                    String inputLine = input.readUTF();

                    if (inputLine.startsWith("MAP_CHANGE")) {
                        String[] parts = inputLine.split(",");
                        int newMapIndex = Integer.parseInt(parts[1]);
                        clientMaps.put(this, newMapIndex);
                        String monsterState = createMonsterStateMessage(newMapIndex, mapMonsterManagers.get(newMapIndex));
                        send(monsterState);
                    }
                    else if (inputLine.startsWith("SKILL")) {
                        // 같은 맵에 있는 다른 클라이언트에게만 스킬 사용 정보 전달
                        int senderMapIndex = clientMaps.get(this);
                        for (ClientHandler client : clients) {
                            if (client != this && clientMaps.get(client) == senderMapIndex) {
                                client.send(inputLine);
                            }
                        }
                    }
                    else if (inputLine.startsWith("HIT_MONSTER")) {
                        String[] data = inputLine.split(",");
                        int monsterId = Integer.parseInt(data[1]);
                        int damage = Integer.parseInt(data[2]);
                        int clientMapIndex = clientMaps.get(this);
                        MonsterManager manager = mapMonsterManagers.get(clientMapIndex);
                        manager.handleMonsterHit(monsterId, damage);
                        String monsterState = createMonsterStateMessage(clientMapIndex, manager);
                        broadcastMonsterStateToMap(clientMapIndex, monsterState);

                        // 모든 맵의 몬스터가 죽었는지 확인
                        if (checkAllMapsCleared()) {
                            broadcastGameEnd();
                        }
                    }
                    else if (inputLine.startsWith("MOVE")) {
                        for (ClientHandler client : clients) {
                            if (client != this) {
                                client.send(inputLine);
                            }
                        }
                    }

                    if (inputLine.startsWith("CHAT")) {
                        // 채팅 메시지 파싱
                        String[] parts = inputLine.split(",");
                        int senderId = Integer.parseInt(parts[1]);
                        String message = parts[2];

                        // 채팅 메시지 형식 재구성
                        String chatMessage = "CHAT," + senderId + "," + message;

                        // 같은 맵에 있는 다른 클라이언트에게만 메시지 전달
                        int senderMapIndex = clientMaps.get(this);
                        for (ClientHandler client : clients) {
                            if (client != this && clientMaps.get(client) == senderMapIndex) {
                                client.send(chatMessage);  // 다른 클라이언트에게 메시지 전송
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        public void send(String message) {
            try {
                if (isConnected && output != null) {
                    // 메시지 전송 전 synchronized 블록으로 보호
                    synchronized (output) {
                        output.writeUTF(message);
                        output.flush();

                        // 연속된 메시지 사이에 작은 지연 추가
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error sending message: " + message);
                e.printStackTrace();
                closeConnection();
            }
        }

        public void closeConnection() {
            try {
                isConnected = false;
                clientMaps.remove(this);  // 클라이언트 맵 정보 제거
                clients.remove(this);
                clientSocket.close();
                System.out.println("Client connection closed. Remaining clients: " + clients.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //모든 맵의 몬스터가 죽었는지 체크
        private boolean checkAllMapsCleared() {
            for (Map.Entry<Integer, MonsterManager> entry : mapMonsterManagers.entrySet()) {
                MonsterManager manager = entry.getValue();
                boolean isMapClear = true;
                for (Monster monster : manager.getMonsters()) {
                    if (monster.isAlive()) {
                        isMapClear = false;
                        break;
                    }
                }
                if (!isMapClear) {
                    return false;
                }
            }
            return true;
        }

        //엔딩화면으로 전환
        private void broadcastGameEnd() {
            for (ClientHandler client : clients) {
                client.send("GAME_END");
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        MapleServer server = new MapleServer(port);
        try {
            server.start();

            // 종료 시그널 처리
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}