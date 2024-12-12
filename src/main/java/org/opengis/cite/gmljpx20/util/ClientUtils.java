package org.opengis.cite.gmljpx20.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientResponse;
import org.opengis.cite.gmljpx20.ReusableEntityFilter;
import org.w3c.dom.Document;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

/**
 * Provides various utility methods for creating and configuring HTTP client components.
 */
public class ClientUtils {

	/**
	 * Builds a client component for interacting with HTTP endpoints. The client will
	 * automatically redirect to the URI declared in 3xx responses. The connection timeout
	 * is 10 s. Request and response messages may be logged to a JDK logger (in the
	 * namespace "com.sun.jersey.api.client").
	 * @return A Client component.
	 */
	public static Client buildClient() {
		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.FOLLOW_REDIRECTS, true);
		config.property(ClientProperties.CONNECT_TIMEOUT, 10000);
		Client client = ClientBuilder.newClient(config);
		client.register(new ReusableEntityFilter());
		return client;
	}

	/**
	 * Constructs a client component that uses a specified web proxy. Proxy authentication
	 * is not supported. Configuring the client to use an intercepting proxy can be useful
	 * when debugging a test.
	 * @param proxyHost The host name or IP address of the proxy server.
	 * @param proxyPort The port number of the proxy listener.
	 * @return A Client component that submits requests through a web proxy.
	 */
	public static Client buildClientWithProxy(final String proxyHost, final int proxyPort) {
		ClientConfig config = new ClientConfig();
		config.connectorProvider(new ApacheConnectorProvider());
		SocketAddress addr = new InetSocketAddress(proxyHost, proxyPort);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
		config.property(ClientProperties.PROXY_URI, proxy);
		config.property(ClientProperties.FOLLOW_REDIRECTS, true);
		Client client = ClientBuilder.newClient(config);
		return client;
	}

	/**
	 * Creates a copy of the given MediaType object but without any parameters.
	 * @param mediaType A MediaType descriptor.
	 * @return A new (immutable) MediaType object having the same type and subtype.
	 */
	public static MediaType removeParameters(MediaType mediaType) {
		return new MediaType(mediaType.getType(), mediaType.getSubtype());
	}

	/**
	 * Obtains the (XML) response entity as a JAXP Source object and resets the entity
	 * input stream for subsequent reads.
	 * @param response A representation of an HTTP response message.
	 * @param targetURI The target URI from which the entity was retrieved (may be null).
	 * @return A Source to read the entity from; its system identifier is set using the
	 * given targetURI value (this may be used to resolve any relative URIs found in the
	 * source).
	 */
	public static Source getResponseEntityAsSource(ClientResponse response, String targetURI) {
		Source source = response.readEntity(DOMSource.class);
		if (null != targetURI && !targetURI.isEmpty()) {
			source.setSystemId(targetURI);
		}
		if (response.getEntityStream().markSupported()) {
			try {
				// NOTE: entity was buffered by client filter
				response.getEntityStream().reset();
			}
			catch (IOException ex) {
				Logger.getLogger(ClientUtils.class.getName())
					.log(Level.WARNING, "Failed to reset response entity.", ex);
			}
		}
		return source;
	}

	/**
	 * Obtains the (XML) response entity as a DOM Document and resets the entity input
	 * stream for subsequent reads.
	 * @param response A representation of an HTTP response message.
	 * @param targetURI The target URI from which the entity was retrieved (may be null).
	 * @return A Document representing the entity; its base URI is set using the given
	 * targetURI value (this may be used to resolve any relative URIs found in the
	 * document).
	 */
	public static Document getResponseEntityAsDocument(ClientResponse response, String targetURI) {
		DOMSource domSource = (DOMSource) getResponseEntityAsSource(response, targetURI);
		Document entityDoc = (Document) domSource.getNode();
		entityDoc.setDocumentURI(domSource.getSystemId());
		return entityDoc;
	}

	/**
	 * Checks if a GET request to a given URI returns HTTP 200 - OK
	 * @param uri The URI to check
	 * @return true, if HTTP 200 - OK was returned after a GET request, false otherwise
	 */
	public static boolean is200Response(URI uri) {
		URL url = null;
		int code = 0;

		try {
			url = uri.toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			code = connection.getResponseCode();

		}
		catch (Exception ee) {
			ee.printStackTrace();
		}

		return (code == 200);
	}

}
