#!/bin/bash

#
#	${mavenStamp}
#


echo "### clear java web start cache"

javaws -uninstall

sleep 1s


THIS_PATH=$(dirname $(readlink -f $0))

echo "### start applet in browser"

URL="file://$THIS_PATH/applet-local-jnlp.html"

google-chrome $URL &

#firefox $URL &

sleep 1s

