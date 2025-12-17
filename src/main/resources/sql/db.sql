CREATE DATABASE mini_football_db;

CREATE USER mini_football_db_manager with password 'football';

\c mini_football_db;

GRANT CONNECT ON DATABASE mini_football_db TO mini_football_db_manager;
GRANT CREATE ON DATABASE mini_football_db TO mini_football_db_manager;
GRANT USAGE, CREATE ON SCHEMA public TO mini_football_db_manager;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT,UPDATE, DELETE ON tables TO mini_football_db_manager;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT, UPDATE ON sequences TO mini_football_db_manager;
