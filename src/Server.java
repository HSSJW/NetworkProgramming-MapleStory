import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    //클라이언트 소켓 연결
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        // 몬스터 이동 스레드 시작
//        new Thread(this::updateMonster).start();

        try {
            while (clients.size() < 2) { // 최대 2명까지만 연결 허용
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    
    //클라이언트 정보 처리
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private DataOutputStream output;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.output = new DataOutputStream(socket.getOutputStream());
        }

        public void run() {
            try (DataInputStream input = new DataInputStream(clientSocket.getInputStream())) {
                String inputLine;
                while ((inputLine = input.readUTF()) != null) {
                    // 클라이언트에서 몬스터 공격 메시지를 받으면 처리
                    if (inputLine.startsWith("HIT_MONSTER")) {
//                        handleMissileHit(); // 몬스터가 공격받았을 때 처리
                    } else {
                        // 다른 메시지는 모든 클라이언트에 전달
                        for (ClientHandler client : clients) {
                            if (client != this) {
                                client.send(inputLine);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    clients.remove(this);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(String message) {
            try {
                output.writeUTF(message);
            } catch (IOException e) {
                clients.remove(this);
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        try {
            new Server(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
