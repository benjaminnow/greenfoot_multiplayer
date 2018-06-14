import java.net.DatagramPacket;
import java.util.*;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class MessageDecoder
{
    
    private Player p;

    public MessageDecoder(Player p) {
        this.p = p;
    }


    public void runCommand(DatagramPacket p) {
        String packetText = new String(p.getData(), 0, p.getLength());
        if(packetText.length() == 0) {
            return;
        }
        String[] splittedByComma = packetText.split(",");
        ArrayList<String[]> commands = new ArrayList<String[]>();
        for(int i = 0; i < splittedByComma.length; i++) {
            commands.add(splittedByComma[i].split(":"));
        }
        

        for(int c = 0; c < commands.size(); c++) {
            if(commands.get(c).length != 1) {
                try {
                    switch(commands.get(c)[1]) {
                        case "SLO":
                            renderOtherEnemies(commands.get(c));
                            break;
                        case "POS":
                            setLocationEnemy(commands.get(c));
                            break;
                        case "ANGL":
                            setAngleEnemy(commands.get(c));
                            break;
                        case "B":
                            renderBullet(commands.get(c));
                            break;
                        default:
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("not valid command");
                    break;
                }
            }
        }
    }
    
    public void renderOtherEnemies(String[] command) {
        World w = p.getWorld();
        int x = Integer.parseInt(command[2]);
        int y = Integer.parseInt(command[3]);
        int idnum = Integer.parseInt(command[0]);
        EnemyClient e = new EnemyClient(idnum);
        w.addObject(e, x, y);
        p.addEnemies(e);
    }

    public void setLocationEnemy(String[] command) {
        World w = p.getWorld();
        List<EnemyClient> enemies = w.getObjects(EnemyClient.class);
        for(int i = 0; i < enemies.size(); i++) {
            if(enemies.get(i).getId() == Integer.parseInt(command[0])) {
                enemies.get(i).setLocation(Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                return;
            }
        }
    }

    public void setAngleEnemy(String[] command) {
        World w = p.getWorld();
        List<EnemyClient> enemies = w.getObjects(EnemyClient.class);
        for(int i = 0; i < enemies.size(); i++) {
            if(enemies.get(i).getId() == Integer.parseInt(command[0])) {
                enemies.get(i).setRotation(Integer.parseInt(command[2]));
                return;
            }
        }
    }

    public void renderBullet(String[] command) {
        World w = p.getWorld();
        int loc = -1;
        ArrayList<Bullet> bullets = p.getEnemyBulletList();
        for(int i = 0; i < bullets.size(); i++) {
            if(bullets.get(i).getEnemyClientNum() == Integer.parseInt(command[0]) && bullets.get(i).getId() == Integer.parseInt(command[2])) {
                loc = i;
                break;
            }
        }
        if(loc == -1) {
            Bullet b = new Bullet(Integer.parseInt(command[3]), Integer.parseInt(command[4]), Integer.parseInt(command[2]), Integer.parseInt(command[0]));
            w.addObject(b, Integer.parseInt(command[3]), Integer.parseInt(command[4]));
            p.addEnemyBullet(b);
        } else {
            bullets.get(loc).setXCoord(Integer.parseInt(command[3]));
            bullets.get(loc).setYCoord(Integer.parseInt(command[4]));
        }
    }
}
