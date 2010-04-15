

it:
	make libdemo.so # && objdump -t libdemo.so

libdemo.so: demo.cpp demo.h framework.h
	gcc -lstdc++ --shared -fPIC demo.cpp -o libdemo.so

clean:
	rm -f libdemo.so


