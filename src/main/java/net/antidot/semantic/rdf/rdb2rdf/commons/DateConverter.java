/***************************************************************************
 *
 * Date Converter
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	RDB2RDF Commons
 * 
 * Fichier			:	DateConverter.java
 *
 * Description		:	Collection of useful tool-methods used for manipulate
 * 						and convert date format.
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.antidot.semantic.xmls.xsd.XSDDataConverter;
import net.antidot.sql.model.type.SQLType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateConverter {
	
	// Log
	private static Log log = LogFactory.getLog(DateConverter.class);

	// Convert type methods

	/**
	 * Convert a SQL timestamp in a date format in a conform string date.
	 * 
	 * @param mySQLType
	 * @param timestamp
	 * @param timeZone
	 */
	public static String dateFormatToDate(SQLType sqlType,
			Long timestamp, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[DateConverter:dateFormatToDate] mySQLType : " + sqlType
					+ " timestamp : " + timestamp);
		// Constructs a Date object using the given milliseconds time value.
		// But, timestamp in MySQL is given in seconds.
		timestamp *= 1000;
		if (!sqlType.isDateType())
			throw new IllegalStateException(
					"[DateConverter:dateFormatToDate] MySQLType forbidden : it must be in a date format.");
		Date date = timestampToDate(timestamp);
		switch (sqlType) {
		case TIME:
			return XSDDataConverter.dateToXSDTime(date, timeZone);

		case DATE:
			return XSDDataConverter.dateToXSDDate(date, timeZone);

		case TIMESTAMP:
			return XSDDataConverter.dateToISO8601(date, timeZone);

		default:
			throw new IllegalStateException(
					"[DateConverter:dateFormatToDate] Unknown format date.");
		}
	}

	/**
	 * Convert a timestamp into a Date object.
	 */
	public static Date timestampToDate(Long timestamp) {
		if (log.isDebugEnabled())
			log.debug("[DateConverter:timestampToDate] timestamp : " + timestamp);
		// Date object represents a unqiue time point like the timestamp
		// Timezone is not take into account here.
		Date date = new Date(timestamp);
		if (log.isDebugEnabled())
			log.debug("[DateConverter:timestampToDate] converted Date : " + date);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddz");
		String test = df.format(date);
		if (log.isDebugEnabled())
			log.debug("[DateConverter:timestampToDate] timezone : " + test);
		return date;
	}
	

}
