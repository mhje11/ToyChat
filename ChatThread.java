import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
public class ChatThread extends Thread {
    protected String acceptUserName = null;
    public void setAcceptUserName(String acceptUserName) {
        this.acceptUserName = acceptUserName;
    }
    private ChatRoom chatRoom;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Map<String, PrintWriter> userList = new HashMap<>();
    private int inviteRoomId;
    public int getInviteRoomId() {
        return inviteRoomId;
    }
    public void setInviteRoomId(int inviteRoomId) {
        this.inviteRoomId = inviteRoomId;
    }
    private String nickName;
    ChatRoomService chatRoomService;
    private boolean currentRoom;
    public String getNickName() {
        return nickName;
    }
    public boolean getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(boolean currentRoom) {
        this.currentRoom = currentRoom;
    }
    public ChatThread(Socket socket, ChatRoomService chatRoomService, Map<String, PrintWriter> userList)
            throws Exception {
        this.socket = socket;
        this.userList = userList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        boolean nickNameAvailable = false;
        while (!nickNameAvailable) {
            nickName = in.readLine();
            if (!userList.containsKey(nickName)) {
                nickNameAvailable = true;
                out.println("닉네임이 등록되었습니다.");
            } else {
                out.println("이미 사용 중인 닉네임입니다." + '\n' + "다른 닉네임을 입력해주세요.");
            }
        }
        this.chatRoomService = chatRoomService;

        synchronized (userList) {
            userList.put(this.nickName, out);
        }

    }
    public ChatRoom getChatRoom() {
        return chatRoom;
    }
    public void userList() {
        StringBuilder sb = new StringBuilder("접속한 유저 목록:\n");
        for (String username : userList.keySet()) {
            sb.append(username).append("\n");
        }
        out.println(sb.toString());
        out.flush();
    }
    public void roomUserList() {
        if (chatRoom == null) {
            out.println("방에 속해있지 않습니다.");
            out.flush();
            return;
        }
        StringBuilder sb = new StringBuilder("같은 방에 있는 유저 목록:\n");
        for (ChatThread thread : chatRoom.chatThreadList) {
            sb.append(thread.nickName).append("\n");
        }
        out.println(sb.toString());
        out.flush();
    }
    public void sendMessage(String msg) {
        System.out.println(msg);
        out.println(msg);
        out.flush();
    }
    @Override
    public void run() {
        try {
            currentRoom = false;
            if (chatRoomService.chatRoomList().equals("")) {
                out.println("생성된 채팅방이 없습니다");
                out.flush();
                out.println("/create 로 채팅방을 생성 해주세요");
                out.flush();
            } else {
                out.println(chatRoomService.chatRoomList());
                out.flush();
            }
            out.println("현재 접속한 유저수 : " + userList.size() + "명");
            out.flush();
            String line = null;
            while ((line = in.readLine()) != null) {
                if ("/quit".equals(line)) {
                    break;
                } else if (line.indexOf("/create") == 0) {
                    if (currentRoom) {
                        out.println("이미 방에 입장한 상태입니다. 방을 나가주세요.");
                        out.flush();
                    } else {
                        if (line.length() >= 9) {
                            currentRoom = true;
                            String title = line.substring(8);
                            ChatRoom chatRoom = chatRoomService.createChatRoom(title);
                            this.chatRoom = chatRoom;
                            this.chatRoom.addChatThread(this);
                            out.println("방을 생성했습니다.");
                            out.flush();
                        } else {
                            out.println("방 제목을 입력하세요.");
                        }
                    }
                } else if (line.equalsIgnoreCase("/passwordRoom")) {
                    try {
                        currentRoom = true;
                        out.println("비밀방을 생성합니다. 방 제목과 암호를 입력하세요.");
                        out.flush();
                        out.println("방 제목:");
                        out.flush();
                        String title = in.readLine();
                        out.println("암호:");
                        out.flush();
                        String password = in.readLine();
                        ChatRoom createdRoom = chatRoomService.createPasswordChatRoom(title, password);
                        this.chatRoom = createdRoom;
                        this.chatRoom.addChatThread(this);
                        out.println("비밀방이 생성되었습니다. 방 번호: " + createdRoom.getId());
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (line.indexOf("/join") == 0) {
                    try {
                        if (currentRoom) {
                            out.println("이미 방에 입장한 상태입니다. 방을 나가주세요.");
                            out.flush();
                        } else {
                            currentRoom = true;
                            int roomId = Integer.parseInt(line.substring(6));
                            ChatRoom targetRoom = chatRoomService.findChatRoomById(roomId);
                            if (targetRoom == null) {
                                out.println("해당 방이 존재하지 않습니다.");
                                out.flush();
                            } else {
                                if (!targetRoom.isPasswordProtected()) {
                                    chatRoomService.join(roomId, this);
                                    out.println(targetRoom.getTitle() + " 방에 입장했습니다.");
                                    out.flush();
                                    chatRoom.broadcastEnterMessage(nickName);
                                } else {
                                    out.println("비밀번호를 입력하세요:");
                                    out.flush();
                                    String inputPassword = in.readLine();
                                    if (targetRoom.checkPassword(inputPassword)) {
                                        chatRoomService.join(roomId, this);
                                        out.println(targetRoom.getTitle() + " 방에 입장했습니다.");
                                        out.flush();
                                        chatRoom.broadcastEnterMessage(nickName);
                                    } else {
                                        out.println("비밀번호가 일치하지 않습니다.");
                                        out.flush();
                                        currentRoom = false;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        currentRoom = false;
                        out.println("방 번호가 잘못 되었습니다.");
                        out.flush();
                    }
                } else if (line.equalsIgnoreCase("/invite")) {
                    if (currentRoom) {
                        userList();
                        out.println("초대할 유저를 입력해주세요.");
                        String userName = in.readLine();
                        if (userList.containsKey(userName)) {
                            PrintWriter targetOut = userList.get(userName);
                            if (userName.equals(nickName)) {
                                out.println("자기자신에게 초대 할 수 없습니다.");
                                out.flush();
                            } else {
                                ChatThread targetThread = (ChatThread) ChatServer.getUserThreadMap().get(userName);
                                targetThread.setAcceptUserName(userName);
                                targetThread.setInviteRoomId(this.getChatRoom().getId());
                                targetOut.println(nickName + "님의 초대가 왔습니다. 수락하시겠습니까? (/accept 또는 /decline을 입력해주세요.)");
                                targetOut.flush();
                            }
                        } else {
                            out.println("해당 사용자가 존재하지 않습니다.");
                            out.flush();
                        }
                    } else {
                        out.println("방을 먼저 생성해주세요.");
                    }
                } else if (line.equalsIgnoreCase("/exit")) {
                    if (!currentRoom) {
                        out.println("방에 속해있지 않습니다. 프로그램 종료는 /quit 를 입력해주세요");
                        out.flush();
                    } else {
                        this.chatRoom.removeChatThread(this);
                        out.println("방에서 퇴장했습니다.");
                        out.flush();
                        chatRoom.broadcastExitMessage(nickName);

                        currentRoom = false;
                        if (this.chatRoom.chatThreadList.isEmpty()) {
                            chatRoomService.removeChatRoom(chatRoom);
                            System.out.println("방이 삭제됐습니다.");
                        }
                        chatRoom = null;
                    }
                } else if (line.equalsIgnoreCase("/roomUser")) {
                    roomUserList();
                } else if (line.equalsIgnoreCase("/userList")) {
                    userList();
                } else if (line.startsWith("/w")) {
                    StringTokenizer st = new StringTokenizer(line, " ");
                    String head = st.nextToken();
                    String targetUser = st.nextToken();
                    if (!st.hasMoreTokens()) {
                        out.println("메시지를 입력해주세요.");
                    } else {
                        String whisperMsg = st.nextToken();
                        whisper(nickName, targetUser, whisperMsg);
                    }
                } else if (line.equalsIgnoreCase("/help")) {
                    out.println("/create : 방 생성");
                    out.flush();
                    out.println("/join + roomID: roomID 방에 접속");
                    out.flush();
                    out.println("/quit : 클라이언트 종료");
                    out.flush();
                    out.println("/roomUser : 같은 방에 있는 유저 리스트 조회");
                    out.flush();
                    out.println("/userList : 클라이언트에 접속한 유저 리스트 조회");
                    out.flush();
                    out.println("/exit : 방 나가기");
                    out.flush();
                    out.println("/list : 생성된 방의 리스트 조회");
                    out.flush();
                    out.println("/help : 명령어");
                    out.flush();
                    out.println("/w + nickName + message : 귓속말 보내기");
                    out.flush();
                    out.println("passwordRoom : 비밀방 생성");
                    out.flush();
                    out.println("/invite : 유저 초대하기");
                    out.flush();
                    out.println("/accept : 초대 수락");
                    out.flush();
                    out.println("/decline : 초대 거부");
                    out.flush();
                } else if (line.equalsIgnoreCase("/accept")) {
                    if (acceptUserName != null) {
                        ChatThread targetThread = (ChatThread) ChatServer.getUserThreadMap().get(acceptUserName);
                        PrintWriter targetOut = userList.get(acceptUserName);
                        if (targetThread != null) {
                            if (targetThread.getCurrentRoom()) {
                                targetOut.println("이미 다른 방에 속해 있습니다. 초대를 수락하려면 먼저 해당 방에서 나가주세요.");
                                targetOut.flush();
                            } else {
                                targetThread.setCurrentRoom(true);
                                chatRoomService.join(this.getInviteRoomId(), targetThread);
                                targetOut.println("초대를 수락했습니다. 방에 입장합니다.");
                                targetOut.flush();
                                this.chatRoom.broadcastEnterMessage(nickName);
                                setAcceptUserName(null);
                            }
                        } else {
                            out.println("초대를 받지 않았습니다.");
                        }
                    } else {
                        out.println("초대를 받지 않았습니다.");
                    }
                } else if (line.equalsIgnoreCase("/decline")) {
                    if (acceptUserName == null) {
                        out.println("초대를 받지 않았습니다.");
                        continue;
                    }
                    out.println("초대를 거부했습니다.");
                    this.setAcceptUserName(null);
                } else if (line.indexOf("/list") == 0) {
                    if (chatRoomService.chatRoomList().equals("")) {
                        out.println("존재하는 방이 없습니다.");
                    }
                    out.println(chatRoomService.chatRoomList());
                    out.flush();
                } else if (this.chatRoom != null) {
                    System.out.println("속한 방에 브로드캐스트 합니다." + line);
                    chatRoom.broadcast(nickName, line);
                } else {
                    out.println("속한 채팅 방이 없습니다. ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            synchronized (userList) {
                userList.remove(this.nickName);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private BufferedReader getInputStream() {
        return in;
    }
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    public void whisper(String nickName, String targetUser, String message) {
        PrintWriter targetOut = userList.get(targetUser);
        PrintWriter idOut = userList.get(nickName);
        if (targetUser.equals(nickName)) {
            targetOut.println("자기자신에게 귓속말을 할 수 없습니다");
            targetOut.flush();
            return;
        }
        if (targetOut != null) {
            targetOut.println(nickName + "님의 귓속말: " + message);
            targetOut.flush();
            idOut.println(nickName + " >>> " + targetUser + " : " + message);
            idOut.flush();
        } else {
            userList.get(nickName).println("귓속말을 보낼 사용자 '" + targetUser + "'를 찾을 수 없습니다.");
        }
    }
}