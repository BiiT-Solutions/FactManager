DROP TABLE IF EXISTS facts;
CREATE TABLE facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `patient_id` long,
  `organization_id` long,
  `category` varchar(190),
  `question` varchar(190),
  `answer` varchar(190),
  `score` double,
  `xpath` varchar(190),
  `examination_name` varchar(190),
  `examination_version` long,
  `company_id` long,
  `created_at` datetime DEFAULT NULL
);