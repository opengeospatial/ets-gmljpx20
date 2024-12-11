package org.opengis.cite.gmljpx20;

import java.net.URI;
import java.util.Map;

import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.opengis.cite.gmljpx20.util.ClientUtils;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;

/**
 * A supporting base class that sets up a common test fixture. These configuration methods
 * are invoked before those defined in a subclass.
 */
public class CommonFixture {

	/**
	 * Root test suite package (absolute path).
	 */
	public static final String ROOT_PKG_PATH = "/org/opengis/cite/gmljpx20/";

	/**
	 * HTTP client component (JAX-RS Client API).
	 */
	protected Client client;

	/**
	 * An HTTP request message.
	 */
	protected ClientRequest request;

	/**
	 * An HTTP response message.
	 */
	protected ClientResponse response;

	/**
	 * Initializes the common test fixture with a client component for interacting with
	 * HTTP endpoints.
	 * @param testContext The test context that contains all the information for a test
	 * run, including suite attributes.
	 */
	@BeforeClass
	public void initCommonFixture(ITestContext testContext) {
		Object obj = testContext.getSuite().getAttribute(SuiteAttribute.CLIENT.getName());
		if (null != obj) {
			this.client = Client.class.cast(obj);
		}
		obj = testContext.getSuite().getAttribute(SuiteAttribute.TEST_SUBJECT.getName());
		if (null == obj) {
			throw new SkipException("Test subject not found in ITestContext.");
		}
	}

	/**
	 * <p>
	 * clearMessages.
	 * </p>
	 */
	@BeforeMethod
	public void clearMessages() {
		this.request = null;
		this.response = null;
	}

	/**
	 * Obtains the (XML) response entity as a DOM Document. This convenience method wraps
	 * a static method call to facilitate unit testing (Mockito workaround).
	 * @param response A representation of an HTTP response message.
	 * @param targetURI The target URI from which the entity was retrieved (may be null).
	 * @return A Document representing the entity.
	 * @see ClientUtils#getResponseEntityAsDocument(com.sun.jersey.api.client.ClientResponse,
	 * java.lang.String)
	 */
	public Document getResponseEntityAsDocument(ClientResponse response, String targetURI) {
		return ClientUtils.getResponseEntityAsDocument(response, targetURI);
	}

}
