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

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

/**
 * Represents an http request and response
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class TrafficItem 
{
	private HttpRequestBase request;
	private Response response;

	public TrafficItem(HttpRequestBase request, Response response)
	{
		this.request = request;
		this.response = response;
	}

	public HttpRequestBase getRequest()
	{
		return request;
	}

	public void setRequest(HttpRequestBase request)
	{
		this.request = request;
	}

	public String getRequestContent()
	{
		if(request instanceof HttpEntityEnclosingRequest)
		{
			try
			{
				HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
				
				if(entity != null)
				{
					return EntityUtils.toString(entity);
				}
			}
			catch(ParseException e)
			{
				return e.getMessage();
			}
			catch(IOException e)
			{
				return e.getMessage();
			}
			catch(UnsupportedOperationException e)
			{
				return e.getMessage();
			}
		}

		return "";
	}

	public Response getResponse() 
	{
		return response;
	}

	public void setResponse(Response response) 
	{
		this.response = response;
	}

	public String getResponseContent()
	{
		return response.getContent();
	}
}
