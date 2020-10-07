DROP TABLE IF EXISTS facts;
CREATE TABLE facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `fact` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL
);