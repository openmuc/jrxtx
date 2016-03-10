#!/bin/sh

DIR_NAME=`dirname $0`
cd $DIR_NAME/../../
PROJECT_NAME=${PWD##*/}
cd doc/userguide

if [ ! -e ~/.asciidoc/themes/openmuc ];
then
    mkdir -p ~/.asciidoc/themes/
    ln -s `pwd`/themes/openmuc ~/.asciidoc/themes/openmuc
fi


asciidoc -a icons -a iconsdir=/etc/asciidoc/images/icons -a toc2 -a theme=openmuc -a toc ${PROJECT_NAME}-doc.txt
cp ${PROJECT_NAME}-doc.html ${PROJECT_NAME}-doc-frames.html
asciidoc -a icons -a iconsdir=/etc/asciidoc/images/icons -a toc1 -a theme=openmuc -a toc ${PROJECT_NAME}-doc.txt
