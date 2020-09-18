```
要使用spark-sql连接hive，需要将hive-site.xml软连接到spark的conf目录下

[hadoop@hadoop conf]$ cd /home/hadoop/app/spark/conf
[hadoop@hadoop conf]$ ln -s /home/hadoop/app/hive/conf/hive-site.xml hive-site.xml


[hadoop@hadoop bin]$ cd ~/app/spark/bin/
[hadoop@hadoop bin]$ ./spark-shell --jars /home/hadoop/lib/mysql-connector-java-5.1.47.jar

# 这个要加上，要不然spark-shell和spark-sql都还要指定--jars
[hadoop@hadoop jars]$ pwd
/home/hadoop/app/spark/jars
[hadoop@hadoop jars]$ ln -s /home/hadoop/lib/mysql-connector-java-5.1.47.jar mysql-connector-java-5.1.47.jar

[hadoop@hadoop bin]$ ./beeline -u jdbc:hive2://localhost:10000
Connecting to jdbc:hive2://localhost:10000
20/09/18 16:50:46 INFO jdbc.Utils: Supplied authorities: localhost:10000
20/09/18 16:50:46 INFO jdbc.Utils: Resolved authority: localhost:10000
20/09/18 16:50:46 INFO jdbc.HiveConnection: Will try to open client transport with JDBC Uri: jdbc:hive2://localhost:10000
Connected to: Spark SQL (version 2.4.6)
Driver: Hive JDBC (version 1.2.1.spark2)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 1.2.1.spark2 by Apache Hive
0: jdbc:hive2://localhost:10000> 
```