package org.opengis.cite.gmljpx20.core;

import org.opengis.cite.gmljpx20.SuiteAttribute;
import org.opengis.cite.gmljpx20.util.TestSuiteLogger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import java.io.File;
import java.util.logging.Level;

/**
 * Checks that all preconditions are satisfied before the test suite is run. If
 * any of these (BeforeSuite) methods fail, all tests are skipped.
 */
public class SuitePreconditions {

    /**
     * Verifies that a a JPEG 2000 image resource was supplied as a test run
     * argument and that the implementation it describes is available.
     *
     * @param testContext
     *            Information about the (pending) test run.
     */
    @BeforeSuite
    public void verifyTestSubject(ITestContext testContext) {
        Object sutObj = testContext.getSuite().getAttribute(SuiteAttribute.TEST_SUBJECT.getName());
        if (null != sutObj && File.class.isInstance(sutObj)) {
            // TODO: Verify test subject
        } else {
            String msg = String.format("Value of test suite attribute %s is missing or is not a File object.",
                    SuiteAttribute.TEST_SUBJECT.getName());
            TestSuiteLogger.log(Level.SEVERE, msg);
            throw new AssertionError(msg);
        }
    }

}
