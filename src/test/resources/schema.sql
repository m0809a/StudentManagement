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
  `course_id` varchar(36) NOT NULL,
   `student_id` varchar(36) NOT NULL,
   `course_name` varchar(50) NOT NULL,
   `course_start_at` date DEFAULT NULL,
   `course_end_at` date DEFAULT NULL,
   `deleted` boolean NOT NULL DEFAULT false,
   `student_course_id` varchar(36) NOT NULL,
   PRIMARY KEY (`student_course_id`)
);

CREATE TABLE IF NOT EXISTS students_course_status (
  status_id varchar(36) NOT NULL,
  student_course_id varchar(36) NOT NULL,
  status varchar(20) NOT NULL,
  deleted boolean NOT NULL DEFAULT false,
  PRIMARY KEY (status_id),
  CONSTRAINT uq_student_course_id UNIQUE (student_course_id),
  CONSTRAINT fk_status_student_course
    FOREIGN KEY (student_course_id)
    REFERENCES students_courses (student_course_id)
);