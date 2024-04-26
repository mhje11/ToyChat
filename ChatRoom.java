import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private int id;
    private String title;
    private List<ChatThread> chatThreadList;

    public ChatRoom(int id, String title) {
        chatThreadList = new ArrayList<>();
        this.title = title;
        this.id = id;
    }


    public void broadcast(String msg) {
        for (int i = 0; i < chatThreadList.size(); i++){
            ChatThread chatThread = chatThreadList.get(i);
            chatThread.sendMessage(msg);
        }
    }

    public void addChatThread(ChatThread chatThread) {
        chatThreadList.add(chatThread);
        chatThread.setChatRoom(this);
    }

    public void removeChatThread(ChatThread chatThread) {
        chatThreadList.remove(chatThread);
        chatThread.setChatRoom(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ChatRoomService{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
