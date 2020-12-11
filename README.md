jmtrace编译说明

1.到达jmtrace根目录

2.执行命令：mvn clean package，默认jar包生成在/target下

3.java -javaagent:target/jmtrace-1.0-SNAPSHOT-jar-with-dependencies.jar [某一个class/-jar 某一个jar包]


如：

java -javaagent:target/jmtrace-1.0-SNAPSHOT-jar-with-dependencies.jar helloworld

java -javaagent:target/jmtrace-1.0-SNAPSHOT-jar-with-dependencies.jar -jar test.jar

4.随后屏幕中开始打印数据