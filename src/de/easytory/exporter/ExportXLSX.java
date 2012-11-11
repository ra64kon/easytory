package de.easytory.exporter;

import java.io.FileOutputStream;
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
		
		// Write Header
		int colNumber = 0;
		ExtendetRow headRow = eSheet.createRow(entity);  	    
		headRow.createCell(colNumber,"XLSX-Header-Key", entity);
	}

	@Override
	public void processItems(String entity, String itemId, String itemName, String note) throws Exception 
	{
		ExtendetSheet eSheet = sheets.get(entity);

		ExtendetRow row = eSheet.createRow(itemId);
		row.createCell(0, itemId, itemName);
		
		eSheet.autoSizeColumn(0);
	}
	
	@Override
	public void processValues(String entity, String itemId, String itemName, String valueString, String attributeName, int valueType, String relatedEntity) throws Exception 
	{
		/*
		ExtendetSheet eSheet = sheets.get(entity);
		ExtendetRow headRow = eSheet.getRow("XLSX-Header-Key");
		int colNumber = headRow.getColNumber(attributeName);
		ExtendetRow entityRow = eSheet.getRow(itemId);
		entityRow.createCell(colNumber, attributeName, valueString);
		*/
	}
	
	@Override
	public void processRelations(String entity, String itemId, String itemName,
			String targetId) throws Exception {
		// nothing todo here for XLSX
		
	}

	@Override
	public void finish() throws Exception 
	{
		FileOutputStream fileOut = new FileOutputStream("easytory.xlsx");
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	class ExtendetSheet
	{
		private Sheet sheet;
		private HashMap<String,ExtendetRow> rows = new HashMap<String,ExtendetRow>();
		private int rowNumber = 0;
		
		public ExtendetSheet(Sheet sheet) 
		{
			this.sheet = sheet;
		}
		
		public ExtendetRow createRow(String name) 
		{
			ExtendetRow eRow = new ExtendetRow(sheet.createRow(rowNumber));
			rows.put(name, eRow);
			rowNumber++;
			return eRow;
		} 
		
		public ExtendetRow getRow(String name) 
		{
			return rows.get(name);
		} 
		
		public void autoSizeColumn(int col)
		{
			sheet.autoSizeColumn(col);
		}
	}

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

	}


}
