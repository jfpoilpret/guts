#!/bin/bash

DIR="guts-test"
URL="https://svn.kenai.com/svn/guts~code/branches/guice-30-support"
PROJ="guts-base"

echo "making test dir"
cd ~
mkdir "$DIR"
cd "$DIR"

echo "running svn checkout"
svn co "$URL/$PROJ" > svn.log

cd "$PROJ"

echo "running mvn test using guice 2.0 (release)"
mvn test > mvn.1.log

echo "running mvn test using guice 3.0 (snapshot)"
mvn test -P guice-snapshot > mvn.2.log
