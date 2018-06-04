public class Client {
    public static void main(String[] args) {
        ChatClient c = new ChatClient();
        new Thread(c::sendMessage).start();
        new Thread(c::receiveMessage).start();
    }
}
