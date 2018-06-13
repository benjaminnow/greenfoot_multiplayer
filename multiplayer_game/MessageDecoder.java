import java.net.DatagramPacket;
import java.util.*;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class MessageDecoder extends Actor
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
        System.out.println(packetText);
        String[] splittedByComma = packetText.split(",");
        ArrayList<String[]> commands = new ArrayList<String[]>();
        for(int i = 0; i < splittedByComma.length; i++) {
            commands.add(splittedByComma[i].split(":"));
        }
        

        for(int c = 0; c < commands.size(); c++) {
            try {
                switch(commands.get(c)[1]) {
                    case "SLO": 
                        renderOtherEnemies(commands.get(c));
                    case "POS":
                        //setLocationEnemy(commands.get(c));
                    default:
                        return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("not valid command");
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

    public void act() 
    {

    }    
}
