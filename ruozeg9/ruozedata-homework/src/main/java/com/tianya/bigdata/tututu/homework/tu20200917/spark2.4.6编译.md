
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

5.编译期间需要下载
https://downloads.lightbend.com/zinc/0.3.15/zinc-0.3.15.tgz
https://downloads.lightbend.com/scala/2.11.12/scala-2.11.12.tgz
有时候下载比较慢，提前下载好之后放在$SPARK_HOME/build下面就行

```