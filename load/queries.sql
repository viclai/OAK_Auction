-- Part E: Testing with queries

-- 1. Return the number of users
SELECT COUNT(*)
FROM ((SELECT *
       FROM BidderRating
       WHERE ID <> ALL (SELECT ID
                        FROM SellerRating))
      UNION
      (SELECT *
       FROM SellerRating)) Users;

-- 2. Return the number of items in location "New York".
SELECT COUNT(*)
FROM Item
WHERE BINARY Location = "New York";

-- 3. Return the number of auctions belonging to exactly 4 categories.
SELECT COUNT(*)
FROM (SELECT ItemID
      FROM ItemCategory
      GROUP BY ItemID
      HAVING COUNT(*) = 4) Four_Category_Auctions;

-- 4. Return the ID(s) of current (unsold) auction(s) with the highest bid.
SELECT ItemID AS ITEMID
FROM Bid
WHERE Amount = (SELECT MAX(B. Amount)
                FROM Item I, Bid B
                WHERE I.Started <= '2001-12-20 00:00:01' AND
                      I.Ends > '2001-12-20 00:00:01' AND
                      I.ItemID = B.ItemID AND
                      I.Number_of_Bids <> 0);

-- 5. Return the number of sellers whose rating is higher than 1000.
SELECT COUNT(*)
FROM SellerRating
WHERE Rating > 1000;

-- 6. Return the number of users who are both sellers and bidders.
SELECT COUNT(*)
FROM (SELECT *
      FROM BidderRating B
      WHERE EXISTS (SELECT *
                    FROM SellerRating S
                    WHERE B.ID = S.ID)) SellersBidders;

-- 7. Return the number of categories that include at least one item with a
--    bid of more than $100.
SELECT COUNT(DISTINCT Category) AS 'COUNT(DISTINCT CATEGORY)'
FROM ItemCategory IC, Bid B
WHERE IC.ItemID = B.ItemID AND B.Amount > 100.00;
