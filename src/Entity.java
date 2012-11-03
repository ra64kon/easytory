import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
public class Entity 
{
    // Name ist auch Schlüssel, um Instanzen auch nach dem Löschen der Entität eine neue Entität zuweisen zu können
    private String name;
    private String oldName;
    private Map<String,Attribute> attributeMap= new HashMap<String,Attribute>();
    
    /**
     * Constructor for objects of class Entity
     */
    public Entity(String name)
    {
        this.name = name;
        this.oldName = name;
    }
    
    /**
     * Add a attribute
     */
    public void setAttribute(String name)
    {
        if (!attributeMap.containsKey(name)) attributeMap.put(name, new Attribute(name));
    }
    
    public Iterator<String> getAttributeNames()
    {
        return attributeMap.keySet().iterator();    
    }
    
   
    public String getName(){return name;}
    
    public String getOldName(){return oldName;}
    
    public void setName(String name)
    {
        this.oldName = this.name;
        this.name = name;
    }
    
    /**
     * Für die Anzeige im GUI
     */
    public String toString(){return name;}
    
    public boolean equals(Object o) 
    {
        Entity e = (Entity)o;
        return name.equals(e.getName());
    }
}
