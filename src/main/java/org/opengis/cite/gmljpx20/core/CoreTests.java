package org.opengis.cite.gmljpx20.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.opengis.cite.gmljpx20.ErrorMessage;
import org.opengis.cite.gmljpx20.ErrorMessageKeys;
import org.opengis.cite.gmljpx20.GMLJP2;
import org.opengis.cite.gmljpx20.Namespaces;
import org.opengis.cite.gmljpx20.SuiteAttribute;
import org.opengis.cite.gmljpx20.ContigousCodestream;
import org.opengis.cite.gmljpx20.util.TestSuiteLogger;
import org.opengis.cite.gmljpx20.util.XMLUtils;
import org.opengis.cite.gmljpx20.util.jp2.Association;
import org.opengis.cite.gmljpx20.util.jp2.Box;
import org.opengis.cite.gmljpx20.util.jp2.JP2Stream;
import org.opengis.cite.gmljpx20.util.jp2.Label;
import org.opengis.cite.gmljpx20.util.jp2.XMLBox;
import org.opengis.cite.gmljpx20.util.jp2.FileType;
import org.opengis.cite.gmljpx20.util.jp2.ResourceRequirements;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CoreTests {

    /** JPEG 2000 image file. */
    private File jp2File;
    /** Flag indicating the presence a root GML instance */
    private Boolean rootInstance = false;
    private DocumentBuilder docBuilder;
    
    static Boolean exists = false;
    static String[] results = null;
    static String[] nodeValues;
    static boolean reset = false;
    static int totalElements = 0;
    static int counter = 0;

    @BeforeClass
    public void initFixture(ITestContext testContext) {
        Object testSubj = testContext.getSuite().getAttribute(SuiteAttribute.TEST_SUBJECT.getName());
        if (null != testSubj) {
            this.jp2File = File.class.cast(testSubj);
            if (!this.jp2File.exists()) {
                throw new SkipException("File not found at " + jp2File.getAbsolutePath());
            }
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            this.docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            throw new SkipException(pce.getMessage());
        }
    }

    /**
     * {@code [Test]} A conforming GMLJP2 encoded file shall use a GMLCOV
     * coverage description in accord with OGC 12-108 so as to describe the
     * coverage collection and to describe the individual coverages. In
     * particular, the permitted coverage types include:
     * <ul>
     * <li>gmlcov:GridCoverage</li>
     * <li>gmlcov:RectifiedGridCoverage</li>
     * <li>gmlcov:ReferenceableGridCoverage</li>
     * <li>any coverage type derived thereof with exactly 2 dimensions</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.1: GMLJP2 file contains a GMLCOV coverage</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_1">
     * gmljp2-gmlcov</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.1")
    public void containsGmlCoverageDescriptions() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovElems = (Boolean) XMLUtils.evaluateXPath(doc, "//cov:*",
                    Collections.singletonMap(Namespaces.GMLCOV, "cov"), XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV, doc.getDocumentElement().getNodeName()));
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} In a JPEG2000 encoded file containing coverage metadata about the internal 
     * structure of the JPEG2000 file (e.g. number of codestreams, number of rows and columns 
     * of a codestream) shall be coherent with the JPEG2000 binary header information. In case 
     * of discrepancies the JPEG2000 binary headers information takes precedence.
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.2: GMLJP2 coverage metadata coherence with JPEG2000 header</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_2">
     * header-precedence</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.2")
    public void containsGmlCoverageMetadataCoherence() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
			Box ContigousCodestream = findContigousCodestream(jp2s.Boxes);
			Assert.assertNotNull(ContigousCodestream, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
			ContigousCodestream auxContigousCodestream = (ContigousCodestream)ContigousCodestream;
			if (auxContigousCodestream != null) {
				
				//Extract Xsize and Ysize from codestream
				int[] fileContigousCodestream = auxContigousCodestream.ContigousCodestreamData;
				//Extract width and height gml:high xmlBox
	            XMLBox auxXmlBox = (XMLBox) ContigousCodestream;
	            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
				
	            Boolean hasGmlCovElems = (Boolean) XMLUtils.evaluateXPath(doc, "//high:*",
	                    null, XPathConstants.BOOLEAN);
	            Assert.assertTrue(hasGmlCovElems,
	                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_METADATA_HIGH, doc.getDocumentElement().getNodeName()));
	    	}
			
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} gmlcov:metadata information shall be coherent with the corresponding 
     * GMLCOV information in gml:domainSet or gmlcov:rangeType (e.g. geometric or radiometric 
     * information in ISO19139 format).
     * <ul>
     * <li>gml:domainSet</li>
     * <li>gmlcov:rangeType</li>
     * <li>Verify if the redundant information in the gmlcov:metadata and in the corresponding 
     * elements of gmlcov is the same. Test passes if it is the same.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.3: GMLJP2 file GMLCOV precedence</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_3">
     * gmljp2-gmlcov:precedence</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.3")
    public void containsGmlcovPrecedence() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovMetadataElems = (Boolean) XMLUtils.evaluateXPath(doc, "//gmlcov:metadata:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovMetadataElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_PRECEDENCE_METADATA, doc.getDocumentElement().getNodeName()));
            
            Boolean hasGmlCovDomainSetElems = (Boolean) XMLUtils.evaluateXPath(doc, "//domainSet:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovDomainSetElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_PRECEDENCE_DOMAIN_SET, doc.getDocumentElement().getNodeName()));
            
            Boolean hasGmlCovRangeTypeElems = (Boolean) XMLUtils.evaluateXPath(doc, "//rangeType:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovRangeTypeElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_PRECEDENCE_RANGE_TYPE, doc.getDocumentElement().getNodeName()));
            
    		String[] A13_0 = findElementContains(doc.getChildNodes(), "gmlcov:metadata");
    		String[] A13_1 = findElementContains(doc.getChildNodes(), "gml:domainSet");
    		String[] A13_2 = findElementContains(doc.getChildNodes(), "gmlcov:rangeType");
            for (int n = 0; n < A13_1.length; n++) {
                Boolean hasCoherence1 = (Boolean) Arrays.asList(A13_1).contains(A13_0[n]);
                Assert.assertTrue(hasCoherence1,
                        ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_PRECEDENCE_COHERENCE1, doc.getDocumentElement().getNodeName()));
                Boolean hasCoherence2 = (Boolean) Arrays.asList(A13_2).contains(A13_0[n]);
                Assert.assertTrue(hasCoherence2,
                        ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_PRECEDENCE_COHERENCE2, doc.getDocumentElement().getNodeName()));
			}
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} gml:metaDataProperty shall neither encode metadata about the coverage collection 
     * 	nor the individual coverages.
     * <ul>
     * <li>gml:metaDataProperty</li>
     * <li>Verify that gml:metaDataProperty is not used in the coverage collection and in the individual 
     * coverages. Test passes if it is not used..</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.4: Usage of gmlcov:metadata instead of gml:metaDataProperty</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_4">
     * gmljp2-gml-metaDataProperty</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.4")
    public void containsGmlcovInsteadmetaDataProperty() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovMetadataElems = (Boolean) XMLUtils.evaluateXPath(doc, "//metaDataProperty:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovMetadataElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_INSTEAD_METADATAPROPERTY, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} In those cases where a CRS is identified by reference to an authority and code, 
     * it SHALL be identified by URI following the OGC document 07-092r3 and maintained in 
     * http://www.opengis.net/def (URIs of Definitions in OGC Namespace).
     * <ul>
     * <li>gmlcov-CRS-byref</li>
     * <li>Verify that CRS are declared using URIs. Test passes if all CRSs are URIs.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.5: Verify that CRS are declared using URIs. Test passes if all CRSs are URIs.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_5">
     * gmljp2-gmlcov-CRS-byref</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.5")
    public void containsCRSdeclaredUsingURIs() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovCRSElems = (Boolean) XMLUtils.evaluateXPath(doc, "//RectifiedGrid:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovCRSElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_CRS_RECTIFIED_GRID, doc.getDocumentElement().getNodeName()));
            
            reset = true;
            String[] A15 = getNodeAttributeValueArray(doc.getChildNodes(), "gml:RectifiedGrid", "srsName");

    		for (int a = 0; a < A15.length; a++) {
                Boolean hasSrsName = (Boolean) Arrays.asList(A15[a]).contains("http");
                Assert.assertTrue(hasSrsName,
                        ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_CRS_HTTP, doc.getDocumentElement().getNodeName()));
    		}
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} The RectifiedGridCoverage model of GMLCOV requires the definition of the CRS associated 
     * to each coverage.
     * <ul>
     * <li>gmlcov-RectifiedGridCoverage-CRS</li>
     * <li>Verify that all GMLJP2RectifiedGridCoverage have CRS defined in the domainSet. Test passes all 
     * GMLJP2RectifiedGridCoverage have a CRSs defined.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.6: Verify that all GMLJP2RectifiedGridCoverage have CRS defined in the domainSet.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_6">
     * gmlcov-RectifiedGridCoverage-CRS</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.6")
    public void containsCRSrectifiedGridCoverage() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovCRSElems = (Boolean) XMLUtils.evaluateXPath(doc, "//RectifiedGrid:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovCRSElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_CRS_RECTIFIED_GRID, doc.getDocumentElement().getNodeName()));
            
            reset = true;
            String[] A15 = getNodeAttributeValueArray(doc.getChildNodes(), "gml:RectifiedGrid", "srsName");

    		for (int a = 0; a < A15.length; a++) {
                Boolean hasSrsName = (Boolean) Arrays.asList(A15[a]).contains("http");
                Assert.assertTrue(hasSrsName,
                        ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_CRS_HTTP, doc.getDocumentElement().getNodeName()));
    		}
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} In a JPEG2000 encoded file with coverage values with units of measure, the element tag 
     * must occur in the GMLCOV (gmlcov:rangeType/swe:DataRecord/swe:uom).
     * <ul>
     * <li>gmlcov:rangeType</li>
     * <li>swe:DataRecord</li>
     * <li>swe:DataRecord</li>
     * <li>swe:uom</li>
     * <li>Verify that all swe:DataRecords that declare variables that requires units have them populated 
     * (gmlcov:rangeType/swe:DataRecord/swe:uom). Test passes if they are present.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.7: UoM in rangeType are defined when applicable</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_7">
     * gmlcov-rangetype-uom</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.7")
    public void containsGmlRangeTypeDataRecordUom() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovDataRecordsElems = (Boolean) XMLUtils.evaluateXPath(doc, "//swe:DataRecords:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovDataRecordsElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_DATARECORDS, doc.getDocumentElement().getNodeName()));
            
            reset = true;
			String A17elements[] = findElementContains(doc.getChildNodes(), "swe:DataRecords");

            Boolean hasRangeType = (Boolean) Arrays.asList(A17elements).contains("gmlcov:rangeType");
            Assert.assertTrue(hasRangeType,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_DATARECORDS_RANGETYPE, doc.getDocumentElement().getNodeName()));

            Boolean hasSweDatarecords = (Boolean) Arrays.asList(A17elements).contains("swe:DataRecord");
            Assert.assertTrue(hasSweDatarecords,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_DATARECORDS_SWEDATARECORD, doc.getDocumentElement().getNodeName()));

            Boolean hasSweUom = (Boolean) Arrays.asList(A17elements).contains("uom");
            Assert.assertTrue(hasSweUom,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_DATARECORDS_SWEUOM, doc.getDocumentElement().getNodeName()));
				
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} In those cases where a UoM is identified by reference to an authority and code, 
     * it SHALL be identified by URI following the OGC document 07-092r3 and maintained in 
     * http://www.opengis.net/def (URIs of Definitions in OGC Namespace).
     * <ul>
     * <li>gmlcov-uom-byref</li>
     * <li>Verify if all UoM in the GEMLJP2 XML document are defined using URIs. Test passes if all are URIs.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.8: UoM are defined by reference.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_8">
     * gmljp2-gmlcov-uom-byref</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.8")
    public void containsUomByReference() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovUOMElems = (Boolean) XMLUtils.evaluateXPath(doc, "//swe:uom:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovUOMElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_UOM_BY_REF, doc.getDocumentElement().getNodeName()));
            
            reset = true;
            String[] A18 = findElementContains(doc.getChildNodes(), "swe:uom");

            Boolean hasHttpUom = (Boolean) Arrays.asList(A18).contains("http");
            Assert.assertTrue(hasHttpUom,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_UOM_HTTP, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} In a JPEG2000 encoded file with nil-values, the element tag shall occur in the 
     * GMLCOV (gmlcov:rangeType/swe:DataRecord/swe:field/swe:Quantity/swe:nilValues) with an appropriate 
     * swe:nilValue/@reason to give the client an indication on how to represent them.
     * <ul>
     * <li>gmlcov-uom-byref</li>
     * <li>Verify that the tag nil-values have value and a reason. Test passes if all these have it.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.9: GMLJP2 file gmlcov-nil-values.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_9">
     * gmljp2-gmlcov-nil-values</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.9")
    public void containsGmlcovNilValues() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovNilValuesElems = (Boolean) XMLUtils.evaluateXPath(doc, "//nil-values:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovNilValuesElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_NIL_VALUES, doc.getDocumentElement().getNodeName()));
            
            reset = true;
            String[] A19 = findElementContains(doc.getChildNodes(), "nil-values");

            Boolean hasHttpUom = (Boolean) !Arrays.asList(A19).contains("");
            Assert.assertTrue(hasHttpUom,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_UOM_HTTP, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} In those cases where the reason is identified by reference to an authority and code, 
     * it SHALL be identified by URI following the OGC document [09-046r2]  and maintained in 
     * http://www.opengis.net/def (URIs of Definitions in OGC Namespace).
     * <ul>
     * <li>gmlcov-nil-reason-byref</li>
     * <li>Verify that the all reasons for nill values are defined as URI’s. Test passes if there are.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.10: Nil-values by reference.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_10">
     * gmljp2-gmlcov-nil-reason-byref</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.10")
    public void containsGmlcovNilValuesByRef() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovNilValuesElems = (Boolean) XMLUtils.evaluateXPath(doc, "//nil-values:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovNilValuesElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_NIL_VALUES_BY_REF, doc.getDocumentElement().getNodeName()));
            
            reset = true;
            String[] A110 = findElementContains(doc.getChildNodes(), "nil-values");

            Boolean hasHttpNilValue = (Boolean) Arrays.asList(A110).contains("http");
            Assert.assertTrue(hasHttpNilValue,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_NIL_VALUES_BY_REF_HTTP, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} A GMLJP2 XML description of an image shall have a gmljp2:GMLJP2CoverageCollection 
     * as single root element derived from gmlcov:AbstractCoverageType thats as a container for other elements. 
     * The sub-elements gml:domainSet, the gml:rangeSet and the gmlcov:rangeType shall be left as blank 
     * as possible because these sub-elements have no meaning for the collection (but are inherited from 
     * the GMLCOV schema); the domainSet should provide a CRS information (defaulted to WGS84, otherwise 
     * the CRS of the single coverage or the common CRS of all coverages included – if the CRS is homogeneous -), 
     * and the bounding box for the collection. Dependency: /req/gmlcov
     * <ul>
     * <li>gmlcov-coverage-collection-container</li>
     * <li>Verify that the all reasons for nill values are defined as URI’s. Test passes if there are.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.11: GMLJP2 file root is a coverage collection.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_11">
     * gmljp2-gmlcov-coverage-collection-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.11")
    public void containsGmlcovCoverageCollectionContainer() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovCollectionElems = (Boolean) XMLUtils.evaluateXPath(doc, "//GMLJP2CoverageCollection:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovCollectionElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_COVERAGE_COLLECTION, doc.getDocumentElement().getNodeName()));
            
    		String[] elements = {
    				"gmljp2:GMLJP2CoverageCollection",
    				"gml:domainSet",
    				"gml:rangeSet",
    				"gmlcov:rangeType"
    		};

            Boolean hasCoverageElement = (Boolean) findElementsArray(doc.getChildNodes(), elements);
            Assert.assertTrue(hasCoverageElement,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_COVERAGE_COLLECTION_ELEMENT, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} For each codestream present in the image single child gmljp2:featureMember derived 
     * from gmlcov:AbstractCoverageType (i.e. gmljp2:GMLJP2GridCoverage, gmljp2:GMLJP2RectifiedGridCoverage 
     * or gmljp2:GMLJP2ReferenceableGridCoverage) (composed by a description of the gml:domainSet, 
     * the gml:rangeSet and the gmlcov:rangeType) shall be provided and populated. Dependency: /req/gmlcov
     * <ul>
     * <li>gmlcov-coverage-container</li>
     * <li>Verify that there are as many gmljp2:featureMembers derived from gmlcov:AbstractCoverageType as 
     * codestreams are present in the image. Test passes if both numbers are equal.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.12: GMLJP2 file coverages.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_12">
     * gmljp2-gmlcov-coverage-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.12")
    public void containsGmlcovCoverageContainer() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
                        
    		String[] elements = {
    				"gmlcov:AbstractCoverageType",
    				"gmljp2:featureMembers"
    		};

            Boolean hasCoverageContainerElement = (Boolean) findElementsArray(doc.getChildNodes(), elements);
            Assert.assertTrue(hasCoverageContainerElement,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_COVERAGE_CONTAINER_ELEMENT, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} A GMLJP2 XML description of an image shall have a gmljp2:GMLJP2CoverageCollection 
     * as single root element derived from gmlcov:AbstractCoverageType thats as a container for other elements. 
     * The sub-elements gml:domainSet, the gml:rangeSet and the gmlcov:rangeType shall be left as blank 
     * as possible because these sub-elements have no meaning for the collection (but are inherited from 
     * the GMLCOV schema); the domainSet should provide a CRS information (defaulted to WGS84, otherwise 
     * the CRS of the single coverage or the common CRS of all coverages included – if the CRS is homogeneous -), 
     * and the bounding box for the collection. Dependency: /req/gmlcov
     * <ul>
     * <li>gmljp2:GMLJP2CoverageCollection</li>
     * <li>gml:domainSet</li>
     * <li>gml:rangeSet</li>
     * <li>gmlcov:rangeType</li>
     * <li>Verify the presence of the gmlcov-metadata if metadata is available. If so, test passes if gmlcov-metadata is populated.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.13:  GMLJP2 file gmlcov-metadata.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_13">
     * gmljp2-gmlcov-metadata</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.13")
    public void containsGmlcovMetadata() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlCovMetadataElems = (Boolean) XMLUtils.evaluateXPath(doc, "//gmlcov:metadata:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlCovMetadataElems,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_METADATA, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} When there are features related to the JPEG 2000 file that should be included 
     * (except the GMLCOV part and annotations if any), these features shall be encoded in GML 3.2 and 
     * shall be included in either in a child featureElement containing gmljp2:GMLJP2Features 
     * (for features common to all codestreams) or from a gmljp2:feature element of the GMLJP2 elements 
     * derived from gmljp2:GMLJP2CoverageType (for features that are related to a single codestream).
     * <ul>
     * <li>gml-feature-container</li>
     * <li>Verify that gmljp2:GMLJP2Features (for features common to all codestreams) or gmljp2:feature 
     * (for features that are related to a single codestream) contain features as necessary that are not 
     * coverages or annotations. If so, test passes if these features are not coverages or annotations.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.14:  GMLJP2 file features.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_14">
     * gmljp2-gml-feature-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.14")
    public void containsFileFeatures() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));

			reset = true;
			String[] A114_1 = getNodeValueArray(doc.getChildNodes(), "gmljp2:GMLJP2Features");

            Boolean hasGMLJP2FeaturesAnnotation = (Boolean) Arrays.asList(A114_1).contains("annotation");
            Assert.assertTrue(hasGMLJP2FeaturesAnnotation,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_FEATURES_ANNOTATION, doc.getDocumentElement().getNodeName()));

            Boolean hasGMLJP2FeaturesCoverage = (Boolean) Arrays.asList(A114_1).contains("coverage");
            Assert.assertTrue(hasGMLJP2FeaturesCoverage,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_FEATURES_COVERAGE, doc.getDocumentElement().getNodeName()));

            reset = true;
    		String[] A114_2 = getNodeValueArray(doc.getChildNodes(), "gmljp2:feature");

            Boolean hasFeaturesAnnotation = (Boolean) Arrays.asList(A114_2).contains("annotation");
            Assert.assertTrue(hasFeaturesAnnotation,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_FEATURES_ANNOTATION, doc.getDocumentElement().getNodeName()));

            Boolean hasFeaturesCoverage = (Boolean) Arrays.asList(A114_2).contains("coverage");
            Assert.assertTrue(hasFeaturesCoverage,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_FEATURES_COVERAGE, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} When there are annotations related the JPEG 2000 file that should be included, 
     * these annotations shall be child elements of the gmljp2:annotation element of the of the GMLJP2 elements 
     * derived from gmljp2:GMLJP2CoverageType.
     * <ul>
     * <li>annotation-container</li>
     * <li>Verify that annotations are contained only in the gmljp2:annotation element as specified. Test passes if they are.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.15: GMLJP2 file annotations.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_15">
     * annotation-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.15")
    public void containsGMLJP2annotation() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlJp2Annotation = (Boolean) XMLUtils.evaluateXPath(doc, "//gmljp2:annotation:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlJp2Annotation,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_ANNOTATION_CONTAINER, doc.getDocumentElement().getNodeName()));
                        
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} When styling information of the features or annotations related the JPEG 2000 file should be included 
     * independent from the features, these styles shall be included in a gmljp2:style element of the coverage collection 
     * or the individual coverages.
     * <ul>
     * <li>style-container</li>
     * <li>Verify that style information is contained only in the gmljp2:style element as specified. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.16: GMLJP2 file styles.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_16">
     * style-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.16")
    public void containsGMLJP2fileStyle() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlJp2Style = (Boolean) XMLUtils.evaluateXPath(doc, "//gmljp2:style:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlJp2Style,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_STYLE_CONTAINER, doc.getDocumentElement().getNodeName()));
                        
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} The fileName subelement of the rangeSet in the coverage description shall contain a reference 
     * to the corresponding codestream in the JPEG2000 file. The fileStructure subelement shall be “inapplicable”.
     * <ul>
     * <li>gmlcov-filename-codestream</li>
     * <li>Verify the correspondence of the rangeSet members fileName and fileStructure are populated as 
     * gmljp2://codestream/# (# being a number) and inapplicable. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.17: GMLJP2 file /req/gmlcov-filename-codestream.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_17">
     * gmlcov-filename-codestream</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.17")
    public void containsGMLJP2filenameCodestream() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasGmlJp2fileName = (Boolean) XMLUtils.evaluateXPath(doc, "//gml:fileName:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasGmlJp2fileName,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_FILENAME_CODESTREAM, doc.getDocumentElement().getNodeName()));

            reset = true;
            String[] A117 = getNodeValueArray(doc.getChildNodes(), "gml:fileName");

    		Boolean hasFilenameCodestream = (Boolean) Arrays.asList(A117).contains("gmljp2://codestream/");
            Assert.assertTrue(hasFilenameCodestream,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_CODESTREAM, doc.getDocumentElement().getNodeName()));
    		
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} GMLJP2 instance data shall be stored in XML boxes. In order to allow references between 
     * these XML boxes, each XML box shall be associated with a label inside of an association box. This label 
     * serves as an identifier by which the XML data can be referenced..
     * <ul>
     * <li>xml-boxes</li>
     * <li>Verify that the image file has an XML box and association box with label that may serve as an 
     * identifier in GMLJP2 descriptions. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.18: GMLJP2 file XML boxes.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_18">
     * xml-boxes</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.18")
    public void containsXMLboxes() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            
        } catch (IOException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} The use of JPX format extension from JPEG2000 Part 2 (Annex M) shall be signalled with 
     * the value ‘jpx\040’ in the brand field of the file type box and the presence of GMLJP2 XML data shall be 
     * signalled with the value 67 in a reader requirement box.
     * <ul>
     * <li>xml-box-signal</li>
     * <li>Verify that the use of JPX format extension is signalled with the value ‘jpx\040’ in the brand field 
     * of the file type box and that the XML box is signaled with the value 67 indicating GML or Geographic 
     * metadata (XMLGISMetaData). If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.19: GMLJP2 file XML boxes signaled correctly.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_19">
     * xml-box-signal</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.19")
    public void containsGMLJP2fileXMLSignaledCorrectly() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            
			//PART 1:
			//Verify that the use of JPX format extension is signalled with the value ‘jpx\040’ in the brand field of the file type box
			Box fileType = findFileType(jp2s.Boxes);
            Assert.assertNotNull(fileType, ErrorMessage.get(ErrorMessageKeys.FILETYPE_NOT_FOUND));
			
            FileType auxFileType = (FileType)fileType;
            Assert.assertNotNull(auxFileType, ErrorMessage.get(ErrorMessageKeys.AUX_FILETYPE_NOT_FOUND));
			
			String fileTypeData = auxFileType.fileTypeData;
			
    		Boolean hasFilenameCodestream = (Boolean) fileTypeData.contains("jpx\040");
            Assert.assertTrue(hasFilenameCodestream,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_XML_SIGNALED_CORRECTLY, auxFileType));


          //PART 2:
			//that the XML box is signaled with the value 67 indicating GML or Geographic metadata (XMLGISMetaData)
			Box resourceRequirements = findResourceRequirements(jp2s.Boxes);
            Assert.assertNotNull(resourceRequirements, ErrorMessage.get(ErrorMessageKeys.RESOURCE_REQUIREMENTS_NOT_FOUND));

            ResourceRequirements rreq = (ResourceRequirements)resourceRequirements;
			
    		int A119_2 = verifyBytes(rreq.rreqData);
			Boolean hasRreq67 = false;
    		if (A119_2 == 67)
    			hasRreq67 = true;
            Assert.assertTrue(hasRreq67,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLCOV_FILENAME_CODESTREAM, rreq));
            
        } catch (IOException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} GMLJP2 will use JPX format specified in JPEG2000 part II Annex M, and shall 
     * consequently signal with the value ‘jpx\040’ in the compatibility list of the File Type box 
     * (see Annex M.8 of [ISO 15444-2]. Moreover and except if opacity channels (if any) are specified 
     * outside the scope of JP2, GMLJP2 files shall be written as JP2 compatible by including the string 
     * ‘jp2\040’ within the compatibility list of the File Type box (see Annex I of [ISO 15444-1] 
     * and Annex M.2.1 of [ISO 15444-2]).
     * <ul>
     * <li>jpx-jp2-compatible</li>
     * <li>Verify that the JPEG 2000 is marked as “jpx” in the compatibility list. Verify that 
     * the JPEG 2000 is marked as “jp2” in the compatibility list (except if opacity channel 
     * is specified outside the scope of jp2). If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.20: GMLJP2 file is a jpx and jp2 compatible.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_20">
     * jpx-jp2-compatible</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.20")
    public void containsGMLJP2fileJPXJP2Compatible() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            
			//PART 1:
			//JPX compatibility
			Box fileType = findFileType(jp2s.Boxes);
            Assert.assertNotNull(fileType, ErrorMessage.get(ErrorMessageKeys.FILETYPE_NOT_FOUND));
			
            FileType auxFileType = (FileType)fileType;
            Assert.assertNotNull(auxFileType, ErrorMessage.get(ErrorMessageKeys.AUX_FILETYPE_NOT_FOUND));
			
			String fileTypeData = auxFileType.fileTypeData;
			
    		Boolean isJPXcompatible = (Boolean) fileTypeData.contains("jpx");
            Assert.assertTrue(isJPXcompatible,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_JPX_COMPATILITY, auxFileType));


          //PART 2:
			//JP2 compatibility
			
    		Boolean isJP2compatible = (Boolean) fileTypeData.contains("jp2");
            Assert.assertTrue(isJP2compatible,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_JP2_COMPATILITY, auxFileType));
            
        } catch (IOException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} The single “outer” association box contains a first box which is a label box. 
     * This shall contain the label gml.data. The outer association box shall contain at least one 
     * additional association box containing GML instance data. This association box shall have a 
     * first box that is a label box with the label gml.root-instance and an XML box. This XML box 
     * shall only contain GML instance data for the following items and shall not contain XML schemas, 
     * CRS dictionaries or units of measure dictionary instance..
     * <ul>
     * <li>jp2-outer-box</li>
     * <li>Verify the structure and naming of the boxes and outer box is as specified, with the XML 
     * instance data preceded by a label box with the label gml.root-instance. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.21: GMLJP2 file /req/ jp2-outer-box.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_21">
     * jp2-outer-box</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.21")
    public void containsGMLJP2fileJp2OuterBox() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            
    		Boolean hasGMLdata = existsGMLData(jp2s.Boxes);
            Assert.assertTrue(hasGMLdata,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_GMLDATA_EXISTS, null));

        } catch (IOException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} Each of the association boxes, other than the gml.root-instance and gml.data boxes, 
     * shall have a label (the first box shall be a label box in each case).  The value of the label 
     * is any value allowed by JPEG 2000 Part II.
     * <ul>
     * <li>jp2-other-inner-box</li>
     * <li>Verify the structure and naming of the boxes is as specified. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.22: GMLJP2 file /req/jp2-other-outer-box.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_22">
     * jp2-other-inner-box</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.22")
    public void containsGMLJP2fileJp2OtherOuterBox() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            
    		Boolean hasTestStructure = testStructureXMLBox(jp2s.Boxes);
            Assert.assertTrue(hasTestStructure,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_TEST_STRUCTURE_XMLBOX, null));

        } catch (IOException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} When XML schema definitions are embedded in a JPEG200 file, then schemaLocation attribute is mandatory.
     * <ul>
     * <li>xsi:schemaLocation</li>
     * <li>Verify that when a XML resource embedded in a JPEG200 file  includes a schema definition, 
     * a reference to a schemaLocation is provided. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.23: GMLJP2 file /req/gmlcov-schemalocation.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_23">
     * gmljp2-schemalocation</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.23")
    public void containsFileSchemaLocation() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasSchemaLocation = (Boolean) XMLUtils.evaluateXPath(doc, "//xsi:schemaLocation:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasSchemaLocation,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_SCHEMA_LOCATION, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} The GMLJP2 file processor should follow the assessment rules for schemas as laid out in XML Schema 
     * Specification, Part I Structures, Section 4.3.2.
     * <ul>
     * <li>xsi:schemaLocation</li>
     * <li>Verify that when a XML resource embedded in a JPEG200 file  includes a schema definition, 
     * a reference to a schemaLocation is provided. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.24: GMLJP2 file /req/external-references.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_24">
     * gmljp2-external-references</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.24")
    public void containsFileExternalReferences() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasSchemaLocation = (Boolean) XMLUtils.evaluateXPath(doc, "//xsi:schemaLocation:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasSchemaLocation,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_SCHEMA_LOCATION, doc.getDocumentElement().getNodeName()));
 
            String A124 = findAttributeValue(doc.getChildNodes(), "xsi:schemaLocation");

    		Boolean hasFileExternalRef = (Boolean) Arrays.asList(A124).contains("http");
                Assert.assertTrue(hasFileExternalRef,
                        ErrorMessage.format(ErrorMessageKeys.GMLJP2_FILE_EXTERNAL_REF, doc.getDocumentElement().getNodeName()));
    		
    		
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} When an external application schema is referenced in the xsi:schemaLocation 
     * attribute or any resource is referenced in an xlink:href, it shall be referenced using a 
     * http://reference type to an XML instance, a relative reference shall be interpreted as 
     * relative to the jpeg2000 file position.
     * <ul>
     * <li>gmljp2:references</li>
     * <li>Verify that the internal references to schemaLocations are made using 
     * gmljp2: references. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.25: GMLJP2 file /req/internal-references.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_25">
     * internal-references</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.25")
    public void containsFileInternalReferences() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));
            // Note: Just check doc element for allowed coverage types?
            Boolean hasSchemaLocation = (Boolean) XMLUtils.evaluateXPath(doc, "//gmljp2:references:*",
                    null, XPathConstants.BOOLEAN);
            Assert.assertTrue(hasSchemaLocation,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_INTERNAL_REFERENCES, doc.getDocumentElement().getNodeName()));
 
            String A125 = findAttributeValue(doc.getChildNodes(), "gmljp2:references");

    		Boolean hasFileInternalRef = (Boolean) !Arrays.asList(A125).contains(null);
                Assert.assertTrue(hasFileInternalRef,
                        ErrorMessage.format(ErrorMessageKeys.GMLJP2_INTERNAL_REF, doc.getDocumentElement().getNodeName()));
    		
    		
        } catch (IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * {@code [Test]} The structure of an internal GMLJP2 URI shall be as follows: 
     * gmljp2://[resource.type]/[resource.id][#fragment-id]
     * <ul>
     * <li>gmljp2://xml/</li>
     * <li>Verify that the internal references to schemaLocations in xmlboxes are made using 
     * gmljp2://xml/ references. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.26: GMLJP2 file /req/internal-references-to-xmlbox.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_26">
     * internal-references-to-xmlbox</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.26")
    public void containsGMLJP2fileInternalRefToXMLBox() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));

			reset = true;
    		String[] A126 = getNodeAttributeValueArray(doc.getChildNodes(), "gml:FeatureCollection", "xsi:schemaLocation");

    		Boolean hasFileInternalRefToXmlBox = (Boolean) Arrays.asList(A126).contains("gmljp2://xml/");
            Assert.assertTrue(hasFileInternalRefToXmlBox,
            	ErrorMessage.format(ErrorMessageKeys.GMLJP2_INTERNAL_REF_TO_XML_BOX, doc.getDocumentElement().getNodeName()));
            
        } catch (IOException | SAXException e) {
            throw new AssertionError(e.getMessage());
        }
    }
    
    /**
     * {@code [Test]} When an specific application schema (xsi:schemaLocation) 
     * or any resource referenced (e.g. xlink:href) is included in a different XML Box 
     * it shall be referenced using a full reference. The URIs with a resource.type 
     * of xml identify a particular XML data box in the JPEG 2000 file shall have the 
     * following form: gmljp2://xml/[label] or gmljp2://xml/[label][#id].
     * <ul>
     * <li>gmljp2://codestream/</li>
     * <li>Verify that the internal references to schemaLocations in codestreams are 
     * made using gmljp2://codestream/ references. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC
     * 08-085r4</a>, A.1.27: GMLJP2 file /req/internal-references-to-codestream.</li>
     * <li><a target="_blank" href=
     * "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_27">
     * internal-references-to-codestream</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.27")
    public void containsInternalRefToCodestream() {
        try (InputStream inStream = new FileInputStream(this.jp2File)) {
            JP2Stream jp2s = new JP2Stream(inStream);
            Box xmlBox = findXMLbox(jp2s.Boxes);
            Assert.assertNotNull(xmlBox, ErrorMessage.get(ErrorMessageKeys.XML_BOX_NOT_FOUND));
            XMLBox auxXmlBox = (XMLBox) xmlBox;
            Document doc = docBuilder.parse(new InputSource(new StringReader(auxXmlBox.xmldata.trim())));

			reset = true;
    		String[] A127 = getNodeValueArray(doc.getChildNodes(), "gml:fileName");

            Boolean hasGMLJP2InternalRefToCodestream = (Boolean) Arrays.asList(A127).contains("gmljp2://codestream/");
            Assert.assertTrue(hasGMLJP2InternalRefToCodestream,
                    ErrorMessage.format(ErrorMessageKeys.GMLJP2_INTERNAL_REF_TO_CODESTREAM, doc.getDocumentElement().getNodeName()));

        } catch (IOException | SAXException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    
    
    
    /**
     * Finds an XML box in a JPEG 2000 codestream.
     * 
     * @param boxes
     *            A collection of boxes extracted from the codestream.
     * @return An XML Box, or null if one could not be found.
     */
    Box findXMLbox(List<Box> boxes) {
        Box xmlBox = null;
        for (int i = 0; i < boxes.size(); i++) {
            Box auxBox = boxes.get(i);
            if (auxBox instanceof Association) {
                xmlBox = findXMLbox(auxBox.Boxes);
                if (xmlBox != null)
                    return xmlBox;
            } else if (auxBox instanceof Label) {
                Label auxLabel = (Label) auxBox;
                TestSuiteLogger.log(Level.FINE, auxLabel.xmldata);
                if (auxLabel.xmldata.contains(GMLJP2.LBL_GML_ROOT))
                    this.rootInstance = true;
            } else if (auxBox instanceof XMLBox && this.rootInstance) {
                return auxBox;
            }
        }
        return null;

    }

    /**
     * Finds an contigous codestream from XML box in a JPEG 2000 codestream.
     * 
     * @param boxes
     *            A collection of boxes extracted from the codestream.
     * @return An contigous codestream from XML Box, or null if one could not be found.
     */
    public Box findContigousCodestream(List<Box> boxes){
    	for (int i = 0; i < boxes.size(); i++) {
    		Box auxBox = boxes.get(i);
    		if(auxBox instanceof ContigousCodestream)
    		{
    			return auxBox;
    		}
		}
		return null;
    	
    }

    /**
     * Find an element contained on nodelist.
     * 
     * @param nodelist, element
     *            Nodelist which find element.
     * @return An array containing elements founded, or null if one could not be found.
     */
   private static String[] findElementContains(NodeList nodeList, String element) {

		
        for (int count = 0; count < nodeList.getLength(); count++) {

	    	Node tempNode = nodeList.item(count);
	
	    	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	
	    		
	    		if (tempNode.hasAttributes()) {
	
	    			NamedNodeMap nodeMap = tempNode.getAttributes();
	    			for (int i = 0; i < nodeMap.getLength(); i++) {
	    				Node node = nodeMap.item(i);
	    				if (node.getNodeName().contains(element)) {
	    					exists = true;
	    					NodeList childrenNodes = node.getChildNodes();
	    					results = new String[childrenNodes.getLength()];
	    					for (int d = 0; d < childrenNodes.getLength(); d++) {
	    						results[d] = childrenNodes.item(d).toString();
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
        if (!exists) {
        	results = new String[1];
        	results[0] = null;
        }
        return results;
    }

   /**
    * Extract an array from nodelist where attribute is within element.
    * 
    * @param nodelist, element, attribute
    *            Nodelist which find attribute into element.
    * @return An array containing attribute founded, or null if one could not be found.
    */
   	private static String[] getNodeAttributeValueArray(NodeList nodeList, String element, String attribute) {
	   	if (reset)
	   		totalElements = 0;
	   		countElementsNode(nodeList, element);
	    	if (reset) {
				//init static variables
			    if (totalElements == 0)
			    	totalElements = 1;
			    nodeValues = new String[totalElements];
			    nodeValues[0] = null;
			    counter = 0;
			    reset = false;
	   	}
	    for (int count = 0; count < nodeList.getLength(); count++) {
	
	       	Node tempNode = nodeList.item(count);
	
	       	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	   		
	       		if (tempNode.getNodeName().contains(element)) {
	       			NamedNodeMap nodeMap = tempNode.getAttributes();
	       			for (int i = 0; i < nodeMap.getLength(); i++) {
	
	       				Node node = nodeMap.item(i);
	       				
	       				if (node.getNodeName().contains(attribute)){
	               			String getVal = node.getTextContent();
	
	       					nodeValues[counter] = getVal;
	           				counter++;
	       				}
	       			}
	       		}
	   			if (tempNode.hasChildNodes()) {
	   				getNodeAttributeValueArray(tempNode.getChildNodes(), element, attribute);
	   			}
	
	       	}
	       	return nodeValues;
		}
	    if (nodeValues == null){
	    	nodeValues[0] = "";
	    }
		return nodeValues;
	}

    /**
     * Find elements in array passed on nodelist.
     * 
     * @param nodelist, elements
     *            Nodelist which find elements.
     * @return True if all elements has been found, or false if one could not be found.
     */
    private static Boolean findElementsArray(NodeList nodeList, String[] elements) {

		boolean[] nodeExists = new boolean[elements.length];
		nodeExists[0] = false;
		
        for (int count = 0; count < nodeList.getLength(); count++) {

	    	Node tempNode = nodeList.item(count);
	
	    	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	
	    		
	    		if (tempNode.hasAttributes()) {
	
	    			NamedNodeMap nodeMap = tempNode.getAttributes();
	    			String mainNode = elements[0];
	    			for (int i = 0; i < nodeMap.getLength(); i++) {
	    				Node node = nodeMap.item(i);
	    				if (node.getNodeName().contains(mainNode)) {
	    					nodeExists[0] = true;
	    					NodeList childrenNodes = node.getChildNodes();

	    					for (int n = 1; n < elements.length; n++) {
	    						nodeExists[n] = false;
		    					for (int d = 0; d < childrenNodes.getLength(); d++) {
		    						if (childrenNodes.item(d).toString() == elements[n])
		    							nodeExists[n] = true;
		    					}
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
        if (!nodeExists[0])
        	return false;
        else {
        	boolean allTrue = true;
        	for (int d = 1; d < nodeExists.length; d++) {
        		if (!nodeExists[d])
        			allTrue = false;
        	}
        	if (allTrue)
        		return true;
        	else
        		return false;
        }
    }
    
    /**
     * Get an array values from nodelist containing element.
     * 
     * @param nodelist, element
     *            Nodelist which find element.
     * @return array strings.
     */
    private static String[] getNodeValueArray(NodeList nodeList, String element) {
    	if (reset)
    		totalElements = 0;
    	countElementsNode(nodeList, element);
    	if (reset) {
			//init static variables
		    if (totalElements == 0)
		    	totalElements = 1;
		    nodeValues = new String[totalElements];
		    nodeValues[0] = "Not found";
		    	
		    counter = 0;
		    reset = false;
    	}

        for (int count = 0; count < nodeList.getLength(); count++) {

        	Node tempNode = nodeList.item(count);

        	// make sure it's element node.
        	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
    		
        		if (tempNode.getNodeName().contains(element)) {
        			String getVal = tempNode.getTextContent();
        			if (getVal != null){
        				nodeValues[counter] = getVal;
        				counter++;
        			}
        		}

    			if (tempNode.hasChildNodes()) {

    				// loop again if has child nodes
    				getNodeValueArray(tempNode.getChildNodes(), element);

    			}
   			}

       	}
       	return nodeValues;
	}
    
    /**
     * Count elements from node.
     * 
     * @param nodelist, element
     *            Nodelist which find element.
     * @return void, fill global variable totalElements.
     */
	private static void countElementsNode(NodeList nodeList, String element) {
	
		for (int count = 0; count < nodeList.getLength(); count++) {
	
	    	Node tempNode = nodeList.item(count);
	
	    	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
			
	    		if (tempNode.getNodeName().contains(element)) {
	    			String getVal = tempNode.getTextContent();
	    			if (getVal != null){
	    				totalElements++;
	    			}
	    		}
	
				if (tempNode.hasChildNodes()) {
					countElementsNode(tempNode.getChildNodes(), element);
				}
	    	}
		}
	}
   
    /**
     * Find file type from box.
     * 
     * @param List of boxes
     * @return An XML Box, or null if one could not be found.
     */
    public Box findFileType(List<Box> boxes){
    	Box XMLBox = null;
    	for (int i = 0; i < boxes.size(); i++) {
    		Box auxBox = boxes.get(i);
    		if (auxBox instanceof Association){
    			XMLBox = findFileType(auxBox.Boxes);
    			if (XMLBox != null)
    				return XMLBox;
    		}
    		else if(auxBox instanceof FileType)
    		{
    			return auxBox;
    		}
		}
		return null;
    	
    }
    
    /**
     * Find box on resource requirement.
     * 
     * @param List of boxes
     * @return An XML Box, or null if one could not be found.
     */
    public Box findResourceRequirements(List<Box> boxes){
    	for (int i = 0; i < boxes.size(); i++) {
    		Box auxBox = boxes.get(i);
    		if(auxBox instanceof ResourceRequirements)
    		{
    			return auxBox;
    		}
		}
		return null;
    }
    
    /**
     * Verify bytes in array of bytes.
     * 
     * @param Array of Bytes
     * @return Integer.
     */
    private static int verifyBytes(byte[] req) {
    	byte[] _DataTemp = new byte[req.length];
    	int position = 0;
    	int valorFinal = 0;
    	if (req[0] != 0){
    		int maskLength = req[position];
    		position += maskLength;
    		int fuam = 0;
    		for (int a = 0; a < maskLength; a++){
    			fuam += req[position + a];
    			position ++;
    		}
    		int dcm = 0;
    		for (int a = 0; a < maskLength; a++){
    			dcm += req[position + a];
    			position ++;
    		}
    		int nsf = req[position] + req[position + 1];
    		position = position + 2;
    		
    		int[] sfi = new int[nsf];
    		int[] smi = new int[nsf];
    		
    		for (int a = 0; a < nsf; a++){
        		sfi[a] = req[position] + req[position + 1];
        		position = position + 2;
        		for (int b = 0; b < maskLength; b++){
        			smi[a] += req[position + b];
        			position ++;
        		}
    		}
    		valorFinal = sfi[1];
    	}  
    	return valorFinal;
    }

    /**
     * Verify if exists GML data on box.
     * 
     * @param Array of Bytes
     * @return True if exists, or false if not be found.
     */
    public Boolean existsGMLData(List<Box> boxes) {
		
		Boolean existsGMLData = false;
		
    	for (int i = 0; i < boxes.size(); i++) {
    		Box auxBox = boxes.get(i);
    		if (auxBox instanceof Association){
    			for (int d = 0; d < auxBox.Boxes.size(); d++) {
    				Box auxBox2 = auxBox.Boxes.get(d);
    				if (auxBox2 instanceof Label) {
    	    			Label auxLabel = (Label)auxBox2;
    	    			if (auxLabel.xmldata.contains("gml.data"))
    	    				existsGMLData = true;
    				}
    			}
    		}
    	}
    	return existsGMLData;
    }

    /**
     * Verify test structure of XML box.
     * 
     * @param List of boxes
     * @return True if exists, or false if not be found.
     */
    public Boolean testStructureXMLBox(List<Box> boxes) {
		
    	Boolean structAssoc = false;
    	Boolean structLabel = false;
    	
    	for (int i = 0; i < boxes.size(); i++) {
    		Box auxBox = boxes.get(i);
    		if (auxBox instanceof Association){
    			structAssoc = true;
    			for (int d = 0; d < auxBox.Boxes.size(); d++) {
    				Box auxBox2 = auxBox.Boxes.get(d);
    				if (auxBox2 instanceof Label) {
    	    			Label auxLabel = (Label)auxBox2;
    	    			structLabel = true;
    				}
    			}
    		}
    	}
    	if (structAssoc && structLabel)
    		return true;
    	else
    		return false;
    }
    
    /**
     * Find attribute value from elements in nodelist.
     * 
     * @param List of boxes, element
     * @return Attribute string, if not found return null.
     */
    private static String findAttributeValue(NodeList nodeList, String element) {

        for (int count = 0; count < nodeList.getLength(); count++) {

	    	Node tempNode = nodeList.item(count);
	
	    	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	
    			//System.out.println("Node Value =" + tempNode.getTextContent());
    			NamedNodeMap nodeMap = tempNode.getAttributes();
	    			
    			for (int i = 0; i < nodeMap.getLength(); i++) {
    				Node node = nodeMap.item(i);
    				if (node.getNodeName().contains(element)) {
    					return node.getNodeValue();
    				}
    			}

	    	}
    		if (tempNode.hasChildNodes()) {

    			// loop again if has child nodes
    			findAttributeValue(tempNode.getChildNodes(), element);

    		}

        }
        return null;
    }

}