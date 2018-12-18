package org.opengis.cite.gmljpx20.core;

import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.STRING;
import static org.opengis.cite.gmljpx20.ErrorMessageKeys.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.opengis.cite.gmljpx20.ErrorMessage;
import org.opengis.cite.gmljpx20.GMLJP2;
import org.opengis.cite.gmljpx20.SuiteAttribute;
import org.opengis.cite.gmljpx20.util.TestSuiteLogger;
import org.opengis.cite.gmljpx20.util.XMLUtils;
import org.opengis.cite.gmljpx20.util.jp2.Association;
import org.opengis.cite.gmljpx20.util.jp2.Box;
import org.opengis.cite.gmljpx20.util.jp2.ContigousCodestream;
import org.opengis.cite.gmljpx20.util.jp2.FileType;
import org.opengis.cite.gmljpx20.util.jp2.JP2Stream;
import org.opengis.cite.gmljpx20.util.jp2.Label;
import org.opengis.cite.gmljpx20.util.jp2.ResourceRequirements;
import org.opengis.cite.gmljpx20.util.jp2.XMLBox;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CoreTests {

    /** JPEG 2000 image file. */
    private File jp2File;

    /** Flag indicating the presence a root GML instance */
    private boolean rootInstance = false;

    private DocumentBuilder docBuilder;

    static boolean exists = false;

    static String[] results = null;

    static String[] nodeValues;

    static boolean reset = false;

    static int totalElements = 0;

    static int counter = 0;

    @BeforeClass
    public void initFixture( ITestContext testContext ) {
        Object testSubj = testContext.getSuite().getAttribute( SuiteAttribute.TEST_SUBJECT.getName() );
        if ( null != testSubj ) {
            this.jp2File = File.class.cast( testSubj );
            if ( !this.jp2File.exists() ) {
                throw new SkipException( "File not found at " + jp2File.getAbsolutePath() );
            }
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware( true );
            this.docBuilder = dbf.newDocumentBuilder();
        } catch ( ParserConfigurationException pce ) {
            throw new SkipException( pce.getMessage() );
        }
    }

    /**
     * {@code [Test]} A conforming GMLJP2 encoded file shall use a GMLCOV coverage description in accord with OGC 12-108
     * so as to describe the coverage collection and to describe the individual coverages. In particular, the permitted
     * coverage types include:
     * <ul>
     * <li>GMLJP2GridCoverage</li>
     * <li>GMLJP2RectifiedGridCoverage</li>
     * <li>GMLJP2ReferenceableGridCoverage</li>
     * <li>any coverage type derived thereof with exactly 2 dimensions</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.1:
     * GMLJP2 file contains a GMLCOV coverage</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_1">
     * gmljp2-gmlcov</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.1")
    public void containsGmlCoverageDescriptions() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?

            boolean hasGmlCovGridElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='GMLJP2GridCoverage']" );
            boolean hasGmlCovRectifiedElems = XMLUtils.evaluateXPath( doc,
                                                                      "//*[local-name()='GMLJP2RectifiedGridCoverage']" );
            boolean hasGmlCovRefereanceableElems = XMLUtils.evaluateXPath( doc,
                                                                           "//*[local-name()='GMLJP2ReferenceableGridCoverage']" );
            if ( !hasGmlCovGridElems && !hasGmlCovRectifiedElems && !hasGmlCovRefereanceableElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} In a JPEG2000 encoded file containing coverage metadata about the internal structure of the
     * JPEG2000 file (e.g. number of codestreams, number of rows and columns of a codestream) shall be coherent with the
     * JPEG2000 binary header information. In case of discrepancies the JPEG2000 binary headers information takes
     * precedence.
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.2:
     * GMLJP2 coverage metadata coherence with JPEG2000 header</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_2">
     * header-precedence</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.2")
    public void containsGmlCoverageMetadataCoherence() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            Box ContigousCodestream = findContigousCodestream( jp2s.Boxes );
            if ( ContigousCodestream == null ) {
                throw new AssertionError( ErrorMessage.get( XML_BOX_NOT_FOUND ) );
            }
            ContigousCodestream auxContigousCodestream = (ContigousCodestream) ContigousCodestream;

            if ( auxContigousCodestream != null ) {
                // Extract Xsize and Ysize from codestream
                int[] fileContigousCodestream = auxContigousCodestream.ContigousCodestreamData;
                // Extract width and height gml:high xmlBox
                XMLBox xmlBox = findXMLbox( jp2s.Boxes );
                assertXmlBox( xmlBox );
                Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );

                String strhigh = (String) XMLUtils.evaluateXPath( doc,
                                                                  "//*[local-name()='GMLJP2RectifiedGridCoverage']//*[local-name()='high']/text()",
                                                                  null, STRING );
                if ( strhigh == null ) {
                    throw new AssertionError( ErrorMessage.get( XML_BOX_NOT_FOUND ) );
                } else {
                    String[] parts = strhigh.split( " " );
                    if ( ( parts[0] + 1 ) == Integer.toString( fileContigousCodestream[0] )
                         || ( parts[1] + 1 ) == Integer.toString( fileContigousCodestream[1] ) ) {
                        throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_METADATA_HIGH ) );
                    }
                }
            }

        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} gmlcov:metadata information shall be coherent with the corresponding GMLCOV information in
     * gml:domainSet or gmlcov:rangeType (e.g. geometric or radiometric information in ISO19139 format).
     * <ul>
     * <li>gml:domainSet</li>
     * <li>gmlcov:rangeType</li>
     * <li>Verify if the redundant information in the gmlcov:metadata and in the corresponding elements of gmlcov is the
     * same. Test passes if it is the same.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.3:
     * GMLJP2 file GMLCOV precedence</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_3">
     * gmljp2-gmlcov:precedence</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.3")
    public void containsGmlcovPrecedence() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasGmlCovMetadataElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='metadata']" );
            if ( hasGmlCovMetadataElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_PRECEDENCE_METADATA ) );
            }
            boolean hasGmlCovDomainSetElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='domainSet']" );
            if ( !hasGmlCovDomainSetElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_PRECEDENCE_DOMAIN_SET ) );
            }
            boolean hasGmlCovRangeTypeElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='rangeType']" );
            if ( !hasGmlCovRangeTypeElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_PRECEDENCE_RANGE_TYPE ) );
            }
            String[] A13_0 = findElementContains( doc.getChildNodes(), "gmlcov:metadata" );
            String[] A13_1 = findElementContains( doc.getChildNodes(), "gml:domainSet" );
            String[] A13_2 = findElementContains( doc.getChildNodes(), "gmlcov:rangeType" );
            for ( int n = 0; n < A13_1.length; n++ ) {
                boolean hasCoherence1 = Arrays.asList( A13_1 ).contains( A13_0[n] );
                if ( !hasCoherence1 ) {
                    throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_PRECEDENCE_COHERENCE1 ) );
                }
                boolean hasCoherence2 = Arrays.asList( A13_2 ).contains( A13_0[n] );
                if ( !hasCoherence2 ) {
                    throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_PRECEDENCE_COHERENCE2 ) );
                }

            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} gml:metaDataProperty shall neither encode metadata about the coverage collection nor the
     * individual coverages.
     * <ul>
     * <li>gml:metaDataProperty</li>
     * <li>Verify that gml:metaDataProperty is not used in the coverage collection and in the individual coverages. Test
     * passes if it is not used..</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.4:
     * Usage of gmlcov:metadata instead of gml:metaDataProperty</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_4">
     * gmljp2-gml-metaDataProperty</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.4")
    public void containsGmlcovInsteadmetaDataProperty() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasGmlCovMetadataElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='metadataProperty']" );
            if ( hasGmlCovMetadataElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_INSTEAD_METADATAPROPERTY ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} In those cases where a CRS is identified by reference to an authority and code, it SHALL be
     * identified by URI following the OGC document 07-092r3 and maintained in http://www.opengis.net/def (URIs of
     * Definitions in OGC Namespace).
     * <ul>
     * <li>gmlcov-CRS-byref</li>
     * <li>Verify that CRS are declared using URIs. Test passes if all CRSs are URIs.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.5:
     * Verify that CRS are declared using URIs. Test passes if all CRSs are URIs.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_5">
     * gmljp2-gmlcov-CRS-byref</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.5")
    public void containsCRSdeclaredUsingURIs() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean isRectifiedGrid = XMLUtils.evaluateXPath( doc, "//*[local-name()='RectifiedGrid']" );
            if ( !isRectifiedGrid ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_CRS_RECTIFIED_GRID ) );

            }
            reset = true;
            NodeList A15 = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='RectifiedGrid']//@srsName", null,
                                                              NODESET );
            for ( int a = 0; a < A15.getLength(); a++ ) {
                Node nd = A15.item( a );
                boolean hasSrsName = nd.getNodeValue().contains( "http" );
                if ( !hasSrsName ) {
                    throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_CRS_HTTP ) );

                }
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} The RectifiedGridCoverage model of GMLCOV requires the definition of the CRS associated to each
     * coverage.
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
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.6:
     * Verify that all GMLJP2RectifiedGridCoverage have CRS defined in the domainSet.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_6">
     * gmlcov-RectifiedGridCoverage-CRS</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.6")
    public void containsCRSrectifiedGridCoverage() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?

            boolean isRecifiedGrid = XMLUtils.evaluateXPath( doc, "//*[local-name()='RectifiedGrid']" );
            if ( !isRecifiedGrid ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_CRS_RECTIFIED_GRID ) );
            }
            reset = true;
            NodeList A15_1 = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='RectifiedGrid']", null,
                    NODESET );
            NodeList A15_2 = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='RectifiedGrid'][@*[local-name()='srsName']]", null,
                                                              NODESET );
            boolean hasSrsName = A15_1.getLength() == A15_2.getLength();
            if ( !hasSrsName ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_CRS_UNDEFINED ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} In a JPEG2000 encoded file with coverage values with units of measure, the element tag must occur
     * in the GMLCOV (gmlcov:rangeType/swe:DataRecord/swe:uom).
     * <ul>
     * <li>gmlcov:rangeType</li>
     * <li>swe:DataRecord</li>
     * <li>swe:uom</li>
     * <li>Verify that all swe:DataRecord that declare variables that requires units have them populated
     * (gmlcov:rangeType/swe:DataRecord/swe:uom). Test passes if they are present.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.7:
     * UoM in rangeType are defined when applicable</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_7">
     * gmlcov-rangetype-uom</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.7")
    public void containsGmlRangeTypeDataRecordUom() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasGmlCovDataRecordElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='DataRecord']" );
            if ( !hasGmlCovDataRecordElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_DATARECORDS ) );
            }

            String A17elements[] = findElementContains( doc.getChildNodes(), "swe:DataRecord" );
            boolean hasRangeType = Arrays.asList( A17elements ).contains( "gmlcov:rangeType" );
            if ( !hasRangeType ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_DATARECORDS_RANGETYPE ) );
            }

            boolean hasSweDatarecords = Arrays.asList( A17elements ).contains( "swe:DataRecord" );
            if ( !hasSweDatarecords ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_DATARECORDS_SWEDATARECORD ) );
            }

            boolean hasSweUom = Arrays.asList( A17elements ).contains( "uom" );
            if ( !hasSweUom ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_DATARECORDS_SWEUOM ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} In those cases where a UoM is identified by reference to an authority and code, it SHALL be
     * identified by URI following the OGC document 07-092r3 and maintained in http://www.opengis.net/def (URIs of
     * Definitions in OGC Namespace).
     * <ul>
     * <li>gmlcov-uom-byref</li>
     * <li>Verify if all UoM in the GMLJP2 XML document are defined using URIs. Test passes if all are URIs.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.8:
     * UoM are defined by reference.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_8">
     * gmljp2-gmlcov-uom-byref</a></li>
     * </ul>
     */

    @Test(description = "OGC 08-085r4, A.1.8")
    public void containsUomByReference() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            NodeList uomElements = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='uom']", null, XPathConstants.NODESET );
            for(int i = 0; i < uomElements.getLength(); i++) {
            	Node element = uomElements.item(i);
            	 boolean hasHttpUom = element.getNodeValue().contains("http");
            	 if ( !hasHttpUom ) {
                     throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_UOM_HTTP ) );
                 }
            }
            
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} In a JPEG2000 encoded file with nil-values, the element tag shall occur in the GMLCOV
     * (gmlcov:rangeType/swe:DataRecord/swe:field/swe:Quantity/swe:nilValues) with an appropriate swe:nilValue/@reason
     * to give the client an indication on how to represent them.
     * <ul>
     * <li>gmlcov-uom-byref</li>
     * <li>Verify that the tag nil-values have value and a reason. Test passes if all these have it.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.9:
     * GMLJP2 file gmlcov-nil-values.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_9">
     * gmljp2-gmlcov-nil-values</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.9")
    public void containsGmlcovNilValues() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            
            NodeList nilValues = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='nilValues']", null, XPathConstants.NODESET );
            for(int i = 0; i < nilValues.getLength(); i++) {
            	Node child = nilValues.item(i);
            	boolean hasValueAndReason = (boolean) XMLUtils.evaluateXPath(child, "//*[local-name()='nilValue'][@*[local-name()='reason']]", null, XPathConstants.BOOLEAN);
            	if(!hasValueAndReason) {
            		throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_NIL_VALUES ));
            	}
            }

        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} In those cases where the reason is identified by reference to an authority and code, it SHALL be
     * identified by URI following the OGC document [09-046r2] and maintained in http://www.opengis.net/def (URIs of
     * Definitions in OGC Namespace).
     * <ul>
     * <li>gmlcov-nil-reason-byref</li>
     * <li>Verify that the all reasons for nill values are defined as URIâ€™s. Test passes if there are.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.10:
     * Nil-values by reference.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_10">
     * gmljp2-gmlcov-nil-reason-byref</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.10")
    public void containsGmlcovNilValuesByRef() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            NodeList nilValues = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='nilValues']", null, XPathConstants.NODESET );
            for(int i = 0; i < nilValues.getLength(); i++) {
            	Node child = nilValues.item(i);
            	ArrayList<String> reasons = (ArrayList<String>) getNodeAttributeValueArray(child,"//*[local-name()='nilValue']", "reason");
            	for (String reason : reasons) {
            		if(!reason.contains("http")) {
            			throw new AssertionError( ErrorMessage.get(GMLJP2_GMLCOV_NIL_VALUES_BY_REF_HTTP));
            		}
            	}
            }

        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} A GMLJP2 XML description of an image shall have a gmljp2:GMLJP2CoverageCollection as single root
     * element derived from gmlcov:AbstractCoverageType thats as a container for other elements. The sub-elements
     * gml:domainSet, the gml:rangeSet and the gmlcov:rangeType shall be left as blank as possible because these
     * sub-elements have no meaning for the collection (but are inherited from the GMLCOV schema); the domainSet should
     * provide a CRS information (defaulted to WGS84, otherwise the CRS of the single coverage or the common CRS of all
     * coverages included â€“ if the CRS is homogeneous -), and the bounding box for the collection. Dependency:
     * /req/gmlcov
     * <ul>
     * <li>gmlcov-coverage-collection-container</li>
     * <li>Verify that the all reasons for nill values are defined as URIâ€™s. Test passes if there are.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.11:
     * GMLJP2 file root is a coverage collection.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_11">
     * gmljp2-gmlcov-coverage-collection-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.11")
    public void containsGmlcovCoverageCollectionContainer() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasGmlCovCollectionElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='GMLJP2GridCoverage']" );
            if ( hasGmlCovCollectionElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_COVERAGE_COLLECTION ) );
            }
            String[] elements = { "gmljp2:GMLJP2CoverageCollection", "gml:domainSet", "gml:rangeSet",
                                 "gmlcov:rangeType" };

            boolean hasCoverageElement = findElementsArray( doc.getChildNodes(), elements );
            if ( hasCoverageElement ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_COVERAGE_COLLECTION_ELEMENT ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} For each codestream present in the image single child gmljp2:featureMember derived from
     * gmlcov:AbstractCoverageType (i.e. gmljp2:GMLJP2GridCoverage, gmljp2:GMLJP2RectifiedGridCoverage or
     * gmljp2:GMLJP2ReferenceableGridCoverage) (composed by a description of the gml:domainSet, the gml:rangeSet and the
     * gmlcov:rangeType) shall be provided and populated. Dependency: /req/gmlcov
     * <ul>
     * <li>gmlcov-coverage-container</li>
     * <li>Verify that there are as many gmljp2:featureMembers derived from gmlcov:AbstractCoverageType as codestreams
     * are present in the image. Test passes if both numbers are equal.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.12:
     * GMLJP2 file coverages.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_12">
     * gmljp2-gmlcov-coverage-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.12")
    public void containsGmlcovCoverageContainer() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            String[] elements = { "gmlcov:AbstractCoverageType", "gmljp2:featureMembers" };
            boolean hasCoverageContainerElement = findElementsArray( doc.getChildNodes(), elements );
            if ( hasCoverageContainerElement ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_COVERAGE_CONTAINER_ELEMENT ) );
            }
        } catch ( IOException | SAXException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} A GMLJP2 XML description of an image shall have a gmljp2:GMLJP2CoverageCollection as single root
     * element derived from gmlcov:AbstractCoverageType thats as a container for other elements. The sub-elements
     * gml:domainSet, the gml:rangeSet and the gmlcov:rangeType shall be left as blank as possible because these
     * sub-elements have no meaning for the collection (but are inherited from the GMLCOV schema); the domainSet should
     * provide a CRS information (defaulted to WGS84, otherwise the CRS of the single coverage or the common CRS of all
     * coverages included â€“ if the CRS is homogeneous -), and the bounding box for the collection. Dependency:
     * /req/gmlcov
     * <ul>
     * <li>gmljp2:GMLJP2CoverageCollection</li>
     * <li>gml:domainSet</li>
     * <li>gml:rangeSet</li>
     * <li>gmlcov:rangeType</li>
     * <li>Verify the presence of the gmlcov-metadata if metadata is available. If so, test passes if gmlcov-metadata is
     * populated.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.13:
     * GMLJP2 file gmlcov-metadata.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_13">
     * gmljp2-gmlcov-metadata</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.13")
    public void containsGmlcovMetadata() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasGmlCovMetadataElems = XMLUtils.evaluateXPath( doc, "//*[local-name()='metadata']" );
            if ( hasGmlCovMetadataElems ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_METADATA ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} When there are features related to the JPEG 2000 file that should be included (except the GMLCOV
     * part and annotations if any), these features shall be encoded in GML 3.2 and shall be included in either in a
     * child featureElement containing gmljp2:GMLJP2Features (for features common to all codestreams) or from a
     * gmljp2:feature element of the GMLJP2 elements derived from gmljp2:GMLJP2CoverageType (for features that are
     * related to a single codestream).
     * <ul>
     * <li>gml-feature-container</li>
     * <li>Verify that gmljp2:GMLJP2Features (for features common to all codestreams) or gmljp2:feature (for features
     * that are related to a single codestream) contain features as necessary that are not coverages or annotations. If
     * so, test passes if these features are not coverages or annotations.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.14:
     * GMLJP2 file features.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_14">
     * gmljp2-gml-feature-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.14")
    public void containsFileFeatures() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );

            NodeList features = (NodeList) XMLUtils.evaluateXPath(doc, "//*[local-name()='GMLJP2Features']", null, NODESET);
            
            for(int i=0; i<features.getLength();i++) {
            	Node feature = features.item(i);
            	boolean hasCommonFeatures = (boolean) XMLUtils.evaluateXPath( feature, "//*[local-name()='GMLJP2Features']", null, XPathConstants.BOOLEAN );
            	boolean hasSingleFeatures = (boolean) XMLUtils.evaluateXPath( feature, "//*[local-name()]='feature'", null, XPathConstants.BOOLEAN );
            	if(!hasCommonFeatures && !hasSingleFeatures) {
            		 throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_FEATURES_ANNOTATION ) );
            	}
            }
            
        } catch ( IOException | SAXException | XPathExpressionException e) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} When there are annotations related the JPEG 2000 file that should be included, these annotations
     * shall be child elements of the gmljp2:annotation element of the of the GMLJP2 elements derived from
     * gmljp2:GMLJP2CoverageType.
     * <ul>
     * <li>annotation-container</li>
     * <li>Verify that annotations are contained only in the gmljp2:annotation element as specified. Test passes if they
     * are.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.15:
     * GMLJP2 file annotations.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_15">
     * annotation-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.15")
    public void containsGMLJP2annotation() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            NodeList annotations = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='annotation']", null, XPathConstants.NODESET );
            for(int i = 0; i < annotations.getLength(); i++) {
            	Node annotation = annotations.item(i);
            	boolean hasGmlJp2Annotation = annotation.getPrefix() != "gmljp2";
                if ( !hasGmlJp2Annotation ) {
                    throw new AssertionError( ErrorMessage.get( GMLJP2_ANNOTATION_CONTAINER ) );
                }
            }  
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} When styling information of the features or annotations related the JPEG 2000 file should be
     * included independent from the features, these styles shall be included in a gmljp2:style element of the coverage
     * collection or the individual coverages.
     * <ul>
     * <li>style-container</li>
     * <li>Verify that style information is contained only in the gmljp2:style element as specified. If so, test passes.
     * </li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.16:
     * GMLJP2 file styles.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_16">
     * style-container</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.16")
    public void containsGMLJP2fileStyle() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            
            NodeList annotations = (NodeList) XMLUtils.evaluateXPath( doc, "//*[local-name()='style']", null, XPathConstants.NODESET );
            for(int i = 0; i < annotations.getLength(); i++) {
            	Node annotation = annotations.item(i);
            	boolean hasGmlJp2Annotation = annotation.getPrefix() != "gmljp2";
                if ( !hasGmlJp2Annotation ) {
                    throw new AssertionError( ErrorMessage.get( GMLJP2_STYLE_CONTAINER ) );
                }
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} The fileName subelement of the rangeSet in the coverage description shall contain a reference to
     * the corresponding codestream in the JPEG2000 file. The fileStructure subelement shall be â€œinapplicableâ€�.
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
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.17:
     * GMLJP2 file /req/gmlcov-filename-codestream.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_17">
     * gmlcov-filename-codestream</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.17")
    public void containsGMLJP2filenameCodestream() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasGmlJp2fileName = XMLUtils.evaluateXPath( doc, "//gml:fileName");
            if ( !hasGmlJp2fileName ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_FILENAME_CODESTREAM ) );
            }
            List<String> A117 = getNodeValueArray( doc, "//gml:fileName" );

            boolean hasFilenameCodestream = A117.contains( "gmljp2://codestream/0" );
            if ( !hasFilenameCodestream ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_FILENAME_CODESTREAM ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} GMLJP2 instance data shall be stored in XML boxes. In order to allow references between these XML
     * boxes, each XML box shall be associated with a label inside of an association box. This label serves as an
     * identifier by which the XML data can be referenced..
     * <ul>
     * <li>xml-boxes</li>
     * <li>Verify that the image file has an XML box and association box with label that may serve as an identifier in
     * GMLJP2 descriptions. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.18:
     * GMLJP2 file XML boxes.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_18">
     * xml-boxes</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.18")
    public void containsXMLboxes() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            Assert.assertNotNull( xmlBox, ErrorMessage.get( XML_BOX_NOT_FOUND ) );
        } catch ( IOException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} The use of JPX format extension from JPEG2000 Part 2 (Annex M) shall be signalled with the value
     * â€˜jpx\040â€™ in the brand field of the file type box and the presence of GMLJP2 XML data shall be signalled with
     * the value 67 in a reader requirement box.
     * <ul>
     * <li>xml-box-signal</li>
     * <li>Verify that the use of JPX format extension is signalled with the value â€˜jpx\040â€™ in the brand field of
     * the file type box and that the XML box is signaled with the value 67 indicating GML or Geographic metadata
     * (XMLGISMetaData). If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.19:
     * GMLJP2 file XML boxes signaled correctly.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_19">
     * xml-box-signal</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.19")
    public void containsGMLJP2fileXMLSignaledCorrectly() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            // PART 1:
            // Verify that the use of JPX format extension is signalled with the value â€˜jpx\040â€™ in the brand field
            // of the file type box
            Box fileType = findFileType( jp2s.Boxes );
            if ( fileType == null ) {
                throw new AssertionError( ErrorMessage.get( FILETYPE_NOT_FOUND ) );
            }
            FileType auxFileType = (FileType) fileType;
            String fileTypeData = auxFileType.fileTypeData;
            boolean hasFilenameCodestream = fileTypeData.contains( "jpx\n" );
            if ( hasFilenameCodestream ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_XML_NOT_SIGNALLED_CORRECTLY ) );
            }

            // PART 2:
            // that the XML box is signaled with the value 67 indicating GML or Geographic metadata (XMLGISMetaData)
            Box resourceRequirements = findResourceRequirements( jp2s.Boxes );
            if ( resourceRequirements == null ) {
                throw new AssertionError( ErrorMessage.get( RESOURCE_REQUIREMENTS_NOT_FOUND ) );
            }
            ResourceRequirements rreq = (ResourceRequirements) resourceRequirements;
            int A119_2 = verifyBytes( rreq.rreqData );
            if ( A119_2 != 67 ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLCOV_FILENAME_CODESTREAM ) );
            }
            /* hasRreq67 = true; */
            /* Assert.assertTrue(hasRreq67, */
            /* ErrorMessage.format(GMLJP2_GMLCOV_FILENAME_CODESTREAM, rreq)); */

        } catch ( IOException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} GMLJP2 will use JPX format specified in JPEG2000 part II Annex M, and shall consequently signal
     * with the value â€˜jpx\040â€™ in the compatibility list of the File Type box (see Annex M.8 of [ISO 15444-2].
     * Moreover and except if opacity channels (if any) are specified outside the scope of JP2, GMLJP2 files shall be
     * written as JP2 compatible by including the string â€˜jp2\040â€™ within the compatibility list of the File Type
     * box (see Annex I of [ISO 15444-1] and Annex M.2.1 of [ISO 15444-2]).
     * <ul>
     * <li>jpx-jp2-compatible</li>
     * <li>Verify that the JPEG 2000 is marked as â€œjpxâ€� in the compatibility list. Verify that the JPEG 2000 is
     * marked as â€œjp2â€� in the compatibility list (except if opacity channel is specified outside the scope of jp2).
     * If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.20:
     * GMLJP2 file is a jpx and jp2 compatible.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_20">
     * jpx-jp2-compatible</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.20")
    public void containsGMLJP2fileJPXJP2Compatible() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );

            // PART 1:
            // JPX compatibility
            Box fileType = findFileType( jp2s.Boxes );

            FileType auxFileType = (FileType) fileType;
            if ( fileType == null ) {
                throw new AssertionError( ErrorMessage.get( FILETYPE_NOT_FOUND ) );
            }
            String fileTypeData = auxFileType.fileTypeData;

            if ( !fileTypeData.contains( "jpx" ) ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_JPX_COMPATILITY ) );
            }

            // PART 2:
            // JP2 compatibility
            if ( !fileTypeData.contains( "jp2" ) ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_JP2_COMPATILITY ) );
            }
        } catch ( IOException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} The single â€œouterâ€� association box contains a first box which is a label box. This shall
     * contain the label gml.data. The outer association box shall contain at least one additional association box
     * containing GML instance data. This association box shall have a first box that is a label box with the label
     * gml.root-instance and an XML box. This XML box shall only contain GML instance data for the following items and
     * shall not contain XML schemas, CRS dictionaries or units of measure dictionary instance..
     * <ul>
     * <li>jp2-outer-box</li>
     * <li>Verify the structure and naming of the boxes and outer box is as specified, with the XML instance data
     * preceded by a label box with the label gml.root-instance. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.21:
     * GMLJP2 file /req/ jp2-outer-box.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_21">
     * jp2-outer-box</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.21")
    public void containsGMLJP2fileJp2OuterBox() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            boolean hasGMLdata = existsGMLData( jp2s.Boxes );
            if ( !hasGMLdata ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_GMLDATA_EXISTS ) );
            }

        } catch ( IOException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} Each of the association boxes, other than the gml.root-instance and gml.data boxes, shall have a
     * label (the first box shall be a label box in each case). The value of the label is any value allowed by JPEG 2000
     * Part II.
     * <ul>
     * <li>jp2-other-inner-box</li>
     * <li>Verify the structure and naming of the boxes is as specified. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.22:
     * GMLJP2 file /req/jp2-other-outer-box.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_22">
     * jp2-other-inner-box</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.22")
    public void containsGMLJP2fileJp2OtherOuterBox() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            boolean hasTestStructure = testStructureXMLBox( jp2s.Boxes );
            if ( !hasTestStructure ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_TEST_STRUCTURE_XMLBOX ) );
            }
        } catch ( IOException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} When XML schema definitions are embedded in a JPEG200 file, then schemaLocation attribute is
     * mandatory.
     * <ul>
     * <li>xsi:schemaLocation</li>
     * <li>Verify that when a XML resource embedded in a JPEG200 file includes a schema definition, a reference to a
     * schemaLocation is provided. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.23:
     * GMLJP2 file /req/gmlcov-schemalocation.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_23">
     * gmljp2-schemalocation</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.23")
    public void containsFileSchemaLocation() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasSchemaLocation = XMLUtils.evaluateXPath( doc, "//@*[local-name()='schemaLocation']" );
            if ( !hasSchemaLocation ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_SCHEMA_LOCATION ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} The GMLJP2 file processor should follow the assessment rules for schemas as laid out in XML Schema
     * Specification, Part I Structures, Section 4.3.2.
     * <ul>
     * <li>xsi:schemaLocation</li>
     * <li>Verify that when a XML resource embedded in a JPEG200 file includes a schema definition, a reference to a
     * schemaLocation is provided. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.24:
     * GMLJP2 file /req/external-references.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_24">
     * gmljp2-external-references</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.24")
    public void containsFileExternalReferences() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasSchemaLocation = XMLUtils.evaluateXPath( doc, "//@*[local-name()='schemaLocation']" );
            if ( !hasSchemaLocation ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_SCHEMA_LOCATION ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} When an external application schema is referenced in the xsi:schemaLocation attribute or any
     * resource is referenced in an xlink:href, it shall be referenced using a http://reference type to an XML instance,
     * a relative reference shall be interpreted as relative to the jpeg2000 file position.
     * <ul>
     * <li>gmljp2:references</li>
     * <li>Verify that the internal references to schemaLocations are made using gmljp2: references. If so, test passes.
     * </li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.25:
     * GMLJP2 file /req/internal-references.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_25">
     * internal-references</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.25")
    public void containsFileInternalReferences() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            // Note: Just check doc element for allowed coverage types?
            boolean hasSchemaLocation = XMLUtils.evaluateXPath( doc, "//@*[local-name()='schemaLocation']" );
            if ( !hasSchemaLocation ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_SCHEMA_LOCATION ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} The structure of an internal GMLJP2 URI shall be as follows:
     * gmljp2://[resource.type]/[resource.id][#fragment-id]
     * <ul>
     * <li>gmljp2://xml/</li>
     * <li>Verify that the internal references to schemaLocations in xmlboxes are made using gmljp2://xml/ references.
     * If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.26:
     * GMLJP2 file /req/internal-references-to-xmlbox.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_26">
     * internal-references-to-xmlbox</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.26")
    public void containsGMLJP2fileInternalRefToXMLBox() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            reset = true;
         
            List<String> A126 = getNodeAttributeValueArray( doc, "//*[local-name()='feature']/*", "xsi:schemaLocation" );
            
            boolean hasGmlFeatureCollection = !A126.isEmpty();
            if(hasGmlFeatureCollection) {
            boolean hasFileInternalRefToXmlBox = A126.contains( "gmljp2://xml" );
	            if ( !hasFileInternalRefToXmlBox ) {
	                throw new AssertionError( ErrorMessage.get( GMLJP2_INTERNAL_REF_TO_XML_BOX ) );
	            }
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * {@code [Test]} When an specific application schema (xsi:schemaLocation) or any resource referenced (e.g.
     * xlink:href) is included in a different XML Box it shall be referenced using a full reference. The URIs with a
     * resource.type of xml identify a particular XML data box in the JPEG 2000 file shall have the following form:
     * gmljp2://xml/[label] or gmljp2://xml/[label][#id].
     * <ul>
     * <li>gmljp2://codestream/</li>
     * <li>Verify that the internal references to schemaLocations in codestreams are made using gmljp2://codestream/
     * references. If so, test passes.</li>
     * </ul>
     *
     * <p style="margin-bottom: 0.5em">
     * <strong>Sources</strong>
     * </p>
     * <ul>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html">OGC 08-085r4</a>, A.1.27:
     * GMLJP2 file /req/internal-references-to-codestream.</li>
     * <li><a target="_blank" href= "http://docs.opengeospatial.org/is/08-085r4/08-085r4.html#requirement_27">
     * internal-references-to-codestream</a></li>
     * </ul>
     */
    @Test(description = "OGC 08-085r4, A.1.27")
    public void containsInternalRefToCodestream() {
        try (InputStream inStream = new FileInputStream( this.jp2File )) {
            JP2Stream jp2s = new JP2Stream( inStream );
            XMLBox xmlBox = findXMLbox( jp2s.Boxes );
            assertXmlBox( xmlBox );

            Document doc = docBuilder.parse( new InputSource( new StringReader( xmlBox.xmldata.trim() ) ) );
            List<String> A127 = getNodeValueArray( doc, "//*[local-name()='fileName']" );
            boolean hasGMLJP2InternalRefToCodestream = A127.contains( "//*[local-name()='fileName']" );
            if ( hasGMLJP2InternalRefToCodestream ) {
                throw new AssertionError( ErrorMessage.get( GMLJP2_INTERNAL_REF_TO_CODESTREAM ) );
            }
        } catch ( IOException | SAXException | XPathExpressionException e ) {
            throw new AssertionError( e.getMessage() );
        }
    }

    /**
     * Finds an XML box in a JPEG 2000 codestream.
     * 
     * @param boxes
     *            A collection of boxes extracted from the codestream.
     * @return An XML Box, or null if one could not be found.
     */
    private XMLBox findXMLbox( List<Box> boxes ) {
        for ( Box auxBox : boxes ) {
            if ( auxBox instanceof Association ) {
                XMLBox xmlBox = findXMLbox( auxBox.Boxes );
                if ( xmlBox != null )
                    return xmlBox;
            } else if ( auxBox instanceof Label ) {
                Label auxLabel = (Label) auxBox;
                TestSuiteLogger.log( Level.FINE, auxLabel.xmldata );
                if ( auxLabel.xmldata.contains( GMLJP2.LBL_GML_ROOT ) )
                    this.rootInstance = true;
            } else if ( auxBox instanceof XMLBox && this.rootInstance ) {
                return (XMLBox) auxBox;
            }
        }
        return null;

    }

    /**
     * Finds an contigous codestream from XML box in a JPEG 2000 codestream.
     * 
     * @param boxes
     *            A collection of boxes extracted from the codestream.
     * @return A contigous codestream from XML Box, or null if it cannot be found.
     */
    private Box findContigousCodestream( List<Box> boxes ) {
        for ( Box auxBox : boxes ) {
            if ( auxBox instanceof ContigousCodestream ) {
                return auxBox;
            }
        }
        return null;

    }

    /**
     * Find an element contained on nodelist.
     * 
     * @param nodeList
     *            element Nodelist which find element.
     * @return An array containing elements founded, or null if one could not be found.
     */
    private static String[] findElementContains( NodeList nodeList, String element ) {
        for ( int count = 0; count < nodeList.getLength(); count++ ) {
            Node tempNode = nodeList.item( count );
            if ( tempNode.getNodeType() == Node.ELEMENT_NODE ) {
                if ( tempNode.hasAttributes() ) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for ( int i = 0; i < nodeMap.getLength(); i++ ) {
                        Node node = nodeMap.item( i );
                        if ( node.getNodeName().contains( element ) ) {
                            exists = true;
                            NodeList childrenNodes = node.getChildNodes();
                            results = new String[childrenNodes.getLength()];
                            for ( int d = 0; d < childrenNodes.getLength(); d++ ) {
                                results[d] = childrenNodes.item( d ).toString();
                            }
                        }
                    }
                }
            }
        }
        if ( !exists ) {
            results = new String[1];
            results[0] = null;
        }
        return results;
    }

    /**
     * Extract a list of attribute from doc within elements fulfilling xpathExpression.
     * 
     * @param doc
     *            xpathExpression, attribute Node which find attribute fulfilling xpathExpression.
     * @return A list containing attribute found within elements fulfilling xpathExpression.
     */
    
    private static List<String> getNodeAttributeValueArray( Node doc, String xpathExpression, String attribute ) throws XPathExpressionException {
    	NodeList resultNodeList = (NodeList) XMLUtils.evaluateXPath(doc, xpathExpression, null, NODESET);
    	List<String> results = new ArrayList<String>();
    	for (int index = 0; index < resultNodeList.getLength(); index++) {
    	    Node node = resultNodeList.item(index);
    	    String value = ((Element) node).getAttribute(attribute);
    	    results.add(value);
    	}
    	
    	return results;
    }
    
    /**
     * Extract an array from nodelist where attribute is within element.
     * 
     * @param nodeList
     *            element, attribute Nodelist which find attribute into element.
     * @return An array containing attribute founded, or null if one could not be found.
     */
    
    private static String[] getNodeAttributeValueArray( NodeList nodeList, String element, String attribute ) {
    	if ( reset )
            totalElements = 0;
        countElementsNode( nodeList, element );
        if ( reset ) {
            // init static variables
            if ( totalElements == 0 )
                totalElements = 1;
            nodeValues = new String[totalElements];
            nodeValues[0] = null;
            counter = 0;
            reset = false;
        }
        for ( int count = 0; count < nodeList.getLength(); count++ ) {
            Node tempNode = nodeList.item( count );
            if ( tempNode.getNodeType() == Node.ELEMENT_NODE ) {
                if ( tempNode.getNodeName().contains( element ) ) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for ( int i = 0; i < nodeMap.getLength(); i++ ) {
                        Node node = nodeMap.item( i );
                        if ( node.getNodeName().contains( attribute ) ) {
                            String getVal = node.getTextContent();
                            nodeValues[counter] = getVal;
                            counter++;
                        }
                    }
                }
                if ( tempNode.hasChildNodes() ) {
                    getNodeAttributeValueArray( tempNode.getChildNodes(), element, attribute );
                }

            }
            return nodeValues;
        }
        if ( nodeValues == null ) {
            nodeValues[0] = "";
        }
        return nodeValues;
    }

    /**
     * Find elements in array passed on nodelist.
     * 
     * @param nodeList
     *            elements Nodelist which find elements.
     * @return True if all elements has been found, or false if one could not be found.
     */
    private static boolean findElementsArray( NodeList nodeList, String[] elements ) {
        boolean[] nodeExists = new boolean[elements.length];
        nodeExists[0] = false;
        for ( int count = 0; count < nodeList.getLength(); count++ ) {
            Node tempNode = nodeList.item( count );
            if ( tempNode.getNodeType() == Node.ELEMENT_NODE ) {
                if ( tempNode.hasAttributes() ) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    String mainNode = elements[0];
                    for ( int i = 0; i < nodeMap.getLength(); i++ ) {
                        Node node = nodeMap.item( i );
                        if ( node.getNodeName().contains( mainNode ) ) {
                            nodeExists[0] = true;
                            NodeList childrenNodes = node.getChildNodes();
                            for ( int n = 1; n < elements.length; n++ ) {
                                nodeExists[n] = false;
                                for ( int d = 0; d < childrenNodes.getLength(); d++ ) {
                                    if ( childrenNodes.item( d ).toString() == elements[n] )
                                        nodeExists[n] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if ( !nodeExists[0] )
            return false;
        else {
            boolean allTrue = true;
            for ( int d = 1; d < nodeExists.length; d++ ) {
                if ( !nodeExists[d] )
                    allTrue = false;
            }
            if ( allTrue )
                return true;
            else
                return false;
        }
    }

    /**
     * Get an array of string values from doc containing the values under the node defined by xpathExpression.
     * 
     * @param doc
     *            Node with the DOM content in which to find values under the xpathExpression.
     * @return list of strings .
     * @throws XPathExpressionException 
     */
    private static List<String> getNodeValueArray( Node doc, String xpathExpression ) throws XPathExpressionException {
    	NodeList resultNodeList = (NodeList) XMLUtils.evaluateXPath(doc, xpathExpression, null, NODESET);
    	List<String> results = new ArrayList<String>();
    	for (int index = 0; index < resultNodeList.getLength(); index++) {
    	    Node node = resultNodeList.item(index);
    	    String value = node.getTextContent();
    	    results.add(value);
    	}
    	
    	return results;
    }
    

    /**
     * Count elements from node.
     * 
     * @param nodeList
     *            element Nodelist which find element.
     * @return void, fill global variable totalElements.
     */
    private static void countElementsNode( NodeList nodeList, String element ) {
        for ( int count = 0; count < nodeList.getLength(); count++ ) {
            Node tempNode = nodeList.item( count );
            if ( tempNode.getNodeType() == Node.ELEMENT_NODE ) {
                if ( tempNode.getNodeName().contains( element ) ) {
                    String getVal = tempNode.getTextContent();
                    if ( getVal != null ) {
                        totalElements++;
                    }
                }
                if ( tempNode.hasChildNodes() ) {
                    countElementsNode( tempNode.getChildNodes(), element );
                }
            }
        }
    }

    /**
     * Find file type from box.
     * 
     * @param boxes
     *            List of boxes
     * @return An XML Box, or null if one could not be found.
     */
    private Box findFileType( List<Box> boxes ) {
        for ( Box auxBox : boxes ) {
            if ( auxBox instanceof Association ) {
                Box XMLBox = findFileType( auxBox.Boxes );
                if ( XMLBox != null )
                    return XMLBox;
            } else if ( auxBox instanceof FileType ) {
                return auxBox;
            }
        }
        return null;

    }

    /**
     * Find box on resource requirement.
     * 
     * @param boxes
     *            List of boxes
     * @return An XML Box, or null if one could not be found.
     */
    private Box findResourceRequirements( List<Box> boxes ) {
        for ( Box auxBox : boxes ) {
            if ( auxBox instanceof ResourceRequirements ) {
                return auxBox;
            }
        }
        return null;
    }

    /**
     * Verify bytes in array of bytes.
     * 
     * @param req
     *            array of Bytes
     * @return Integer.
     */
    private static int verifyBytes( byte[] req ) {
        int position = 0;
        int valorFinal = 0;
        if ( req[0] != 0 ) {
            int maskLength = req[position];
            position += maskLength;
            int fuam = 0;
            for ( int a = 0; a < maskLength; a++ ) {
                fuam += req[position + a];
                position++;
            }
            int dcm = 0;
            for ( int a = 0; a < maskLength; a++ ) {
                dcm += req[position + a];
                position++;
            }
            int nsf = req[position] + req[position + 1];
            position = position + 2;

            int[] sfi = new int[nsf];
            int[] smi = new int[nsf];

            for ( int a = 0; a < nsf; a++ ) {
                sfi[a] = req[position] + req[position + 1];
                position = position + 2;
                for ( int b = 0; b < maskLength; b++ ) {
                    smi[a] += req[position + b];
                    position++;
                }
            }
            valorFinal = sfi[1];
        }
        return valorFinal;
    }

    /**
     * Verify if exists GML data on box.
     * 
     * @param boxes
     *            List of Bytes
     * @return True if exists, or false if not be found.
     */
    private boolean existsGMLData( List<Box> boxes ) {
        boolean existsGMLData = false;
        for ( Box auxBox : boxes ) {
            if ( auxBox instanceof Association ) {
                for ( int d = 0; d < auxBox.Boxes.size(); d++ ) {
                    Box auxBox2 = auxBox.Boxes.get( d );
                    if ( auxBox2 instanceof Label ) {
                        Label auxLabel = (Label) auxBox2;
                        if ( auxLabel.xmldata.contains( "gml.data" ) )
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
     * @param boxes
     *            List of boxes
     * @return True if exists, or false if not be found.
     */
    private boolean testStructureXMLBox( List<Box> boxes ) {
        boolean structAssoc = false;
        boolean structLabel = false;
        for ( Box auxBox : boxes ) {
            if ( auxBox instanceof Association ) {
                structAssoc = true;
                for ( int d = 0; d < auxBox.Boxes.size(); d++ ) {
                    Box auxBox2 = auxBox.Boxes.get( d );
                    if ( auxBox2 instanceof Label ) {
                        structLabel = true;
                    }
                }
            }
        }
        return structAssoc && structLabel;
    }

    private void assertXmlBox( XMLBox xmlBox ) {
        if ( xmlBox == null ) {
            throw new AssertionError( ErrorMessage.get( XML_BOX_NOT_FOUND ) );
        }
    }

}