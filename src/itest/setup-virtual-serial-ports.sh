#!/bin/sh


# configure the directory /dev 
# in a way that the tool tty0tty 
# can add ttyS* devices without being superuser
if [ ! -w /dev ]
then 
    sudo chown root:$USER /dev
    sudo chmod 775 /dev
fi


if [ ! -e /usr/bin/tty0tty ]
then
    DIR_NAME=`dirname $0`
    cd ${DIR_NAME}
    WORKING_DIRECTORY=`pwd`
    sudo cp ${WORKING_DIRECTORY}/tty0tty/tty0tty /usr/bin/tty0tty
fi

killall tty0tty
tty0tty /dev/ttyS99 /dev/ttyS100 &
