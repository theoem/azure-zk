#!/bin/bash

apt-get update && apt-get install -y default-jdk
sleep 60
apt-get update && apt-get install -y default-jdk

mkdir -p /data/zookeeper
wget -c https://dlcdn.apache.org/zookeeper/zookeeper-3.8.0/apache-zookeeper-3.8.0-bin.tar.gz -O - | sudo tar -xz -C /opt
ln -s /opt/apache-zookeeper-3.8.0-bin/ /opt/zookeeper

wget https://raw.githubusercontent.com/theoem/azure-zk/main/monitor.sh -P /opt/zookeeper

echo "Creating zoo.fg file"
echo 'tickTime=2000
dataDir=/data/zookeeper
clientPort=2181
maxClientCnxns=0
initLimit=10
syncLimit=5
4lw.commands.whitelist=*
server.1=10.0.0.4:2888:3888
server.2=10.0.0.5:2888:3888
server.3=10.0.0.6:2888:3888' > /opt/zookeeper/conf/zoo.cfg

sed -i 's/<!--appender name="ROLLINGFILE"/<appender name="ROLLINGFILE"/g' /opt/zookeeper/conf/logback.xml
sed -i 's/appender-ref ref="CONSOLE"/appender-ref ref="ROLLINGFILE"/g' /opt/zookeeper/conf/logback.xml
sed -i 's/appender--/appender/g' /opt/zookeeper/conf/logback.xml
sed -i 's/!--property name="zookeeper.tracelog.dir"/property name="zookeeper.tracelog.dir"/g' /opt/zookeeper/conf/logback.xml
sed -i 's/"zookeeper.log.dir" value="."/"zookeeper.log.dir" value="\/var\/log"/g' /opt/zookeeper/conf/logback.xml

grep `hostname -I` /opt/zookeeper/conf/zoo.cfg | egrep -o 'server.{0,2}' | awk -F . '{print $2}' >/data/zookeeper/myid

cd /opt/zookeeper && bin/zkServer.sh start
