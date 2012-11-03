import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
public class ItemValue
{
    private Value value;
    private JLabel label;
    private JTextField text;
    private JComboBox combo;
    private JCheckBox checkBox;
    private Gui gui;
    private boolean checked;
    private Vector<Entity> entityList;
    private Controller controller;

    public ItemValue(Value value, Gui gui, Vector<Entity> entityList, final Controller controller ,JPanel itemPanel, int position)
    {   
        this.value = value;
        this.gui = gui;
        this.entityList = entityList;
        this.controller = controller;
        label = new JLabel(value.getName());
        text = new JTextField(value.getValue(),15); 
        
        checked = !value.getRelatedEntity().equals("");
        checkBox = new JCheckBox(value.getRelatedEntity());
        addCheckBoxListener();
        combo = new JComboBox(new Vector<String>());
        initCombo();
        if (checked) 
        {
            check(value.getRelatedEntity()); 
        }
        else
        {
            unCheck();
        }
        
                
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.ipadx = 5; c.ipady = 5; c.gridy = position;
        
        c.gridx = 0; itemPanel.add(label,c);
        c.gridx = 1; itemPanel.add(text,c);
        c.gridx = 2; itemPanel.add(checkBox,c);
        c.gridx = 3; itemPanel.add(combo,c);
    }
    
      
    private void addCheckBoxListener()
    {
      checkBox.addActionListener(new ActionListener() 
      {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if (checkBox.isSelected())
            {
                    
                    String s = (String)JOptionPane.showInputDialog(
                        gui,
                        "Choose an entity to link with attribute '" + value.getName() + "'\n\n","Link Attribute",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        getEntityArray(value.getName()),
                        value.getName());
                            
                    if ((s != null) && (s.length() > 0)) 
                    {
                        check(s);
                    }
            }
            else
            {
                     int response = JOptionPane.showConfirmDialog(gui, "Do you really want to unlink this attribute from entity '" + value.getRelatedEntity() + "' ?","Unlink Attribute", JOptionPane.YES_NO_OPTION);
                     if (response == JOptionPane.YES_OPTION) 
                     {   
                         unCheck();
                     }
                     else
                     {
                         checkBox.setSelected(true);    
                     }
            }
        }
      });    
        
    }    
    

    private Object[] getEntityArray(String extendetEntity)
    {
        boolean existExtendetEntity = false;
        Object[] entityArray = new Object[entityList.size()];
        Object[] extendetArray = new Object[entityList.size()+1];
        
        for (int i = 0; i < entityList.size(); i++)
        {
            entityArray[i] = entityList.get(i).getName();
            extendetArray[i] = entityArray[i];
            if (extendetEntity.equals(entityArray[i])) existExtendetEntity = true;
        }
        
        if (!existExtendetEntity)
        {
           extendetArray[entityList.size()] = extendetEntity;
           return extendetArray;
        }
        else
        {
            return entityArray;    
        }    
    }

    public Value getValue()
    {
        value.setValue(text.getText());
        return value;    
    }    
    
    private void check(String relatedEntity)
    {
        checked = true; 
        checkBox.setSelected(true);  
        value.setRelatedEntity(relatedEntity);
        checkBox.setText(relatedEntity);
        Vector<String> list = controller.getThingNames("", value.getRelatedEntity());
        combo.setModel(new DefaultComboBoxModel(list));
        combo.setVisible(true);
        combo.getEditor().setItem("");
    }
    
    private void unCheck()
    {  
        checked = false; 
        checkBox.setSelected(false);
        value.setRelatedEntity("");
        checkBox.setText("");
        combo.setModel(new DefaultComboBoxModel(new Vector<String>()));
        combo.setVisible(false);
    }
    
    private void initCombo()
    {
        
        combo.setEditable(true);
        combo.setSelectedItem("");
        combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() 
    	{
    	   @Override
    	  public void keyPressed(KeyEvent e) //Strange ENTER-KEY on combo box
    	  {
    	        if (checked && e.getKeyCode()==KeyEvent.VK_ENTER)
        	    {
        	       combo.getEditor().setItem(combo.getSelectedItem());
        	    }
    	  }
    	  
    	  @Override
    	  public void keyReleased(KeyEvent e)  
    	  {
        	    if (checked && e.getKeyCode()!=KeyEvent.VK_ENTER && e.getKeyCode()!=KeyEvent.VK_DOWN && e.getKeyCode()!=KeyEvent.VK_UP) 
        	    {
        	        combo.showPopup();
        	        String text = (String) combo.getEditor().getItem();
        	        Vector<String> list = controller.getThingNames(text, value.getRelatedEntity());
        	        combo.setModel(new DefaultComboBoxModel(list));
        	        combo.getEditor().setItem(text);
        	        combo.showPopup();
        	    }
     	  }
    	});
    	combo.addActionListener(new ActionListener() 
    	{
              public void actionPerformed(ActionEvent actionEvent) 
              {
                  String selectedText = (String)combo.getSelectedItem();
                  if (!selectedText.equals("")) text.setText(selectedText);
              }
        });
    }    

}
