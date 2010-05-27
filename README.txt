
= Visualizing a pipeline =
==========================

== Get dotty, etc. ==
aptitude install libsaxonb-java graphviz

== View the pipeline ==
# just run:
PipelineViewer/view-pipeline.sh your-pipeline.xml

== Export as a svg file ==
PipelineViewer/view-pipeline.sh   -svg output.svg   your-pipeline.xml



= Building the java executable =
================================

== Get Maven 2 ==
aptitude install maven2../PipelineViewer/view-pipeline.sh pipeline-pure-java-6-factory.xml


== Compile and setup for execution ==
cd GSPFramework
yes | unzip bridj-binaries.zip
mvn dependency:copy-dependencies install jar:jar
rm -f target/lib && ln -s dependency target/lib
cd ..



===========================
Then see GSPExample folder.
===========================




DEPRECATED COMMENTS
======================

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

END OF DEPRECATED COMMENTS
=============================
