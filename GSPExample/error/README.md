
# Setup

     export PYTHONPATH=../python
     export LD_LIBRARY_PATH=../build/cpp
     export LD_PRELOAD=/usr/lib/libpython2.7.so
     GO="java -cp  ../../GSPFramework/target/dependency/*:../../GSPFramework/target/*:../build/java/GSPExampleJava.jar fr.prima.gsp.Launcher"    


# Java and core error cases

     $GO generic-duplicate-namespace.xml
     $GO java-missing-type.xml
     $GO java-missing-param-and-duplicate-name.xml
     $GO generic-unreplaced-variable.xml
     $GO generic-unreplaced-variable.xml da.plop=t

# C++ error cases

     $GO cpp-missing-library.xml
     $GO cpp-missing-class.xml
     $GO cpp-missing-param.xml
     $GO cpp-wrong-param.xml

# Python error cases

     $GO python-missing-file.xml 
     $GO python-missing-class.xml 
     $GO python-missing-param.xml 
     $GO python-wrong-param-type.xml 


