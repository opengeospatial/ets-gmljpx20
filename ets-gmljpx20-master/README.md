## Conformance Test Suite - GML in JPEG 2000

### Scope

This test suites checks JPEG 2000 image data for conformance against _OGC GML 
in JPEG 2000 (GMLJP2) Encoding Standard Part 1_ [OGC 08-085r4](http://docs.opengeospatial.org/is/08-085r4/08-085r4.html).
The nominal conformance target is a software component (an application or library) 
that can produce JPEG 2000 codestreams containing supplementary geospatial metadata.

Visit the [project documentation website](http://opengeospatial.github.io/ets-gmljpx20/) 
for more information, including the API documentation.'


### Requirements

The test suite uses the Geospatial Data Abstraction Library ([GDAL](http://www.gdal.org/)) 
along with its Java bindings. In a Windows environment the installation directory 
must be added to the `PATH` environment variable; in a UNIX-like environment the 
native binaries must be found in the shared library path (`LD_LIBRARY_PATH`).


### How to run the tests
There are several options for executing the test suite.

#### 1. OGC test harness

Use [TEAM Engine](https://github.com/opengeospatial/teamengine), the official OGC test harness.
The latest test suite release should be available at the [beta testing facility](http://cite.opengeospatial.org/te2/). 
You can also [build and deploy](https://github.com/opengeospatial/teamengine) the test 
harness yourself and use a local installation.

#### 2. Integrated development environment (IDE)
Use a Java IDE such as Eclipse, NetBeans, or IntelliJ. Clone the repository and build the project.

Set the main class to run: `org.opengis.cite.gmljpx20.TestNGController`

Arguments: The first argument must refer to an XML properties file containing the 
required test run argument (a reference to a JPEG 2000 image resource). If not 
specified, the default location at `${user.home}/test-run-props.xml` will be used.
   
You can modify the sample file in `src/main/config/test-run-props.xml`

```xml   
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties version="1.0">
  <comment>Test run arguments (ets-gmljpx20)</comment>
  <entry key="iut">https://github.com/bitsgalore/jp2kMagic/raw/master/sampleImages/balloon.jp2</entry>
</properties>
```

The TestNG results file (`testng-results.xml`) will be written to a subdirectory
in `${user.home}/testng/` having a UUID value as its name.

#### 3. Command shell (console)

One of the build artifacts is an "all-in-one" JAR file that includes the test 
suite and all of its dependencies; this makes it very easy to execute the test 
suite in a command shell:

`java -jar ets-gmljpx20-${version}-aio.jar [-o|--outputDir $TMPDIR] [test-run-props.xml]`


### How to contribute

If you would like to get involved, you can:

* [Report an issue](https://github.com/opengeospatial/ets-gmljpx20/issues) such as a defect or 
an enhancement request
* Help to resolve an [open issue](https://github.com/opengeospatial/ets-gmljpx20/issues?q=is%3Aopen)
* Fix a bug: Fork the repository, apply the fix, and create a pull request
* Add new tests: Fork the repository, implement (and verify) the tests on a new topic branch, 
and create a pull request
