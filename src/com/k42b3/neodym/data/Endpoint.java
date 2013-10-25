/**
 * neodym
 * A java library to access the REST API of amun
 * 
 * Copyright (c) 2011-2013 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of neodym. neodym is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * neodym is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with neodym. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.neodym.data;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.Service;

/**
 * Endpoint
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Endpoint
{
	protected Http http;
	protected Service item;
	
	public Endpoint(Http http, Service item) throws Exception
	{
		this.http = http;
		this.item = item;
		
		if(!item.getTypes().contains("http://ns.amun-project.org/2011/amun/data/1.0"))
		{
			throw new Exception("Not an amun data type endpoint");
		}
	}

	public ResultSet getAll(ArrayList<String> fields, int startIndex, int count, String filterBy, String filterOp, String filterValue) throws Exception
	{
		String url = item.getUri();

		if(fields != null)
		{
			StringBuilder queryFields = new StringBuilder();

			for(int i = 0; i < fields.size(); i++)
			{
				queryFields.append(fields.get(i) + ",");
			}

			if(queryFields.length() > 0)
			{
				url = Http.appendQuery(url, "fields=" + queryFields.substring(0, queryFields.length() - 1));
			}
		}

		url = Http.appendQuery(url, "startIndex=" + startIndex);

		if(count > 0)
		{
			url = Http.appendQuery(url, "count=" + count);
		}
		
		if(filterBy != null)
		{
			url = Http.appendQuery(url, "filterBy=" + URLEncoder.encode(filterBy, "UTF-8"));
		}
		
		if(filterOp != null)
		{
			url = Http.appendQuery(url, "filterOp=" + URLEncoder.encode(filterOp, "UTF-8"));
		}
		
		if(filterValue != null)
		{
			url = Http.appendQuery(url, "filterValue=" + URLEncoder.encode(filterValue, "UTF-8"));
		}
		
		ResultSet result = new ResultSet();
		Document response = http.requestXml(Http.GET, url);

		Node totalResultsNode = response.getElementsByTagName("totalResults").item(0);
		if(totalResultsNode != null)
		{
			result.setTotalResults(Integer.parseInt(totalResultsNode.getTextContent()));
		}

		Node startIndexNode = response.getElementsByTagName("startIndex").item(0);
		if(startIndexNode != null)
		{
			result.setStartIndex(Integer.parseInt(startIndexNode.getTextContent()));
		}
		
		Node itemsPerPageNode = response.getElementsByTagName("itemsPerPage").item(0);
		if(itemsPerPageNode != null)
		{
			result.setItemsPerPage(Integer.parseInt(itemsPerPageNode.getTextContent()));
		}
		
		NodeList entries = response.getElementsByTagName("entry");
		
		for(int i = 0; i < entries.getLength(); i++)
		{
			Record record = new Record();
			Node entry = entries.item(i);
			NodeList childs = entry.getChildNodes();
			
			for(int j = 0; j < childs.getLength(); j++)
			{
				if(childs.item(j) instanceof Element)
				{
					String key = childs.item(j).getNodeName();
					String value = childs.item(j).getTextContent();
					
					record.put(key, value);
				}
			}
			
			result.add(record);
		}
		
		return result;
	}
	
	public ResultSet getAll(ArrayList<String> fields, int startIndex, int count) throws Exception
	{
		return getAll(fields, startIndex, count, null, null, null);
	}

	public ArrayList<String> getSupportedFields() throws Exception
	{
		ArrayList<String> fields = new ArrayList<String>();
		Document response = http.requestXml(Http.GET, item.getUri() + "/@supportedFields");
		NodeList items = response.getElementsByTagName("item");
		
		for(int i = 0; i < items.getLength(); i++)
		{
			fields.add(items.item(i).getTextContent());
		}
		
		return fields;
	}
	
	public void create(Record record) throws Exception
	{
		sendRequest("POST", record);
	}
	
	public void update(Record record) throws Exception
	{
		sendRequest("PUT", record);
	}
	
	public void delete(Record record) throws Exception
	{
		sendRequest("DELETE", record);
	}
	
	protected void sendRequest(String method, Record record) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		doc.setXmlStandalone(true);

		Element root = doc.createElement("request");

		Iterator<Entry<String, String>> it = record.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> entry = it.next();

			Element element = doc.createElement(entry.getKey());
			element.setTextContent(entry.getValue());

			root.appendChild(element);
		}

		doc.appendChild(root);

		// xml to string
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String xml = result.getWriter().toString();

		// send request
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/xml");
		header.put("X-HTTP-Method-Override", method);
		
		Document response = http.requestXml(Http.POST, item.getUri(), header, new StringEntity(xml));
		
		// parse response
		Message msg = Message.parseMessage(response.getDocumentElement());

		if(!msg.hasSuccess())
		{
			throw new Exception(msg.getText());
		}
	}
}
