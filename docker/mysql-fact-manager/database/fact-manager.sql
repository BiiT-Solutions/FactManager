DROP TABLE IF EXISTS facts;
CREATE TABLE IF NOT EXISTS facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `tenant_id` long DEFAULT NULL,
  `category` varchar(190),
  `value` varchar(190),
  `element_id` varchar(190),
  `created_at` datetime DEFAULT NULL,
);