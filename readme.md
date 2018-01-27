# Info

- [Install the cytoscape plugin here](http://apps.cytoscape.org/apps/ictnet2)
- [Background Paper](https://www.ncbi.nlm.nih.gov/pubmed/26834985)

# Dependencies

- [Maven](https://www.vultr.com/docs/how-to-install-apache-maven-on-ubuntu-16-04)
- [Cytoscape](http://www.cytoscape.org/download.php)
    - [ubuntu install instructions](http://www.network-science.org/cytoscape-download-install-ubuntu-linux-unix.html)

# Credentials

To connect to the database, you'll need the `credentials.properties` file, e.g:

```
CON_URL=jdbc:mysql://some.domain:1234/
CON_NAME=wileycoyote
CON_PWD=loyalacmecustomer
CON_DATABASE=foo
```

This should be stored at `src/main/resources/credentials.properties`

# Build

To compile:

```
18-01-17[20:05:55]:iCTNet2-raw:0$mvn package
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building ictnet 1.0.21
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:3.0.2:resources (default-resources) @ ictnet-cyaction-app ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 2 resources
[INFO] 
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ ictnet-cyaction-app ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:3.0.2:testResources (default-testResources) @ ictnet-cyaction-app ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/sjezewski/workspace/baranzini/iCTNet2-raw/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ ictnet-cyaction-app ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.7.1:test (default-test) @ ictnet-cyaction-app ---
[INFO] Surefire report directory: /home/sjezewski/workspace/baranzini/iCTNet2-raw/target/surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
There are no tests to run.

Results :

Tests run: 0, Failures: 0, Errors: 0, Skipped: 0

[INFO] 
[INFO] --- maven-bundle-plugin:2.3.7:bundle (default-bundle) @ ictnet-cyaction-app ---
[WARNING] Bundle org.cytoscape.ictnet2:ictnet-cyaction-app:bundle:1.0.21 : Instructions in Export-Package that are never used: org\.cytoscape\.ictnet2
Classpath: Jar:.,Jar:org.osgi.core,Jar:service-api,Jar:swing-application-api,Jar:work-swing-api,Jar:application-api,Jar:session-api,Jar:vizmap-api,Jar:property-api,Jar:pax-logging-api,Jar:work-api,Jar:model-api,Jar:event-api,Jar:swing-util-api,Jar:slf4j-jdk14,Jar:slf4j-api,Jar:core-task-api,Jar:viewmodel-api,Jar:layout-api,Jar:presentation-api,Jar:mysql-connector-java

[WARNING] Bundle org.cytoscape.ictnet2:ictnet-cyaction-app:bundle:1.0.21 : Superfluous export-package instructions: [org.cytoscape.ictnet2]
[WARNING] Bundle org.cytoscape.ictnet2:ictnet-cyaction-app:bundle:1.0.21 : Unknown directive version: in Import-Package, allowed directives are resolution:, and 'x-*'.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.230 s
[INFO] Finished at: 2018-01-17T20:06:15-08:00
[INFO] Final Memory: 16M/467M
[INFO] ------------------------------------------------------------------------

```

# Run

## Load the Plugin

Start cytoscape:

```
Cytoscape &
```

Then:

- navigate to Apps -> App Manager -> Install From File ...
- then navigate to this repo directory, then to `/target`
- and select the latest build (note the build number from your output above), e.g. `ictnet-cyaction-app-1.0.21.jar`

## Use the Plugin:

- Navigate to the `iCTNet Panel` under the Control Panel (the lefthand pane)
- Select a disease
- Scroll down and click `Load`
