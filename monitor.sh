#!/bin/bash

old_requests=0
old_date=`date '+%s'`
elapsed_time=0
request_diff=0

zero_counter=0

while true
do
  date=`date '+%s'`
  requests=`echo mntr | nc localhost 2181 | grep -E "zk_cnt_$1_read_per_namespace|zk_cnt_$1_write_per_namespace" | awk '{sum+=$2;} END{print sum;}'`

  if [ -z "$requests" ]
  then
	  sleep 1
  else
	  elapsed_time=$(($elapsed_time+$date-$old_date))
	  request_diff=$(($requests-$old_requests))
	  echo $elapsed_time,$request_diff

	  old_requests=$requests
	  old_date=$date
	  sleep 1
  fi

  if [ "$request_diff" -eq "0" ]
  then
	  zero_counter=$(($zero_counter+1))
  else
	  zero_counter=0
  fi

  if [ "$zero_counter" -gt "10" ]
  then
	  exit 1
  fi
done
