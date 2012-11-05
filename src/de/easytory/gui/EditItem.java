package de.easytory.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.easytory.main.Controller;
import de.easytory.main.Entity;
import de.easytory.main.Thing;
import de.easytory.main.Value;

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
public class EditItem extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private Controller controller = new Controller();
    private LinkedList<ItemValue> attributeList = new LinkedList<ItemValue>();
    private Gui gui;
    private int position = 0;
    
    /**
     * Constructor for objects of class ItemDialog
     */
    public EditItem(Controller controller, Entity entity, Thing thing, Gui gui)
    {
        super(new BorderLayout());
        this.controller = controller;
        this.gui = gui;
        
        Vector<Entity> entityList = controller.getEntities();
       
        boolean isAddOperation = thing.getName().equals("");
        
        JPanel thingPanel = new JPanel(new GridBagLayout());
        JPanel notePanel = new JPanel(new GridBagLayout());
        JPanel valuePanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel();
                   
        // Item und EntityFeld
        JTextField itemField = createSimpleInputField("Name", thing.getName(), thingPanel,0,1);
        JTextField entityField = createSimpleInputField("Entity", entity.getName(), thingPanel,0,2);
        JTextArea noteField = createTextAreaField("Note", thing.getNote(), notePanel,0,0);
        
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.ipadx = 15;c.ipady = 5;
        c.gridy = 0;c.gridx = 0;leftPanel.add(thingPanel,c);
        c.gridy = 0;c.gridx = 1;leftPanel.add(notePanel,c);
        
              
        setNewAttributePanel(thing, valuePanel, entityList);
        
        // Attributfelder anhand der Entity-Attribute erzeugen
        position = 1;
        if (!entity.getName().equals(""))
        {
            if (!isAddOperation) entityField.setEnabled(false);
            Iterator<String> iter = entity.getAttributeNames();
            while (iter.hasNext())
            {
                String attributeName = iter.next();
                String relatedEntity="";
                relatedEntity = controller.getRelatedEntity(attributeName);
                Iterator<Value> i = thing.getValues(attributeName,relatedEntity);
                while (i.hasNext())
                {
                    ItemValue iValue = new ItemValue(i.next(), gui, entityList, controller, valuePanel, position);
                    attributeList.add(iValue);
                    position++;
                }
            }
            
        }
                
        if (isAddOperation) // Add or Update?
        {
            buttonPanel.add(createNewItemButton(itemField, entityField, noteField));
        }
        else
        {
            buttonPanel.add(createUpdateItemButton(itemField, entityField, noteField ,thing));
        }
        
        buttonPanel.add(createCancelButton());
        
        /*
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(leftPanel);
        centerPanel.add(valuePanel);
        */         
        add(new JScrollPane(leftPanel),BorderLayout.NORTH);
        add(new JScrollPane(valuePanel),BorderLayout.CENTER);
        add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);
 
    }

    
    /**
     * Anlage eines neuen Items inkl. aller Attribute
     */ 
    private JButton createNewItemButton(final JTextField itemField, final JTextField entityField, final JTextArea noteField)
    {
      JButton b = new JButton("Add new item");
      b.addActionListener(new ActionListener() 
      {
            public void actionPerformed(ActionEvent e) 
            {
               Thing thing = new Thing(itemField.getText(),entityField.getText(),noteField.getText());
               Iterator<ItemValue> iter = attributeList.iterator();
               while (iter.hasNext())
               {
                  ItemValue f = iter.next();
                  Value v = f.getValue();
                  thing.addValue(v);
               }
               controller.addThing(thing);
               closeTab();
               gui.showStatus();
               gui.refreshLists();
            }
      });
      return b;
    }
    
    private JButton createUpdateItemButton(final JTextField itemField, final JTextField entityField, final JTextArea noteField, final Thing thing)
    {
      JButton b = new JButton("Save item");
      b.addActionListener(new ActionListener() 
      {
            public void actionPerformed(ActionEvent e) 
            {
               thing.setName(itemField.getText());
               thing.setNote(noteField.getText());
               Iterator<ItemValue> iter = attributeList.iterator();
               while (iter.hasNext())
               {
                  ItemValue f = iter.next();
                  Value v = f.getValue();
                  thing.addOrSetValue(v); 
               }
               if (thing.isVirtual())controller.addThing(thing); else controller.updateThing(thing, false);
               closeTab();
               gui.showStatus();
               gui.refreshLists();
            }
      });
      return b;
    }
    
    private JButton createCancelButton()
    {
      JButton b = new JButton("Cancel");
      b.addActionListener(new ActionListener() 
      {
            public void actionPerformed(ActionEvent e) 
            {
               closeTab();
            }
      });
      return b;
    }
    
      
    private JTextField createSimpleInputField(String labelName, String defaultValue, JPanel panel, int x, int y)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 5; c.ipady = 5;
        c.gridy = y;
        
        JLabel label = new JLabel(labelName);
        c.gridx = x;
        panel.add(label, c);

        JTextField text = new JTextField(defaultValue,15);
        text.setName(labelName);
        c.gridx = x+1;
        panel.add(text, c);
        return text;
    }  
    
    private JTextArea createTextAreaField(String labelName, String defaultValue, JPanel panel, int x, int y)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 5;c.ipady = 5;
        
        JLabel label = new JLabel(labelName);
        c.gridy = y;c.gridx = x;
        panel.add(label, c);

        JTextArea text = new JTextArea(defaultValue,3,40);
        text.setName(labelName);
        text.setLineWrap(true);
        c.gridy = y;c.gridx = x+1;
                
        JScrollPane sp = new JScrollPane(text);
        panel.add(sp, c);
        
        return text;
    }  
    
    private void setNewAttributePanel(final Thing thing,final JPanel valuePanel,final Vector<Entity> entityList)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.ipadx = 5; c.ipady = 5; c.gridy = 0; 
        
        final JLabel label = new JLabel("(New)");
        c.gridx = 0; valuePanel.add(label,c);

        final JTextField text = new JTextField("",15);
        c.gridx = 1; valuePanel.add(text,c);
                
        final JButton b = new JButton("Add");
        b.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                if (!text.getText().equals("")) 
                {
                    String relatedEntity="";
                    Value v = new Value(text.getText(), "", thing.getId(),relatedEntity);
                    ItemValue iValue = new ItemValue(v, gui, entityList, controller, valuePanel, position);
                    attributeList.add(iValue);
                    position++;
                }                      
                valuePanel.revalidate();
                valuePanel.repaint();
            }
        });

        c.gridx = 2; valuePanel.add(b);
    }    
    
    private void closeTab()
    {
        gui.closeTab(this);    
    }    
    
    

}
