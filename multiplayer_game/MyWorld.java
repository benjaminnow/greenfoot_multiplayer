import greenfoot.*;
  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class MyWorld extends World
{

    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1); 
        Player p = new Player("192.168.56.1", 4444);
        addObject(p, (int)(Math.random()*400), (int)(Math.random()*400));
        //System.out.println("hfgsdfi");
    }
}
