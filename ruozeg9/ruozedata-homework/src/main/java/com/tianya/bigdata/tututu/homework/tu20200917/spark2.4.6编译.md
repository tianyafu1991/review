
##源码下载地址：https://archive.apache.org/dist/spark/spark-2.4.6/spark-2.4.6.tgz


## 编译
```$xslt
1.修改pom文件：在repositories标签中新增阿里云的maven仓库地址和cloudera的仓库地址
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

2.修改make-distribution.sh脚本,最好修改xmx的那行，增加内存，防止报OOM

修改
export MAVEN_OPTS="${MAVEN_OPTS:--Xmx4g -XX:ReservedCodeCacheSize=2g}"


添加：
VERSION=2.4.6
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

3.修改scala的版本
./dev/change-scala-version.sh 2.12

4.编译：
./dev/make-distribution.sh --name 2.6.0-cdh5.16.2 --tgz -Phadoop-2.6 -Dhadoop.version=2.6.0-cdh5.16.2 -Dscala.version=2.12.10 -Phive -Phive-thriftserver -Pyarn

5.编译期间需要下载(这个是在$SPARK_HOME/build/mvn的脚本中指定的)
https://downloads.lightbend.com/zinc/0.3.15/zinc-0.3.15.tgz
https://downloads.lightbend.com/scala/2.11.12/scala-2.11.12.tgz
有时候下载比较慢，提前下载好之后放在$SPARK_HOME/build下面就行


IDEA编译：（需要idea安装antlr v4插件）

相关资料，很多未看，可能用得着：
编译相关：
http://www.superxiaojie.com/2019/05/17/spark-source-code/
https://www.coder.work/article/6831950
3.0spark get_table_req相关：
https://github.com/HotelsDotCom/waggle-dance/issues/110
https://stackoverflow.com/questions/63476121/hive-queries-failing-with-unable-to-fetch-table-test-table-invalid-method-name

编译后的坑:

第0个坑：
编译spark一定要在idea中安装antlr v4插件

第1个坑：
编译之后，我的所有的spark任务都无法执行，报错：
org.apache.spark.SparkException: Could not find spark-version-info.properties

解决思路：
参考了这个http://www.superxiaojie.com/2019/05/17/spark-source-code/，
但不管用，不过受这个博客启发，直接在spark的github上找spark-version-info.properties的信息
找到这个类：
看这个脚本：https://github.com/apache/spark/blob/5264164a67df498b73facae207eda12ee133be7d/build/spark-build-info，应该是因为windows的环境，所以没执行相关的shell脚本导致的
看这个类：https://github.com/apache/spark/blob/d66a4e82eceb89a274edeb22c2fb4384bed5078b/core/src/main/scala/org/apache/spark/package.scala
props.getProperty("version", unknownProp),
props.getProperty("branch", unknownProp),
props.getProperty("revision", unknownProp),
props.getProperty("user", unknownProp),
props.getProperty("url", unknownProp),
props.getProperty("date", unknownProp)
所以要手动建一个spark-version-info.properties文件：
然后里面写上:
version=2.4.6
branch=
revision=
user=
url=
date=

通过$SPARK_HOME/bin/spark-sql脚本，定位到主入口类：${SPARK_HOME}"/bin/spark-submit --class org.apache.spark.sql.hive.thriftserver.SparkSQLCLIDriver

第2个坑：这个坑下面的解决方法也可以，但最好的解决方法参考第6个坑的解决方法
运行main方法报错,参考：https://github.com/apache/spark/pull/2876
java.lang.ClassNotFoundException: com.google.common.cache.CacheLoader
这个是因为项目父pom文件中guava的scope是provided的，改成runtime即可（注意：修改之后，如果后续要重新编译，编译是过不去的，要重新改回provided才能编译通过）
<dependency>
<groupId>com.google.guava</groupId>
<artifactId>guava</artifactId>
<version>14.0.1</version>
<scope>runtime</scope>
</dependency>

第3个坑：
这个坑应该是scala版本不一致引起的，编译的时候已经指定了scala.version，但居然还是发生了这个错，
最终解决方法，直接修改了父pom的scala.version，直接写死成2.12.10
Exception in thread "main" java.lang.NoSuchMethodError: scala.Predef$.refArrayOps([Ljava/lang/Object;)[Ljava/lang/Object;
	at org.apache.spark.deploy.SparkHadoopUtil$.org$apache$spark$deploy$SparkHadoopUtil$$appendSparkHadoopConfigs(SparkHadoopUtil.scala:470)
	at org.apache.spark.deploy.SparkHadoopUtil$.org$apache$spark$deploy$SparkHadoopUtil$$appendS3AndSparkHadoopConfigurations(SparkHadoopUtil.scala:462)
	at org.apache.spark.deploy.SparkHadoopUtil$.newConfiguration(SparkHadoopUtil.scala:436)
	at org.apache.spark.deploy.SparkHadoopUtil.newConfiguration(SparkHadoopUtil.scala:114)
	at org.apache.spark.deploy.SparkHadoopUtil.<init>(SparkHadoopUtil.scala:52)
	at org.apache.spark.deploy.SparkHadoopUtil$.instance$lzycompute(SparkHadoopUtil.scala:392)
	at org.apache.spark.deploy.SparkHadoopUtil$.instance(SparkHadoopUtil.scala:392)
	at org.apache.spark.deploy.SparkHadoopUtil$.get(SparkHadoopUtil.scala:413)
	at org.apache.spark.sql.hive.thriftserver.SparkSQLCLIDriver$.main(SparkSQLCLIDriver.scala:89)
	at org.apache.spark.sql.hive.thriftserver.SparkSQLCLIDriver.main(SparkSQLCLIDriver.scala)

第4个坑：这个坑下面的解决方法也可以，但最好的解决方法参考第6个坑的解决方法
参考：https://github.com/apache/spark/pull/4933
https://issues.apache.org/jira/browse/SPARK-6205
Exception in thread "main" java.lang.NoClassDefFoundError: org/w3c/dom/ElementTraversal
继续修改父pom文件，把scope由test改为runtime
<dependency>
  <groupId>xml-apis</groupId>
  <artifactId>xml-apis</artifactId>
  <version>1.4.01</version>
  <scope>runtime</scope>
</dependency>

第5个错：这个很熟悉，就是没指定master，在类启动的时候VM Options加上-Dspark.master=local
Exception in thread "main" org.apache.spark.SparkException: A master URL must be set in your configuration

第6个坑：
Exception in thread "main" java.lang.NoClassDefFoundError: org/eclipse/jetty/server/handler/ContextHandler
所有的groupId为org.eclipse.jetty的依赖，scope由provided改为compile
<dependency>
<groupId>org.eclipse.jetty</groupId>
<artifactId>jetty-util</artifactId>
<version>${jetty.version}</version>
<scope>compile</scope>
</dependency>
这个明明是有的 都能找到对应的.class文件了，意思是jar包冲突了，
https://github.com/SANSA-Stack/SANSA-Examples/commit/7049662750cddaa29b6d7700c3e6aa28ffc1d222这篇还没来得及看
这个链接的没看懂，不过不像是解决方法
解决方法：在启动类的配置选项中，将Include dependencies with "Provided" scope这个选项勾选上就可以解决了
通过这个解决方法，再回去看上面的坑，有些就是没有必要修改scope的了

第7个坑：
需要在hive-thriftserver这个项目里面新建一个resources目录并右键mark directory as resources，在里面放上hive-site.xml
然后需要重新编译，要不然编译目录target中是没有hive-site.xml的，就会导致读取的Hive元数据库是内置的Derby

至此，可以正常在idea中跑Hive SQL了






```