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
    private boolean ready; //ready from the server
    private ArrayList<EnemyClient> enemies;
    private MessageDecoder decoder;
    private Thread datastream = new Thread(new PlayerDataStream(this));
    private boolean do_once = false;
    private ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
    private ArrayList<Bullet> enemyBulletList = new ArrayList<Bullet>();


    public Player(String ip, int port) {
        ammo = 10;
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

    public ArrayList<Bullet> getBulletList() {
        return(bulletList);
    }

    public ArrayList<Bullet> getEnemyBulletList() {
        return(enemyBulletList);
    }

    public void addEnemyBullet(Bullet b) {
        enemyBulletList.add(b);
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
            double angle = Math.atan2((getY()-(double) info.getY()), (getX()-(double) info.getX()));
            setRotation((int) Math.toDegrees(angle+Math.PI));
        }
    }

    public void shoot() {
        World w = getWorld();
        if(Greenfoot.mouseClicked(null)) {
            int[] vals = getBulletMovement();
            Bullet b = new Bullet(vals[0], vals[1], this);
            w.addObject(b, vals[2], vals[3]);
            bulletList.add(b);
        }
    }

    public int[] getBulletMovement() {
        int[] vals = new int[4];
        int speed = 10;
        double angleInRadians = Math.toRadians(getRotation());
        int y_mov = (int)(speed * (Math.sin(angleInRadians)));
        int x_mov = (int)(speed * (Math.cos(angleInRadians)));
        // x and y bullet movement
        vals[0] = x_mov;
        vals[1] = y_mov;
        // gets starting x and y so looks like it shoots from gun
        vals[2] = getX() + (int)(Math.cos(angleInRadians) * 35);
        vals[3] = getY() + (int)(Math.sin(angleInRadians) * 25);
        return(vals);
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

    public String getBullets() {
        String bullets = "";
        for(int i = 0; i < bulletList.size(); i++) {
            bullets += "B:" + bulletList.get(i).getId() + ":" + bulletList.get(i).getX() + ":" + bulletList.get(i).getY() + ",";
        }
        return(bullets);
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

    public String allInfo() {
        String toSend = getLocation() + "," + getAngle() + "," + getBullets();
        return(toSend);
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
