import java.net.DatagramPacket;
import java.net.InetAddress;

public class Message {
    private final InetAddress address;
    private final int port;
    private final String message;

    public Message() {
        address = null;
        port = -1;
        message = null;
    }

    public Message(DatagramPacket packet) {
        this.address = packet.getAddress();
        this.port = packet.getPort();
        this.message = new String(packet.getData(), 0, packet.getLength());
    }

    public InetAddress getAddress() {
        return(address);
    }

    public int getPort() {
        return(port);
    }

    public String getMessage() {
        return(message);
    }
}
