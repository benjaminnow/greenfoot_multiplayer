import java.io.*;
import java.net.*;
import java.util.*;

public class IRCServerThread extends Thread {

    private DatagramSocket socket = null;

    public IRCServerThread() throws IOException {
        this("IRCServerThread");
    }

    public IRCServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
    }

    public void run() {
        while(true) {
            try {
                Scanner in = new Scanner(System.in);
                byte[] buf = new byte[256];
                buf = in.nextLine().getBytes();

                InetAddress group = InetAddress.getByName("230.0.0.1");
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
                socket.send(packet);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    socket.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

}
