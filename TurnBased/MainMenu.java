import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MainMenu here.  
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainMenu extends World
{
    public MainMenu() {    
        super(1280, 720, 1); 
        
        // 1. Tạo đối tượng ảnh từ file
        GreenfootImage bg = new GreenfootImage("dungeon_bg.jpg");
        // 2. Scale nó cho bằng đúng kích thước World (1280x720)
        bg.scale(1280, 720);
        
        // 3. Set cái nền đã scale
        setBackground(bg);
        addObject(new Title(), 640, 250);
        addObject(new Button("NEW GAME"), 640, 350);
        addObject(new Button("LOAD GAME"), 640, 470);
        addObject(new Button("OFF"), 640, 590);
    }
}
