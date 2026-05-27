import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Lớp Hero đại diện cho thực thể nhân vật chính trong trò chơi.
 * Quản lý vòng đời, chỉ số thuộc tính (HP, ATK), cơ chế tính toán sát thương,
 * hệ thống phản đòn đỡ giá (Parry) và các kỹ năng chiến đấu chủ động/bị động.
 */
public class Hero extends Actor
{
    // --- Các thuộc tính chỉ số nền tảng của nhân vật ---
    private int hp = 400;           // Sinh lực hiện tại
    private int maxHp = 400;        // Ngưỡng sinh lực tối đa động (phục vụ tính tỷ lệ hiển thị UI)
    private int atk = 25;           // Chỉ số sát thương vật lý cơ bản
    private int parryRate = 20;     // Tỷ lệ phần trăm kích hoạt phản đòn khi bị tấn công (20%)
    private int binhMau = 2;        // Số lượng vật phẩm tiêu hao khả dụng trong một màn chơi

    // --- Cờ hiệu trạng thái bổ trợ chiến thuật ---
    private boolean isDefending = false; // Đánh dấu trạng thái thủ thế (giảm 50% dame nhận vào)

    // --- Các hàm Getter và Setter phục vụ bao đóng và đồng bộ dữ liệu với hệ thống World ---
    public int getAtk() { return this.atk; }
    public void setAtk(int atk) { this.atk = atk; }
    public int getHp() { return this.hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return this.maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getHP() { return this.hp; } 
    public boolean getIsDefending() { return this.isDefending; }
    public void setIsDefending(boolean state) { this.isDefending = state; }

    /**
     * Hàm khởi tạo (Constructor) của đối tượng Hero.
     * Thực hiện nạp tài nguyên hình ảnh Knight Pixel Art từ thư mục images
     * và cấu hình tỷ lệ hiển thị tương thích với lưới đồ họa của World.
     */
    public Hero() {
        // Nạp file ảnh asset đã được cấu hình trong thư mục dự án
        GreenfootImage img = new GreenfootImage("hero.png");
        img.scale(img.getWidth() * 6/25, img.getHeight() * 6/25);
        setImage(img);
    }

    /**
     * Hàm xử lý tiếp nhận và khấu trừ sinh lực khi chịu sát thương từ bên ngoài.
     * Tích hợp bộ lọc giảm trừ sát thương từ trạng thái phòng thủ chủ động (isDefending)
     * và cơ chế kiểm tra xác suất nổ Parry để phản công đơn mục tiêu.
     */
    public void receiveDamage(int damage, Monster attacker) {
        int finalDamage = damage;

        // Bộ lọc 1: Kiểm tra hiệu ứng giảm sát thương từ kỹ năng đặc biệt
        if (this.isDefending) {
            finalDamage = damage / 2; // Giảm trừ trực tiếp 50% sát thương gốc
            getWorld().addObject(new EffectText("🛡️ GIẢM LỰC: -50% DAME!", Color.YELLOW), getX(), getY() - 90);
        }

        // Bộ lọc 2: Kiểm tra xác suất kích hoạt cơ chế Parry (Phản đòn)
        if (Greenfoot.getRandomNumber(100) < this.parryRate) {
            int reducedDamage = finalDamage / 2; // Giảm tiếp 50% lượng sát thương sau khi đã qua bộ lọc 1
            this.hp -= reducedDamage;
            
            // Khởi tạo văn bản thông báo trạng thái Parry thành công
            getWorld().addObject(new EffectText("PARRY & COUNTER!", Color.CYAN), getX(), getY() - 60);
            
            // Kích hoạt chuỗi hoạt ảnh và dồn sát thương phản hồi vào thực thể tấn công
            counterAttack(attacker);
        } else {
            // Trường hợp không kích hoạt được Parry, thực hiện khấu trừ máu theo sát thương sau cùng
            this.hp -= finalDamage;
            getWorld().addObject(new EffectText("-" + finalDamage, Color.RED), getX(), getY() - 60);
        }
        
        // Kiểm tra điều kiện cạn kiệt sinh lực để kích hoạt các luồng kết thúc game ngầm
        if (this.hp <= 0) {
            System.out.println("Anh Hùng đã hy sinh!");
        }
    }

    /**
     * Hàm xử lý logic và hoạt ảnh phản đòn chớp nhoáng (Counter Attack).
     * Tạo hiệu ứng tịnh tiến vật lý lao lên tiếp cận mục tiêu, áp sát thương lực tay
     * và thu hồi vị trí gốc trong cùng một chu kỳ tính toán khung hình.
     */
    private void counterAttack(Monster target) {
        int startX = getX();
        int startY = getY();

        // Hoạt ảnh tịnh tiến tiến về phía mục tiêu dựa trên hướng tọa độ X
        for (int i = 0; i < 5; i++) {
            setLocation(getX() + (target.getX() > getX() ? 30 : -30), getY() - 10);
            Greenfoot.delay(1);
        }
        
        // Áp sát thương phản hồi bằng 100% lực tay cơ bản vào quái vật
        target.takeDamage(this.atk);
        Greenfoot.delay(5);
        
        // Thu hồi thực thể Hero về tọa độ xuất phát trên sân đấu
        setLocation(startX, startY);
    }

    /**
     * Kỹ năng tấn công cơ bản đơn mục tiêu (Single Target Attack).
     * Đồng bộ hóa hoạt ảnh di chuyển tiếp cận một mục tiêu quái vật cụ thể được chỉ định,
     * thực hiện hàm trừ máu quái và lùi về vị trí ban đầu.
     */
    public void attackMonster(Monster target) {
        if (target == null) return;
        int startX = getX();
        int startY = getY();

        // Chu kỳ vòng lặp tạo hoạt ảnh di chuyển mượt mà áp sát tọa độ của quái vật mục tiêu
        for (int i = 0; i < 10; i++) {
            setLocation(getX() + (target.getX() > getX() ? 20 : -20), getY() + (target.getY() > getY() ? 20 : -20));
            Greenfoot.delay(1);
        }

        // Gọi hàm tiếp nhận sát thương của lớp Monster để trừ máu quái vật
        target.takeDamage(this.atk);
        
        // Tạo độ trễ hiển thị và đưa nhân vật lùi về vị trí phòng thủ ban đầu
        Greenfoot.delay(5);
        setLocation(startX, startY);
    }

    /**
     * Kỹ năng đặc biệt diện rộng: Chém Ngang Thủ Thế (AoE & Defend Skill).
     * Quét sát thương tương đương 80% ATK gốc chia đều dựa trên tổng số lượng mục tiêu thực tế.
     * Đồng thời bật cờ hiệu phòng thủ chủ động để giảm 50% gánh nặng sát thương ở lượt quái kế tiếp.
     */
    public void useSpecialSkill() {
        if (getWorld() == null) return;
        
        // Lấy danh sách toàn bộ các thực thể quái vật đang hiện diện trên sân đấu
        List<Monster> danhSachQuai = getWorld().getObjects(Monster.class);
        int soLuongQuai = danhSachQuai.size();
        
        // Chặn xử lý nếu danh sách trống để tránh các lỗi tính toán logic
        if (soLuongQuai == 0) {
            System.out.println("Không có mục tiêu để ra chiêu!");
            return;
        }
        
        // 1. Cân bằng game: Tính toán tổng lượng sát thương đầu ra đạt mức 80% chỉ số ATK hiện tại
        int tongSatThuong = (int)(this.atk * 0.8);
        
        // 2. Thuật toán chia sát thương diện rộng: Chia đều tổng sát thương cho số lượng mục tiêu hiện hành
        int satThuongMoiCon = tongSatThuong / soLuongQuai;
        if (satThuongMoiCon < 1) satThuongMoiCon = 1; // Rào bảo hiểm tối thiểu 1 dame tránh lỗi chia cho số 0 ngầm
        
        // 3. Kích hoạt trạng thái bảo vệ bọc giáp phòng thủ chủ động
        this.isDefending = true;
        
        // Tạo văn bản thông báo kích hoạt kỹ năng đặc biệt hiển thị trên màn hình
        getWorld().addObject(new EffectText("⚔️ CHÉM LAN THỦ THẾ: GIẢM 50% SÁT THƯƠNG PHÒNG THỦ! 🛡️", Color.YELLOW), getX(), getY() - 80);
        
        // Vòng lặp duyệt danh sách để áp lượng sát thương đã tính toán lên từng mục thể quái vật
        for (Monster quai : danhSachQuai) {
            if (quai.getWorld() != null) {
                quai.takeDamage(satThuongMoiCon);
            }
        }
        
        // Giữ độ trễ ngắn để tối ưu hóa hiệu ứng thị giác cho người chơi quan sát
        Greenfoot.delay(15);
    }

    /**
     * Cơ chế tiêu thụ vật phẩm phục hồi sinh lực (Hồi máu).
     * Kiểm tra số lượng vật phẩm khả dụng, thực hiện cộng máu hiện tại
     * và áp dụng hàm khống chế ngưỡng tối đa dựa trên MaxHP thực tế của trận đấu.
     * @param amount Lượng sinh lực cần hồi phục
     * @return true nếu hồi phục thành công, false nếu số lượng vật phẩm đã cạn kiệt
     */
    public boolean heal(int amount) {
        // Kiểm tra điều kiện số lượng bình máu còn lại trước khi thực hiện logic hồi phục
        if (this.binhMau <= 0) {
            getWorld().addObject(new EffectText("HẾT BÌNH MÁU RỒI FEN!", Color.RED), getX(), getY() - 60);
            return false; 
        }

        // Thực hiện cộng dồn lượng sinh lực hồi phục vào thuộc tính hp hiện tại
        this.hp += amount;
        
        // Hàm khống chế: Đảm bảo lượng máu sau hồi phục không vượt quá giới hạn maxHp thực tế của Wave đấu
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp; 
        }
        
        // Khấu trừ một đơn vị vật phẩm và xuất hiệu ứng thông báo sinh lực phục hồi lên giao diện
        this.binhMau--; 
        getWorld().addObject(new EffectText("+" + amount + " HP (Còn " + this.binhMau + " bình)", Color.GREEN), getX(), getY() - 60);
        
        return true; 
    }

    /**
     * Hàm thiết lập lại trạng thái vật phẩm tiêu hao (Reset Bình Máu).
     * Phục vụ việc dọn dẹp bộ nhớ và thiết lập lại từ đầu khi người chơi bắt đầu game mới.
     */
    public void resetBinhMau() {
        this.binhMau = 2;
    }
}
