import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Lớp StatusBox đại diện cho thành phần hiển thị thanh trạng thái (Thanh máu/HP Bar).
 * Chịu trách nhiệm đồng bộ dữ liệu chỉ số sinh lực thực tế của nhân vật và kẻ địch,
 * thực hiện tính toán tỷ lệ hình học phần trăm và vẽ đồ họa động tương ứng (Dynamic Rendering).
 */
public class StatusBox extends Actor
{
    // --- Các thuộc tính quản lý chỉ số trạng thái và hiển thị ---
    private String name;      // Văn bản hiển thị tên của thực thể nhân vật
    private int maxHp;        // Giá trị ngưỡng sinh lực tối đa phục vụ tính tỷ lệ phần trăm
    private boolean isHero;   // Cờ hiệu phân định thực thể (true: Nhân vật chính, false: Kẻ địch)

    /**
     * Hàm khởi tạo (Constructor) của lớp StatusBox.
     * @param name Tên định danh hiển thị trên thanh trạng thái
     * @param maxHp Ngưỡng sinh lực tối đa khởi tạo ban đầu
     * @param isHero Xác định loại đối tượng để cấu hình màu sắc giao diện tương thích
     */
    public StatusBox(String name, int maxHp, boolean isHero) {
        this.name = name;
        this.maxHp = maxHp;
        this.isHero = isHero;
        
        // Khởi tạo trạng thái lấp đầy thanh máu ban đầu tại thời điểm sinh đối tượng
        updateBar(maxHp); 
    }

    /**
     * Hàm xử lý cập nhật đồ họa hiển thị sinh lực (HP Bar Rendering UI).
     * Thực hiện cơ chế đồng bộ hóa mốc MaxHP động từ thế giới trận đấu, tính toán độ dài hình học
     * theo tỷ lệ phần trăm và vẽ các lớp đồ họa bao phủ (Khung nền, Tên, Thanh màu, Viền).
     * @param currentHp Lượng sinh lực hiện tại truyền vào từ thực thể sở hữu
     */
    public void updateBar(int currentHp) {
        // Rào khống chế giới hạn dưới: Ngăn chặn chỉ số máu nhận giá trị âm toán học
        if (currentHp < 0) currentHp = 0;
        
        // Ghi chú: Đồng bộ hóa dữ liệu giới hạn sinh lực tối đa (MaxHP) thực tế từ Hero để chống tràn khung giao diện.
        if (isHero && getWorld() instanceof MyWorld) {
            MyWorld world = (MyWorld) getWorld();
            if (world.getHero() != null) {
                // Truy vấn trực tiếp thuộc tính MaxHP động từ thực thể Hero đang hiện diện trên sân đấu
                this.maxHp = world.getHero().getMaxHp(); 
            }
        }
        
        // Cấu hình 1: Khởi tạo kích thước khung phôi nền tổng thể cho thanh trạng thái (220x50 pixel)
        GreenfootImage img = new GreenfootImage(220, 50);
        img.setColor(Color.BLACK);
        img.fillRect(0, 0, 220, 50);
        
        // Cấu hình 2: Ghi văn bản hiển thị tên nhân vật lên góc trên khung trạng thái
        img.setColor(Color.WHITE);
        img.setFont(new Font("Arial", true, false, 14));
        img.drawString(name, 10, 20);
        
        // Cấu hình 3: Phân định màu sắc (Xanh cho phe ta, Đỏ cho phe địch) và tính toán tỷ lệ độ dài thanh máu thực tế
        img.setColor(isHero ? Color.GREEN : Color.RED);
        int barWidth = (int) (((double) currentHp / this.maxHp) * 200);
        
        // Rào khống chế giới hạn trên: Ngăn chặn lỗi hiển thị tràn viền đồ họa vượt mốc chiều dài khung cố định 200 pixel
        if (barWidth > 200) barWidth = 200; 
        
        // Tiến hành đổ màu lấp đầy không gian thanh máu dựa trên thông số hình học đã tính toán
        img.fillRect(10, 30, barWidth, 12);
        
        // Cấu hình 4: Vẽ viền trắng bọc ngoài cùng của thanh máu để tăng độ tương phản và sắc nét
        img.setColor(Color.WHITE);
        img.drawRect(10, 30, 200, 12);
        
        // Nạp kết quả ảnh đồ họa hoàn chỉnh vào lớp thực thể Actor để hiển thị lên màn hình
        setImage(img);
    }
}