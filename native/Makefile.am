###########################################################################
#	Copyright 1997-2009 by Trent Jarvi and others
#	Copyright 1998 Kevin Hester, kevinh@acm.org
#	Copyright 2016 Fraunhofer ISE and others
#	
#	This file is part of jRxTx.
#	jRxTx is a fork of RXTX originally maintained by Trent Jarvi.
#	
#	jRxTx is free software: you can redistribute it and/or modify
#	it under the terms of the GNU Lesser General Public License as published by
#	the Free Software Foundation, either version 2.1 of the License, or
#	(at your option) any later version.
#	
#	jRxTx is distributed in the hope that it will be useful,
#	but WITHOUT ANY WARRANTY; without even the implied warranty of
#	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#	GNU Lesser General Public License for more details.
#	
#	You should have received a copy of the GNU Lesser General Public License
#	along with jRxTx.  If not, see <http://www.gnu.org/licenses/>.
############################################################################
srcdir = @srcdir@
abs_srcdir=$(TOP)/@srcdir@
VPATH = @srcdir@

AUTOMAKE_OPTIONS = foreign
INCLUDES= -I$(TOP) -I$(DEST) -I. $(JAVAINCLUDE) $(JAVANATINC)

CLASSTOP=gnu

# linking java-jdk headers to lib
AM_LDFLAGS = -l$(JAVANATINC) -l$(JAVANATINC)

####################### tools
LIBTOOLC=$(GLIBTOOL) --mode=compile $(CC) $(INCLUDES) $(CPPFLAGS) $(CFLAGS) $(VERBOSE_IOEXCEPTIONS) -c
LIBTOOL_INST=$(GLIBTOOL) --mode=install $(INSTALL_PROGRAM)
####################### dirs
SPATH = $(abs_srcdir)/src
DEST=$(target_triplet)

# for SCO builds everything else is currently using lib%.la below
# I'm thinking about taking libtool out completely.  See also Mac OS X builds.
$(target_triplet)/lib%.so: $(SPATH)/%Imp.@EXTENSION@
	$(COMPILE) $(SPATH)/$*Imp.@EXTENSION@ -o $(TOP)/$(DEST)/$*Imp.o
	$(LINK) -G -o lib$*.so $(TOP)/$(DEST)/$*Imp.o

#  compile src/SerialImp.c and similar files into libraries.
#  LIBTOOL_FIX handles problems encountered on some OS's.  See configure.ac
#  If a LIBTOOL_FIX is added be sure to provide a trailing ';' in configure.ac

$(target_triplet)/librxtx%.@LIB_EXTENSION@: $(SPATH)/%Imp.c $(SPATH)/%Imp.h
	(													\
	$(LIBTOOL_FIX)										\
	cd $(DEST); 										\
	$(LIBTOOLC) $(SPATH)/$*Imp.@EXTENSION@ -o			\
		$(TOP)/$(DEST)/$*Imp.@OBJECT_EXTENSION@;		\
	if [ "$*" = "Serial" ]; then 						\
		$(LIBTOOLC) $(SPATH)/fuserImp.@EXTENSION@ -o	\
			$(TOP)/$(DEST)/fuserImp.@OBJECT_EXTENSION@;	\
		@LINK@ $(TOP)/$(DEST)/$*Imp.@OBJECT_EXTENSION@  \
			$(TOP)/$(DEST)/fuserImp.@OBJECT_EXTENSION@; \
	else												\
		@LINK@ $(TOP)/$(DEST)/$*Imp.@OBJECT_EXTENSION@; \
	fi;													\
	)

#  install librxtxSerial.so into the proper directory and copy $(JARTARGET) to its
#  proper location
install: all
	@$(LIBTOOL_INST) \
	`for i in $(TARGETLIB);do \
		if [ -f $$i ];then \
			echo $$i; \
		fi; \
	done` $(RXTX_PATH)
	$(INSTALL_PROGRAM) $(JARTARGET) $(JHOME)/

uninstall:
	cd $(RXTX_PATH) && $(RM) librxtx*
	$(RM) $(JHOME)/$(JARTARGET)

clean-generic:
	rm -rf $(DEST) $(TOP)/$(CLASSTOP)
	-test -z "$(CLEANFILES)" || rm -f $(CLEANFILES)

all: $(TARGETLIB)

################  WIN32 CrossCompiling from here down #######################

$(DEST)/rxtxSerial.dll: $(TOP)/Makefile
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/SerialImp.c -o $(DEST)/SerialImp.o
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/termios.c -o $(DEST)/termios.o
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/fixup.c -o $(DEST)/fixup.o
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/init.cc -o $(DEST)/init.o
	ld --base-file $(DEST)/Serial.base --dll -o $(DEST)/rxtxSerial.dll $(DEST)/termios.o $(DEST)/SerialImp.o $(DEST)/fixup.o $(DEST)/init.o -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmingw32 -lmoldname -lcrtdll -e _dll_entry@12 --base-file $(DEST)/Serial.base
	 echo EXPORTS >$(DEST)/Serial.def;for i in `nm $(DEST)/rxtxSerial.dll | grep "T _Java"|cut -b 13-`;do echo -n $$i|sed s#@.*##;echo "="$$i;done >> $(DEST)/Serial.def
	dlltool -b$(DEST) --as=as --dllname $(DEST)/rxtxSerial.dll --def $(DEST)/Serial.def --base-file $(DEST)/Serial.base --output-exp $(DEST)/Serial.exp
	ld --base-file $(DEST)/Serial.base $(DEST)/Serial.exp -dll -o $(DEST)/rxtxSerial.dll $(DEST)/termios.o $(DEST)/SerialImp.o $(DEST)/fixup.o $(DEST)/init.o -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmingw32 -lmoldname -lcrtdll -e _dll_entry@12 --base-file $(DEST)/Serial.base
	dlltool -b$(DEST) --as=as --dllname $(DEST)/rxtxSerial.dll --def $(DEST)/Serial.def --base-file $(DEST)/Serial.base --output-exp $(DEST)/Serial.exp
	ld $(DEST)/Serial.exp -dll -o $(DEST)/rxtxSerial.dll $(DEST)/termios.o $(DEST)/SerialImp.o $(DEST)/fixup.o $(DEST)/init.o -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmingw32 -lmoldname -lcrtdll -e _dll_entry@12 --base-file $(DEST)/Serial.base

$(DEST)/rxtxParallel.dll: $(TOP)/Makefile
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/ParallelImp.c -o $(DEST)/ParallelImp.o
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/termios.c -o $(DEST)/termios.o
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/fixup.c -o $(DEST)/fixup.o
	gcc -DWIN32 -D __int64="long long" -mno-fp-ret-in-387 -b$(target_alias) -I $(TOP) -I $(DEST) -I . $(WINDOWS_JAVA_INCLUDE) -I $(SPATH) -Wall -c $(SPATH)/init.cc -o $(DEST)/init.o
	ld --base-file $(DEST)/Parallel.base --dll -o $(DEST)/rxtxParallel.dll $(DEST)/termios.o $(DEST)/ParallelImp.o $(DEST)/fixup.o $(DEST)/init.o -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmingw32 -lmoldname -lcrtdll -e _dll_entry@12 --base-file $(DEST)/Parallel.base
	 echo EXPORTS >$(DEST)/Parallel.def;for i in `nm $(DEST)/rxtxParallel.dll | grep "T _Java"|cut -b 13-`;do echo -n $$i|sed s#@.*##;echo "="$$i;done >> $(DEST)/Parallel.def
	dlltool -b$(DEST) --as=as --dllname $(DEST)/rxtxParallel.dll --def $(DEST)/Parallel.def --base-file $(DEST)/Parallel.base --output-exp $(DEST)/Parallel.exp
	ld --base-file $(DEST)/Parallel.base $(DEST)/Parallel.exp -dll -o $(DEST)/rxtxParallel.dll $(DEST)/termios.o $(DEST)/ParallelImp.o $(DEST)/fixup.o $(DEST)/init.o -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmingw32 -lmoldname -lcrtdll -e _dll_entry@12 --base-file $(DEST)/Parallel.base
	dlltool -b$(DEST) --as=as --dllname $(DEST)/rxtxParallel.dll --def $(DEST)/Parallel.def --base-file $(DEST)/Parallel.base --output-exp $(DEST)/Parallel.exp
	ld $(DEST)/Parallel.exp -dll -o $(DEST)/rxtxParallel.dll $(DEST)/termios.o $(DEST)/ParallelImp.o $(DEST)/fixup.o $(DEST)/init.o -luser32 -lgdi32 -lcomdlg32 -lkernel32 -ladvapi32 -lmingw32 -lmoldname -lcrtdll -e _dll_entry@12 --base-file $(DEST)/Parallel.base

