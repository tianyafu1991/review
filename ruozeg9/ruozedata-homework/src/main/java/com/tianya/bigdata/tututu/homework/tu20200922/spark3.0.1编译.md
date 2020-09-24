# 下载
```$xslt
https://archive.apache.org/dist/spark/spark-3.0.1/spark-3.0.1.tgz
```

# git bash 编译
```$xslt
1.修改$SPARK_HOME/pom.xml文件：在repositories标签中新增阿里云的maven仓库地址和cloudera的仓库地址
<repository>
  <id>aliyun</id>
  <name>cloudera Repository</name>
  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
</repository>

<repository>
  <id>cloudera</id>
  <name>cloudera Repository</name>
  <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
</repository>
2.修改$SPARK_HOME/dev/make-distribution.sh脚本
(1)显式指定一些版本信息，跳过编译时的一些比较耗时的版本检查，提高编译效率
显式指定一下版本信息
VERSION=3.0.1
SCALA_VERSION=2.12
SPARK_HADOOP_VERSION=2.6.0-cdh5.16.2
SPARK_HIVE=1

并将脚本中原来的检查给注释掉(就是下面这段)
#VERSION=$("$MVN" help:evaluate -Dexpression=project.version $@ 2>/dev/null\
#    | grep -v "INFO"\
#    | grep -v "WARNING"\
#    | tail -n 1)
#SCALA_VERSION=$("$MVN" help:evaluate -Dexpression=scala.binary.version $@ 2>/dev/null\
#    | grep -v "INFO"\
#    | grep -v "WARNING"\
#    | tail -n 1)
#SPARK_HADOOP_VERSION=$("$MVN" help:evaluate -Dexpression=hadoop.version $@ 2>/dev/null\
#    | grep -v "INFO"\
#    | grep -v "WARNING"\
#    | tail -n 1)
#SPARK_HIVE=$("$MVN" help:evaluate -Dexpression=project.activeProfiles -pl sql/hive $@ 2>/dev/null\
#    | grep -v "INFO"\
#    | grep -v "WARNING"\
#    | fgrep --count "<id>hive</id>";\
#    # Reset exit status to 0, otherwise the script stops here if the last grep finds nothing\
#    # because we use "set -o pipefail"
#    echo -n)

(2)增加编译时的内存设置，防止OOM
修改这一行的内存配置，我这里统一放大了一倍
export MAVEN_OPTS="${MAVEN_OPTS:--Xmx4g -XX:ReservedCodeCacheSize=2g}"

3.修改scala的版本(这一步不用执行，spark3.0.1默认scala2.12)
./dev/change-scala-version.sh 2.12

4.编译期间需要下载(这个是在$SPARK_HOME/build/mvn的脚本中指定的)
这个zinc的版本和scala的版本要看一下mvn这个脚本确认一下
https://downloads.lightbend.com/zinc/0.3.15/zinc-0.3.15.tgz
https://downloads.lightbend.com/scala/2.12.10/scala-2.12.10.tgz
有时候下载比较慢，提前下载好之后放在$SPARK_HOME/build下面就行

5.编译：
./dev/make-distribution.sh --name 2.6.0-cdh5.16.2 --tgz -Dhadoop.version=2.6.0-cdh5.16.2 -Dscala.version=2.12.10 -Phive-1.2 -Phive-thriftserver -Pyarn
[INFO] Compiling 25 Scala sources to F:\study\ruozedata\sourcecode\spark-3.0.1\resource-managers\yarn\target\scala-2.12\classes ...
[ERROR] [Error] F:\study\ruozedata\sourcecode\spark-3.0.1\resource-managers\yarn\src\main\scala\org\apache\spark\deploy\yarn\Client.scala:298: value setRolledLogsIncludePattern is not a member of org.apache.hadoop.yarn.api.records.LogAggregationContext
[ERROR] [Error] F:\study\ruozedata\sourcecode\spark-3.0.1\resource-managers\yarn\src\main\scala\org\apache\spark\deploy\yarn\Client.scala:300: value setRolledLogsExcludePattern is not a member of org.apache.hadoop.yarn.api.records.LogAggregationContext
[ERROR] two errors found
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for Spark Project Parent POM 3.0.1:
[INFO]
[INFO] Spark Project Parent POM ........................... SUCCESS [  3.519 s]
[INFO] Spark Project Tags ................................. SUCCESS [  6.976 s]
[INFO] Spark Project Sketch ............................... SUCCESS [  6.617 s]
[INFO] Spark Project Local DB ............................. SUCCESS [  2.225 s]
[INFO] Spark Project Networking ........................... SUCCESS [  4.952 s]
[INFO] Spark Project Shuffle Streaming Service ............ SUCCESS [  2.012 s]
[INFO] Spark Project Unsafe ............................... SUCCESS [  8.718 s]
[INFO] Spark Project Launcher ............................. SUCCESS [  3.070 s]
[INFO] Spark Project Core ................................. SUCCESS [02:34 min]
[INFO] Spark Project ML Local Library ..................... SUCCESS [ 27.608 s]
[INFO] Spark Project GraphX ............................... SUCCESS [ 36.183 s]
[INFO] Spark Project Streaming ............................ SUCCESS [01:01 min]
[INFO] Spark Project Catalyst ............................. SUCCESS [02:44 min]
[INFO] Spark Project SQL .................................. SUCCESS [04:20 min]
[INFO] Spark Project ML Library ........................... SUCCESS [03:04 min]
[INFO] Spark Project Tools ................................ SUCCESS [  9.778 s]
[INFO] Spark Project Hive ................................. SUCCESS [02:38 min]
[INFO] Spark Project REPL ................................. SUCCESS [ 35.457 s]
[INFO] Spark Project YARN Shuffle Service ................. SUCCESS [ 27.328 s]
[INFO] Spark Project YARN ................................. FAILURE [ 18.961 s]
[INFO] Spark Project Hive Thrift Server ................... SKIPPED
[INFO] Spark Project Assembly ............................. SKIPPED
[INFO] Kafka 0.10+ Token Provider for Streaming ........... SKIPPED
[INFO] Spark Integration for Kafka 0.10 ................... SKIPPED
[INFO] Kafka 0.10+ Source for Structured Streaming ........ SKIPPED
[INFO] Spark Project Examples ............................. SKIPPED
[INFO] Spark Integration for Kafka 0.10 Assembly .......... SKIPPED
[INFO] Spark Avro ......................................... SKIPPED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  19:39 min
[INFO] Finished at: 2020-09-22T00:41:42+08:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal net.alchim31.maven:scala-maven-plugin:4.3.0:compile (scala-compile-first) on project spark-yarn_2.12: Execution scala-compile-first of goal net.alchim31.maven:scala-maven-plugin:4.3.0:compile failed.: CompileFailed -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/PluginExecutionException
[ERROR]


编译不通过，报错原因：
[ERROR] [Error] F:\study\ruozedata\sourcecode\spark-3.0.1\resource-managers\yarn\src\main\scala\org\apache\spark\deploy\yarn\Client.scala:298: value setRolledLogsIncludePattern is not a member of org.apache.hadoop.yarn.api.records.LogAggregationContext
[ERROR] [Error] F:\study\ruozedata\sourcecode\spark-3.0.1\resource-managers\yarn\src\main\scala\org\apache\spark\deploy\yarn\Client.scala:300: value setRolledLogsExcludePattern is not a member of org.apache.hadoop.yarn.api.records.LogAggregationContext

查找spark issues
https://issues.apache.org/jira/browse/SPARK-19545
通过19545找到https://github.com/apache/spark/pull/16884

其他没有遇到编译上的坑

```
#注意
```
上面的编译过程是通过指定了-Phive-1.2，所以编译的Hive版本是1.2.1,而spark3.0.1编译时，如果不指定-Phive-1.2 而是指定-Phive，那么Hive版本就是2.3的
如果编译了Hive的2.3版本，因为Hive2.x与Hive1.x版本的元数据表发生了变化，在编译完之后去访问Hive1.x中的表时，会报错
具体看这个issues：https://issues.apache.org/jira/browse/SPARK-28711
解决方案：https://github.com/apache/spark/pull/25431中提到sbin/start-thriftserver.sh --conf spark.sql.hive.metastore.version=1.2 --conf spark.sql.hive.metastore.jars=maven
所以在启动spark-sql脚本的时候，也可以通过加上这两个参数，通过这2个参数来访问Hive1.x中的数据，第一次查询会报错,后面就没问题了
这里还有个小坑：--conf spark.sql.hive.metastore.jars=maven 会从 maven仓库去下相关的jar包，可以用spark.sql.hive.metastore.jars=builtin或者使用第3中方式
这个参数在http://spark.apache.org/docs/3.0.1/configuration.html上有介绍3种方式

spark的父pom中：
<profile>
      <id>hive-1.2</id>
      <properties>
        <hive.group>org.spark-project.hive</hive.group>
        <hive.classifier></hive.classifier>
        <!-- Version used in Maven Hive dependency -->
        <hive.version>1.2.1.spark2</hive.version>
        <!-- Version used for internal directory structure -->
        <hive.version.short>1.2</hive.version.short>
        <hive.parquet.scope>${hive.deps.scope}</hive.parquet.scope>
        <hive.storage.version>2.6.0</hive.storage.version>
        <hive.storage.scope>provided</hive.storage.scope>
        <hive.common.scope>provided</hive.common.scope>
        <hive.llap.scope>provided</hive.llap.scope>
        <hive.serde.scope>provided</hive.serde.scope>
        <hive.shims.scope>provided</hive.shims.scope>
        <orc.classifier>nohive</orc.classifier>
        <datanucleus-core.version>3.2.10</datanucleus-core.version>
      </properties>
    </profile>
<profile>
      <id>hive-2.3</id>
      <!-- Default hive profile. Uses global properties. -->
    </profile>

```


# 在Linux上部署

```
[hadoop@hadoop software]$ tar -xvf spark-3.0.1-bin-2.6.0-cdh5.16.2.tgz -C ~/app/
[hadoop@hadoop software]$ cd ~/app/
[hadoop@hadoop01 app]$ rm -rf spark
[hadoop@hadoop app]$ ln -s spark-3.0.1-bin-2.6.0-cdh5.16.2 spark
[hadoop@hadoop app]$ cd ~/app/spark/conf/
[hadoop@hadoop conf]$ cp spark-env.sh.template spark-env.sh
[hadoop@hadoop conf]$ cp spark-defaults.conf.template spark-defaults.conf
[hadoop@hadoop conf]$ vim spark-env.sh
SPARK_LOCAL_IP=hadoop
HADOOP_CONF_DIR=/home/hadoop/app/hadoop/etc/hadoop
export SPARK_HISTORY_OPTS="-Dspark.history.fs.logDirectory=hdfs://hadoop:9000/tmp/logs/spark -Dspark.history.ui.port=7777 -Dspark.history.fs.cleaner.enabled=true"

[hadoop@hadoop conf]$ vim spark-defaults.conf
spark.master                     local[1]
spark.eventLog.enabled           true
spark.eventLog.dir               hdfs://hadoop:9000/tmp/logs/spark

[hadoop@hadoop conf]$ ln -s  /home/hadoop/app/hive/conf/hive-site.xml hive-site.xml
[hadoop@hadoop conf]$ cd ~/app/spark/bin/
[hadoop@hadoop conf]$ ./spark-sql --master yarn --jars /home/hadoop/lib/mysql-connector-java-5.1.47.jar 

```
