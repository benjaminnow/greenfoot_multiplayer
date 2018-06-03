import java.io.IOException;

public class IRCServer {
    public static void main(String args[]) throws IOException {
        new IRCServerThread().start();
    }
}
