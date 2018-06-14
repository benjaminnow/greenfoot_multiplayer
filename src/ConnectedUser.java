import java.net.DatagramPacket;
import java.net.InetAddress;

public class ConnectedUser {
    private InetAddress address;
    private int port;
    private Message msg;
    private boolean ready;
    private int[] startingLocation;
    private static int counter = 0; //used to get unique ids
    private final int id;
    private String command;

    public ConnectedUser(InetAddress addr, int p) {
        address = addr;
        port = p;
        msg = new Message();
        ready = false;
        startingLocation = new int[2];
        id = counter++;
        command = "";
    }

    public void setCommand(DatagramPacket packet) {
        if(packet == null) {
            command = "";
        } else {
            String formed = "";
            String[] commands =  new String(packet.getData(), 0, packet.getLength()).split(",");
            for(int i = 0; i < commands.length; i++) {
                commands[i] = id + ":" + commands[i] + ",";
            }
            for(int j = 0; j < commands.length; j++) {
                formed += commands[j];
            }
            command = formed;
        }
    }

    public String getCommand() {
        return(command);
    }

    public Message getMessage() {
        return(msg);
    }

    public int getId() {
        return(id);
    }

    public void setStartingLocation(int x, int y) {
        startingLocation[0] = x;
        startingLocation[1] = y;
    }

    public int[] getStartingLocation() {
        return(startingLocation);
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
