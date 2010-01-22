
ocv=/local_home/softs/prefix/opencv

h=$ocv/include/opencv/cxcore.h
egrep -v -e '[/][*][*][/]$' $h > ,,.h
\mv ,,.h $h
sed -i -e 's@^#ifndef _CXCORE_H_$@#define CVAPI(a) a/**/\n#define CV_DEFAULT(a) /**/\n\0@g' $h

h=$ocv/include/opencv/cv.h
egrep -v -e '[/][*][*][/]$' $h > ,,.h
\mv ,,.h $h
sed -i -e 's@^#ifndef _CV_H_$@#define CVAPI(a) a/**/\n#define CV_DEFAULT(a) /**/\n\0@g' $h



##define CVAPI(a) a/**/
##define CV_DEFAULT(a) = a/**/



rm -rf ,,
mkdir ,,
#(cd ../src/main/java && java -jar  ../../../jnaerator/jnaerator-0.9.2.jar -direct -noRuntime -noLibBundle -I$ocv/include/opencv -o ../../../jnaerator/,, -jar ../../../jnaerator/,,/opencv.jar -root fr.prima.jna.opencv $ocv/include/opencv/{cvaux,cv,cxcore,highgui,ml}.h )
(cd ../src/main/java && java -Xmx200m -jar  ../../../jnaerator/jnaerator-0.9.2.jar -nocpp -noRuntime -noLibBundle -noMangling -I$ocv/include/opencv -o ../../../jnaerator/,, -jar ../../../jnaerator/,,/opencv.jar -root fr.prima.jna.opencv $ocv/include/opencv/*.h )

(cd ,, && jar xf opencv.jar)
(cd ,, && find fr/prima/jna/opencv -type d -exec mkdir -p "../../src/main/java/{}" \; ) 
(cd ,, && find . -name \*.java -exec cp "{}" "../../src/main/java/{}" \; ) 


h=$ocv/include/opencv/cxcore.h
egrep -v -e '[/][*][*][/]$' $h > ,,.h
\mv ,,.h $h
h=$ocv/include/opencv/cv.h
egrep -v -e '[/][*][*][/]$' $h > ,,.h
\mv ,,.h $h


#BELOW are old problems with jnaerator 0.7.....

# compilation poblems
# * change cvtypes.CvtypesLibrary.rect_struct.ByReference[]
# * to cvtypes.CvtypesLibrary.CvHaarFeature.rect_struct.ByReference[]
# * and another one (same principle)

# linkage problems
# getLibraryPath("cxtypes", true, CxtypesLibrary.class) -> getLibraryPath("cxcore", true, CxtypesLibrary.class)

# pass-struct-by-parameter problem
# convert cvCreateImage(cxtypes.CxtypesLibrary.CvSize         size
# to      cvCreateImage(cxtypes.CxtypesLibrary.CvSize.ByValue size
#sed -i -e 's@, cxtypes.CxtypesLibrary.CvSize @, cxtypes.CxtypesLibrary.CvSize.ByValue @g' -e 's@(cxtypes.CxtypesLibrary.CvSize @(cxtypes.CxtypesLibrary.CvSize.ByValue @g' `fed ../src/main/java/fr/prima/jna/opencv java`
#sed -i -e 's@, cxtypes.CxtypesLibrary.CvRect @, cxtypes.CxtypesLibrary.CvRect.ByValue @g' -e 's@(cxtypes.CxtypesLibrary.CvRect @(cxtypes.CxtypesLibrary.CvRect.ByValue @g' `fed ../src/main/java/fr/prima/jna/opencv java`

#sed -i -e 's@	cxtypes.CxtypesLibrary.CvRect @	cxtypes.CxtypesLibrary.CvRect.ByValue @g' `fed ../src/main/java/fr/prima/jna/opencv java`
#sed -i -e 's@	cxtypes.CxtypesLibrary.CvScalar @	cxtypes.CxtypesLibrary.CvScalar.ByValue @g' `fed ../src/main/java/fr/prima/jna/opencv java`
