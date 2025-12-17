
CREATE TYPE continent_enum AS ENUM ('AFRICA', 'EUROPA', 'ASIA', 'AMERICA');

CREATE TABLE team(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    continent continent_enum
);

CREATE TYPE position_enum AS ENUM ('GK', 'DEF', 'MIDF', 'STR');

CREATE TABLE player(
    id INT PRIMARY KEY,
    name VARCHAR(255),
    age INT,
    position position_enum,
    id_team INT REFERENCES team(id)
);

