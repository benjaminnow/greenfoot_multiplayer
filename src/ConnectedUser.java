import java.net.DatagramPacket;
import java.net.InetAddress;

public class ConnectedUser {
    private InetAddress address;
    private int port;
    private Message msg;
    private boolean ready;

    public ConnectedUser(InetAddress addr, int p) {
        address = addr;
        port = p;
        msg = new Message();
        ready = false;
    }

    public Message getMessage() {
        return(msg);
    }

    public boolean getReady() {
        return(ready);
    }

    public void setReady() {
        ready = true;
    }

    public void setMessage(DatagramPacket packet) {
        msg = new Message(packet);
    }

    public int getPort() {
        return(port);
    }

    public InetAddress getAddress() {
        return(address);
    }

    public boolean equals(ConnectedUser other) {
        return(other.getPort() == port && other.getAddress().equals(address));
    }

}
