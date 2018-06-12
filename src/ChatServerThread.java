import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ChatServerThread extends Thread {
    private DatagramSocket socket = null;
    private ArrayList<ConnectedUser> users;
    private int tick;

    public ChatServerThread() throws IOException{
        this("ChatServerThread");
    }

    public ChatServerThread(String name) throws IOException {
        super(name);
        // creates a datagram socket bound to computer's address with any available port
        socket = new DatagramSocket(0, InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
        users = new ArrayList<ConnectedUser>();
        tick = 0; //todo: increment tick feature
        System.out.println(socket.getLocalAddress() + ": " + socket.getLocalPort());
    }



    public void addToConnected(DatagramPacket p) {
        int port = p.getPort();
        InetAddress addr = p.getAddress();
        boolean in = false;
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getPort() == port) {
                in = true;
            }
        }
        if(!in) {
            users.add(new ConnectedUser(addr, port));
        }
    }

    public void sendToEveryoneButOne(DatagramPacket p) {
        int packetPort = p.getPort();
        InetAddress packetAddr = p.getAddress();
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getPort() != packetPort) {
                try {
                    byte[] buff = new byte[256];
                    String packetText = new String(p.getData(), 0, p.getLength());
                    buff = packetText.getBytes();
                    DatagramPacket packet = new DatagramPacket(buff, buff.length, users.get(i).getAddress(), users.get(i).getPort());
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void packetToSender(DatagramPacket packet) {
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getAddress().equals(packet.getAddress()) && users.get(i).getPort() == packet.getPort()) {
                users.get(i).setMessage(packet);
                break;
            }
        }
    }

    public void run() {
        while(true) {
            try {
                byte[] buff = new byte[256];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                addToConnected(packet);
                sendToEveryoneButOne(packet);
                packetToSender(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
