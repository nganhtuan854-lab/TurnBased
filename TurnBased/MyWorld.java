import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Lớp MyWorld quản lý vòng lặp logic cốt lõi của trò chơi (Game Loop).
 * Điều phối trạng thái lượt đấu (Turn State), hệ thống sinh quái vật theo đợt (Wave System),
 * cơ chế ban phước ngẫu nhiên đầu trận và đồng bộ dữ liệu với các thành phần giao diện (UI).
 */
public class MyWorld extends World
{
    // --- Các thuộc tính theo dõi trạng thái tiến trình trận đấu ---
    public int dotQuai = 1;            // Vị trí Wave hiện tại (Mặc định khởi tạo từ Wave 1)
    public boolean isHeroTurn = false; // Khóa tương tác nút bấm của Hero khi đang chuyển cảnh hoặc trong lượt quái

    // --- Các thực thể cốt lõi và thành phần giao diện hệ thống ---
    private Hero myHero;
    private Monster selectedMonster = null;
    public StatusBox heroHPBox;
    private int buffTimer = 120;       // Bộ đếm thời gian duy trì hiển thị thông báo Buff (120 khung hình ~ 2 giây)
    private EffectText thongBaoBuff;

    /**
     * Hàm khởi tạo (Constructor) của thế giới MyWorld.
     * Cấu hình kích thước không gian, nạp hình nền, khởi tạo nhân vật chính,
     * thiết lập giao diện menu hành động và thực hiện cơ chế gacha ban phước ngẫu nhiên.
     */
    public MyWorld()
    {    
        // Khởi tạo màn hình độ phân giải chuẩn kích thước 1280x720 ô lưới pixel
        super(1280, 720, 1); 
        
        // Cài đặt hình ảnh nền đồ họa hệ thống
        GreenfootImage background = new GreenfootImage("bg2.png"); 
        background.scale(1280, 720);
        setBackground(background);
        
        // Khởi tạo thực thể nhân vật chính với các thông số thuộc tính gốc (500 HP, 25 ATK)
        myHero = new Hero();
        myHero.setMaxHp(500);
        myHero.setAtk(25);
        addObject(myHero, 650, 500);
        
        // Khởi tạo menu hành động điều khiển cho người chơi
        prepareUI(); 
        
        // --- Cơ chế lắc xúc xắc ban phước ngẫu nhiên khi bắt đầu trận đấu ---
        int xucXac = Greenfoot.getRandomNumber(2); 
        String tenBuff = "";
        Color mauBuff = Color.WHITE;

        if (xucXac == 0) {
            myHero.setAtk(myHero.getAtk() + 15); // Nhánh Cuồng Chiến: Cộng thẳng sát thương vào chỉ số gốc
            tenBuff = "⚔️ CUỒNG CHIẾN: +15 SÁT THƯƠNG GỐC!";
            mauBuff = Color.RED;
        } 
        else {
            myHero.setMaxHp(myHero.getMaxHp() + 100); // Nhánh Khổng Lồ: Tăng giới hạn sinh lực tối đa của trận
            tenBuff = "💖 KHỔNG LỒ: +100 MÁU TỐI ĐA!";
            mauBuff = Color.GREEN;
        }
        myHero.setHp(myHero.getMaxHp()); // Hồi phục đầy thanh máu cho nhân vật ở Wave 1 theo ngưỡng giới hạn mới

        // Khởi tạo đối tượng hiển thị văn bản thông báo loại Buff nhận được lên trung tâm màn hình
        thongBaoBuff = new EffectText("🌟 WAVE " + dotQuai + " - NHẬN BUFF: " + tenBuff, mauBuff);
        addObject(thongBaoBuff, 640, 360); 

        // Ghi chú: Ép thanh máu hiển thị đúng tỷ lệ phần trăm MaxHP động ngay từ khung hình đầu tiên
        heroHPBox.updateBar(myHero.getHP());
    }

    /**
     * Vòng lặp Act vận hành liên tục theo chu kỳ thời gian thực của Greenfoot Framework.
     * Quản lý bộ đếm ngược hiển thị Buff đầu trận để dọn dẹp bộ nhớ và kích hoạt thả đợt quái đầu tiên.
     */
    public void act() {
        if (buffTimer > 0) {
            buffTimer--; 
            if (buffTimer == 0) {
                removeObject(thongBaoBuff); // Xóa bỏ đối tượng chữ thông báo khỏi bộ nhớ World
                spawnWave();                // Triệu hồi bầy quái vật dựa trên chỉ số cấu hình Wave hiện hành
                revealMonsterIntents();     // Kích hoạt hiển thị biểu tượng gồng chiêu dự định của phe quái
                
                // Ghi chú: Xóa bỏ trạng thái bọc khiên phòng thủ (nếu có) khi bắt đầu lượt đi đầu tiên của Hero
                if (myHero != null) {
                    myHero.setIsDefending(false); 
                }
                
                this.isHeroTurn = true;     // Mở khóa lượt cho phép người chơi bắt đầu thực hiện thao tác bấm nút
            }
            return; 
        }
    }

    /**
     * Hàm điều phối triệu hồi quái vật tự động dựa trên chỉ số tiến trình Wave hiện tại.
     * Đảm bảo cấu trúc phân bổ độ khó tăng tiến tuyến tính qua 3 chu kỳ chiến đấu.
     */
    public void spawnWave() {
        if (dotQuai == 1) {
            // Wave 1: Phân bổ 3 vị trí quái vật cấp độ lính lác (Tier 1)
            spawnRandomMonster(1, 475, 245);
            spawnRandomMonster(1, 645, 245);
            spawnRandomMonster(1, 815, 245);
        }
        else if (dotQuai == 2) {
            // Wave 2: Phân bổ 1 Tinh anh (Tier 2) ở vị trí trung tâm và 2 lính lác hai bên
            spawnRandomMonster(1, 475, 245);
            spawnRandomMonster(2, 645, 245); 
            spawnRandomMonster(1, 815, 245);
        }
        else if (dotQuai == 3) {
            // Wave 3: Triệu hồi duy nhất 1 thực thể Trùm cuối (Tier 3 Boss) tại trung tâm
            spawnRandomMonster(3, 645, 245);
        }
    }

    /**
     * Máy tạo quái vật ngẫu nhiên (Procedural Spawning Generator).
     * Dựa trên tham số phân tầng Level đầu vào để bốc ngẫu nhiên lớp con quái vật tương ứng.
     */
    private void spawnRandomMonster(int level, int x, int y) {
        int r = Greenfoot.getRandomNumber(3); 
        Monster m;
        
        if (level == 1) {
            if (r == 0) m = new GoblinLinhLac();
            else if (r == 1) m = new Slime();
            else m = new HeoRung();
        } 
        else if (level == 2) {
            if (r == 0) m = new GoblinDoiTruong();
            else if (r == 1) m = new Orc();
            else m = new HeoBrute();
        } 
        else { 
            if (r == 0) m = new DaiGoblinBoss();
            else if (r == 1) m = new Orc2Dau();
            else m = new Dragon();
        }
        addObject(m, x, y);
    }

    /**
     * Hàm xử lý logic kếtthuộc lượt đánh của người chơi và bàn giao trạng thái vòng lặp.
     * Tích hợp bộ kiểm tra điều kiện chiến thắng, luồng tăng cấp độ Wave, tự động phản công từ phe địch
     * và dọn dẹp cờ hiệu trạng thái phòng thủ phòng ngừa lỗi lưu cửu chỉ số.
     */
    public void endHeroTurn() {
        isHeroTurn = false;
        
        // Ghi chú: Chặn điều kiện chiến thắng tối thượng khi diệt sạch Boss Wave 3 để ngắt luồng xử lý phía sau ngay lập tức
        List<Monster> checkMonsters = getObjects(Monster.class);
        if (checkMonsters.isEmpty() && dotQuai == 3) {
            Greenfoot.setWorld(new VictoryWorld()); // Chuyển dịch tức thời sang thế giới Chiến Thắng
            return; 
        }

        Greenfoot.delay(20); 
        List<Monster> monsters = getObjects(Monster.class);
        
        // --- NHÁNH 1: PHE ĐỊCH BỊ TIÊU DIỆT SẠCH TRÊN SÂN ĐẤU ---
        if (monsters.isEmpty()) {
            if (dotQuai < 3) {
                dotQuai++; // Tăng chỉ số tiến trình Wave lên nấc tiếp theo
                selectedMonster = null; // Xóa trạng thái mục tiêu được chọn cũ
                
                // Khởi tạo hoạt ảnh văn bản thông báo vượt Wave thành công
                EffectText textClear = new EffectText("⚔️ WAVE CLEAR! PREPARING FOR WAVE " + dotQuai + " ⚔️", Color.ORANGE);
                addObject(textClear, 640, 360);
                Greenfoot.delay(60); 
                removeObject(textClear);
                
                // Ghi chú: Giữ nguyên lượng máu hiện tại khi sang Wave mới theo đúng cơ chế sinh tồn thiết kế gốc, không tự hồi phục sinh lực bậy bạ.
                heroHPBox.updateBar(myHero.getHP());
                
                // Khởi tạo đợt quái vật tiếp theo ra trận
                spawnWave();
                revealMonsterIntents(); 
                
                // Ghi chú: Hủy bỏ khiên giáp bảo vệ (isDefending) khi dịch chuyển giao sang Wave đấu mới
                if (myHero != null) {
                    myHero.setIsDefending(false); 
                }
                
                isHeroTurn = true; // Trả quyền tương tác nút bấm cho người chơi ở đầu Wave mới
            } 
        } 
        // --- NHÁNH 2: PHE ĐỊCH CÒN SỐNG -> VẬN HÀNH LƯỢT TẤN CÔNG TỰ ĐỘNG CỦA QUÁI ---
        else {
            // Duyệt danh sách tuần tự để bắt từng thực thể quái vật thực hiện chiêu thức gây sát thương
            for (Monster m : monsters) {
                if (m.getWorld() != null) {
                    m.attackAction(); // Thực thi hàm hành động tấn công của quái vật
                    heroHPBox.updateBar(myHero.getHP()); // Cập nhật thanh sinh lực Hero theo thời gian thực (Realtime)
                    Greenfoot.delay(15);
                }
            }
            
            // Tái thiết lập hiển thị hành động dự định tiếp theo (Intent) cho chu kỳ mới của bầy quái
            revealMonsterIntents(); 
            
            // Ghi chú: Giải phóng trạng thái phòng thủ phòng bọc giáp (isDefending) khi kết thúc lượt phản công của quái
            if (myHero != null) {
                myHero.setIsDefending(false); 
            }
            
            isHeroTurn = true; // Trả quyền tương tác điều khiển lại cho người chơi
        }
    }

    /**
     * Hàm cập nhật trạng thái lật bài ngửa dự định hành động (Intent Icons) của phe địch.
     * Duyệt qua toàn bộ danh sách lớp Monster hiện diện để bắt ép tính toán lại chiêu thức.
     */
    public void revealMonsterIntents() {
        List<Monster> monsters = getObjects(Monster.class);
        for (Monster m : monsters) {
            m.rollIntent(); 
        }
    }

    /**
     * Hàm thiết lập các thành phần giao diện đồ họa cố định khi bắt đầu màn trận.
     * Tạo dựng khung hộp thông số hiển thị sinh lực và nạp các thực thể nút bấm tương tác vật lý.
     */
    public void prepareUI() {
        heroHPBox = new StatusBox("HIỆP SĨ KNIGHT", 500, true);
        addObject(heroHPBox, 200, 600);
        ActionMenu menu = new ActionMenu();
        addObject(menu, 1100, 580);
        addObject(new Button("TẤN CÔNG"), 1100, 500);
        addObject(new Button("KỸ NĂNG"), 1100, 560);
        addObject(new Button("BÌNH MÁU  "), 1100, 620);
        addObject(new Button("RÚT LUI"), 1100, 680);
    }

    // --- Các hàm Getter/Setter bổ trợ truy vấn dữ liệu nhanh giữa các thực thể hệ thống ---
    public Monster getSelectedMonster() { return this.selectedMonster; }
    public void setSelectedMonster(Monster m) { this.selectedMonster = m; }
    public void clearSelection() { this.selectedMonster = null; }
    public Hero getHero() { return myHero; }   
}   