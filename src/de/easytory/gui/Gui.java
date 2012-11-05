package de.easytory.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.easytory.Start;
import de.easytory.exporter.ExportGraphML;
import de.easytory.importer.ImportCVS;
import de.easytory.main.Controller;
import de.easytory.main.Entity;
import de.easytory.main.Thing;

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
public class Gui extends JFrame 
{

private static final long serialVersionUID = 1L;
private Controller controller = new Controller();

private final JTextPane itemView = new JTextPane(); 
private final JTextArea statusArea = new JTextArea(2, 10);

private final JList entityList = new JList();
private final JList itemList = new JList();
private final JList filterList = new JList();
private final JList relationToList = new JList();
private final JList relationFromList = new JList();

private Thing selectedItem;
private Thing selectedItemInList;
private Entity selectedEntity;

final JTextField thing = new JTextField();

final JTabbedPane tpMain = new JTabbedPane();

public void showStatus()
{
    statusArea.setText(controller.getStatusMessage());
}

public void showStatus(String message)
{
    statusArea.setText(message);
    //System.out.println(message); 
    
    //nothing of this works:
    statusArea.revalidate(); 
    statusArea.repaint();
    this.repaint();
}

public Gui() 
{
    super("Easytory - " + Start.getVersion());
    
    itemView.setContentType("text/html"); 
    itemView.setEditable(false);
           
    initMenu();    
    initItemList();
    initEntityList();
    initFilterList();
    initRelationToList();
    initRelationFromList();
    
    //Quicksearch
    JPanel input = new JPanel(new GridLayout(1, 2));
    initThingTextField();
    input.add(thing);
    input.add(new JLabel(""));
    
    // add Border
    entityList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Entities"),BorderFactory.createEmptyBorder(5,5,5,5)));
    filterList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Filter"),BorderFactory.createEmptyBorder(5,5,5,5)));
    itemList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Items"),BorderFactory.createEmptyBorder(5,5,5,5)));
    relationToList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Relations to"),BorderFactory.createEmptyBorder(5,5,5,5)));
    relationFromList.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Relations from"),BorderFactory.createEmptyBorder(5,5,5,5)));
    input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Quick Search"),BorderFactory.createEmptyBorder(5,5,5,5)));
    
    JScrollPane spEntity = new JScrollPane(entityList);
    JScrollPane spFilter = new JScrollPane(filterList);
    JScrollPane spItem = new JScrollPane(itemList);
    JScrollPane spRelationTo = new JScrollPane(relationToList);
    JScrollPane spRelationFrom = new JScrollPane(relationFromList);
           
    // No ScrollPane border
    spEntity.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
    spFilter.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
    spItem.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
    spRelationTo.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
    spRelationFrom.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
    
    // Split relations
    JPanel relationPanel = new JPanel(new GridLayout(2, 1));
    relationPanel.add(spRelationTo);
    relationPanel.add(spRelationFrom);
    
    // Central Lists
    JPanel listPanel = new JPanel(new GridLayout(1, 4));
    listPanel.add(spEntity);
    listPanel.add(spFilter);
    listPanel.add(spItem);
    listPanel.add(relationPanel);
    
    //Buttonleiste
    JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
    buttonPanel.add(createAddButton());
    buttonPanel.add(createEditButton());
    buttonPanel.add(createRemoveButton());
   
    // Item detail panel with buttons
    JPanel detailPanel = new JPanel(new BorderLayout());
    detailPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Item Details"),BorderFactory.createEmptyBorder(5,5,5,5)));
    detailPanel.add(itemView,BorderLayout.CENTER);
    detailPanel.add(buttonPanel,BorderLayout.EAST);
    
    //Zentrales Panel mit Suche und Deteilanzeige des aktuellen Item
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(listPanel,BorderLayout.CENTER);
    centerPanel.add(detailPanel,BorderLayout.SOUTH);
    tpMain.addTab(Start.getDatabaseName(),centerPanel);
    
       
    statusArea.setText("This program comes with ABSOLUTELY NO WARRANTY! This is free software, and you are welcome to redistribute it.");
           
    getContentPane().add(input, BorderLayout.NORTH);
    getContentPane().add(tpMain, BorderLayout.CENTER);
    getContentPane().add(statusArea, BorderLayout.SOUTH);
  }
  
  private JButton createAddButton()
  {
      final Gui gui = this;
      JButton b = new JButton("Add");
      b.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent e) 
        {
            Entity entity = (Entity) entityList.getSelectedValue();
            if (entity==null) entity = controller.getEntity("");
            if (selectedItem==null) 
            {   
                entity = controller.getEntity(entity.getName());          
            }
            else
            {
                entity = controller.getEntity(selectedItem.getEntity());
            }
            EditItem editItem = new EditItem(controller, entity, new Thing("", entity.getName(),false), gui);
            tpMain.add("New item",editItem);
            tpMain.setSelectedComponent(editItem);
        }
      });
      return b;
  }
  
  private JButton createRemoveButton()
  {
      JButton b = new JButton("Remove");
      statusArea.setText("");
      final Gui gui = this;
      b.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent e) 
        {
            if (selectedItem != null)
            {
                if (!selectedItem.isVirtual())
                {
                    int response = JOptionPane.showConfirmDialog(gui, "Do you really want to delete item '" + selectedItem.getName() + "'?","Delete item", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) 
                    {
                        controller.deleteThing(selectedItem);
                        showStatus();
                        gui.refreshLists();
                    }
                }
                else
                {
                    int response = JOptionPane.showConfirmDialog(gui, "Do you want to delete all relations to this item?\n\n","Delete item", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) 
                    {
                        controller.deleteThingRelations(selectedItem);
                        showStatus();
                        gui.refreshLists();
                    } 
                    //JOptionPane.showMessageDialog(gui, "Item '" + selectedItem.getName() + "' is virtual and derived from a related item.\nTake a look at 'Relations from'.\n");    
                }
            }
            else
            {
                statusArea.setText("No item selected.");
            }
        }
      });
      return b;
  }
  
  private JButton createEditButton()
  {
      final Gui gui = this;
      JButton b = new JButton("Edit");
      b.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent e) 
        {
            if (selectedItem==null) 
            {   
                statusArea.setText("No item selected.");   
                return;            
            }
            Entity entity = controller.getEntity(selectedItem.getEntity());
            EditItem editItem = new EditItem(controller, entity, selectedItem, gui);
            tpMain.add("Edit item",editItem);
            tpMain.setSelectedComponent(editItem);
        }
      });
      return b;
  }

  private void initThingTextField()
  {
    thing.getDocument().addDocumentListener(new DocumentListener()
    {
            @Override
            public void changedUpdate(DocumentEvent e) {}       
            @Override
            public void insertUpdate(DocumentEvent e) 
            {
                clearAllLists(false);
                itemList.setListData(controller.getThings(thing.getText()));
            }
            @Override
            public void removeUpdate(DocumentEvent e) 
            {
                if (!thing.getText().equals("")) 
                {
                    itemList.setListData(controller.getThings(thing.getText()));
                    unSelectItem();
                    showStatus();
                }
                else
                {
                    clearAllLists(true);   
                }
                showStatus();
            }
     });    
  }
   
  private void initItemList()
  {
      //itemList.setListData(controller.getThings("",""));
      showStatus();
      itemList.setFont( entityList.getFont().deriveFont(Font.PLAIN)); // Font ohne bold
      itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      itemList.addListSelectionListener(new ListSelectionListener() 
      {
            public void valueChanged(ListSelectionEvent listSelectionEvent) 
            {
                if (!listSelectionEvent.getValueIsAdjusting()) 
                {                      
                   Thing t = (Thing) itemList.getSelectedValue();
                   if (t!=null)
                   {
                        selectItem(t);
                        selectedItemInList = t;
                        relationToList.setListData(controller.getRelationsTo(t));
                        relationFromList.setListData(controller.getRelationsFrom(t));
                        showStatus();
                   }
                }
            }
      });
  }
  
  private void selectItem(Thing t)
  {
        itemView.setText(t.toHTML());  
        selectedItem = t;
  }
  
  private void selectEntity(Entity e)
  {
        selectedEntity = e;
  }
  
  private void unSelectItem()
  {
        itemView.setText("");  
        selectedItem = null;
  }
  
  /**
   * Shows options to filter itemlist
   */
  private void initFilterList()
  {
      filterList.setCellRenderer(new CheckListRenderer());
      filterList.setFont(filterList.getFont().deriveFont(Font.PLAIN)); // Font ohne bold
      filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);         
      filterList.addMouseListener(new MouseAdapter() 
      {
        public void mouseClicked(MouseEvent e) 
        {
            int index = filterList.locationToIndex(e.getPoint());
            ListModel model = filterList.getModel();
            FilterListItem f = (FilterListItem) model.getElementAt(index);
            f.setSelected(!f.isSelected()); // Click CheckBox 
            viewFilteredItems();
            filterList.repaint(filterList.getCellBounds(index, index));
        }
      });
  }
  
  private boolean viewFilteredItems()
  {
        ListModel model = filterList.getModel();
        int n = model.getSize();
        if (n > 0)
        {
            LinkedList<FilterListItem> fList = new LinkedList<FilterListItem>();
            for (int i = 0; i < n; i++) 
            {
                FilterListItem fi = (FilterListItem) model.getElementAt(i);
                if (fi.isSelected()) fList.add(fi);
            } 
            itemList.setListData(controller.getFilteredThings(fList,selectedEntity.getName()));   
            showStatus();
            return true;
        }
        else
        {
            return false;    
        }
  }
  
  private void initRelationToList()
  {
      relationToList.setFont(filterList.getFont().deriveFont(Font.PLAIN)); // Font ohne bold
      relationToList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      relationToList.addListSelectionListener(new ListSelectionListener() 
      {
            public void valueChanged(ListSelectionEvent listSelectionEvent) 
            {
                if (!listSelectionEvent.getValueIsAdjusting()) 
                {
                   Thing t = (Thing) relationToList.getSelectedValue();
                   if (t!=null) selectItem(t);
                }
            }
      });
      relationToList.addMouseListener(new MouseAdapter() 
      {
        public void mouseClicked(MouseEvent e) 
        {
            if(e.getClickCount() == 2) 
            {
                Thing t = (Thing) relationToList.getSelectedValue();
                t.setRelatedEntity("");
                updateItemList(t);
            }
        }
      });
  }
  
  private void initRelationFromList()
  {
      relationFromList.setFont(filterList.getFont().deriveFont(Font.PLAIN)); // Font ohne bold
      relationFromList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      relationFromList.addListSelectionListener(new ListSelectionListener() 
      {
            public void valueChanged(ListSelectionEvent listSelectionEvent) 
            {
                if (!listSelectionEvent.getValueIsAdjusting()) 
                {
                   Thing t = (Thing) relationFromList.getSelectedValue();
                   if (t!=null) selectItem(t);
                }
            }
      });
      relationFromList.addMouseListener(new MouseAdapter() 
      {
        public void mouseClicked(MouseEvent e) 
        {
            if(e.getClickCount() == 2) updateItemList((Thing) relationFromList.getSelectedValue());
        }
      });
  }
  
  
  private void initEntityList()
  {
      entityList.setListData(controller.getEntities());
      showStatus();
      entityList.setFont(entityList.getFont().deriveFont(Font.PLAIN)); // Font ohne bold
      entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      entityList.addListSelectionListener(new ListSelectionListener() 
      {
            public void valueChanged(ListSelectionEvent listSelectionEvent) 
            {
                if (!listSelectionEvent.getValueIsAdjusting()) 
                {
                   Entity entity = (Entity) entityList.getSelectedValue();
                   if (entity!=null)
                   {
                        clearAllLists(false);
                        selectEntity(entity);
                        itemList.setListData(controller.getThingsByEntity(selectedEntity.getName()));
                        filterList.setListData(controller.getFilter(selectedEntity.getName()));
                        showStatus();            
                   }                                 
                }
            }
      });
  }
  
  public void refreshLists()
  {
      if (selectedEntity!=null)
      {
          entityList.setListData(controller.getEntities());
          if (!viewFilteredItems()) itemList.setListData(controller.getThingsByEntity(selectedEntity.getName()));
          if (selectedItemInList != null)
          {
              selectItem(selectedItemInList);
              relationToList.setListData(controller.getRelationsTo(selectedItemInList));
              relationFromList.setListData(controller.getRelationsFrom(selectedItemInList));
          }
          else
          {
              relationToList.setListData(new Thing[0]);
              relationFromList.setListData(new Thing[0]);
          }     
      }
      else
      {
          entityList.setListData(controller.getEntities());
      }
  }
  
  /**
   * write a new item in item-list and select the item
   */
  private void updateItemList(Thing t)
  {
        clearAllLists(true);
        Vector<Thing> v = new Vector<Thing>();
        v.add(t);
        itemList.setListData(v);
        itemList.setSelectedIndex(0); // otherwise setSelectedValue will raise a nullpointer-expeption (only once) 
        itemList.setSelectedValue(t,false);    
  }
  
  
  public void closeTab(JPanel p)
  {
        tpMain.remove(p);    
  }
  

  private void clearAllLists(boolean reloadEntities)
  {
      unSelectItem();
      filterList.setListData(new FilterListItem[0]);
      relationToList.setListData(new Thing[0]);
      relationFromList.setListData(new Thing[0]);
      itemList.setListData(new Thing[0]);
      if (reloadEntities) entityList.setListData(controller.getEntities());
      //showStatus(); // Überlagert evtl. andere Statusmeldungen
  }


  private void initMenu()
  {
      // Menu items
    JMenuItem about = new JMenuItem("About");
    final Gui gui = this;
    about.addActionListener(new ActionListener() 
    {
        public void actionPerformed(ActionEvent e) 
        {
             AboutDialog about = new AboutDialog(gui);
             about.setSize(450, 400);
             about.setLocationRelativeTo(gui);
             about.setVisible(true);
        }
    });
        
    JMenuItem importCSV = new JMenuItem("Import CSV");
    importCSV.addActionListener(new ActionListener() 
    {
        public void actionPerformed(ActionEvent e) 
        {
            
                final JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(gui);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    int response = JOptionPane.showConfirmDialog(gui, "Backup your database file before importing data!\nDo you really want to start importing '" + chooser.getSelectedFile() + "' now?","Import CSV", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) 
                    {
                        final ImportCVS ie = new ImportCVS(controller,gui);
                        
                        SwingUtilities.invokeLater(new Runnable() 
                        {     
                            public void run() 
                            {         
                                 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); 
                                 String result = ie.importCSV(chooser.getSelectedFile());
                                 setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
                                 JOptionPane.showMessageDialog(gui, result);
                                 clearAllLists(true);
                            }
                        }); 
                    }
                } 
                else 
                {
                    //cancel
                }
            
        }
    });
    
    JMenuItem exportGraphML = new JMenuItem("Export GraphML");
    exportGraphML.addActionListener(new ActionListener() 
    {
        public void actionPerformed(ActionEvent e) 
        {
            SwingUtilities.invokeLater(new Runnable() 
            {     
                public void run() 
                {         
                     int response = JOptionPane.showConfirmDialog(gui, "Do you want to export all data to GraphML (this can take a while)?","Export GraphML", JOptionPane.YES_NO_OPTION);
                     if (response == JOptionPane.YES_OPTION) 
                     {
                         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); 
                         ExportGraphML e = new ExportGraphML(controller);
                         if (e.exportGraphML())
                         {
                            JOptionPane.showMessageDialog(gui, "Export file 'Easytory.graphml' successful created.");
                         }
                         else
                         {
                             JOptionPane.showMessageDialog(gui,"Exporting GraphML failed.", "Export error",JOptionPane.ERROR_MESSAGE);
                         }
                         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
                     }
                }
            }); 
        }
    });
    
    JMenuItem deleteEntity = new JMenuItem("Delete Entity");
    deleteEntity.addActionListener(new ActionListener() 
    {
        public void actionPerformed(ActionEvent e) 
        {
            SwingUtilities.invokeLater(new Runnable() 
            {     
                public void run() 
                {         
                   Entity entity = (Entity) entityList.getSelectedValue();
                   if (entity!=null)
                   {
                        int response = JOptionPane.showConfirmDialog(gui, "Entity '" + entity.getName() +  "' will be deleted!\n\nDo you want to delete all items and all their relations?\n\n","Delete Entity", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) 
                        {
                             if (controller.deleteEntity(entity)) 
                             {
                                 clearAllLists(true);
                                 JOptionPane.showMessageDialog(gui, "Entity '" + entity.getName() +  "' deleted.");   
                             }
                             else 
                             {
                                 JOptionPane.showMessageDialog(gui,"Error deleting items.", "Delete Entity",JOptionPane.ERROR_MESSAGE);
                             }
                        } 
                   }
                   else
                   {
                         JOptionPane.showMessageDialog(gui,"Please choose an entity.", "Delete Entity",JOptionPane.WARNING_MESSAGE);
                   } 
                }
            }); 
        }
    });
    
    
    JMenuItem renameEntity = new JMenuItem("Rename Entity");
    renameEntity.addActionListener(new ActionListener() 
    {
        public void actionPerformed(ActionEvent e) 
        {
            SwingUtilities.invokeLater(new Runnable() 
            {     
                public void run() 
                {         
                   Entity entity = (Entity) entityList.getSelectedValue();
                   if (entity!=null)
                   {
                         String newName = (String)JOptionPane.showInputDialog(gui,"Change entity name from '" + entity.getName() + "' to:");
                         if (newName!=null)
                         {
                             if (newName.equals(""))
                             {
                                 JOptionPane.showMessageDialog(gui,"Name cannot be empty.", "New entity name",JOptionPane.WARNING_MESSAGE);
                             }
                             else
                             {
                                 entity.setName(newName);
                                 if (controller.updateEntity(entity)) 
                                 {
                                     clearAllLists(true);
                                     JOptionPane.showMessageDialog(gui, "Entity name changed.");   
                                 }
                                 else 
                                 {
                                     JOptionPane.showMessageDialog(gui,"Error updating items.", "New entity name",JOptionPane.ERROR_MESSAGE);
                                 }
                             }
                         }
                   }
                   else
                   {
                         JOptionPane.showMessageDialog(gui,"Please choose an entity.", "New entity name",JOptionPane.WARNING_MESSAGE);
                   }
                }
            }); 
        }
    });
    
    // Menu
    JMenu fMenu = new JMenu("File");
    fMenu.add(importCSV);
    fMenu.add(exportGraphML);
    
    JMenu eMenu = new JMenu("Entity");
    eMenu.add(renameEntity);
    eMenu.add(deleteEntity);
    
    JMenu iMenu = new JMenu("Info");
    iMenu.add(about);
    
    JMenuBar menuBar = new JMenuBar();
    //menuBar.add(Box.createHorizontalGlue());
    menuBar.add(fMenu);
    menuBar.add(eMenu);
    menuBar.add(iMenu);
    setJMenuBar(menuBar);
  }
  
  /**
   * JList with checkable Items
   */
  class CheckListRenderer extends JCheckBox implements ListCellRenderer
  {
	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus)
       {
          setEnabled(list.isEnabled());
          setSelected(((FilterListItem)value).isSelected());
          setFont(list.getFont());
          setBackground(list.getBackground());
          setForeground(list.getForeground());
          setText(value.toString());
          return this;
       }
  }
 
 
}
