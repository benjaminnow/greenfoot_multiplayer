import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class GameServerThread extends Thread {
    private DatagramSocket socket = null;
    private ArrayList<ConnectedUser> users;
    private int tick;
    private boolean lobbyReady;

    public GameServerThread() throws IOException {
        this("ChatServerThread");
    }

    public GameServerThread(String name) throws IOException {
        super(name);
        // creates a datagram socket bound to computer's address with any available port
        socket = new DatagramSocket(4444, InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())); //change port 4444 to random, 4444 for testing only
        users = new ArrayList<ConnectedUser>();
        lobbyReady = false;
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
            System.out.println(addr + ":" + port + " just connected");
        }
    }

    public boolean lobbyReadyCheck() {
        System.out.println("Ready(R, NR)?");
        Scanner in = new Scanner(System.in);
        if(in.nextLine().equals("R") && checkAllReady()) {
            return (true);
        } else if(in.nextLine().equals("NR")){
            return(false);
        } else {
            return(false);
        }
    }

    public void sendReady() {
        byte[] buff = new byte[256];
        buff = "R".getBytes();
        DatagramPacket packet;
        for(int i  = 0; i < users.size(); i++) {
            try {
                packet = new DatagramPacket(buff, buff.length, users.get(i).getAddress(), users.get(i).getPort());
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveReady(DatagramPacket packet) {
        String received = new String(packet.getData(), 0, packet.getLength());
        if(received.equals("RB")) { //if client sends a ready back message set them to ready
            InetAddress packetAddress = packet.getAddress();
            int packet_port = packet.getPort();
            for(int i = 0; i < users.size(); i++) {
                if(packet_port == users.get(i).getPort() && packetAddress.equals(users.get(i).getAddress())) {
                    users.get(i).setReady();
                    System.out.println("ready to go");
                }
            }
        }
    }

    public boolean checkAllReady() {
        for(int i = 0; i < users.size(); i++) {
            if(!users.get(i).getReady()) {
                return(false);
            }
        }
        return(true);
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
        while(!lobbyReady) { //loops while server still wants players to connect and not all ready packets received
            try{
                byte[] buff = new byte[256];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                addToConnected(packet);
                sendReady(); //sends ready packet to client for them to respond
                receiveReady(packet); //gets ready packet from client and sets them to ready
                if(lobbyReadyCheck()) {
                    lobbyReady = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("all clients ready");
        while(true) {
            try {
                byte[] buff = new byte[256];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                //addToConnected(packet);
                sendToEveryoneButOne(packet);
                packetToSender(packet); //sets current msg/command to command received(could be null)

                //String received = new String(packet.getData(), 0, packet.getLength());
                //System.out.println(received);

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

