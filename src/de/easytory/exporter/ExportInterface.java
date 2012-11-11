package de.easytory.exporter;

import java.util.Iterator;


/*
Easytory - the easy repository
Copyright (C) 2012, Ralf Konwalinka

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
public interface ExportInterface 
{
	//Datatypes
	public static final int STRING = 0;	// Value is a String
	
	public void init() throws Exception;
	public void processEntities(String entity, Iterator<String> attributeList) throws Exception;
	public void processItems(String entity, String itemId, String itemName, String note) throws Exception;
	public void processValues(String entity, String itemId, String itemName, String valueString, String attributeName, int valueType, String relatedEntity) throws Exception;
	public void processRelations(String entity, String itemId, String itemName, String targetId) throws Exception;
	public void finish() throws Exception;
}
