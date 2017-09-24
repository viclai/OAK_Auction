-- Part D: Create tables

CREATE TABLE Item (
    ItemID INTEGER PRIMARY KEY,
    Name VARCHAR(50) NOT NULL,
    Currently DECIMAL(8, 2) NOT NULL,
    First_Bid DECIMAL(8, 2) NOT NULL,
    Number_of_Bids INTEGER NOT NULL,
    Location VARCHAR(90) NOT NULL,
    Country VARCHAR(20) NOT NULL,
    Started TIMESTAMP,
    Ends TIMESTAMP,
    SellerID VARCHAR(40) NOT NULL,
    Description VARCHAR(4000) NOT NULL
);

CREATE TABLE ItemCategory (
    ItemID INTEGER,
    Category VARCHAR(40),
    PRIMARY KEY (ItemID, Category)
);

CREATE TABLE ItemBuyPrice (
    ItemID INTEGER PRIMARY KEY,
    Buy_Price DECIMAL(8, 2) NOT NULL
);

CREATE TABLE Bid (
    BidderID VARCHAR(40),
    ItemID INTEGER,
    Time TIMESTAMP,
    Amount DECIMAL(8, 2) NOT NULL,
    PRIMARY KEY (BidderID, ItemID, Time)
);

CREATE TABLE UserLocation (
    ID VARCHAR(40) PRIMARY KEY,
    Location VARCHAR(90),
    Country VARCHAR(20)
);

CREATE TABLE BidderRating (
    ID VARCHAR(40) PRIMARY KEY,
    Rating INTEGER NOT NULL
);

CREATE TABLE SellerRating (
    ID VARCHAR(40) PRIMARY KEY,
    Rating INTEGER NOT NULL
);

CREATE TABLE ItemLocation (
    ItemID INTEGER PRIMARY KEY,
    Latitude DECIMAL(9, 6),
    Longitude DECIMAL(9, 6)
);
