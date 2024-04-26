import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private int id;
    private String title;
    protected List<ChatThread> chatThreadList;

    public ChatRoom(int id, String title) {
        chatThreadList = new ArrayList<>();
        this.title = title;
        this.id = id;
    }


    public void broadcast(String sender, String msg) {
        for (ChatThread chatThread : chatThreadList) {
            chatThread.sendMessage(sender + " : " + msg);
        }
    }

    public void addChatThread(ChatThread chatThread) {
        chatThreadList.add(chatThread);
        chatThread.setChatRoom(this);
    }

    public void removeChatThread(ChatThread chatThread) {
        chatThreadList.remove(chatThread);
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
        return "방 id : " + id +
                ", 방의 제목 : " + title;
    }
}
