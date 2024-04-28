import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8008);
        System.out.println("서버가 준비되었습니다.");
        Map<String, PrintWriter> userList = new HashMap<>();
        ChatRoomService chatRoomService = new ChatRoomService();

        while(true) {
            Socket socket = serverSocket.accept();
            System.out.println("접속 : " + socket);
            ChatThread chatThread = new ChatThread(socket, chatRoomService, userList);
            chatThread.start();
        }
    }
}