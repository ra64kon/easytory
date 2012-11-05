package de.easytory.main;
import java.util.UUID;

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
public class Value 
{
    private String id;
    private String value;
    private String name;
    private String thingId;
    private String relatedEntity;
    
    public Value(String name, String value, String thingId, String relatedEntity) 
    {
        this.value = value;
        this.name = name;
        this.thingId = thingId;
        this.relatedEntity = relatedEntity;
        id = UUID.randomUUID().toString();
    }
    
    public Value(String name, String value, String thingId, String id, String relatedEntity) 
    {
        this.value = value;
        this.name = name;
        this.thingId = thingId;
        this.id = id;
        this.relatedEntity = relatedEntity;
    }
    
    public String getValue() 
    {
        return value;
    }
    
    public String getRelatedEntity() 
    {
        return relatedEntity;
    }
    
    public void setRelatedEntity(String relatedEntity) 
    {
        this.relatedEntity = relatedEntity;
    }
    
    public void setValue(String value) 
    {
        this.value = value;
    }
    
    public String getName() 
    {
        return name;
    }
    
    public String toString()
    {
        return value;    
    }    

    public String getId()
    {
        return id;
    }
    
    public String getThingId()
    {
        return thingId;
    }
    
}
	