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
        
        // Nạp ảnh nền hầm ngục Dungeon Kingdom fen gửi vào
        setBackground("dungeon_bg.jpg"); 
        
        // Thả 3 cái nút đa năng vào đúng vị trí giữa màn hình
        addObject(new Button("NEW GAME"), 640, 350);
        addObject(new Button("LOAD GAME"), 640, 470);
        addObject(new Button("OFF"), 640, 590);
    }
}
