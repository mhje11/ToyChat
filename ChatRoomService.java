import java.util.*;

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

    public String chatRoomList() {
        StringBuilder sb = new StringBuilder();
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.isPasswordProtected()) {
                sb.append(chatRoom.toString()).append(" [암호]\n");
            } else {
                sb.append(chatRoom.toString()).append(" [일반]\n");
            }
        }
        return sb.toString();
    }

    public void join(int id, ChatThread chatThread) {
        for (int i = 0; i < chatRoomList.size(); i++) {
            ChatRoom chatRoom = chatRoomList.get(i);
            if (chatRoom.getId() == id) {
                chatRoom.addChatThread(chatThread);
                break;
            }
        }
    }
    public ChatRoom createPasswordChatRoom(String title, String password) {
        System.out.println("비밀방 생성 : " + title);
        ChatRoom chatRoom = new ChatRoom(GEN_ID, title, password);
        System.out.println("비밀방 번호 " + GEN_ID + "가 생성되었습니다.");
        GEN_ID++;
        chatRoomList.add(chatRoom);
        return chatRoom;
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        chatRoomList.remove(chatRoom);
    }

    public ChatRoom findChatRoomById(int roomId) {
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.getId() == roomId) {
                return chatRoom;
            }
        }
        return null;
    }
}
