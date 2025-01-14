-- create db and schema
CREATE DATABASE restdb;
CREATE SCHEMA restdb;

-- create user
CREATE USER restadmin WITH PASSWORD 'password';

GRANT CONNECT ON DATABASE restdb TO restadmin;

GRANT USAGE ON SCHEMA restdb TO restadmin;
GRANT CREATE ON SCHEMA restdb TO restadmin; -- includes DROP, INDEX, ALTER, CREATE VIEW, CREATE ROUTINE, ALTER ROUTINE
GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES, TRIGGER ON ALL TABLES IN SCHEMA restdb TO restadmin;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA restdb to restadmin;
ALTER ROLE restadmin SET search_path = restdb; -- users are properly referred to as roles


--GRANT EVENT:
--PostgreSQL does not have a direct equivalent for MySQL's EVENT privilege
--for handling events (e.g. scheduled events or cron-like jobs),
--because PostgreSQL handles scheduled jobs through extensions like pg_cron or tools like pgAgent.
--
--GRANT SHOW VIEW:
--PostgreSQL does not have a direct SHOW VIEW privilege,
--but querying the view itself (via SELECT) is sufficient to view its definition.
--CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci: PostgreSQL uses UTF8 encoding
--(which covers a wide range of Unicode characters, similar to MySQL's utf8mb4).


--------------
-- spring-6 --
--------------
--DROP USER IF EXISTS `restadmin`@`%`;
--CREATE DATABASE IF NOT EXISTS restdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--CREATE USER IF NOT EXISTS `restadmin`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
--GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
--CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `restdb`.* TO `restadmin`@`%`;