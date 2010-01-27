
= Building the java executable =
================================

== Get Maven 2 ==
aptitude install maven2

== Compile and setup for execution ==
cd GSPFramework
mvn dependency:copy-dependencies jar:jar
rm -f target/lib && ln -s dependency target/lib
cd ..



= Running the interactive GSPFactory =
======================================

== Important things to know ==
The interactive java client requires a connection to a video service (to be changed).
The video service is specified using a service filter on the command line (the first found is taken).

The client will list all .xml files (pipelines) in the current working directory.

The client will load dynamic libraries from the current directory.

Tip: use symbolic links to your .xml files and your dynamic libraries.


== Launching the GSPFactory ==
# you must provide a service filter
java -jar where/is/GSPFramework/target/gsp-framework-1.0-SNAPSHOT.jar 'and(nameIs("ServiceVideo"), hostPrefixIs("prometheus"))'

# once the connection is done to the service video, the interface should show you the list of .xml files, click on any to instantiate the corresponding pipeline

