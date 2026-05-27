import greenfoot.*; 

public class Title extends Actor {
    public Title() {
        // 1. Dùng greenfoot.Font, không dùng java.awt.Font
        // Cú pháp: new Font(tênFont, kiểu, cỡ)
        Font myFont = new Font("SansSerif", false, true, 150); 
        
        // 2. Tạo ảnh và set font
        GreenfootImage img = new GreenfootImage("Turnbased Dungeon", 90, Color.WHITE, null);
        img.setFont(myFont);
        
        setImage(img);                                                                       
    }
}