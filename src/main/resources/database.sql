DROP TABLE IF EXISTS Tickets CASCADE;
DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Coordinates CASCADE;
DROP TABLE IF EXISTS Venue CASCADE;

DROP TYPE IF EXISTS TicketType CASCADE;
DROP TYPE IF EXISTS VenueType CASCADE;

DROP DOMAIN IF EXISTS capacity CASCADE;
DROP DOMAIN IF EXISTS price CASCADE;
DROP DOMAIN IF EXISTS discount CASCADE;

CREATE TYPE VenueType AS ENUM ('BAR','CINEMA','MALL','STADIUM');
CREATE TYPE TicketType AS ENUM ('VIP', 'USUAL', 'BUDGETARY', 'CHEAP');

CREATE DOMAIN capacity AS BIGINT CHECK (VALUE > 0);
CREATE DOMAIN price AS FLOAT CHECK (VALUE > 0);
CREATE TABLE Users(
  username varchar(70) PRIMARY KEY,
  passwordHash varchar(70) NOT NULL
);

CREATE TABLE Coordinates(
id SERIAL PRIMARY KEY,
	x FLOAT ,
	y FLOAT NOT NULL
);

CREATE TABLE Venue(
	id SERIAL PRIMARY KEY,
	name varchar(50) NOT NULL,
	capacity BIGINT NOT NULL,
	type VenueType NOT NULL
);


CREATE TABLE Tickets (
    id SERIAL PRIMARY KEY,
    owner VARCHAR(200) REFERENCES Users(username),
    name VARCHAR(100) NOT NULL,
    coordinates INTEGER REFERENCES Coordinates(id),
    creationDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    price FLOAT,
    discount DOUBLE PRECISION NOT NULL CHECK (discount > 0 AND discount <= 100),
    refundable BOOLEAN NOT NULL,
    type TicketType NOT NULL,
    venue INT REFERENCES Venue(id)
);
INSERT INTO Users (username, passwordHash)
VALUES ('s408622',12345);

INSERT INTO Venue (id, name, capacity, type)
VALUES (15, 'ZenitArena', 10000,'STADIUM'),
(239, '239lyceum', 7, 'MALL');

INSERT INTO Coordinates (x, y)
VALUES (10, 20),
(14, 3);

INSERT INTO Tickets (owner, name, coordinates, creationDate, price, discount, refundable, type, venue)
VALUES ('s408622', 'Gorgorod', 1, CURRENT_TIMESTAMP, 100.0, 10, FALSE, 'VIP', 15),
('s408622', 'FataMorgana', 2, CURRENT_TIMESTAMP, 590.0, 1, FALSE,'USUAL', 239);

