import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("서버가 준비되었습니다.");

        ChatRoomService chatRoomService = new ChatRoomService();

        while(true) {
            Socket socket = serverSocket.accept();
            System.out.println("접속 : " + socket);
            ChatThread chatThread = new ChatThread(socket, chatRoomService);
            chatThread.start();
        }
    }
}