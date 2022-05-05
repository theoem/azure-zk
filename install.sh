#!/bin/bash

apt update -y
apt install default-jdk -y

mkdir -p /data/zookeeper
wget -c https://dlcdn.apache.org/zookeeper/zookeeper-3.8.0/apache-zookeeper-3.8.0-bin.tar.gz -O - | sudo tar -xz -C /opt
ln -s /opt/apache-zookeeper-3.8.0-bin/ /opt/zookeeper

echo "Creating zoo.fg file"
echo 'tickTime=2000
dataDir=/data/zookeeper
clientPort=2181
maxClientCnxns=60
initLimit=10
syncLimit=5
server.1=10.0.0.4:2888:3888
server.2=10.0.0.5:2888:3888
server.3=10.0.0.6:2888:3888' > /opt/zookeeper/conf/zoo.cfg

grep `hostname -I` /opt/zookeeper/conf/zoo.cfg | egrep -o 'server.{0,2}' | awk -F . '{print $2}'

cd /opt/zookeeper && bin/zkServer.sh start
