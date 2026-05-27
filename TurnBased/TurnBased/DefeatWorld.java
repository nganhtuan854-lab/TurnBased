    import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
    
    /**
     * Write a description of class DefeatWorld here.
     * 
     * @author (your name) 
     * @version (a version number or a date)
     */
    public class DefeatWorld extends World
    {
        public DefeatWorld() {    
            super(1280, 720, 1); 
            GreenfootImage bg = new GreenfootImage(1280, 720);
            bg.setColor(Color.BLACK);
            bg.fill();
            
            bg.setColor(Color.RED);
            bg.setFont(new Font("Arial", true, false, 60));
            bg.drawString("GAME OVER", 460, 320);
            
            bg.setColor(Color.WHITE);
            bg.setFont(new Font("Arial", false, false, 22));
            bg.drawString("Anh hùng đã ngã xuống... Bấm RESET để phục thù!", 400, 420);
            setBackground(bg);
        }
    }
