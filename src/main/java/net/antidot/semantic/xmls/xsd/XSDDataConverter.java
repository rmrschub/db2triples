/***************************************************************************
 *
 * XSD Data Converter
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Tools
 * 
 * Fichier			:	XSDDataConverter.java
 *
 * Description		:   Collection of useful tool-methods used for convert
 * 						XSD data. 
 * 
 * Reference 		:   XML Schema Part 2: Datatypes Second Edition, 
 * 						Paul V. Biron, Ashok Malhotra.
 * 						World Wide Web Consortium, 28 October 2004. 
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.xmls.xsd;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XSDDataConverter {

	// Log
	private static Log log = LogFactory.getLog(XSDDataConverter.class);

	/**
	 * Convert a Date object into a string in valid xsd:time format.
	 * 
	 * @param date
	 * @param timeZone
	 *            (optional, null otherwise)
	 * @return
	 */
	public static String dateToXSDTime(Date date, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] date : " + date
					+ " timeZone : " + timeZone);
		String result = null;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		result = df.format(date);
		// xsd:date requires a ":" in timeZone
		if (timeZone != null)
			result += timeZone;
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] result : " + result);
		return result;
	}

	/**
	 * Convert a Date object into a string in valid xsd:date format.
	 * 
	 * @param date
	 * @param timeZone
	 *            (optional, null otherwise)
	 * @return
	 */
	public static String dateToXSDDate(Date date, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] date : " + date
					+ " timeZone : " + timeZone);
		String result = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		result = df.format(date);
		// xsd:date requires a ":" in timeZone
		if (timeZone != null)
			result += timeZone;
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] result : " + result);
		return result;
	}

	/**
	 * Convert a Date object into a string in valid xsd:dateTime.
	 * 
	 * @param date
	 * @param timeZone
	 *            (optional, null otherwise)
	 * @return
	 */
	public static String dateToISO8601(Date date, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] date : " + date
					+ " timezone : " + timeZone);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String result = df.format(date);
		// xsd:dateTime requires a ":" in timeZone
		if (timeZone != null)
			result += timeZone;
		if (log.isDebugEnabled())
			log.debug("[SQLConnector:dateToISO8601] result : " + result);
		return result;
	}

}
