package de.easytory.main;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import de.easytory.Start;
import de.easytory.database.Database;
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
public class Controller
{
    private String statusMessage = "";
    private Database db = new Database();
    
    /**
     * Constructor 
     */
    public Controller()
    {

    }

    public String getStatusMessage(){return statusMessage;}
    
    /**
     * Laden von Things aus der DB 
     */
    public Vector<Thing> getThings(String nameLike)
    {
        Vector<Thing> thingList = new Vector<Thing>();
        try
        {
           ResultSet rs = db.getThingyList(nameLike);
           while (rs.next()) 
           {
               Thing t = new Thing(rs.getString("name"), rs.getString("entity"), rs.getString("id"),rs.getString("note"));
               addValuesToThing(t);
               thingList.add(t); 
           }
           rs = db.getVirtualThingList(nameLike);
           while (rs.next()) 
           {
               Thing t = new Thing(rs.getString("value"), rs.getString("related_entity"), true);
               thingList.add(t); 
           }
        }
        catch(Exception e){ writeLog(e); }
        return thingList;
    }

    public Vector<String> getThingNames(String nameLike, String entity)
    {
        Vector<String> thingList = new Vector<String>();
        try
        {
           ResultSet rs = db.getThings(nameLike, entity);
           while (rs.next()) thingList.add(rs.getString("name")); 
           rs = db.getVirtualThings(nameLike, entity);
           while (rs.next()) thingList.add(rs.getString("value")); 
        }
        catch(Exception e){ writeLog(e); }
        return thingList;
    }
    
    public Vector<Thing> getThingsByEntity(String entity)
    {
        Vector<Thing> thingList = new Vector<Thing>();
        try
        {
           ResultSet rs = db.getThingyListByEntity(entity);
           while (rs.next()) 
           {
               Thing t = new Thing(rs.getString("name"), rs.getString("entity"), rs.getString("id"),rs.getString("note"));
               addValuesToThing(t);
               thingList.add(t); 
           }
           rs = db.getVirtualThings(entity);
           while (rs.next()) 
           {
               Thing t = new Thing(rs.getString("value"), entity, true);
               thingList.add(t); 
           }
        }
        catch(Exception e){ writeLog(e); }
        return thingList;
    }
    
     /**
     * Laden von Things aus der DB mit Filter
     */
    public Vector<Thing> getFilteredThings(LinkedList<FilterListItem> filterList, String entity)
    {
        if (filterList.size()==0) return getThingsByEntity(entity); //Kein Filter mitgegeben
        Vector<Thing> thingList = new Vector<Thing>();
        try
        {
           ResultSet rs = db.getFilteredThingyList(filterList, entity);
           while (rs.next()) 
           {
               Thing t = new Thing(rs.getString("name"), rs.getString("entity"), rs.getString("id"),rs.getString("note"));
               addValuesToThing(t);
               thingList.add(t); 
           }
        }
        catch(Exception e){ writeLog(e); }
        return thingList;
    }
    
    /**
     * Load things which are related to
     */
    public Vector<Thing> getRelationsTo(Thing thing)
    {
        Vector<Thing> thingList = new Vector<Thing>();
        try
        {
           //Outgoing Relations
           Iterator<Value> i = thing.getValues(); // Search thing-relations to all attributes
           while (i.hasNext())
           {
                Value v = i.next();
                if (!v.getRelatedEntity().equals("") && !v.getValue().equals("")) 
                {   
                    ResultSet rs = db.getThing(v.getValue(),v.getRelatedEntity()); 
                    while (rs.next()) 
                    {
                        Thing t = new Thing(rs.getString("name"), rs.getString("entity"), rs.getString("id"),rs.getString("note"));
                        t.setRelatedEntity(v.getName());
                        addValuesToThing(t);
                        thingList.add(t); 
                    }
                    //Add virtual things
                    Thing t = new Thing(v.getValue(), v.getRelatedEntity(), true);
                    t.setRelatedEntity(v.getName());
                    if (!thingList.contains(t)) thingList.add(t); // Distinct
                }
           }
        }
        catch(Exception e){ writeLog(e); }
        return thingList;
    }
    
     /**
     * Load thing which are related to
     */
    public Vector<Thing> getRelationsFrom(Thing thing)
    {
        Vector<Thing> thingList = new Vector<Thing>();
        try
        {
           //Incoming Relations
           ResultSet rs = db.getRelatedThingyList(thing.getName(),thing.getEntity()); 
           while (rs.next()) 
           {
               Thing t = new Thing(rs.getString("name"), rs.getString("entity"), rs.getString("id"),rs.getString("note"));
               addValuesToThing(t);
               thingList.add(t); 
           }
        }
        catch(Exception e){ writeLog(e); }
        return thingList;
    }
    
    public Entity getEntity(String name)
    {
        Entity entity = new Entity(name);
        try
        {
           addAttributes(entity);
        }
        catch(Exception e){ writeLog(e); }
        return entity;
    }
    
    private void addValuesToThing(Thing t) throws Exception
    {
        ResultSet rs = db.getValueList(t.getId());
        while (rs.next()) 
        {
            Value v = new Value(rs.getString("name"), rs.getString("value"), rs.getString("thing_id"), rs.getString("id"),rs.getString("related_entity"));
            t.addValue(v);
        }
    }
    
   
    /**
     * Attribute aus Values von Things erzeugen
     */
    private void addAttributes(Entity entity) throws Exception
    {
           ResultSet rs = db.getAttributeList(entity.getName());
           while (rs.next()) 
           {
               entity.setAttribute(rs.getString("name"));
           }
     
    }
  
    public Vector<FilterListItem> getFilter(String entity)
    {
        Vector<FilterListItem> v = new Vector<FilterListItem>();
        try
        {
           ResultSet rs = db.countValues(entity);
           while (rs.next()) 
           {
               String label =  "<html>" + rs.getString("c") + ": " + rs.getString("value") + " - <font style=\"color:999999;font-size:8px\">" + rs.getString("name") + "</font></hmtl>";
               v.add(new FilterListItem(label, rs.getString("name"), rs.getString("value"), entity));
               //if (rs.getInt("c")>1) v.add(new FilterListItem(label, rs.getString("name"), rs.getString("value"), entity));
           }
        }
        catch(Exception e){ writeLog(e); }
        return v;
    }
    
    /**
     * Get all entities that can be used as filter
     * 
     * @param entity
     * @return
     *
    public Iterator<String> getEntitiesByFilter(String entity)
    {
    	Vector<String> entityList = new Vector<String>();
    	Iterator<FilterListItem> filterList = getFilter(entity).iterator();
    	while (filterList.hasNext())
    	{
    		FilterListItem f = filterList.next();
    		if (!entityList.contains(f.getEntity())) entityList.add(f.getEntity());
    	}
    	return entityList.iterator();
    }*/
    
    public Vector<Entity> getEntities()
    {
        Vector<Entity> v = new Vector<Entity>();
        try
        {
           ResultSet rs = db.getEntityListFromThing();
           while (rs.next()) 
           {
               Entity e = new Entity(rs.getString("entity"));
               addAttributes(e);
               v.add(e);
           }
           rs = db.getEntityListFromValue();
           while (rs.next()) 
           {
               Entity e = new Entity(rs.getString("related_entity"));
               if (!v.contains(e)) v.add(e);
           }
        }
        catch(Exception e){ writeLog(e); }
        return v;
    }
    
    public void deleteThing(Thing t)
    {
        try
        {
           db.deleteThing(t.getId());
           statusMessage = "Item '" + t.getName() + "' deleted.";
        }
        catch(Exception e){ writeLog(e); }
    }    
    
    /**
     * Speichert neues Thing inkl. der Attribute
     * Erzeugt neue Entity, falls noch nicht vorhanden
     */
    public boolean addThing(Thing t)
    {
        if (t.getName().equals("") || t.getEntity().equals(""))
        {
            statusMessage = "Cannot create empty item or entity.";
            return false; 
        }
        else
        {
            try
            {
                db.insertThing(t.getId(), t.getName(), t.getEntity(), t.getNote()); 
                Iterator<Value> iter = t.getValues();
                while (iter.hasNext())
                {
                    Value value = iter.next(); 
                    if (!value.getValue().equals(""))
                    {
                        db.insertValue(value.getId(), value.getName(), value.getValue(), t.getId(), value.getRelatedEntity());
                        db.updateValueRelation(t.getEntity(),value.getRelatedEntity(),value.getName());
                    }
                }
                Entity e = new Entity(t.getEntity());
                addAttributes(e);
                statusMessage = "Item '" + t.getName() + "' successful created.";
                return true;
            }
            catch(Exception e)
            { 
            	writeLog(e);
            	return false;
            }
        }
    }
    
    public boolean deleteEntity(Entity e)
    {
         try
            {
                db.deleteThings(e.getName());
                db.deleteRelatedValues(e.getName());
                return true;
            }
            catch(Exception ex)
            {
            	writeLog(ex);
            	return false;
            }   
    
    }
    
    public boolean deleteThingRelations(Thing t)
    {
        try
        {
            db.deleteRelatedValues(t.getEntity(), t.getName());
            return true;
        }
        catch(Exception ex)
        {
        	writeLog(ex);
        	return false;
        }   
    
    }
    
    public boolean updateEntity(Entity e)
    {
        if (e.getName().equals(""))
        {
            statusMessage = "Cannot update empty entity.";
            return false; 
        }
        else
        {
            try
            {
                if (!e.getName().equals(e.getOldName())) 
                {
                    db.updateEntityName(e.getOldName(), e.getName());
                    db.updateRelatedEntities(e.getOldName(), e.getName());
                }
                return true;
            }
            catch(Exception ex)
            {
            	writeLog(ex);
            	return false;
            }
        }
    }
    
    
    public boolean updateThing(Thing t, boolean isImport)
    {
        if (t.getName().equals("") || t.getEntity().equals(""))
        {
            statusMessage = "Cannot update empty item or entity.";
            return false; 
        }
        else
        {
            try
            {
                int row = db.updateThing(t.getId(), t.getName(), t.getEntity(), t.getNote()); 
                Iterator<Value> iter = t.getValues();
                while (iter.hasNext())
                {
                    Value value = iter.next(); 
                    if (db.existValue(value.getId())) // exact check on id
                    {
                        if (!value.getValue().equals(""))
                        {
                            db.updateValue(value.getId(), value.getName(), value.getValue(), t.getId(),value.getRelatedEntity());
                            db.updateValueRelation(t.getEntity(),value.getRelatedEntity(),value.getName());
                        }
                        else //remove value, if there is no value
                        {
                            db.deleteValue(value.getId()); 
                        }
                    }
                    else //new Value added to existing item
                    {
                        if (!value.getValue().equals(""))
                        {
                            db.insertValue(value.getId(), value.getName(), value.getValue(), t.getId(), value.getRelatedEntity());
                            //Don't update during import
                            if (!isImport) db.updateValueRelation(t.getEntity(),value.getRelatedEntity(),value.getName()); 
                        }
                    }
                }
                if (!t.getName().equals(t.getOldName())) db.updateRelatedValues(t.getOldName(), t.getName() ,t.getEntity());
                statusMessage = row + " item '" + t.getName() + "' successful saved.";
                return true;
            }
            catch(Exception e)
            {
            	writeLog(e);
            	return false;
            }
        }
    }
    
    public String getRelatedEntity(String attribute)
    {
        try
        {
           //ResultSet rs = db.getVirtualThingsByEntity(entity);
           ResultSet rs = db.getRelatedEntities(attribute);
           if (rs.next()) return rs.getString("related_entity"); else return "";
        }
        catch(Exception e)
        {
        	writeLog(e);
        	return "";
        }
    }
    
    
    
    public boolean existValue(String name, String value, String thingId)
    {
        try
        {
           return db.existValue(name, value, thingId);
        }
        catch(Exception e)
        {
        	writeLog(e);
        	return false;
        }
        
    }
    
    
    /**
     * return null if thing not found
     */
    public Thing getThing(String thing, String entity)
    {
        try
        {
           ResultSet rs = db.getThing(thing, entity);
           Thing t=null;
           if (rs.next()) t = new Thing(rs.getString("name"), rs.getString("entity"), rs.getString("id"),rs.getString("note"));
           return t;
        }
        catch(Exception e)
        {
        	writeLog(e);
        	return null;
        }
    }
        
	private void writeLog(Exception e) 
	{
		Logger.getInstance().log(e.toString()); 
		statusMessage = "Operation failed (Refer to '" + Start.getLogFileName() + "' for details): " + e.toString();
	}
        
    public void dbDisconnext()
    {
        db.dbDisconnect();    
    }

}