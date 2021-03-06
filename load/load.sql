-- Part D: Load data

LOAD DATA LOCAL INFILE 'item.del'
INTO TABLE Item
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'item_category.del'
INTO TABLE ItemCategory
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'item_buy_price.del'
INTO TABLE ItemBuyPrice
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'bid.del'
INTO TABLE Bid
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'user_location.del'
INTO TABLE UserLocation
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'bidder_rating.del'
INTO TABLE BidderRating
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'seller_rating.del'
INTO TABLE SellerRating
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;

LOAD DATA LOCAL INFILE 'item_location.del'
INTO TABLE ItemLocation
FIELDS
    TERMINATED BY '|@|'
LINES
    TERMINATED BY '\n';

SHOW WARNINGS;
