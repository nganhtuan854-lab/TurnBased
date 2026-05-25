        import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
        
        /**
         * Lớp Monster đại diện cho thực thể quái vật (kẻ địch) trong trò chơi.
         * Quản lý các thuộc tính chỉ số (HP, ATK), cơ chế hiển thị lá bài (Card UI),
         * hệ thống chọn ngẫu nhiên hành động dự định (Intent) và logic tương tác chiến đấu.
         */
        public class Monster extends Actor {
            // --- Các thuộc tính chỉ số cơ bản của quái vật ---
            protected int hp;
            protected int maxHp;
            protected int atk;
            
            // --- Các thuộc tính quản lý đồ họa và hiển thị trạng thái ---
            protected String hinhAnhGoc;      // Lưu trữ đường dẫn file ảnh gốc để tái tạo giao diện
            protected String intent = "ATTACK"; // Ý định hành động mặc định ban đầu (TẤN CÔNG)
            private IntentIcon intentVisual = null; // Thực thể hiển thị icon biểu tượng ý định trên đầu quái vật
            protected String baseImageName;   // Lưu trữ tên file ảnh asset gốc của quái vật
        
            // --- Hàm Getter phục vụ việc truy vấn sinh lực từ hệ thống bên ngoài ---
            public int getHP() {
                return this.hp;
            }
            
            /**
             * Hàm khởi tạo (Constructor) của đối tượng Monster.
             * Thiết lập chỉ số thuộc tính nền tảng và kích hoạt chu kỳ vẽ giao diện lá bài đầu trận.
             * @param imageName Tên file ảnh asset của quái vật trong thư mục dự án
             * @param hp Lượng sinh lực khởi tạo cho quái vật
             * @param atk Chỉ số sát thương cơ bản của quái vật
             */
            public Monster(String imageName, int hp, int atk) {
                this.baseImageName = imageName; 
                this.hp = hp;
                this.maxHp = hp;
                this.atk = atk;
                
                // Khởi tạo hoạt ảnh đồ họa cấu trúc thẻ bài khi đối tượng xuất hiện
                updateCardImage();
            }
        
            /**
             * Hàm đồ họa xây dựng cấu trúc giao diện thẻ bài (Card Rendering UI).
             * Thực hiện tạo phôi thẻ trống, định dạng viền màu sắc, nạp ảnh thực thể thu nhỏ,
             * thiết lập hộp mô tả và ghi các chỉ số thuộc tính thời gian thực (Realtime).
             */
            public void updateCardImage() {
                if (baseImageName == null) return;
                
                // Cấu hình 1: Khởi tạo kích thước phôi thẻ bài chuẩn 160x220 pixel
                GreenfootImage card = new GreenfootImage(160, 220);
                
                // Đổ màu nền xám đậm cho tổng thể thẻ bài
                card.setColor(new Color(50, 45, 40)); 
                card.fill();
                
                // Vẽ cấu trúc viền đôi màu vàng hổ phách (Gold) để tăng tính thẩm mỹ đồ họa
                card.setColor(new Color(218, 165, 32)); 
                card.drawRect(0, 0, 159, 219);
                card.drawRect(1, 1, 157, 217); 
                
                // Cấu hình 2: Nạp và xử lý tỷ lệ hình ảnh của quái vật vào trung tâm thẻ
                GreenfootImage goblinImg = new GreenfootImage(baseImageName);
                goblinImg.scale(140, 150); // Cân đối kích thước ảnh vừa vặn lòng thẻ bài
                card.drawImage(goblinImg, 10, 10); // Định vị tọa độ dán ảnh cách lề trái và lề trên
                
                // Cấu hình 3: Khởi tạo hộp nền đen mô tả chỉ số ở đáy lá bài
                card.setColor(new Color(0, 0, 0, 220)); 
                card.fillRect(5, 220 - 45, 150, 40); 
                
                // Cấu hình 4: Xuất văn bản hiển thị chỉ số HP và ATK hiện hành
                card.setColor(Color.WHITE); 
                card.setFont(new Font("Arial", true, false, 13)); 
                
                int textX = 20;  
                int textY = 220 - 20; 
                
                String statusText = "HP: " + this.hp + " | ATK: " + this.atk;
                card.drawString(statusText, textX, textY);
                
                // Nạp tấm thẻ bài đồ họa hoàn chỉnh vào thực thể Actor
                setImage(card);
            }
        
            /**
             * Vòng lặp Act chạy liên tục trong chu kỳ hoạt động của Greenfoot.
             * Lắng nghe sự kiện nhấp chuột của người chơi để thực hiện thao tác chọn mục tiêu (Target Selection).
             */
            public void act() {
                if (Greenfoot.mouseClicked(this)) {
                    MyWorld world = (MyWorld) getWorld();
                    // Điều kiện bảo vệ: Chỉ cho phép chọn mục tiêu khi đang trong lượt hành động của Hero
                    if (world != null && world.isHeroTurn) {
                        world.setSelectedMonster(this); // Đồng bộ lưu vết đối tượng bị chọn vào hệ thống World
                    }
                }
            }
        
            /**
             * Logic vận hành đòn phản công chủ động (Attack Action).
             * Dựa trên ý định hiện tại để thực thi chuỗi hoạt ảnh tịnh tiến áp sát gây sát thương lên Hero,
             * hoặc đứng im tích lũy trạng thái phòng thủ phòng thủ (Defend).
             */
            public void attackAction() {
                if (getWorld() == null) return;
            
                if (this.intent.equals("ATTACK")) {
                    int startX = getX(); 
                    int startY = getY();
                    MyWorld world = (MyWorld) getWorld();
                    Hero hero = world.getHero();
                
                    if (hero != null && hero.getWorld() != null) {
                        // Chu kỳ hoạt ảnh tịnh tiến di chuyển lao mạnh về phía tọa độ của nhân vật chính
                        for (int i = 0; i < 5; i++) {
                            setLocation(getX() - 30, getY() + 20); 
                            Greenfoot.delay(2);
                        }
                        // Triệu hồi hàm trừ máu của Hero và áp chỉ số sát thương gốc công kích
                        hero.receiveDamage(this.atk, this); 
                        Greenfoot.delay(20); // Duy trì độ trễ tại tọa độ tiếp cận để tạo điểm nhấn hoạt ảnh
                        
                        // Thu hồi thực thể quái vật quay trở lại vị trí phòng thủ ban đầu trên làn đấu
                        setLocation(startX, startY);
                    }
                } else {
                    // Trường hợp ý định là DEFEND: Đứng im kích hoạt hoạt ảnh gồng thủ thế
                    getWorld().addObject(new EffectText("🛡️ GỒNG THỦ!", Color.BLUE), getX(), getY() - 50);
                    Greenfoot.delay(30); // Duy trì trạng thái tĩnh trong 30 khung hình để đồng bộ thị giác
                }
            }
        
            /**
             * Logic tiếp nhận sát thương đầu vào và khấu trừ sinh lực từ các đòn đánh của Hero.
             * Tích hợp bộ giảm trừ 50% gánh nặng sát thương nếu quái vật đang duy trì trạng thái gồng thủ.
             * @param damage Lượng sát thương gốc từ Hero truyền vào
             */
            public void takeDamage(int damage) {
                if (getWorld() == null) return;
        
                // Bộ lọc giảm trừ sát thương: Giảm 50% lượng dame nhận vào nếu đang đặt ý định phòng thủ
                if (this.intent.equals("DEFEND")) {
                    damage = damage / 2;
                    getWorld().addObject(new EffectText("BLOCKED 50%!", Color.BLUE), getX(), getY() - 90);
                }
        
                // Thực hiện khấu trừ sinh lực hiện tại
                this.hp -= damage;
                if (this.hp < 0) this.hp = 0; // Rào khống chế ngăn chỉ số máu hiển thị mốc giá trị âm bậy bạ
        
                // Đồng bộ vẽ lại thông số văn bản hiển thị trên lá bài ngay lập tức khi chỉ số HP biến động
                updateCardImage();
        
                // Hiển thị số lượng sát thương nhận vào dạng văn bản bay trên đầu đối tượng
                getWorld().addObject(new EffectText("-" + damage, Color.RED), getX(), getY() - 50);
                
                // Kiểm tra điều kiện sinh tồn để dọn dẹp giải phóng tài nguyên thực thể khỏi bộ nhớ World
                if (this.hp <= 0) {
                    if (intentVisual != null && intentVisual.getWorld() != null) {
                        getWorld().removeObject(intentVisual); // Xóa bỏ icon ý định đi kèm
                    }
                    getWorld().removeObject(this); // Giải phóng thực thể lá bài quái vật khỏi thế giới
                }
            }
        
            /**
             * Cơ chế xúc xắc ngẫu nhiên chọn ý định hành động (Procedural Intent Rolling).
             * Thiết lập tỷ lệ xác suất cân bằng (50% Tấn công / 50% Phòng thủ) cho mỗi chu kỳ lượt mới.
             */
            public void rollIntent() {
                if (Greenfoot.getRandomNumber(100) < 50) {
                    this.intent = "ATTACK";
                } else {
                    this.intent = "DEFEND";
                }
                // Gọi hàm đồng bộ cập nhật hiển thị icon hành động tương ứng
                updateIntentVisual();
            } 
            
            /**
             * Hàm khởi tạo và đồng bộ hóa biểu tượng ý định hiển thị (Intent Visual Synchronization).
             * Giải phóng icon lưu cửu vòng cũ khỏi bộ nhớ hệ thống và nạp thực thể biểu tượng mới tương thích.
             */
            public void updateIntentVisual() {
                // Thực hiện dọn dẹp giải phóng đối tượng đồ họa ý định cũ khỏi World
                if (intentVisual != null && intentVisual.getWorld() != null) {
                    getWorld().removeObject(intentVisual);
                }
                
                // Khởi tạo thực thể đồ họa icon mới dựa trên kết quả tính toán ý định
                if (this.intent.equals("ATTACK")) {
                    intentVisual = new IntentIcon("⚔️ ATTACK", Color.RED);
                } else {
                    intentVisual = new IntentIcon("🛡️ DEFEND", Color.BLUE);
                }
                
                // Thiết lập thả thực thể đồ họa icon cố định tại không gian ngay trên đầu lá bài quái vật
                getWorld().addObject(intentVisual, getX(), getY() - 70);
            }
        }