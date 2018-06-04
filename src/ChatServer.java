import java.io.IOException;

public class ChatServer {
    public static void main(String[] args) throws IOException {
        new ChatServerThread().start();
    }
}
