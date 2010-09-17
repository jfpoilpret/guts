#!/bin/bash

SITE="https://kenai.com/website/guts"
DISK="/media/webdav"

sudo apt-get install davfs2

sudo mount.davfs "$SITE" "$DISK"




