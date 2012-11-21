package de.easytory.exporter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
public class ExportXLSX implements ExportInterface 
{
	private Workbook wb;
	private HashMap<String,ExtendetSheet> sheets = new HashMap<String,ExtendetSheet>();
	
	@Override
	public void init() throws Exception
	{
		wb = new XSSFWorkbook();
	}
	
	@Override
	public void processEntities(String entity, Iterator<String> attributeList) throws Exception 
	{
		// Create a new sheet for each entity
		ExtendetSheet eSheet = new ExtendetSheet(wb.createSheet(WorkbookUtil.createSafeSheetName(entity)));
		sheets.put(entity, eSheet);
		
		// Write entity to headRow / column:0
		Row headRow = eSheet.createRow("XLSX-HeadRowId");  	    
		Cell cell = headRow.createCell(0);
		cell.setCellValue(entity);
	}

	@Override
	public void processItems(String entity, String itemId, String itemName, String note) throws Exception 
	{
		ExtendetSheet eSheet = sheets.get(entity);
		Row row = eSheet.createRow(itemId);
		Cell cell = row.createCell(0);
		cell.setCellValue(itemName);
	}
	
	@Override
	public void processValues(String entity, String itemId, String itemName, String valueString, String attributeName, int valueType, String relatedEntity) throws Exception 
	{
		ExtendetSheet eSheet = sheets.get(entity);
		eSheet.storeValue(itemId, valueString, attributeName);
	}
	
	@Override
	public void processRelations(String entity, String itemId, String itemName, String targetId) throws Exception 
	{
		// nothing todo here for XLSX
	}

	@Override
	public void finish() throws Exception 
	{
		Iterator<ExtendetSheet> iSheets = sheets.values().iterator();
		while (iSheets.hasNext()) // iterate sheets
		{
			ExtendetSheet eSheet = iSheets.next();
			int col = 0;
			int maxOffset = 0; // Offset for more than one attribute with the same name
			String lastAttribute = "";
			Iterator<ExtendetValue> iValues = eSheet.getValues();
			Row headRow = eSheet.getRow("XLSX-HeadRowId");
			while (iValues.hasNext()) // iterate attribute-sorted values
			{
				ExtendetValue e = iValues.next();
				Row row = eSheet.getRow(e.getItemId());
				if (!e.getAttributeName().equals(lastAttribute)) // next column 
				{
					col = col + maxOffset;
					maxOffset = 0;
					col++;
					lastAttribute = e.getAttributeName();
				}
				// Set Value
				int offset = getOffset(row, col);
				if (maxOffset<offset) maxOffset = offset;
				row.createCell(col+offset).setCellValue(e.getValueString());
				
				// Set header
				if (headRow.getCell(col+offset)==null) 
				{
					headRow.createCell(col+offset).setCellValue(e.getAttributeName());
				}
			}
			// Autosize all columns
			col = 0;
			while(headRow.getCell(col)!=null) 
			{
				eSheet.autoSizeColumn(col);
				col++;
			}
		}
		FileOutputStream fileOut = new FileOutputStream("easytory.xlsx");
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	/**
	 * Get column offset (more than one attribute with the same name possible)
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private int getOffset(Row row, int col)
	{
		int offset = 0;
		while (row.getCell(col+offset)!=null) offset ++;
		return offset;
	}
	
	class ExtendetSheet
	{
		private Sheet sheet;
		private HashMap<String,Row> rows = new HashMap<String,Row>();
		private int rowNumber = 0;
		private ArrayList<ExtendetValue> values = new ArrayList<ExtendetValue> ();
		
		public ExtendetSheet(Sheet sheet) 
		{
			this.sheet = sheet;
		}
		
		public void storeValue(String itemId, String valueString, String attributeName)
		{
			ExtendetValue e = new ExtendetValue(itemId, valueString, attributeName);
			values.add(e);
		}
		
		public Iterator<ExtendetValue> getValues()
		{
			Collections.sort(values);
			return values.iterator();
		}
		
		public Row createRow(String id) 
		{
			Row eRow = sheet.createRow(rowNumber);
			rows.put(id, eRow);
			rowNumber++;
			return eRow;
		} 
		
		public Row getRow(String id) 
		{
			return rows.get(id);
		} 
		
		public void autoSizeColumn(int col)
		{
			sheet.autoSizeColumn(col);
		}
	}

	class ExtendetValue implements Comparable<ExtendetValue>
	{
		private String itemId;
		private String valueString;
		private String attributeName;
		
		public ExtendetValue(String itemId, String valueString, String attributeName)
		{
			this.itemId = itemId;
			this.valueString = valueString;
			this.attributeName = attributeName;
		}
		
		public String getItemId() 
		{
			return itemId;
		}
		public String getValueString() 
		{
			return valueString;
		}
		public String getAttributeName() 
		{
			return attributeName;
		}

		/**
		 * Sort by attributeName 
		 */
		@Override
		public int compareTo(ExtendetValue e) 
		{
			return attributeName.compareTo(e.getAttributeName());
		}

	}

	/*
	class ExtendetRow
	{
		private Row row;
		private HashMap<String,Integer> colNumbers = new HashMap<String,Integer>();
				
		public ExtendetRow(Row row)
		{
			this.row = row;
		}
		
		public int getColNumber(String name)
		{
			return colNumbers.get(name);
		}
		
		public void createCell(int colNumber, String key, String value)
		{
			Cell cell = row.createCell(colNumber);
			cell.setCellValue(value);
			colNumbers.put(key, colNumber);
		}

	}*/


}
