# 下载
```
https://archive.apache.org/dist/spark/spark-1.5.2/spark-1.5.2.tgz
```

```
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
2.修改$SPARK_HOME/make-distribution.sh脚本
(1)显式指定一些版本信息，跳过编译时的一些比较耗时的版本检查，提高编译效率
显式指定一下版本信息
VERSION=1.5.2
SCALA_VERSION=2.11
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
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=4G -XX:ReservedCodeCacheSize=2G"

3.修改scala的版本(这一步不用执行，spark1.5.2默认scala2.10)


4.编译期间需要下载(这个是在$SPARK_HOME/build/mvn的脚本中指定的)
这个zinc的版本和scala的版本要看一下mvn这个脚本确认一下
https://downloads.lightbend.com/zinc/0.3.5.3/zinc-0.3.5.3.tgz
https://downloads.lightbend.com/scala/2.10.4/scala-2.10.4.tgz
有时候下载比较慢，提前下载好之后放在$SPARK_HOME/build下面就行

5.编译：
./make-distribution.sh --name 2.6.0-cdh5.16.2 --tgz -Dhadoop.version=2.6.0-cdh5.16.2 -Dscala.version=2.10.4 -Phadoop-2.6 -Phive -Phive-thriftserver -Pyarn

这个编译没有什么坑
```