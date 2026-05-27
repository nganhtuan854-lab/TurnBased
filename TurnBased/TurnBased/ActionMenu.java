import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ActionMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ActionMenu extends Actor
{
public ActionMenu() {
        GreenfootImage img = new GreenfootImage(250, 250); // Hình vuông
        img.setColor(new Color(0, 0, 0, 150)); // Đen mờ
        img.fillRect(0, 0, 250, 250);
       img.setColor(Color.WHITE);
        img.drawRect(0, 0, 249, 249);
        setImage(img);
    }
}
