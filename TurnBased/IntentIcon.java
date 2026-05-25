import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class IntentIcon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class IntentIcon extends Actor
{
    public IntentIcon(String text, Color color) {
        // Tạo một cái ảnh nền trong suốt để ghi chữ lên
        GreenfootImage img = new GreenfootImage(120, 30);
        img.setColor(color);
        img.setFont(new Font("Arial", true, false, 16));
        img.drawString(text, 10, 20);
        setImage(img);
    }
}
