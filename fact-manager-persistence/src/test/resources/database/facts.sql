---
-- #%L
-- FactManager (Persistence)
-- %%
-- Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
-- #L%
---
INSERT INTO facts (id, tenant_id, value, element_id, created_at, type,
grouping, organization_id, tag) VALUES ('1', '1', '[1,5,6,7]', '1', '2022-01-30 00:00:00', 'StringFact',
'1', '1', '1');
INSERT INTO facts (`id`, `tenant_id`, `value`, `element_id`, `created_at`, `type`, `grouping`,
`organization_id`, `tag`) VALUES ('2', '2', '[1,5,6,7]', '2', '2022-01-30 00:00:00', 'StringFact', '2', '2', '2');
INSERT INTO facts (`id`, `tenant_id`, `value`, `element_id`, `created_at`, `type`,
`grouping`, `organization_id`, `tag`) VALUES ('3', '3', '[1,5,6,7]', '3', '2022-01-30 00:00:00', 'StringFact',
'3', '3', '3');
