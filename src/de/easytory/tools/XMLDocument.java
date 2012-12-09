package de.easytory.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XMLDocument 
{
	protected Document doc;
	protected Element rootElement;
	
	public XMLDocument(String rootElementName,String namespace) throws ParserConfigurationException
	{
		createDocument(rootElementName, namespace);
	}
	
	protected Element createElement(Document doc, Element parent, String child, String childValue)
    {
        Element e = doc.createElement(child);
        if (!childValue.equals("")) e.appendChild(doc.createTextNode(childValue)); 
        parent.appendChild(e);
        return e;
    }
    
	protected void setAttr(Document doc, Element e, String key, String value)
    {
        Attr attr = doc.createAttribute(key);
        attr.setValue(value);
        e.setAttributeNode(attr);    
    }
    
	
	private void createDocument(String rootElementName, String namespace) throws ParserConfigurationException
    {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    	doc = docBuilder.newDocument();
        if (namespace.equals("")) 
        {
        	rootElement = doc.createElement(rootElementName);
        }
        else 
        {
        	rootElement = doc.createElementNS(namespace, rootElementName);
        }
    }

	public void writeFile(String filenname) throws Exception
	{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filenname));
        transformer.transform(source, result);
	} 
	
	public byte[] getByteArray() throws Exception
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(byteStream);
        transformer.transform(source, result);
        return byteStream.toByteArray();		
	}
	
	
}
