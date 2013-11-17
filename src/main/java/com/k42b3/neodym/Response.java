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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * Response
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Response
{
	protected HttpResponse response;
	protected String content;

	public Response(HttpResponse response)
	{
		this.response = response;
	}
	
	public StatusLine getStatusLine()
	{
		return response.getStatusLine();
	}

	public Header[] getAllHeaders()
	{
		return response.getAllHeaders();
	}
	
	public HttpResponse getResponse()
	{
		return response;
	}

	public String getContent()
	{
		if(content == null)
		{
			try
			{
				content = EntityUtils.toString(response.getEntity());
			}
			catch(ParseException e)
			{
				content = e.getMessage();
			}
			catch(IOException e)
			{
				content = e.getMessage();
			}
		}

		return content;
	}
}
