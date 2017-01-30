package org.opengis.cite.gmljpx20;

import java.net.URI;

/**
 * XML namespace names.
 * 
 * @see <a href="http://www.w3.org/TR/xml-names/">Namespaces in XML 1.0</a>
 *
 */
public class Namespaces {

    private Namespaces() {
    }

    /** SOAP 1.2 message envelopes. */
    public static final String SOAP_ENV = "http://www.w3.org/2003/05/soap-envelope";
    /** W3C XLink */
    public static final String XLINK = "http://www.w3.org/1999/xlink";
    /** OGC 06-121r3 (OWS 1.1) */
    public static final String OWS = "http://www.opengis.net/ows/1.1";
    /** ISO 19136 (GML 3.2) */
    public static final String GML = "http://www.opengis.net/gml/3.2";
    /** W3C XML Schema namespace */
    public static final URI XSD = URI.create("http://www.w3.org/2001/XMLSchema");
    /** Schematron (ISO 19757-3) namespace */
    public static final URI SCH = URI.create("http://purl.oclc.org/dsdl/schematron");
    /** OGC 09-146r2 (OGC GML Application Schema - Coverages, v1.0.1) */
    public static final String GMLCOV = "http://www.opengis.net/gmlcov/1.0";
    /** OGC 09-146r2 (OGC GML Application Schema - GMLJP2, v2.0) */
    public static final String GMLJP2 = "http://www.opengis.net/gmljp2/2.0";
    /** OGC 09-146r2 (OGC GML Application Schema - SWE, v2.0) */
    public static final String SWE = "http://www.opengis.net/swe/2.0";

}
