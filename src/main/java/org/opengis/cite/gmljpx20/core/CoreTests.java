package org.opengis.cite.gmljpx20.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import org.opengis.cite.gmljpx20.util.TestSuiteLogger;
import org.opengis.cite.gmljpx20.util.XMLUtils;
import org.opengis.cite.gmljpx20.util.jp2.Association;
import org.opengis.cite.gmljpx20.util.jp2.Box;
import org.opengis.cite.gmljpx20.util.jp2.JP2Stream;
import org.opengis.cite.gmljpx20.util.jp2.Label;
import org.opengis.cite.gmljpx20.util.jp2.XMLBox;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CoreTests {

    /** JPEG 2000 image file. */
    private File jp2File;
    /** Flag indicating the presence a root GML instance */
    private Boolean rootInstance = false;
    private DocumentBuilder docBuilder;

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
}
