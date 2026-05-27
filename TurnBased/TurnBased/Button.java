import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Lớp Button đại diện cho các thành phần nút bấm tương tác trên giao diện người dùng (UI).
 * Xử lý sự kiện nhấp chuột (Click Event) và điều hướng luồng logic xử lý dựa trên 
 * phân cảnh hiện tại của trò chơi (Menu chính, Trận đấu, hoặc Màn hình kết thúc).
 */
public class Button extends Actor
{
    private String tenNut; // Thuộc tính lưu trữ chuỗi văn bản định danh định hướng hành động của nút

    /**
     * Hàm khởi tạo (Constructor) của đối tượng Button.
     * Thực hiện thiết lập đồ họa hộp chứa, vẽ khung viền đa lớp và ghi văn bản định danh nút.
     * @param text Chuỗi ký tự hiển thị trên bề mặt nút bấm
     */
    public Button(String text) {
        this.tenNut = text; 
        
        // Cấu hình kích thước và nạp màu nền đen cho hộp chứa nút
        GreenfootImage img = new GreenfootImage(200, 50);
        img.setColor(Color.BLACK);
        img.fillRect(0, 0, 200, 50);
        
        // Vẽ cấu trúc viền trắng đa lớp xếp chồng để tăng độ dày và tính thẩm mỹ đồ họa
        img.setColor(Color.WHITE);
        img.drawRect(0, 0, 199, 49);
        img.drawRect(1, 1, 197, 47);
        img.drawRect(2, 2, 195, 45);
        
        // Loại bỏ khoảng trống thừa ở hai đầu chuỗi để chuẩn hóa điều kiện so sánh dữ liệu
        String textChuan = text.trim();
        img.setFont(new Font("Arial", true, false, 20));
        img.drawString(textChuan, 30, 32);
        
        setImage(img); 
    }

    /**
     * Vòng lặp Act vận hành liên tục để lắng nghe và tiếp nhận tương tác nhấp chuột từ người dùng.
     * Phân tách luồng xử lý hành động dựa trên việc kiểm tra kiểu thực thể lớp World hiện tại (instanceof).
     */
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            
            // =================================================================
            // NHÁNH 1: XỬ LÝ SỰ KIỆN TẠI GIAO DIỆN SẢNH CHỜ CHÍNH (MAIN MENU)
            // =================================================================
            if (getWorld() instanceof MainMenu) {
                String nutChuan = tenNut.trim();
                if (nutChuan.equals("START") || nutChuan.equals("NEW GAME")) {
                    // Chuyển dịch sang thế giới trận đấu mới, giải phóng bộ nhớ cũ
                    Greenfoot.setWorld(new MyWorld()); 
                } 
                else if (nutChuan.equals("LOAD GAME")) {
                    System.out.println("Tính năng Lưu/Tải trận chưa được cấu hình mở rộng.");
                } 
                else if (nutChuan.equals("OFF")) {
                    System.exit(0); // Khấu trừ tiến trình đóng ứng dụng hệ thống
                }
                return; 
            }

            // =================================================================
            // NHÁNH 2: XỬ LÝ SỰ KIỆN TẠI GIAO DIỆN CHIẾN ĐẤU (MY WORLD)
            // =================================================================
            if (getWorld() instanceof MyWorld) {
                MyWorld world = (MyWorld) getWorld();
                Hero hero = world.getHero();
                Monster target = world.getSelectedMonster();

                // Xác thực điều kiện phân định trạng thái lượt đi hiện tại của nhân vật chính
                if (world.isHeroTurn) { 
                    String nutChuan = tenNut.trim(); 
                    
                    if (nutChuan.equals("TẤN CÔNG")) {
                        if (target != null) {
                            world.isHeroTurn = false; // Đóng khóa lượt tương tác tức thời để ngăn chặn spam click bậy bạ
                            hero.attackMonster(target); // Thực thi hoạt ảnh áp sát gây sát thương đơn mục tiêu
                            finishTurn(world); 
                        } else {
                            System.out.println("Hệ thống yêu cầu chỉ định mục tiêu trước khi tấn công.");
                        }
                    } 
                    else if (nutChuan.equals("KỸ NĂNG")) {
                        world.isHeroTurn = false; // Phong tỏa trạng thái tương tác phòng ngừa lỗi spam lệnh
                        hero.useSpecialSkill(); // Kích hoạt kỹ năng quét diện rộng chia dame và tạo ấn phòng thủ
                        finishTurn(world);
                    }
                    else if (nutChuan.equals("BÌNH MÁU")) {
                        // Gọi hàm tiêu hao vật phẩm hồi phục sinh lực và kiểm tra rào bảo hiểm số lượng bình khả dụng
                        if (hero.heal(80)) { 
                            world.isHeroTurn = false; // Chỉ khấu trừ kết thúc lượt khi thao tác sử dụng thành công
                            finishTurn(world); 
                        }
                    }
                    else if (nutChuan.equals("RÚT LUI")) {
                        // Phá hủy trạng thái trận đấu hiện tại và điều hướng quay về giao diện Menu chính
                        Greenfoot.setWorld(new MainMenu());
                    }
                } else {
                    System.out.println("Thao tác bị chặn: Trò chơi đang trong lượt phản công của phe địch.");
                }
            }
            
            // =================================================================
            // NHÁNH 3: XỬ LÝ SỰ KIỆN TẠI GIAO DIỆN CHIẾN THẮNG (VICTORY WORLD)
            // =================================================================
            if (getWorld() instanceof VictoryWorld) {
                if (tenNut.trim().equals("MENU CHÍNH")) {
                    Greenfoot.setWorld(new MainMenu()); // Thu hồi luồng và chuyển dịch trạng thái về sảnh chờ chính
                }
                return;
            }
        }
    }

    /**
     * Hàm phụ trợ dọn dẹp trạng thái và kết thúc chu kỳ lượt đi của Hero.
     * Thực hiện hủy bỏ vùng mục tiêu được chọn cũ và bàn giao quyền xử lý lượt cho hệ thống World.
     */
    private void finishTurn(MyWorld world) {
        world.clearSelection(); // Tái thiết lập đưa con trỏ mục tiêu (Selected Monster) về null
        world.endHeroTurn();    // Gọi luồng vận hành chuyển tiếp đợt tấn công của quái vật hoặc nâng cấp Wave mới
    }
}