#!/bin/bash

#This script is used to compile SPF. Copy it to jpf-symbc, and run it from there.

CURDIR=`pwd`
JPFDIR="${CURDIR}/.."
COREDIR="${JPFDIR}/jpf-core"
CP=""

## building JPF (if necessary)
for x in `ls ${COREDIR}/build/*.jar` 
do
    CP=${CP}:$x
done

#### edit your path to junit
CP=$CP:${COREDIR}/lib/junit-4.10.jar

LIBSDIR=${CURDIR}/lib
#### general jars
for x in `ls ${LIBSDIR}/*.jar` 
do
     CP=${CP}:$x
done

#### prepare the directory to compile
rm -r ${CURDIR}/build
mkdir ${CURDIR}/build
mkdir ${CURDIR}/build/annotations
mkdir ${CURDIR}/build/classes
mkdir ${CURDIR}/build/examples
mkdir ${CURDIR}/build/main
mkdir ${CURDIR}/build/peers
mkdir ${CURDIR}/build/tests

CP=${CP}:${CURDIR}'/build/main'

find src/annotations -name "*.java" | \
 xargs javac -g -cp $CP -d ${CURDIR}/build/annotations

CP=${CP}:${CURDIR}/build/annotations/

find src/main -name "*.java" | \
 xargs javac -g -cp $CP -d ${CURDIR}/build/main

find src/classes -name "*.java" | \
 xargs javac -g -cp $CP -d ${CURDIR}/build/classes

CP=${CP}:${CURDIR}/build/classes/

find src/peers -name "*.java" | \
 xargs javac -g -cp $CP -d ${CURDIR}/build/peers

CP=${CP}:${CURDIR}/build/peers/

find src/examples -name "*.java" | \
 xargs javac -g -cp $CP -d ${CURDIR}/build/examples

find src/tests -name "*.java" | \
 xargs javac -g -cp $CP -d ${CURDIR}/build/tests

cd build
jar cf jpf-symbc.jar main peers
jar cf jpf-symbc-classes.jar classes annotations
jar cf jpf-symbc-annotations.jar annotations
