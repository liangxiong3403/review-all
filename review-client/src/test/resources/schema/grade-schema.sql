CREATE TABLE `grade` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `score` int(4) DEFAULT NULL COMMENT '分数',
  `name` varchar(16) DEFAULT NULL COMMENT '科目',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;