# Obstacle detection visualization

Visualization endpoint for the obstacle detection system. Creates a TCP server at port 6871 to which the visualization data can be sent. 

The build system used is Maven. The following tasks are relevant:

* `mvn package` will create an executable jar file
* `mvn exec:java` will start the application

Requires java and maven to be installed.

## Troubleshooting

> The build fails with "class file has wrong version 52.0, should be 50.0"

On Mac OS X, this was caused because Maven was using Java 1.6 but the dependencies were built for 1.8. I solved this by manually pointing maven to the correct Java version, i.e.

    JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home" mvn package

Note that on OS X you can find out the correct `JAVA_HOME` by running

    /usr/libexec/java_home -v 1.8
