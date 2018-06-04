import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
    private DatagramSocket socket = null;

    public ChatClient() {
        try {
            socket = new DatagramSocket();
            connectToServer();
        } catch (SocketException s) {
            s.printStackTrace();
        }

    }

    public void connectToServer() {
        try {
            InetAddress serverAddress = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
            int serverPort = 4444;
            InetAddress address = socket.getLocalAddress();
            int port = socket.getLocalPort();
            byte[] addArr = ("ip:" + address.getHostAddress() + ":").getBytes();
            byte[] portArr = Integer.toString(port).getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( addArr );
            outputStream.write( portArr );
            byte buff[] = outputStream.toByteArray( );
            //buff = addArr;
            DatagramPacket packet = new DatagramPacket(buff, buff.length, serverAddress, serverPort);
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
                InetAddress address = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
                DatagramPacket packet = new DatagramPacket(buff, buff.length, address, 4444);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}