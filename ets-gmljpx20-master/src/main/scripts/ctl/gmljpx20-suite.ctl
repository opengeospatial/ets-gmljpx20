<?xml version="1.0" encoding="UTF-8"?>
<ctl:package xmlns:ctl="http://www.occamlab.com/ctl"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:tns="http://www.opengis.net/cite/gmljpx20"
  xmlns:saxon="http://saxon.sf.net/"
  xmlns:tec="java:com.occamlab.te.TECore"
  xmlns:tng="java:org.opengis.cite.gmljpx20.TestNGController">

  <ctl:function name="tns:run-ets-gmljpx20">
		<ctl:param name="testRunArgs">A Document node containing test run arguments (as XML properties).</ctl:param>
    <ctl:param name="outputDir">The directory in which the test results will be written.</ctl:param>
		<ctl:return>The test results as a Source object (root node).</ctl:return>
		<ctl:description>Runs the gmljpx20 ${version} test suite.</ctl:description>
    <ctl:code>
      <xsl:variable name="controller" select="tng:new($outputDir)" />
      <xsl:copy-of select="tng:doTestRun($controller, $testRunArgs)" />
    </ctl:code>
	</ctl:function>

   <ctl:suite name="tns:ets-gmljpx20-${version}">
     <ctl:title>Conformance Test Suite - GML in JPEG 2000</ctl:title>
     <ctl:description>Checks JPEG 2000 codestreams for conformance against "OGC GML in JPEG 
     2000 (GMLJP2) Encoding Standard Part 1" (OGC 08-085r4) and related specifications.</ctl:description>
     <ctl:starting-test>tns:Main</ctl:starting-test>
   </ctl:suite>
 
   <ctl:test name="tns:Main">
      <ctl:assertion>The test subject satisfies all applicable constraints.</ctl:assertion>
	  <ctl:code>
        <xsl:variable name="form-data">
           <ctl:form method="POST" width="800" height="600" xmlns="http://www.w3.org/1999/xhtml">
             <h2>Conformance Test Suite - GML in JPEG 2000</h2>
             <div style="background:#F0F8FF" bgcolor="#F0F8FF">
               <p>The test subject is checked against the following specifications:</p>
               <ul>
                 <li><a href="http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>: 
                 OGC GML in JPEG 2000 (GMLJP2) Encoding Standard Part 1: Core, Version 2.0</li>
                 <li><a href="http://docs.opengeospatial.org/is/12-108/12-108.html">OGC 12-108</a>: 
                 OGC GML Application Schema - Coverages - JPEG2000 Coverage Encoding Extension, Version 1.0</li>
                 <li><a href="https://portal.opengeospatial.org/files/?artifact_id=48553">OGC 09-146r2</a>: 
                 OGC GML Application Schema - Coverages, Version 1.0.1</li>
                 <li><a href="http://www.iso.org/iso/catalogue_detail.htm?csnumber=33160">ISO/IEC 15444-2:2004</a>: 
                 Information technology -- JPEG 2000 image coding system: Extensions</li>
                 <li><a href="http://www.iso.org/iso/catalogue_detail.htm?csnumber=37674">ISO/IEC 15444-1:2004</a>: 
                 Information technology -- JPEG 2000 image coding system: Core coding system</li>
               </ul>
               <p>The following conformance levels are defined:</p>
               <ul>
                 <li>Core (<code>http://www.opengis.net/spec/GMLJP2/2.0/conf/core</code>): OGC 08-085, A.1</li>
               </ul>
             </div>
             <fieldset style="background:#ccffff">
               <legend style="font-family: sans-serif; color: #000099; 
			                 background-color:#F0F8FF; border-style: solid; 
                       border-width: medium; padding:4px">Test subject</legend>
               <p>
                 <label for="uri">
                   <h4 style="margin-bottom: 0.5em">Location (absolute 'http' or 'file' URI)</h4>
                 </label>
                 <input id="uri" name="uri" size="128" type="text" value="" />
               </p>
               <p>
                 <label for="doc">
                   <h4 style="margin-bottom: 0.5em">Upload representation</h4>
                 </label>
                 <input name="doc" id="doc" size="128" type="file" />
               </p>
               <p>
                 <label for="level">Conformance class: </label>
                 <input id="core" type="radio" name="level" value="1" checked="checked" />
                 <label for="core"> Core | </label>
                 <!-- NOT APPLICABLE
                 <input id="level-2" type="radio" name="level" value="2" />
                 <label class="form-label" for="level-2"> Level 2</label>
                 -->
               </p>
             </fieldset>
             <p>
               <input class="form-button" type="submit" value="Start"/> | 
               <input class="form-button" type="reset" value="Clear"/>
             </p>
           </ctl:form>
        </xsl:variable>
        <xsl:variable name="iut-file" select="$form-data//value[@key='doc']/ctl:file-entry/@full-path" />
	      <xsl:variable name="test-run-props">
		    <properties version="1.0">
          <entry key="iut">
            <xsl:choose>
              <xsl:when test="empty($iut-file)">
                <xsl:value-of select="normalize-space($form-data/values/value[@key='uri'])"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:copy-of select="concat('file:///', $iut-file)" />
              </xsl:otherwise>
            </xsl:choose>
          </entry>
          <entry key="ics"><xsl:value-of select="$form-data/values/value[@key='level']"/></entry>
		    </properties>
		   </xsl:variable>
       <xsl:variable name="testRunDir">
         <xsl:value-of select="tec:getTestRunDirectory($te:core)"/>
       </xsl:variable>
       <xsl:variable name="test-results">
        <ctl:call-function name="tns:run-ets-gmljpx20">
			    <ctl:with-param name="testRunArgs" select="$test-run-props"/>
          <ctl:with-param name="outputDir" select="$testRunDir" />
			  </ctl:call-function>
		  </xsl:variable>
      <xsl:call-template name="tns:testng-report">
        <xsl:with-param name="results" select="$test-results" />
        <xsl:with-param name="outputDir" select="$testRunDir" />
      </xsl:call-template>
      <xsl:variable name="summary-xsl" select="tec:findXMLResource($te:core, '/testng-summary.xsl')" />
      <ctl:message>
        <xsl:value-of select="saxon:transform(saxon:compile-stylesheet($summary-xsl), $test-results)"/>
See detailed test report in the TE_BASE/users/<xsl:value-of 
select="concat(substring-after($testRunDir, 'users/'), '/html/')" /> directory.
        </ctl:message>
        <xsl:if test="xs:integer($test-results/testng-results/@failed) gt 0">
          <xsl:for-each select="$test-results//test-method[@status='FAIL' and not(@is-config='true')]">
            <ctl:message>
Test method <xsl:value-of select="./@name"/>: <xsl:value-of select=".//message"/>
		    </ctl:message>
		  </xsl:for-each>
		  <ctl:fail/>
        </xsl:if>
        <xsl:if test="xs:integer($test-results/testng-results/@skipped) eq xs:integer($test-results/testng-results/@total)">
        <ctl:message>All tests were skipped. One or more preconditions were not satisfied.</ctl:message>
        <xsl:for-each select="$test-results//test-method[@status='FAIL' and @is-config='true']">
          <ctl:message>
            <xsl:value-of select="./@name"/>: <xsl:value-of select=".//message"/>
          </ctl:message>
        </xsl:for-each>
        <ctl:skipped />
      </xsl:if>
	  </ctl:code>
   </ctl:test>

  <xsl:template name="tns:testng-report">
    <xsl:param name="results" />
    <xsl:param name="outputDir" />
    <xsl:variable name="stylesheet" select="tec:findXMLResource($te:core, '/testng-report.xsl')" />
    <xsl:variable name="reporter" select="saxon:compile-stylesheet($stylesheet)" />
    <xsl:variable name="report-params" as="node()*">
      <xsl:element name="testNgXslt.outputDir">
        <xsl:value-of select="concat($outputDir, '/html')" />
      </xsl:element>
    </xsl:variable>
    <xsl:copy-of select="saxon:transform($reporter, $results, $report-params)" />
  </xsl:template>
</ctl:package>
