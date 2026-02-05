INSERT INTO students (id, name, kana_name, nickname, email, address, age, gender)
 VALUES
('S000001', '山田太郎', 'ヤマダタロウ', 'タロ', 'ymd@gmail.com', '東京都千代田区', 34, '男'),
('S000002', '鈴木花子', 'スズキハナコ', 'ハナ', 'suzuki_hanako@example.com', '東京都杉並区', 24, '女'),
('S000003', '佐藤友樹', 'サトウトモキ', 'トモ', 'tomo.sato@gmail.com', '東京都武蔵野市', 20, '男'),
('S000004', '岡部ひより', 'オカベヒヨリ', 'オヒヨ', 'hyr-okb@outlook.jp', '東京都調布市', 30, '女'),
('S000005', '高橋一輝', 'タカハシイツキ', 'イツキ', 'takahashitsuki@example.com', '東京都渋谷区', 22, '男');

INSERT INTO students_courses(id, student_id, course_name, course_start_at, course_end_at)
VALUES
('C000001', 'S000001', 'Java入門コース', '2025-02-01', '2025-05-31'),
('C000002', 'S000002', 'Webアプリ開発コース', '2025-03-01', '2025-06-30'),
('C000003', 'S000003', 'AWS基礎コース', '2025-03-16', '2025-07-15'),
('C000004', 'S000004', 'DB設計コース', '2025-06-01', '2025-09-30'),
('C000005', 'S000005', 'python基礎コース', '2025-07-03', '2025-10-31');

