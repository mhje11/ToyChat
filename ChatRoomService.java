import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatRoomService {

    private static int GEN_ID = 1;
    private List<ChatRoom> chatRoomList;

    public ChatRoomService() {
        this.chatRoomList = new ArrayList<>();
    }

    public ChatRoom createChatRoom(String title) {
        System.out.println("방 생성 : " + title);
        ChatRoom chatRoom = new ChatRoom(GEN_ID, title);
        System.out.println("방 번호" + GEN_ID + "가 생성되었습니다.");
        GEN_ID++;
        chatRoomList.add(chatRoom);
        return chatRoom;
    }

    public Iterator<ChatRoom> getChatRoomIterator(){
        return chatRoomList.iterator();
    }

    public void join(int id, ChatThread chatThread) {
        for(int i=0;i<chatRoomList.size();i++){
            ChatRoom chatRoom = chatRoomList.get(i);
            if(chatRoom.getId() == id ){
                chatRoom.addChatThread(chatThread);
                break;
            }
        }
    }

}
