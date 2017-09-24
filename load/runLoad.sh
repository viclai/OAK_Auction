#!/bin/bash

# Run drop.sql to drop existing tables
mysql CS144 < drop.sql

# Run create.sql to create the database and tables
mysql CS144 < create.sql

# Compile and run the parser to generate the appropriate load files
ant run-all

# Remove duplicate tuples in the load files generated
ls *.del | xargs -I {} sort -u {} -o {}

# Run load.sql to load the data
mysql CS144 < load.sql

# Remove all temporary files
rm -r bin/ *.del
