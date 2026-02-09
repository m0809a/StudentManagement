 CREATE TABLE IF NOT EXISTS `students`
 (
  `id` varchar(36) NOT NULL,
  `name` varchar(30) NOT NULL,
  `kana_name` varchar(50) NOT NULL,
  `nickname` varchar(30) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `address` varchar(100) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `remark` varchar(500) NOT NULL DEFAULT '',
  `deleted` boolean NOT NULL DEFAULT false,
  PRIMARY KEY (`id`)
);

 CREATE TABLE IF NOT EXISTS `students_courses`
 (
  `id` varchar(36) NOT NULL,
  `student_id` varchar(36) NOT NULL,
  `course_name` varchar(50) NOT NULL,
  `course_start_at` date DEFAULT NULL,
  `course_end_at` date DEFAULT NULL,
  `deleted` boolean NOT NULL DEFAULT false
);