package de.easytory.main;


import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;


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
public class Thing 
{

    private Vector<Value> valueList = new Vector<Value>();
    private String id;
    private String name;
    private String oldName;
    private String relatedEntity="";
    private String entity;
    private String note="";
    private boolean isVirtual=false;    
 
    public Thing(String name, String entity, boolean isVirtual) 
    {
        this.name = name;
        this.oldName = name;
        this.entity = entity;
        this.isVirtual = isVirtual;
        id = UUID.randomUUID().toString();
    }
    
    public Thing(String name, String entity, String note) 
    {
        this.name = name;
        this.oldName = name;
        this.entity = entity;
        if (note!=null) this.note = note; // null-check is downwards compatible
        id = UUID.randomUUID().toString();
    }
    
    public Thing(String name, String entity, String id, String note) 
    {
        this.name = name;
        this.oldName = name;
        this.entity = entity;
        this.id = id;
        if (note!=null) this.note = note; // null-check is downwards compatible
    }
  

    public void addValue(Value value)
    {
        valueList.add(value);
    }
    
    public void addOrSetValue(Value value)
    {
        Iterator<Value> iter = valueList.iterator();
        boolean found = false;
        while (iter.hasNext())
        {
            Value v = iter.next();
            if (v.getId().equals(value.getId())) 
            {
                v.setValue(value.getValue());
                found = true;
            }
        }
        if (!found){addValue(value);}        
    }
    
    
    public Iterator<Value> getValues()
    {
        return valueList.iterator();    
    }
    
    /**
     * 
     *  Get all values to a given value name
     * 
     */
    
    public Iterator<Value> getValues(String valueName, String relatedEntity)
    {
        Vector<Value> result = new Vector<Value>(); 
        
        Iterator<Value> iter = valueList.iterator();
        boolean found = false;
        while (iter.hasNext())
        {
            Value v = iter.next();
            if (v.getName().equals(valueName)) 
            {
                result.add(v);   
                found = true;
            }
        }
        
        if (!found) // create new value if entity (other items) has new attribute
        {
            Value v = new Value(valueName,"",id,relatedEntity);    
            addValue(v);
            result.add(v);
        }
        
        return result.iterator();
    }
    
    
    public String getId(){return id;}
    
    public String getNote(){return note;}
    
    public String getName(){return name;}
    
    public String getRelatedEntity(){return relatedEntity;}
    
    public String getOldName(){return oldName;}
    
    public boolean isVirtual(){return isVirtual;}
    
    public void setRelatedEntity(String relatedEntity){this.relatedEntity = relatedEntity;}
    
    public void setName(String name)
    {
        this.oldName = this.name;
        this.name=name;
    }
    
    public void setNote(String note){this.note=note;}
    
    //public void setId(String id){this.id=id;}
    
    public String toString()
    {
        String view = "<html>";
        if (isVirtual) view = view + "<font style=\"color:666666\">"; else view = view + "<font>";
        view = view +  name + "</font><font style=\"color:999999;font-size:8px\"> - " + entity ;
        if (!relatedEntity.equals(entity) && !relatedEntity.equals("")) view = view + " (" + relatedEntity + ")";
        view = view + "</font></html>";
        return view;
    }
    
    public String toHTML()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><body><div style=\"font-family:Arial,sans-serif;color:222222;font-size:9px\">");
        buf.append("<table><tr><td valign=\"top\">");
        
        buf.append("<table><tr><td>" + entity + ": <b style=color:006666>" + name + "</b></td></tr>");
        buf.append("<tr><td></b>" + note + "</td></tr></table>"); 
        
        buf.append("</td><td valign=\"top\">");
        
        buf.append("<table>");
        Iterator<Value> i = getValues();
        while (i.hasNext())
        {
            Value v = i.next();
            if (v.getRelatedEntity().equals("")) // Don't show related items
            {
                buf.append("<tr><td>");
                buf.append(v.getName());
                buf.append(": </td><td><b style=color:444444>");
                buf.append(v.getValue());
                buf.append("</b></td></tr>");
            }
        }    
        buf.append("</table>");
        
        buf.append("</td></tr></table></div></body></html>");
        return buf.toString();
    }
    
    
    
    public String getEntity(){return entity;}
    
    public boolean equals(Object o) 
    {
        Thing t = (Thing)o;
        return name.equals(t.getName()) && entity.equals(t.getEntity());
    }
    
}