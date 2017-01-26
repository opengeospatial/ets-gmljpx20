package org.opengis.cite.gmljpx20.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.opengis.cite.gmljpx20.SuiteAttribute;
import org.testng.ISuite;
import org.testng.ITestContext;

public class VerifyCoreTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static ITestContext testContext;
    private static ISuite suite;

    @BeforeClass
    public static void initTestFixture() {
        testContext = mock(ITestContext.class);
        suite = mock(ISuite.class);
        when(testContext.getSuite()).thenReturn(suite);
    }

    @Test
    public void rootInstanceContainsGmlCoverageElements() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlCoverageDescriptions();
    }

    @Test
    public void rootInstancecontainsGmlCoverageMetadataCoherence() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlCoverageMetadataCoherence();
    }

    @Test
    public void rootInstancecontainsGmlcovPrecedence() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlCoverageMetadataCoherence();
    }

    @Test
    public void rootInstancecontainsGmlcovInsteadmetaDataProperty() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlcovInsteadmetaDataProperty();
    }

    @Test
    public void rootInstancecontainsCRSdeclaredUsingURIs() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsCRSdeclaredUsingURIs();
    }

    @Test
    public void rootInstancecontainsCRSrectifiedGridCoverage() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsCRSrectifiedGridCoverage();
    }

    @Test
    public void rootInstancecontainsGmlRangeTypeDataRecordUom() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlRangeTypeDataRecordUom();
    }

    @Test
    public void rootInstancecontainsUomByReference() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsUomByReference();
    }

    @Test
    public void rootInstancecontainsGmlcovNilValues() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlcovNilValues();
    }

    @Test
    public void rootInstancecontainsGmlcovNilValuesByRef() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlcovNilValuesByRef();
    }

    @Test
    public void rootInstancecontainsGmlcovCoverageCollectionContainer() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlcovNilValuesByRef();
    }

    @Test
    public void rootInstancecontainsGmlcovCoverageContainer() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlcovCoverageContainer();
    }

    @Test
    public void rootInstancecontainsGmlcovMetadata() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlcovMetadata();
    }

    @Test
    public void rootInstancecontainsFileFeatures() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsFileFeatures();
    }

    @Test
    public void rootInstancecontainsGMLJP2annotation() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2annotation();
    }

    @Test
    public void rootInstancecontainsGMLJP2fileStyle() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2fileStyle();
    }

    @Test
    public void rootInstancecontainsGMLJP2filenameCodestream() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2filenameCodestream();
    }

    @Test
    public void rootInstancecontainsXMLboxes() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsXMLboxes();
    }

    @Test
    public void rootInstancecontainsGMLJP2fileXMLSignaledCorrectly() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2fileXMLSignaledCorrectly();
    }

    @Test
    public void rootInstancecontainsGMLJP2fileJPXJP2Compatible() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2fileJPXJP2Compatible();
    }

    @Test
    public void rootInstancecontainsGMLJP2fileJp2OuterBox() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2fileJp2OuterBox();
    }

    @Test
    public void rootInstancecontainsGMLJP2fileJp2OtherOuterBox() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2fileJp2OtherOuterBox();
    }

    @Test
    public void rootInstancecontainsFileSchemaLocation() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsFileSchemaLocation();
    }

    @Test
    public void rootInstancecontainsFileExternalReferences() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsFileExternalReferences();
    }

    @Test
    public void rootInstancecontainsFileInternalReferences() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsFileInternalReferences();
    }

    @Test
    public void rootInstancecontainsGMLJP2fileInternalRefToXMLBox() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGMLJP2fileInternalRefToXMLBox();
    }

    @Test
    public void rootInstancecontainsInternalRefToCodestream() throws URISyntaxException {
        URL url = this.getClass().getResource("/jp2/romagmljp2-collection2-rreq7.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsInternalRefToCodestream();
    }

    @Test
    public void plainImage() throws URISyntaxException {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML box not found in codestream");
        URL url = this.getClass().getResource("/jp2/nogml.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlCoverageDescriptions();
        spy.containsGmlCoverageMetadataCoherence();
        spy.containsGmlcovPrecedence();
        spy.containsGmlcovInsteadmetaDataProperty();
        spy.containsCRSdeclaredUsingURIs();
        spy.containsCRSrectifiedGridCoverage();
        spy.containsGmlRangeTypeDataRecordUom();
        spy.containsUomByReference();
        spy.containsGmlcovNilValues();
        spy.containsGmlcovNilValuesByRef();
        spy.containsGmlcovNilValuesByRef();
        spy.containsGmlcovCoverageContainer();
        spy.containsGmlcovMetadata();
        spy.containsFileFeatures();
        spy.containsGMLJP2annotation();
        spy.containsGMLJP2fileStyle();
        spy.containsGMLJP2filenameCodestream();
        spy.containsXMLboxes();
        spy.containsGMLJP2fileXMLSignaledCorrectly();
        spy.containsGMLJP2fileJPXJP2Compatible();
        spy.containsGMLJP2fileJp2OuterBox();
        spy.containsGMLJP2fileJp2OtherOuterBox();
        spy.containsFileSchemaLocation();
        spy.containsFileExternalReferences();
        spy.containsFileInternalReferences();
        spy.containsGMLJP2fileInternalRefToXMLBox();
        spy.containsInternalRefToCodestream();
   }


}
