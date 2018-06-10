import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ChatServerThread extends Thread {
    private DatagramSocket socket = null;
    private ArrayList<Integer> ports;
    private ArrayList<String> msgs;
    private ArrayList<InetAddress> ips;

    public ChatServerThread() throws IOException{
        this("ChatServerThread");
    }

    public ChatServerThread(String name) throws IOException {
        super(name);
        // creates a datagram socket bound to computer's address with any available port
        socket = new DatagramSocket(0, InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
        ports = new ArrayList<Integer>();
        msgs = new ArrayList<String>();
        ips = new ArrayList<InetAddress>();
        System.out.println(socket.getLocalAddress() + ": " + socket.getLocalPort());
    }



    public void addToConnected(DatagramPacket p) {
        int port = p.getPort();
        InetAddress addr = p.getAddress();
        boolean in = false;
        for(int i = 0; i < ports.size(); i++) {
            if(ports.get(i) == port) {
                in = true;
            }
        }
        if(!in) {
            ips.add(addr);
            ports.add(port);
        }
        System.out.println("ips: " + ips);
        System.out.println("ports: " + ports);
    }

    public void sendToEveryoneButOne(DatagramPacket p) {
        int packetPort = p.getPort();
        InetAddress packetAddr = p.getAddress();
        for(int i = 0; i < ports.size(); i++) {
            if(ports.get(i) != packetPort) {
                try {
                    byte[] buff = new byte[256];
                    String packetText = new String(p.getData(), 0, p.getLength());
                    buff = packetText.getBytes();
                    DatagramPacket packet = new DatagramPacket(buff, buff.length, ips.get(i), ports.get(i));
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                String received = new String(packet.getData(), 0, packet.getLength());
                msgs.add(received);
                System.out.println("msgs: " + msgs);

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
