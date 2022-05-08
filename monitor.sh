#!/bin/bash

old_requests=0
while true
do
  date=`date "+%T"`
  requests=`echo mntr | nc localhost 2181 | grep -E "zk_cnt_$1_read_per_namespace|zk_cnt_$1_write_per_namespace" | awk '{sum+=$2;} END{print sum;}'`
  if [ -z "$requests" ]
  then
	  sleep 1
  else
	  echo $date,$(($requests-$old_requests))
	  old_requests=$requests
	  sleep 1
  fi
done
