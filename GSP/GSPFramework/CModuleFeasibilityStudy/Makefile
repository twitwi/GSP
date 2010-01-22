
JNA=jna-3.2.4.jar
CP=${JNA}:.

it:
	make libadder.so && objdump -t libadder.so
	make Main.class
	java -cp ${CP} -Djna.library.path=/usr/lib:/lib:. Main

libadder.so: adder.cpp framework.c adder.h framework.h
	gcc -lstdc++ --shared -fPIC adder.cpp framework.c -o libadder.so

Main.class: Main.java ${JNA}
	javac -cp ${CP} Main.java


clean:
	rm -f libadder.so
	rm -f *.class

${JNA}:
	wget 'http://download.java.net/maven/2/net/java/dev/jna/jna/3.2.4/jna-3.2.4.jar'

