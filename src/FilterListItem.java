
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
public class FilterListItem
{
   private String label;
   private String entity;
   private String valueName;
   private String value;
   private boolean isSelected = false;

   public FilterListItem(String label, String valueName, String value, String entity)
   {
      this.label = label;
      this.valueName = valueName;
      this.value = value;
      this.entity = entity;
   }

   public boolean isSelected()
   {
      return isSelected;
   }

   public void setSelected(boolean isSelected)
   {
      this.isSelected = isSelected;
   }

   public String getValue()
   {
        return value;    
   }
   
   public String getValueName()
   {
        return valueName;    
   }
   
   public String getEntity()
   {
        return entity;    
   }
   
   public String toString()
   {
      return label;
   }

}
