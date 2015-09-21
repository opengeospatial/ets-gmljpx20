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


### How to contribute

If you would like to get involved, you can:

* [Report an issue](https://github.com/opengeospatial/ets-gmljpx20/issues) such as a defect or 
an enhancement request
* Help to resolve an [open issue](https://github.com/opengeospatial/ets-gmljpx20/issues?q=is%3Aopen)
* Fix a bug: Fork the repository, apply the fix, and create a pull request
* Add new tests: Fork the repository, implement (and verify) the tests on a new topic branch, 
and create a pull request
