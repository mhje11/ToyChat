import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatThread extends Thread {
    private ChatRoom chatRoom;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Map<String, PrintWriter> userList = new HashMap<>();
    private String nickName;
    ChatRoomService chatRoomService;

    public ChatThread(Socket socket, ChatRoomService chatRoomService, Map<String, PrintWriter> userList) throws Exception {
        this.socket = socket;
        this.userList = userList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        nickName = in.readLine();
        this.chatRoomService = chatRoomService;
        synchronized (userList) {
            userList.put(this.nickName, out);
        }
    }
    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void sendMessage(String msg) {
        System.out.println(msg);
        out.println(msg);
        out.flush();
    }

    @Override
    public void run() {
        try{
            String line = null;
            while((line = in.readLine()) != null){
                if("/quit".equals(line)){
                    break;
                }
                else if(line.indexOf("/create") == 0){
                    if(line.length() >= 9) {
                        String title = line.substring(8);
                        ChatRoom chatRoom = chatRoomService.createChatRoom(title);
                        this.chatRoom = chatRoom;
                        this.chatRoom.addChatThread(this);
                    }else{
                        System.out.println("방 제목을 입력하세요.");
                    }
                }
                else if(line.indexOf("/join") == 0){
                    try {
                        chatRoomService.join(Integer.parseInt(line.substring(6)), this);
                    } catch(Exception ex){
                        out.println("방 번호가 잘못 되었습니다.");
                        out.flush();
                    }
                }
                else if(line.indexOf("/exit") == 0){
                    chatRoom.removeChatThread(this);
                    if (chatRoom.chatThreadList.isEmpty()) {
                        chatRoomService.removeChatRoom(chatRoom);
                        System.out.println("방이 삭제됐습니다.");
                    }
                        chatRoom = null;
                }

                else if(line.indexOf("/list") == 0){
                    if (chatRoomService.chatRoomList().equals("")) {
                        out.println("존재하는 방이 없습니다.");
                    }
                    out.println(chatRoomService.chatRoomList());
                    out.flush();
                }
                else if(this.chatRoom != null){
                    System.out.println("속한 방에 브로드캐스트 합니다."+ line);
                    chatRoom.broadcast(nickName, line);
                }else{
                    System.out.println("속한 채팅 방이 없습니다. ");
                }
            }
        }catch (Exception e){
            e.printStackTrace();;
        }
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
