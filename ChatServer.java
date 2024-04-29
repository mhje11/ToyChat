import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static Map<String, ChatThread> userThreadMap;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8008);
        System.out.println("서버가 준비되었습니다.");
        Map<String, PrintWriter> userList = new HashMap<>();
        userThreadMap = new HashMap<>();
        ChatRoomService chatRoomService = new ChatRoomService();

        while(true) {
            Socket socket = serverSocket.accept();
            ChatThread chatThread = new ChatThread(socket, chatRoomService, userList);
            System.out.println("접속 : " + chatThread.getNickName());
            userThreadMap.put(chatThread.getNickName(), chatThread);
            chatThread.start();
        }
    }
    public static Map getUserThreadMap() {
        return userThreadMap;
    }
}