package de.easytory.exporter;

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
public class ExportGraphML implements ExportInterface
{
	private Document doc;
	private Element graph;
	private int edgeId=0;
	
	@Override
	public void init() throws Exception 
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        Element root = doc.createElement("graphml");
        setAttr(doc, root, "xmlns", "http://graphml.graphdrawing.org/xmlns"); 
        setAttr(doc, root, "xmlns:y", "http://www.yworks.com/xml/graphml"); 
        doc.appendChild(root);
        
        Element key = createElement(doc, root, "key", "");setAttr(doc, key, "id", "d0"); setAttr(doc, key, "for", "node"); setAttr(doc, key, "yfiles.type", "nodegraphics");
        graph = createElement(doc, root, "graph", ""); setAttr(doc, graph, "id", "G"); setAttr(doc, graph, "edgedefault", "directed");
	}


	@Override
	public void finish() throws Exception
	{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("easytory.graphml"));
        transformer.transform(source, result);
	}

	@Override
	public void processItems(String entity, String itemId, String itemName, String note) throws Exception 
	{
	    Element node = createElement(doc, graph, "node", ""); setAttr(doc, node, "id", itemId);
		Element data = createElement(doc, node, "data", ""); setAttr(doc, data, "key", "d0");
		Element shape = createElement(doc, data, "y:ShapeNode", "");
		createElement(doc, shape, "y:NodeLabel", itemName);
	}
	
	@Override
	public void processRelations(String entity, String itemId, String itemName, String targetId) throws Exception 
	{
		Element edge = createElement(doc, graph, "edge", "");
        setAttr(doc, edge, "id", "e" + edgeId); edgeId++; 
        setAttr(doc, edge, "source", itemId); 
        setAttr(doc, edge, "target", targetId);
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


	@Override
	public void processEntities(String entity, Iterator<String> attributeList) throws Exception 
	{
		// nothing todo here for GraphML
		
	}


	@Override
	public void processValues(String entity, String itemId, String itemName,
			String valueString, String valueName, int valueType,
			String relatedEntity) throws Exception 
	{
		// nothing todo here for GraphML
		
	}

}
