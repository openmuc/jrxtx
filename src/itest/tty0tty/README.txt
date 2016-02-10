
tty0tty - linux null modem emulator v1.2 

   Copyright (c) : 2013  Luis Claudio Gambôa Lopes and Maximiliano Pin max.pin@bitroit.com

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   For more details, please have a look in the original tty0tty ZIP folder.
   

Fraunhofer ISE uses the pts (unix98) version of tty0tty.

When run connect two pseudo-ttys and show the connection names.
  - To run:
         ./tty0tty   
         
         (/dev/pts/1) <=> (/dev/pts/2) 
         
  - Or, with fix aliases, run:       
         ./tty0tty /dev/ttyS99 /dev/ttyS100 
        
         (/dev/ttyS99) <=> (/dev/ttyS100) 

  the connection is (no handshaking!):
  
  TX -> RX
  RX <- TX 	


Sources from https://github.com/freemed/tty0tty.git
