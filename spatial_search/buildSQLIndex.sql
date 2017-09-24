-- Part B: Create spatial index

CREATE TABLE SpatialTable (
    ItemID INTEGER PRIMARY KEY,
    Location Point NOT NULL,
    SPATIAL INDEX(Location)
) ENGINE=MyISAM;

INSERT INTO SpatialTable
  SELECT ItemID, Point(Latitude, Longitude)
  FROM ItemLocation;

CREATE SPATIAL INDEX LocationIndex
ON SpatialTable(Location);
