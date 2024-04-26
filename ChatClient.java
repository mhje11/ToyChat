import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("사용법 : java com.example.chat2.ChatClient 닉네임");
            return;
        }

        String name = args[0];
        Socket socket = new Socket("127.0.0.1", 12345);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        out.println(name);

        new InputThread(socket, in).start();

        try {
            String line = null;
            while ((line = keyboard.readLine()) != null) {
                out.println(line);
                if ("/quit".equals(line)) {
                    out.println("/quit");
                    out.flush();
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