
# Setup

     export PYTHONPATH=../python
     export LD_LIBRARY_PATH=../build/cpp
     export LD_PRELOAD=/usr/lib/libpython2.7.so
     GO="java -cp  ../../GSPFramework/target/dependency/*:../../GSPFramework/target/*:../build/java/GSPExampleJava.jar fr.prima.gsp.Launcher"    


# Java/core error cases

     $GO generic-duplicate-namespace.xml
     $GO java-missing-type.xml
     $GO java-missing-param-and-duplicate-name.xml
     $GO generic-unreplaced-variable.xml
     $GO generic-unreplaced-variable.xml da.plop=t

# Python error cases



