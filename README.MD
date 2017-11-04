# OAK Auction

## Synopsis

*OAK Auction* is a [course](https://goo.gl/YDLJTk) project in development
which provides a website that allows users to search and navigate auction
bidding data from eBay. This project was created for educational purposes.

![Website](hub/oak_auction.gif "OAK Auction")

## Usage

Because this project was developed on a virtual machine, there is currently no
public website available. This project may be deployed on
[OpenShift](https://www.openshift.com/) in the future.

The website created allows users to search for auction items by keyword,
location, or ID, and view information about the items such as the current
price, first bid amount, associated categories, location, bid history, item
description, and seller information. It also allows one to simulate the
purchase of an item.

## Development

This sections shows specifications of the virtual machine that this project
was developed on and the technologies used.

The following is information related to the version of
[Apache Tomcat](http://tomcat.apache.org/) that the virtual instance used.

| Attribute      | Value                         |
| -------------- | ----------------------------- |
| Server version | Apache Tomcat/7.0.52 (Ubuntu) |
| Server built   | Jul 24 2014 08:38:51          |
| Server number  | 7.0.52.0                      |
| OS Name        | Linux                         |
| OS Version     | 3.13.0-24-generic             |
| Architecture   | i386                          |
| JVM Version    | 1.7.0_65-b32                  |
| JVM Vendor     | Oracle Corporation            |

[Apache Axis2](http://axis.apache.org/axis2/java/core/) version 1.6.2 was used
to create the web service.

[Apache Lucene](https://lucene.apache.org/core/) was used to enable keyword
searches on the data.

The version of [MySQL](https://www.mysql.com/) used was

`mysql  Ver 14.14 Distrib 5.5.40, for debian-linux-gnu (i686) using readline 6.3`.
