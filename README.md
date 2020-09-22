For phase 3 of the Project, please refer to the "code" directory"

# Instructions for running phase 3

1. ssh into the bolt server:  ssh netID@bolt.cs.ucr.edu
2. ssh into the Lab computer: ssh wch133-xx
3. cd /tmp/netID
4. Clone this repository
5. cd code
6. cd postgresql
7. chmod +x (asterisk).sh
8. ./startPostgreSQL.sh
9. ./createPostgreDB.sh

* To run the java code

10. cd code
11. cd java
12. source compile.sh
13. ./run,sh $LOGNAME_DB 5432 $USER

* To exit the server:

14. cd code
15. cd postgresql
16. ./stopPostgreDB.sh

# Demo Link:
https://drive.google.com/file/d/1PQNY4DBx0hXAXcj4fJYMEK93UYK_f8We/view?usp=sh
