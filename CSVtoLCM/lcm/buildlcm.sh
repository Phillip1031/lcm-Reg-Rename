
#!/bin/sh

# generate lcm files for c++, python, and java
lcm-gen -x --cpp-std=c++11 --cpp-hpath=cpp -p --ppath=python -j --jpath=ja *.lcm

# create java jars for lcm-spy
LCM_JAR=`pkg-config --variable=classpath lcm-java`
if [ $? != 0 ] ; then
    if [ -e /usr/local/share/java/lcm.jar ] ; then
        LCM_JAR=/usr/local/share/java/lcm.jar
    else
        if [ -e ../../lcm-java/lcm.jar ] ; then
            LCM_JAR=../../lcm-java/lcm.jar
        fi
    fi
fi

cd ja
javac -cp $LCM_JAR LcmGui/*.java

jar cf lcm_gui.jar LcmGui/*.class
