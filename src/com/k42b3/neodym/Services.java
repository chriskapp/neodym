/**
 * $Id: Services.java 221 2012-03-31 18:57:33Z k42b3.x@gmail.com $
 * 
 * neodym
 * A java library to access the REST API of amun
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains all discovered items of the XRDS
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 221 $
 */
public class Services
{
	private String baseUrl;
	private Http http;

	private ArrayList<ServiceItem> services = new ArrayList<ServiceItem>();

	private Logger logger = Logger.getLogger("com.k42b3.neodym");

	public Services(Http http, String baseUrl)
	{
		this.baseUrl = baseUrl;
		this.http = http;
	}

	public void discover() throws Exception
	{
		String url = this.getXrdsUrl(baseUrl);

		if(url != null)
		{
			this.request(url);
		}
		else
		{
			throw new Exception("Could not find xrds location");
		}
	}
	
	public ServiceItem getElementAt(int index) 
	{
		return services.get(index);
	}

	public int getSize() 
	{
		return services.size();
	}

	public ServiceItem getItem(String type)
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

	private String getXrdsUrl(String url) throws Exception
	{
		http.request(Http.GET, url, null, null, false);


		// find x-xrds-location header
		Header[] headers = http.getLastResponse().getAllHeaders();
		String xrdsLocation = null;

		for(int i = 0; i < headers.length; i++)
		{
			if(headers[i].getName().toLowerCase().equals("x-xrds-location"))
			{
				xrdsLocation = headers[i].getValue();

				logger.info("Found XRDS location: " + xrdsLocation);

				break;
			}
		}


		return xrdsLocation;
	}

	private void request(String url) throws Exception
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

				services.add(new ServiceItem(uri, types));
			}
		}

		logger.info("Found " + services.size() + " services");
	}
}
