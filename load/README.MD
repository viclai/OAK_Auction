# Data Conversion and Loading

## Database Design

### Relational Schema

Based on the information given in the auction data, the following relations
were created in the MySQL database.

**Item**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ItemID         | `int(11)`       | No   | Primary |
| Name           | `varchar(50)`   | No   |         |
| Currently      | `decimal(8,2)`  | No   |         |
| First_Bid      | `decimal(8,2)`  | No   |         |
| Number_of_Bids | `int(11)`       | No   |         |
| Location       | `varchar(90)`   | No   |         |
| Country        | `varchar(20)`   | No   |         |
| Started        | `timestamp`     | No   |         |
| Ends           | `timestamp`     | No   |         |
| SellerID       | `varchar(40)`   | No   |         |
| Description    | `varchar(4000)` | No   |         |

**ItemCategory**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ItemID         | `int(11)`       | No   | Primary |
| Category       | `varchar(40)`   | No   | Primary |

**ItemBuyPrice**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ItemID         | `int(11)`       | No   | Primary |
| Buy_Price      | `decimal(8,2)`  | No   |         |

**Bid**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| BidderID       | `varchar(40)`   | No   | Primary |
| ItemID         | `int(11)`       | No   | Primary |
| Time           | `timestamp`     | No   | Primary |
| Amount         | `decimal(8,2)`  | No   |         |

**UserLocation**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ID             | `varchar(40)`   | No   | Primary |
| Location       | `varchar(90)`   | Yes  |         |
| Country        | `varchar(20)`   | Yes  |         |

**BidderRating**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ID             | `varchar(40)`   | No   | Primary |
| Rating         | `int(11)`       | No   |         |

**SellerRating**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ID             | `varchar(40)`   | No   | Primary |
| Rating         | `int(11)`       | No   |         |

**ItemLocation**
| Field          | Type            | Null | Key     |
| -------------- | --------------- | ---- | ------- |
| ItemID         | `int(11)`       | No   | Primary |
| Location       | `decimal(9,6)`  | Yes  |         |
| Country        | `decimal(9,6)`  | Yes  |         |

#### Functional Dependencies

The following are all the completely nontrivial functional dependencies that
were interpreted from the context of the auction data. 

**Item**:

  * ItemID -> Name, Currently, First_Bid, Number_of_Bids, Location, Country, SellerID, Description
  * Name -> Started, Ends

**ItemCategory**

  * ItemID ->> Category

**ItemBuyPrice**:

  * ItemID -> Buy_Price

**Bid**

  * BidderID, ItemID, Time -> Amount

**UserLocation**:

  * ID -> Location, Country

**BidderRating**:

  * ID -> Rating

**SellerRating**:

  * ID -> Rating

**ItemLocation**:

  * ItemID -> Latitude, Longitude

