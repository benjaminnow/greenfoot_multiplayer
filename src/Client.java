public class Client {
    public static void main(String[] args) {
        ChatClient c = new ChatClient();
        c.connectToServer();
        new Thread(c::sendMessage).start();
        new Thread(c::receiveMessage).start();
    }
}
