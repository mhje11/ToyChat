import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

public class ChatThread extends Thread {
    private ChatRoom chatRoom;
    private String name;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    ChatRoomService chatRoomService;

    public ChatThread(Socket socket, ChatRoomService chatRoomService) throws Exception {
        this.socket = socket;
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = in;
        this.out = out;
        this.name = in.readLine();
        this.chatRoomService = chatRoomService;
    }

    public void sendMessage(String msg) {
        System.out.println("보낸 메시지 : " + msg);
        out.println(msg);
        out.flush();
    }

    @Override
    public void run() {
        System.out.println("ChatThread" + Thread.currentThread().getName());
        try{
            String line = null;

            while((line = in.readLine()) != null){
//                System.out.println("line -" + line);
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
                    this.chatRoom.removeChatThread(this);
                }

                else if(line.indexOf("/list") == 0){
                    Iterator<ChatRoom> chatRoomIterator= chatRoomService.getChatRoomIterator();
                    while(chatRoomIterator.hasNext()){
                        ChatRoom cr = chatRoomIterator.next();
                        out.println(cr.getId() + " - " + cr.getTitle());
                        out.flush();
                    }
                }
                else if(this.chatRoom != null){
                    System.out.println("속한 방에 브로드캐스트 합니다."+ line);
                    chatRoom.broadcast(line);

                }else{
                    System.out.println("방에 들어와있지 않아요. " + line);
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
