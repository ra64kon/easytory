package de.easytory.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;

import de.easytory.Start;
import de.easytory.gui.FilterListItem;

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
public class Database
{
    private Connection conn;
    private boolean isConnected = false;
    
    private void dbConnect() throws Exception
    {
           Class.forName("org.h2.Driver");
           conn = DriverManager.getConnection("jdbc:h2:" + Start.getDatabaseName(), "sa", "");
           isConnected = true;
           
           DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables = dbm.getTables(null, null, "THING", null);
           if (!tables.next()) initDatabase();           
    }
    
    public void dbDisconnect() 
    {
        isConnected = false;
        try
        {
            conn.close();
        }
        catch(Exception e)
        {
            System.out.println("Database Disconnection Error: " + e.toString()); 
        }
    }
    
    
    private void initDatabase() throws Exception
    {
        String thing = "CREATE TABLE thing(id CHAR(36) PRIMARY KEY, name VARCHAR(100), entity VARCHAR(100), note VARCHAR(2048));";
        String value = "CREATE TABLE value(id CHAR(36) PRIMARY KEY, name VARCHAR(100), value VARCHAR(2048), thing_id CHAR(36), related_entity VARCHAR(100));";
        String index1 = "CREATE INDEX index_thing_name ON thing(name);";
        String index2 = "CREATE INDEX index_thing_entity ON thing(entity);";
        String index3 = "CREATE INDEX index_value_thing_id ON value(thing_id);";
        String index4 = "CREATE INDEX index_value_name ON value(name);";
        String index5 = "CREATE INDEX index_value_value ON value(value);";
        
        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);
        stmt.addBatch(thing);
        stmt.addBatch(value);   
        stmt.addBatch(index1);
        stmt.addBatch(index2);
        stmt.addBatch(index3);
        stmt.addBatch(index4);
        stmt.addBatch(index5);
        stmt.executeBatch();
        conn.setAutoCommit(true); 
        stmt.close();
    }
             
    public void insertThing(String id, String name, String entity, String note) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "INSERT INTO thing (id, name, entity, note) VALUES (?,?,?,?);";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,id);
        stmt.setString(2,name);
        stmt.setString(3,entity);
        stmt.setString(4,note);
        stmt.executeUpdate();
        stmt.close();
    }
    
    public void insertValue(String id, String name, String value, String thingId, String relatedEntity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "INSERT INTO value (id, name, value, thing_id, related_entity) VALUES (?,?,?,?,?);";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,id);
        stmt.setString(2,name);
        stmt.setString(3,value);
        stmt.setString(4,thingId);
        stmt.setString(5,relatedEntity);
        stmt.executeUpdate();
        stmt.close();
    }
    
    public int updateThing(String id, String name, String entity, String note) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "UPDATE thing SET name=?,entity=?,note=? WHERE id=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,name);
        stmt.setString(2,entity);
        stmt.setString(3,note);
        stmt.setString(4,id);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    public int updateEntityName(String oldEntityName, String newEntityName) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "UPDATE thing SET entity=? WHERE entity=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,newEntityName);
        stmt.setString(2,oldEntityName);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    /**
     * during rename of entities
     */
    public void updateRelatedEntities(String oldEntityName, String newEntityName) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql1 = "UPDATE value SET name=? WHERE related_entity=? AND name=?;";
        PreparedStatement stmt1 = conn.prepareStatement(sql1);
        stmt1.setString(1,newEntityName);
        stmt1.setString(2,oldEntityName);
        stmt1.setString(3,oldEntityName);
        stmt1.executeUpdate();
        stmt1.close();
        
        String sql2 = "UPDATE value SET related_entity=? WHERE related_entity=?;";
        PreparedStatement stmt2 = conn.prepareStatement(sql2);
        stmt2.setString(1,newEntityName);
        stmt2.setString(2,oldEntityName);
        stmt2.executeUpdate();
        stmt2.close();
    }
    
    public int updateValue(String id, String name, String value, String thingId,String relatedEntity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "UPDATE value SET name=?,value=?,thing_id=?,related_entity=? WHERE id=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,name);
        stmt.setString(2,value);
        stmt.setString(3,thingId);
        stmt.setString(4,relatedEntity);
        stmt.setString(5,id);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    /**
     * Update all other items with the given related_entity
     */
    public int updateValueRelation(String entity,String relatedEntity,String name) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "UPDATE value SET related_entity=? WHERE name=? AND thing_id IN (SELECT id FROM thing WHERE entity=?);";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,relatedEntity);
        stmt.setString(2,name);
        stmt.setString(3,entity);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    /**
     * Update all related values 
     */
    public int updateRelatedValues(String oldValue,String newValue, String relatedEntity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "UPDATE value SET value=? WHERE value=? AND related_entity=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,newValue);
        stmt.setString(2,oldValue);
        stmt.setString(3,relatedEntity);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    /**
     * Löschen eines Thing und aller zugehörigen Values
     */
    public void deleteThing(String id) throws Exception
    {
        if (!isConnected) dbConnect();
        String thing = "DELETE FROM thing WHERE id=?;";
        String value = "DELETE FROM value WHERE thing_id=?;";
        //Statement stmt = conn.createStatement();
        PreparedStatement stmtThing = conn.prepareStatement(thing);
        PreparedStatement stmtValue = conn.prepareStatement(value);
        stmtThing.setString(1,id);
        stmtValue.setString(1,id);
        
        conn.setAutoCommit(false);
        stmtValue.executeUpdate();
        stmtThing.executeUpdate();
        conn.commit();
        conn.setAutoCommit(true); 
        
        stmtThing.close();
        stmtValue.close();
    }
    
    public void deleteThings(String entityName) throws Exception
    {
        if (!isConnected) dbConnect();
        String value = "DELETE FROM value WHERE thing_id IN (SELECT id FROM thing WHERE entity=?);";
        String thing = "DELETE FROM thing WHERE entity=?;";
        //Statement stmt = conn.createStatement();
        PreparedStatement stmtThing = conn.prepareStatement(thing);
        PreparedStatement stmtValue = conn.prepareStatement(value);
        stmtThing.setString(1,entityName);
        stmtValue.setString(1,entityName);
        
        conn.setAutoCommit(false);
        stmtValue.executeUpdate();
        stmtThing.executeUpdate();
        conn.commit();
        conn.setAutoCommit(true); 
        
        stmtThing.close();
        stmtValue.close();
    }
    
    
    public int deleteValue(String id) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "DELETE FROM value WHERE id=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,id);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    public int deleteRelatedValues(String relatedEntity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "DELETE FROM value WHERE related_entity=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, relatedEntity);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    public int deleteRelatedValues(String relatedEntity, String value) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "DELETE FROM value WHERE related_entity=? AND value=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, relatedEntity);
        stmt.setString(2, value);
        int rows = stmt.executeUpdate();
        stmt.close();
        return rows;
    }
    
    
    public ResultSet getThingyList(String name) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT * FROM thing WHERE UPPER(name) LIKE ? ORDER BY entity,name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,name.toUpperCase() + "%");
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    public ResultSet getThings(String name, String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT * FROM thing WHERE entity=? AND UPPER(name) LIKE ? ORDER BY entity,name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entity);
        stmt.setString(2,name.toUpperCase() + "%");
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    /**
     *  Get virtual things - things that not in database, but should be in database (derived from value relation)
     * 
     */
    public ResultSet getVirtualThingList(String name) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT DISTINCT value,related_entity FROM value WHERE related_entity!='' AND UPPER(value) LIKE ? AND NOT (value IN (SELECT name FROM thing) AND related_entity IN (SELECT entity FROM thing)) ORDER BY related_entity,value;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,name.toUpperCase() + "%");
        //stmt.setString(2,name);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
     /**
     *  Get virtual things - things that not in database, but should be in database (derived from value relation)
     * 
     */
    public ResultSet getVirtualThings(String name, String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT DISTINCT value,related_entity FROM value WHERE related_entity=? AND UPPER(value) LIKE ? AND NOT (value IN (SELECT name FROM thing) AND related_entity IN (SELECT entity FROM thing)) ORDER BY related_entity,value;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entity);
        stmt.setString(2,name.toUpperCase() + "%");
        //stmt.setString(2,name);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    /**
     *  Gesucht werden die Things, deren Values mit der übergebenen Liste übereinstimmen
     *  
     *  Mangels unbekannter Alternative wurde eine etwas eigenwillige Variante implementiert, welche die Werte zunächst mit 
     *  OR verknüpft, nach Things gruppiert und die Anzahl Treffer mit der Anzahl Suchwerte vergleicht.
     */
    public ResultSet getFilteredThingyList(LinkedList<FilterListItem> filterList, String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT * FROM thing WHERE entity=? AND id IN (SELECT thing_id FROM value"; 
        
        //WHERE
        Iterator<FilterListItem> iter = filterList.iterator();
        boolean first = true;
        while (iter.hasNext())
        {
            if (first) sql = sql + " WHERE ("; else sql = sql + " OR (";
            sql = sql + "value.name=? AND value=?)";
            iter.next();
            first = false;
        }
        sql = sql + " GROUP BY thing_id HAVING count(DISTINCT id)=" + filterList.size() + " );";
       
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entity);
        
        //Set dynamic parameter
        iter = filterList.iterator();
        int counter = 2;
        while (iter.hasNext())
        {
            FilterListItem filterItem = iter.next();
            stmt.setString(counter,filterItem.getValueName());
            counter++;
            stmt.setString(counter,filterItem.getValue());
            counter++;
        }
        
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
   
    public ResultSet getThing(String name, String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT * FROM thing WHERE name=? AND entity=? ORDER BY entity,name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,name);
        stmt.setString(2,entity);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    /**
     * 
     * Incoming Relations
     * 
     */
    public ResultSet getRelatedThingyList(String thingName, String entityName) throws Exception
    {
        if (!isConnected) dbConnect();
        //String sql = "SELECT * FROM thing INNER JOIN value ON thing.id = value.thing_id WHERE value=? AND value.name=?;";
        String sql = "SELECT * FROM thing INNER JOIN value ON thing.id = value.thing_id WHERE value=? AND value.related_entity=? ORDER BY entity,name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,thingName);
        stmt.setString(2,entityName);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
   
    /**
     *  Get virtual things - things that not in database, but should be in database (derived from value relation)
     * 
     */
    public ResultSet getVirtualThings(String entityName) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT DISTINCT related_entity,value FROM value WHERE related_entity=? AND value NOT IN (SELECT name FROM thing WHERE entity=?) ORDER BY related_entity,value;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entityName);
        stmt.setString(2,entityName);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    /**
     * 
     * Abfrage der Filterwerte
     * 
     */
    public ResultSet countValues(String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        //String sql = "SELECT value.name, value ,COUNT(value) AS c FROM thing INNER JOIN value ON thing.id = value.thing_id WHERE entity=? GROUP BY value, value.name ORDER BY c DESC;";
        String sql = "SELECT value.name, value ,COUNT(value) AS c FROM thing INNER JOIN value ON thing.id = value.thing_id WHERE entity=? AND related_entity!='' GROUP BY value, value.name ORDER BY c DESC;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entity);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;      
    }
    
    public ResultSet getValueList(String thingId) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT * FROM value WHERE thing_id=? ORDER BY value;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,thingId);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    public boolean existValue(String name, String value, String thingId) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT id FROM value WHERE thing_id=? AND name=? AND value=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,thingId);
        stmt.setString(2,name);
        stmt.setString(3,value);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
    
    
    public boolean existValue(String id) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT id FROM value WHERE id=?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,id);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
    
    public ResultSet getAttributeList(String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT DISTINCT value.name FROM value INNER JOIN thing ON value.thing_id=thing.id WHERE thing.entity=? ORDER BY value.name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entity);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    public ResultSet getThingyListByEntity(String entity) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT * FROM thing WHERE entity=? ORDER BY name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,entity);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    public ResultSet getRelatedEntities(String attribute) throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT related_entity FROM value WHERE name=? AND related_entity<>'';";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,attribute);
        ResultSet rs = stmt.executeQuery();
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    
    public ResultSet getEntityListFromThing() throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT DISTINCT entity FROM thing ORDER BY entity;";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    public ResultSet getEntityListFromValue() throws Exception
    {
        if (!isConnected) dbConnect();
        String sql = "SELECT DISTINCT related_entity FROM value WHERE related_entity!='' ORDER BY related_entity;";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        //stmt.close(); ...waiting for garbage collector
        return rs;
    }
    
    
    
}