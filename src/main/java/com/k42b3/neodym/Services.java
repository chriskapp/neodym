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

package com.k42b3.neodym;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.k42b3.neodym.data.Endpoint;
import com.k42b3.neodym.oauth.SignatureException;

/**
 * Contains all discovered items of the XRDS
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Services
{
	private String baseUrl;
	private Http http;
	private ArrayList<Service> services = new ArrayList<Service>();

	private Logger logger = Logger.getLogger("com.k42b3.neodym");

	public Services(Http http, String baseUrl)
	{
		this.baseUrl = baseUrl;
		this.http = http;
	}

	public void discover() throws IOException, SignatureException, ParserConfigurationException, SAXException, XrdsNotFoundException
	{
		this.request(this.getXrdsUrl(baseUrl));
	}
	
	public Service getElementAt(int index) 
	{
		return services.get(index);
	}

	public int getSize() 
	{
		return services.size();
	}

	public Service getService(String type)
	{
		for(int i = 0; i < services.size(); i++)
		{
			if(services.get(i).hasType(type))
			{
				return services.get(i);
			}
		}

		return null;
	}

	public Service getServiceByUri(String uri)
	{
		for(int i = 0; i < services.size(); i++)
		{
			if(services.get(i).getUri().equals(uri))
			{
				return services.get(i);
			}
		}

		return null;
	}

	public Endpoint getEndpoint(String type) throws ServiceNotFoundException
	{
		Service service = getService(type);

		if(service != null)
		{
			return new Endpoint(http, service);
		}
		else
		{
			throw new ServiceNotFoundException("Could not find service " + type);
		}
	}

	private String getXrdsUrl(String url) throws XrdsNotFoundException, IOException
	{
		String xrdsLocation = null;
		Response response = http.requestNotSigned(Http.GET, url);

		// find x-xrds-location header
		Header[] headers = response.getAllHeaders();

		for(int i = 0; i < headers.length; i++)
		{
			if(headers[i].getName().toLowerCase().equals("x-xrds-location"))
			{
				xrdsLocation = headers[i].getValue();

				logger.info("Found XRDS location: " + xrdsLocation);

				break;
			}
		}

		if(xrdsLocation == null)
		{
			throw new XrdsNotFoundException("Could not find xrds location");
		}

		return xrdsLocation;
	}

	private void request(String url) throws SAXException, ParserConfigurationException, SignatureException, IOException
	{
		// request
		Document doc = http.requestXml(Http.GET, url);

		// parse services
		NodeList serviceList = doc.getElementsByTagName("Service");

		for(int i = 0; i < serviceList.getLength(); i++) 
		{
			Node serviceNode = serviceList.item(i);
			Element serviceElement = (Element) serviceNode;

			NodeList typeElementList = serviceElement.getElementsByTagName("Type");
			Element uriElement = (Element) serviceElement.getElementsByTagName("URI").item(0);

			if(typeElementList.getLength() > 0 && uriElement != null)
			{
				ArrayList<String> types = new ArrayList<String>();

				for(int j = 0; j < typeElementList.getLength(); j++)
				{
					types.add(typeElementList.item(j).getTextContent());
				}

				String uri = uriElement.getTextContent();

				services.add(new Service(uri, types));
			}
		}

		logger.info("Found " + services.size() + " services");
	}
}
