#!/usr/bin/env bash
set -x
 
HADOOP_STREAMING_JAR=../opt/hadoop-3.2.1/share/hadoop/tools/lib/hadoop-streaming-3.2.1.jar

hdfs dfs -rm -r -skipTrash "$4"
 
hadoop jar $HADOOP_STREAMING_JAR \
    -file "$1" \
    -file "$2" \
    -mapper "python3 $1" \
    -reducer "python3 $2" \
    -input "$3" \
    -output "$4"
