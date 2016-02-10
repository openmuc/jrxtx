These integration tests use virtual serial ports under Linux. In order
to run these integration tests the null modem emulator tty0tty has to
be used to create two serial ports that are connected (one for the
server and one for the client).

Steps:

- first change the group ownership of the /dev folder so that you as a
  regular user can create the serial ports.

- run tty0tty to create the two connected virtual serial ports:
  tty0tty /dev/ttyS99 /dev/ttyS100 .

- run the itest using "gradle itest"
