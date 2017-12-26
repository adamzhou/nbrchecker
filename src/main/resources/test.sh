#!/bin/bash
#

if [ "a$1" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

if [ "a$2" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

if [ "a$3" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

if [ "a$4" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

echo $1
echo $2
echo $3
echo $4

WORK_DIR=$1
IDX_FILE=$2
DAT_FILE=$3
OUTPUT_PREFIX=$4
EXECUTOR=${WORK_DIR}"/nbrtool"
CMD=${EXECUTOR}" v "${IDX_FILE}" "${DAT_FILE}" "${OUTPUT_PREFIX}

date
export LD_LIBRARY_PATH=$WORK_DIR
export DISPLAY=:0.0
echo $CMD
eval $CMD
date
