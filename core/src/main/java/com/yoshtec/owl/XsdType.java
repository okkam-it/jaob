package com.yoshtec.owl;

import org.semanticweb.owlapi.model.IRI;

/**
 * Enum Representing the XML Schema built in types as well as some 
 * String constants representing xsd built in URIs.</br>
 * 
 * Every type has two name spaces. The more common one can be used by
 * using the {@code toString()} or {@code getUri()} Methods, the alternate 
 * one can be obtained using the {@code toAltString()} and {@code getAltUri()}
 * Methods.</br>  
 * 
 * Further Reading:</br>
 * <a href="http://www.w3.org/TR/xmlschema-2/#namespaces">XML Schema Namespaces</a></br>
 * <a href="http://www.w3.org/TR/xmlschema-2/#built-in-datatypes">XML Schema Builtin Datatypes</a></br>
 * 
 * @author Jonas von Malottki
 *
 */

public enum XsdType {
	// Boolean
	BOOLEAN 			("boolean"),
		
	// Date
	DATETIME 			("dateTime"),
	DATE 				("date"),
	TIME 				("time"),
	GDATE 				("gDate"),
	GYEAR 				("gYear"),
	GDAY 				("gDay"),
	GMONTH 				("gMonth"),
	GYEARMONTH			("gYearMonth"),
	GMONTHDAY			("gMonthDay"),
	DURATION			("duration"),
	
	// Number
	DECIMAL 			("decimal"),
	FLOAT 				("float"),
	DOUBLE				("double"),
	
	// Integer Types
	INT 				("int"),
	SHORT 				("short"),
	BYTE 				("byte"),
	UNSIGNED_SHORT 		("unsignedShort"),
	UNSIGNED_BYTE 		("unsignedByte"),
	
	// Long Types
	LONG 				("long"),
	INTEGER 			("integer"),
	NEGATIVE_INTEGER 	("negativeInteger"), 
	NON_POSITIVE_INTEGER ("nonPositiveInteger"),
	NON_NEGATIVE_INTEGER ("nonNegativeInteger"),
	UNSIGNED_LONG 		("unsignedLong"),
	UNSIGNED_INT 		("unsignedInt"),
	POSITIVE_INTEGER 	("positiveInteger"),
	
	// string
	STRING				("string"),
	NORMALIZED_STRING 	("normalizedString"),
	TOKEN				("token"),
	LANGUAGE			("language"),
	NAME				("name"),
	NCNAME				("NCName"),
	ID 					("ID"),
	IDREF				("IDREF"),
	IDREFS				("IDREFS"),
	ENTITY				("ENTITY"),
	ENTITIES			("ENTITIES"),
	NMTOKEN				("NMTOKEN"), 
	NMTOKENS			("NMTOKENS"),
	ANYURI				("anyURI"),
	QNAME				("QName"), 
	NOTATION			("NOTATION");
		
	//// Constants
	/** the base URI for all xsd built-in types */
	public static final String XML_SCHEMA_BASE_URI = "http://www.w3.org/2001/XMLSchema";
	
	/** the alternate base URI for all xsd built-in types */
	public static final String XML_SCHEMA_BASE_URI_ALT = "http://www.w3.org/2001/XMLSchema-datatypes";

	// Often used Data URIs. Practical use in annotations.	
	/** URI String of xsd:string data type */ 
	public static final String XSD_STRING_URI = XML_SCHEMA_BASE_URI + "#string";
	/** URI String of xsd:int data type */
	public static final String XSD_INT_URI = XML_SCHEMA_BASE_URI + "#int";
	/** URI String of xsd:long data type */
	public static final String XSD_LONG_URI = XML_SCHEMA_BASE_URI + "#long";
	/** URI String of xsd:integer data type */
	public static final String XSD_INTEGER_URI = XML_SCHEMA_BASE_URI + "#integer";
	/** URI String of xsd:date data type */
	public static final String XSD_DATE_URI = XML_SCHEMA_BASE_URI + "#date";
	/** URI String of xsd:dateTime data type */
	public static final String XSD_DATETIME_URI = XML_SCHEMA_BASE_URI + "#dateTime";
	/** URI String of xsd:time data type */
	public static final String XSD_TIME_URI = XML_SCHEMA_BASE_URI + "#time";

	
	/*
	 * Enum Class Implementation
	 */
	
	private final IRI uri;
	private final IRI urialt;
	
	private XsdType(String uriFragment) {
		this.uri = IRI.create(XML_SCHEMA_BASE_URI + "#" + uriFragment);
		this.urialt = IRI.create(XML_SCHEMA_BASE_URI_ALT + "#" + uriFragment);
	}
	
	/**
	 * @return String of the URI of this XsdType
	 */
	@Override
	public String toString(){
		return uri.toString();
	}
	
	/**
	 * @return String of the alternate URI of this XsdType
	 */
	public String toAltString(){
		return uri.toString();
	}
	
	/**
	 * @return URI of the built in Type
	 */
	public IRI getUri(){
		return uri;
	}
	
	/**
	 * @return Alternative URI of the build in Type
	 */
	public IRI getAltUri(){
		return urialt;
	}
	
	/**
	 * 
	 * @param uri String representation of an uri
	 * @return The XsdType corresponding to the given uri, if existent otherwise {@code null}
	 */
	public static XsdType fromUri(String uri){
		for(XsdType x : XsdType.values()){
			if(x.toString().equals(uri) || x.toAltString().equals(uri)){
				return x;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param uri the URI of a builtin type
	 * @return The XsdType corresponding to the given uri, if existent otherwise {@code null}
	 */
	public static XsdType fromIri(IRI uri){
		if("http://www.w3.org/2000/01/rdf-schema#Literal".equals(uri.toString())){
			return XsdType.STRING;//TODO ---- porcata
		}
		if("http://www.w3.org/2001/rdf-schema#Literal".equals(uri.toString())){
			return XsdType.STRING;//TODO ---- porcata
		}
		for(XsdType x : XsdType.values()){
			if(x.uri.equals(uri) || x.urialt.equals(uri)){
				return x;
			}
		}
		return null;
	}
}