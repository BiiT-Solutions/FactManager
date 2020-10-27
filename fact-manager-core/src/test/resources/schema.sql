DROP TABLE IF EXISTS facts;
CREATE TABLE facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `patient_id` long,
  `organization_id` long,
  `category` text,
  `question` text,
  `answer` text,
  `score` double,
  `xpath` text,
  `examination_name` text,
  `examination_version` long,
  `company_id` long,
  `created_at` datetime DEFAULT NULL
);