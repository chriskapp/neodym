/**
 * $Id: HostMeta.java 211 2011-12-19 23:50:07Z k42b3.x@gmail.com $
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

package com.k42b3.neodym.webfinger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * HostMeta
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 211 $
 */
public class HostMeta 
{
	private Document doc;

	public HostMeta(Document doc) throws Exception
	{
		if(!doc.getNamespaceURI().equals("http://docs.oasis-open.org/ns/xri/xrd-1.0"))
		{
			throw new Exception("Invalid host meta namespace");
		}

		this.doc = doc;
	}

	public Document getDocument()
	{
		return doc;
	}

	public String getTemplate()
	{
		NodeList links = doc.getElementsByTagName("Link");

		if(links.getLength() > 0)
		{
			for(int i = 0; i < links.getLength(); i++)
			{
				Element link = (Element) links.item(i);

				if(link.getAttribute("rel").equals("lrdd") && link.getAttribute("type").equals("application/xrd+xml"))
				{
					return link.getAttribute("template");
				}
			}
		}

		return null;
	}
}
