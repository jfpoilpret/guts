#!/bin/bash

#
#	http://www.jroller.com/holy/entry/releasing_a_project_to_maven
#	http://maven.apache.org/plugins/maven-deploy-plugin/deploy-file-mojo.html
#

file_bin="guice-with-deps.jar"
file_src="guice-snapshot-src.jar"

repositoryId="sonatype-nexus-snapshots"
url="https://oss.sonatype.org/content/repositories/snapshots/"

groupId="net.guts"
artifactId="guts-guice"
version="3.0.0-SNAPSHOT"

mvn deploy:deploy-file \
  -Durl="$url" \
  -DrepositoryId="$repositoryId" \
  -Dfile="$file_bin" \
  -DgroupId="$groupId" \
  -DartifactId="$artifactId" \
  -Dversion="$version" \
  -Dpackaging="jar" \
  -DgeneratePom="true" \
  -DuniqueVersion="false"
  ""

mvn deploy:deploy-file \
   -Durl="$url" \
   -DrepositoryId="$repositoryId" \
   -Dfile="$file_src" \
   -DgroupId="$groupId" \
   -DartifactId="$artifactId" \
   -Dversion="$version" \
   -Dpackaging="java-source" \
   -Dclassifier=sources \
   -DgeneratePom="false" \
   -DuniqueVersion="false"
   ""
