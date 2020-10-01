DROP TABLE IF EXISTS facts;
CREATE TABLE facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `fact` text,
  `created_at` datetime DEFAULT NULL
);