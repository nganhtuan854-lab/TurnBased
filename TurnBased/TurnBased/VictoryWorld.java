import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class VictoryWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VictoryWorld extends World
{
    public VictoryWorld() 
    {    
        // Tạo màn hình kích thước chuẩn 1280x720
        super(1280, 720, 1); 
        
        // Vẽ một cái nền màu xanh lá cây đậm (đặc trưng của chiến thắng)
        GreenfootImage bg = new GreenfootImage(1280, 720);
        bg.setColor(new Color(20, 60, 20)); 
        bg.fill();
        setBackground(bg);
        
        // Vẽ chữ CHIẾN THẮNG màu vàng chói lọi giữa màn hình
        bg.setColor(Color.YELLOW);
        bg.setFont(new Font("Arial", true, false, 100));
        bg.drawString("VICTORY", 420, 320);
        
        // Thả cái nút "MENU CHÍNH" vào để người chơi bấm quay về sảnh
        addObject(new Button("MENU CHÍNH"), 640, 480);
    }
}
