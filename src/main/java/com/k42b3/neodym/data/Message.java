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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Represents an message from the API
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Message 
{
	private String text;
	private boolean success;

	public Message(boolean success, String text)
	{
		this.setSuccess(success);
		this.setText(text);
	}

	public Message(boolean success)
	{
		this(success, null);
	}

	public boolean getSuccess() 
	{
		return success;
	}

	public void setSuccess(boolean success) 
	{
		this.success = success;
	}
	
	public boolean hasSuccess()
	{
		return success;
	}
	
	public String getText() 
	{
		return text == null || text.isEmpty() ? "No message available" : text;
	}

	public void setText(String text) 
	{
		this.text = text;
	}

	public static Message parseMessage(Document document)
	{
		NodeList childs = document.getDocumentElement().getChildNodes();

		boolean isMessage = false;
		boolean success = false;
		String text = "";

		for(int i = 0; i < childs.getLength(); i++)
		{
			if(childs.item(i) instanceof Element)
			{
				Element el = (Element) childs.item(i);

				if(el.getNodeName().equals("text") || el.getNodeName().equals("message"))
				{
					text = el.getTextContent();
				}

				if(el.getNodeName().equals("success"))
				{
					isMessage = true;

					success = Boolean.parseBoolean(el.getTextContent());
				}
			}
		}

		if(isMessage)
		{
			return new Message(success, text);
		}

		return null;
	}
}
