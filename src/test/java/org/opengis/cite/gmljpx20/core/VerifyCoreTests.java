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
    public void plainImage() throws URISyntaxException {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML box not found in codestream");
        URL url = this.getClass().getResource("/jp2/nogml.jp2");
        File file = new File(url.toURI());
        when(suite.getAttribute(SuiteAttribute.TEST_SUBJECT.getName())).thenReturn(file);
        CoreTests spy = Mockito.spy(new CoreTests());
        spy.initFixture(testContext);
        spy.containsGmlCoverageDescriptions();
    }
}
