package de.easytory.old;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.easytory.main.Controller;
import de.easytory.main.Thing;
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
public class ExportGraphML
{
    private Controller controller;
    
    public ExportGraphML(Controller controller)
    {
        this.controller = controller;
    }

    public boolean exportGraphML() 
    {
          try 
          {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
         
                // root elements
                Document doc = docBuilder.newDocument();
                Element root = doc.createElement("graphml");
                setAttr(doc, root, "xmlns", "http://graphml.graphdrawing.org/xmlns"); 
                setAttr(doc, root, "xmlns:y", "http://www.yworks.com/xml/graphml"); 
                doc.appendChild(root);
                
                Element key = createElement(doc, root, "key", "");setAttr(doc, key, "id", "d0"); setAttr(doc, key, "for", "node"); setAttr(doc, key, "yfiles.type", "nodegraphics");
                Element graph = createElement(doc, root, "graph", ""); setAttr(doc, graph, "id", "G"); setAttr(doc, graph, "edgedefault", "directed");
                
                createNodes(doc, graph);
                createEdges(doc, graph);
              
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("easytory.graphml"));
         
                transformer.transform(source, result);
         
                System.out.println("File saved!");
                return true;
     
          } 
          catch (Exception e) 
          {
        	  writeLog(e);
        	  return false;
          } 
    }
    
	private void createNodes(Document doc, Element graph) 
	{
		Iterator<Thing> i = controller.getThings("").iterator();
		while (i.hasNext())
		{
		    Thing t = i.next();
		    String nodeId = t.getId(); if (t.isVirtual()) nodeId = t.getEntity() + "-" + t.getName();
		    Element node = createElement(doc, graph, "node", ""); setAttr(doc, node, "id", nodeId);
		    Element data = createElement(doc, node, "data", ""); setAttr(doc, data, "key", "d0");
		    Element shape = createElement(doc, data, "y:ShapeNode", "");
		    createElement(doc, shape, "y:NodeLabel", t.getName());
		}
	}

	private void createEdges(Document doc, Element graph)
	{
		int edgeId=0;
		Iterator<Thing> i = controller.getThings("").iterator();
		while (i.hasNext())
		{
		    Thing source = i.next();
		    Iterator<Thing> j = controller.getRelationsTo(source).iterator();
		    while(j.hasNext())
		    {
		        Thing target = j.next();
		        String targetId = target.getId(); if (target.isVirtual()) targetId = target.getEntity() + "-" + target.getName();
		        Element edge = createElement(doc, graph, "edge", "");
		        setAttr(doc, edge, "id", "e" + edgeId); edgeId++; 
		        setAttr(doc, edge, "source", source.getId()); 
		        setAttr(doc, edge, "target", targetId);
		     }
		}
	}

    private Element createElement(Document doc, Element parent, String child, String childValue)
    {
        Element e = doc.createElement(child);
        if (!childValue.equals("")) e.appendChild(doc.createTextNode(childValue)); 
        parent.appendChild(e);
        return e;
    }
    
    private void setAttr(Document doc, Element e, String key, String value)
    {
        Attr attr = doc.createAttribute(key);
        attr.setValue(value);
        e.setAttributeNode(attr);    
    }
    
	private void writeLog(Exception e) 
	{
		Logger.getInstance().log(e.toString()); 
	}
}
