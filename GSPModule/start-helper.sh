#/bin/bash

export LD_LIBRARY_PATH=/usr/lib:/usr/local/lib/:/home/twilight/app/ServiceVideo/build/:./build
java -cp  ../GSPFramework/target/dependency/*:../GSPFramework/target/*:../GSPBaseutils/target/* fr.prima.gsp.Launcher "$@"
