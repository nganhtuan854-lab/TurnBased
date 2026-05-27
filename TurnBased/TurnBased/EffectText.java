import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class EffectText here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EffectText extends Actor
{
private int timer = 30; // Hiện trong 30 frame

    public EffectText(String text, Color color) {
        setImage(new GreenfootImage(text, 30, color, new Color(0,0,0,0)));
    }

    public void act() {
        setLocation(getX(), getY() - 1); // Bay lên trên
        timer--;
        if (timer <= 0) getWorld().removeObject(this);
    }
}
