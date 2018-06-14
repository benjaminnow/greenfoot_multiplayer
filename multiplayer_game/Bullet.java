import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;

public class Bullet extends Actor
{

    private int x_mov;
    private int y_mov;
    private Actor fromwho;
    private static int counter = 0;
    private final int id;
    private int enemyClientNum = -1;
    private int x_coord;
    private int y_coord;

    public Bullet(int x, int y, Actor who) {
        x_mov = x;
        y_mov = y;
        fromwho = who;
        id = counter++;
    }

    public Bullet(int x, int y, int id, int enemy) {
        x_coord = x;
        y_coord = y;
        this.id = id;
        enemyClientNum = enemy;
    }

    public void setXCoord(int x) {
        x_coord = x;
    }

    public void setYCoord(int y) {
        y_coord = y;
    }

    public int getEnemyClientNum() {
        return(enemyClientNum);
    }

    public int getXMovement() {
        return(x_mov);
    }

    public int getYMovement() {
        return(y_mov);
    }

    public int getId() {
        return(id);
    }

    public void removeBulletFromList() {
        ArrayList<Bullet> bList = ((Player)fromwho).getBulletList();
        for(int i = 0; i < bList.size(); i++) {
            if(bList.get(i).getId() == id) {
                bList.remove(i);
                return;
            }
        }
    }


    public void act()
    {
        //Player p = getWorld().getObjects(Player.class);
        //Actor e = getOneIntersectingObject(Enemy.class);

        World w = getWorld();
        /*if(e != null) {
            w.removeObject(e);
            w.removeObject(this);
        } else {
            if(getX() + x_mov >= w.getWidth() || getX() + x_mov <= 0 || getY() + y_mov >= w.getHeight() || getY() + y_mov <= 0) {
                w.removeObject(this);
            } else {
                setLocation(getX() + x_mov, getY() + y_mov);
            }
        }*/
        if(getX() + x_mov >= w.getWidth() || getX() + x_mov <= 0 || getY() + y_mov >= w.getHeight() || getY() + y_mov <= 0) {
            //remove bullet from bullet list in actor here
            removeBulletFromList();
            w.removeObject(this);
        } else {
            if(enemyClientNum == -1) {
                setLocation(getX() + x_mov, getY() + y_mov);
            } else {
                setLocation(x_coord, y_coord);
            }
        }
    }
}
