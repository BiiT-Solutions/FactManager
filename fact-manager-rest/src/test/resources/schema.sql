DROP TABLE IF EXISTS facts;
CREATE TABLE facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `patient_id` bigint,
  `organization_id` bigint,
  `professional_id` bigint,
  `category` varchar(190),
  `question` varchar(190),
  `answer` varchar(190),
  `score` double,
  `xpath` varchar(190),
  `examination_name` varchar(190),
  `examination_version` bigint,
  `company_id` bigint,
  `created_at` datetime DEFAULT NULL
);
DROP TABLE IF EXISTS formrunner_facts;
CREATE TABLE IF NOT EXISTS formrunner_facts (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `patient_id` bigint,
  `organization_id` bigint,
  `professional_id` bigint,
  `category` varchar(190),
  `question` varchar(190),
  `answer` varchar(190),
  `score` double,
  `xpath` varchar(190),
  `examination_name` varchar(190),
  `examination_version` bigint,
  `company_id` bigint,
  `created_at` datetime DEFAULT NULL
);
