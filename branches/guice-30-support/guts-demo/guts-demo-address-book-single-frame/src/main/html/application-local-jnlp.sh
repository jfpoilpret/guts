#!/bin/bash

#
#	${mavenStamp}
#

THIS_PATH=$(dirname $(readlink -f $0))

echo "### clear java web start cache"

javaws -uninstall

sleep 1s

###

echo "### start application via web start"

TEST="${jnlpAppTest}"

JNLP="$THIS_PATH/jnlp/$TEST"

javaws $JNLP

sleep 1s

###
