/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * SQL model : row
 *
 * Represents candidate keys of a database according to W3C database model.
 * A row consists of one set of attributes (or one tuple) corresponding to one 
 * instance of the entity that a table schema describes.
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 * @author jhomo
 *
 */
package net.antidot.sql.model.db;

import java.util.TreeMap;


public class Row implements Tuple {

	TreeMap<String, String> values;
	StdBody parentBody;

	public StdBody getParentBody() {
		return parentBody;
	}

	public void setParentBody(StdBody parentBody) {
		this.parentBody = parentBody;
	}

	public Row(TreeMap<String, String> values, StdBody parentBody) {
		this.values = values;
		this.parentBody = parentBody;
	}

	public TreeMap<String, String> getValues() {
		return values;
	}

	public void setValues(TreeMap<String, String> values) {
		this.values = values;
	}

	public String toString() {
		String result = "{[Row:toString] values = ";
		int i = 0;
		for (String columnName : values.navigableKeySet()) {
			i++;
			result += columnName + " => " + values.get(columnName);
			if (i < values.size())
				result += ", ";
		}
		result += "; parentBody = " + parentBody;
		if (parentBody != null) result += "; parentTable = " + parentBody.getParentTable();
		else result += ";";
		result += "}";
		return result;
	}
}
