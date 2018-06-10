import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
    private DatagramSocket socket = null;
    private InetAddress server_address;
    private int server_port;

    public ChatClient() {
        try {
            socket = new DatagramSocket();
            //choose();
        } catch (SocketException s) {
            s.printStackTrace();
        }
        server_address = null;
        server_port = -1;
    }

    public void choose() {
        System.out.println("Connect to host? (y, n)");
        Scanner in = new Scanner(System.in);
        if(in.nextLine().equals("n")) {
            try {
                new ChatServerThread().start();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else if(in.nextLine().equals("y")) {
            connectToServer();
        } else {
            connectToServer();
        }
    }

    public void connectToServer() {
        try {
            System.out.println("Type server ip then port on next line");
            Scanner in = new Scanner(System.in);
            //setting instance variable
            server_address = InetAddress.getByName(in.nextLine());

            System.out.println("port");
            //setting instance variable
            server_port = Integer.parseInt(in.nextLine());


            InetAddress address = socket.getLocalAddress();
            int port = socket.getLocalPort();
            byte[] addArr = ("ip:" + address.getHostAddress() + ":").getBytes();
            byte[] portArr = Integer.toString(port).getBytes();
            //combines 2 byte arrays together
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( addArr );
            outputStream.write( portArr );
            byte buff[] = outputStream.toByteArray( );
            DatagramPacket packet = new DatagramPacket(buff, buff.length, server_address, server_port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        while(true) {
            try {
                byte[] buff = new byte[256];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage() {
        while(true) {
            try{
                Scanner in = new Scanner(System.in);
                byte[] buff = new byte[256];
                buff = in.nextLine().getBytes();
                DatagramPacket packet = new DatagramPacket(buff, buff.length, server_address, server_port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}