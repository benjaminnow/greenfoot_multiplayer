import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Player extends Actor
{

    private DatagramSocket socket = null;
    private InetAddress server_address;
    private int server_port;
    private int ammo;
    private boolean shot;
    private boolean ready; //ready from the server
    private ArrayList<EnemyClient> enemies;
    private MessageDecoder decoder;
    private Thread datastream = new Thread(new PlayerDataStream(this));
    boolean do_once = false;
    
    public Player(String ip, int port) {
        ammo = 10;
        shot = false;
        enemies = new ArrayList<EnemyClient>();
        decoder = new MessageDecoder(this);
        try {
            socket = new DatagramSocket();
        } catch(SocketException s) {
            s.printStackTrace();
        }
        try {
            server_address = InetAddress.getByName(ip);
            server_port = port;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveReady() {
        try {
            byte[] buff = new byte[256];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            if(received.equals("R")) {
                ready = true;
                send("RB");
                String start = "SL:" + getX() + ":" + getY();
                send(start); // sends starting location to server which is then sent to all other clients so they can add them to world
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void move() {
        if(Greenfoot.isKeyDown("w")) {
            setLocation(getX(), getY()-3);
        }
        if(Greenfoot.isKeyDown("a")) {
            setLocation(getX()-3, getY());
        }
        if(Greenfoot.isKeyDown("s")) {
            setLocation(getX(), getY()+3);
        }
        if(Greenfoot.isKeyDown("d")) {
            setLocation(getX()+3, getY());
        }
    }
    
    public void aim() {
        MouseInfo info = Greenfoot.getMouseInfo();
        if(info != null) {
            double angle = Math.atan2((getY ()-(double) info.getY()), (getX()-(double) info.getX()));
            setRotation((int) Math.toDegrees(angle+Math.PI));
        }
    }
    
    public void shoot() {
        World w = getWorld();
        MouseInfo info = Greenfoot.getMouseInfo();
        if(info != null && info.getButton() == 1 && !shot) {
            System.out.println("shoot");
            //shot = !shot;
        }
        if(info != null && info.getButton() == 1 && !shot) {
            shot = !shot;
            System.out.println("up");
        }
        shot = !shot;
    }

    public String getLocation() {
        String loc = "POS:" + getX() + ":" + getY();
        //System.out.println("sent: " + loc);
        return(loc);
    }

    public String getAngle() {
        String angle = "ANGL:" + getRotation();
        //System.out.println("sent: " + angle);
        return(angle);
    }
    
    public void send(String msg) {
        try {
            byte[] buff = new byte[256];
            buff = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buff, buff.length, server_address, server_port);
            socket.send(packet);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void getTick() {
        try {
            byte[] buff = new byte[256];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        try {
            byte[] buff = new byte[256];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            socket.receive(packet);
            decoder.runCommand(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEnemies(EnemyClient e) {
        enemies.add(e);
    }

    
    public void act() 
    {
        while(!ready) {
            send("connecting"); //random message to get server aware of client
            System.out.println("sent");
            receiveReady();
        }
        receiveMessage();
        move();
        aim();
        shoot();
        if(!do_once) {
            datastream.start();
            do_once = true;
        }
        //send(getLocation()); //todo: stream this so that socket on server won't get stuck
        
        //send(getAngle());

    }    
}
