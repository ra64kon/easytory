package de.easytory.exporter;

import java.util.Iterator;
import de.easytory.main.Controller;
import de.easytory.main.Entity;
import de.easytory.main.Thing;
import de.easytory.main.Value;
import de.easytory.tools.Logger;

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
public class ExportController 
{
    private Controller controller;
    
    public ExportController(Controller controller)
    {
        this.controller = controller;
    }
    
    public boolean export(String className)
    {
        ExportInterface export;
		try 
		{
			Class<?> theClass  = Class.forName("de.easytory.exporter." + className);
			export = (ExportInterface)theClass.newInstance();

			export.init();
			exportEntities(export);
			Iterator<Thing> i = controller.getThings("").iterator();
			while (i.hasNext())
			{
			    Thing t = i.next();
			    exportItem(export, t);
			    exportValues(export, t);
			    exportRelations(export, t);	
			}   
			export.finish();
			return true;
		} 
    	catch (Exception e) 
    	{
    		Logger.getInstance().log(e.toString());
    		return false;
		}
    }

    private void exportEntities(ExportInterface export) throws Exception 
	{
    	Iterator<Entity> entityList = controller.getEntities().iterator();
    	while (entityList.hasNext())
		{
		   Entity entity = entityList.next();
		   Iterator<String> attributeList = entity.getAttributeNames();
		   export.processEntities(entity.getName(), attributeList);
		}   
	}
    
	private void exportItem(ExportInterface export, Thing t) throws Exception 
	{
		String itemId = t.getId();
		String entity = t.getEntity();
		String itemName = t.getName();
		String note = t.getNote();
				if (t.isVirtual()) itemId = entity + "-" + itemName;
		export.processItems(entity, itemId, itemName, note);
	}
	
    private void exportValues(ExportInterface export, Thing t) throws Exception
    {
	    Iterator<Value> valueList = t.getValues();
	    while(valueList.hasNext())
	    {
	        Value v = valueList.next();
	        String itemId = t.getId();
		    String entity = t.getEntity();
		    String itemName = t.getName();
		    int valueType = ExportInterface.STRING;
		    String valueString = v.getValue();
		    String attributeName = v.getName();
		    String relatedEntity = v.getRelatedEntity();
		    export.processValues(entity, itemId, itemName, valueString, attributeName, valueType, relatedEntity);
	     }
    }
    
    private void exportRelations(ExportInterface export, Thing t) throws Exception
    {
	    Iterator<Thing> j = controller.getRelationsTo(t).iterator();
	    while(j.hasNext())
	    {
	        Thing target = j.next();
	        String itemId = t.getId();
		    String entity = t.getEntity();
		    String itemName = t.getName();
		    String targetId = target.getId(); 
		    if (target.isVirtual()) targetId = target.getEntity() + "-" + target.getName();;
		    export.processRelations(entity, itemId, itemName, targetId);
	     }
    }
    

	

}
