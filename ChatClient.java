import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("127.0.0.1", 1245);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("닉네임을 입력해주세요.");
        String name = keyboard.readLine();
        out.println(name);
        new InputThread(socket, in).start();
        try {
            String line = null;
            while ((line = keyboard.readLine()) != null) {
                out.println(line);
                if ("/quit".equals(line)) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("...");
        }
    }
}

class InputThread extends Thread {
    private Socket socket;
    BufferedReader in;

    public InputThread(Socket socket, BufferedReader in) {
        this.in = in;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            System.out.println("...");
        }
    }
}