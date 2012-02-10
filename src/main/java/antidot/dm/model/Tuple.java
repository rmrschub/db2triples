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

/***************************************************************************
 *
 * Direct Mapping : Tuple
 * 
 * Empty interface which defines a generic tuple. 
 * A "tuple" is a representation of an entity stored in a table in database.
 * 
 * Example : in the 2 DirectMappingEngine, the same model is used (package antidot.sql.model), in this case Tuple == Row.
 * But another implementation of DirectMapping could use another SQL model (Tuple == SpecificRow for example).
 * 
 ****************************************************************************/
package antidot.dm.model;

public interface Tuple {}
