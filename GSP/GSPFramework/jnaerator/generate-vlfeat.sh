
# vlfeat needs to be “installed”
vl=/local_home/softs/sources/vlfeat
rm -rf ,,
mkdir ,,
(cd ../src/main/java && java -jar ../../../jnaerator/jnaerator-0.9.2.jar -nocpp -noRuntime -o ../../../jnaerator/,, -library VLFeat -root fr.prima.jna $vl/vl/*.h $vl/bin/glx/libvl.so)
(cd ,, && jar xf aib.jar)

sed -i -e 's@(nullfr[.]prima@(fr.prima@g' `find ,,/ -name \*.java`
(cd ,, && find fr/prima/jna/vlfeat -type d -exec mkdir -p "../../src/main/java/{}" \; ) 
(cd ,, && find . -name \*.java -exec cp "{}" "../../src/main/java/{}" \; ) 
