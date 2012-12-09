package de.easytory.tools;

import javax.xml.parsers.ParserConfigurationException;

public class RDFDocument extends XMLDocument 
{
	public RDFDocument() throws ParserConfigurationException 
	{
		super("rdf:RDF","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	}
}
