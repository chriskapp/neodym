/**
 * $Id: TrafficItem.java 221 2012-03-31 18:57:33Z k42b3.x@gmail.com $
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Represents an http request and response
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 221 $
 */
public class TrafficItem 
{
	private HttpRequestBase request;
	private String requestContent = "";

	private HttpResponse response;
	private String responseContent = "";

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
		return requestContent;
	}

	public void setRequestContent(String requestContent)
	{
		this.requestContent = requestContent;
	}

	public HttpResponse getResponse() 
	{
		return response;
	}

	public void setResponse(HttpResponse httpResponse) 
	{
		this.response = httpResponse;
	}

	public String getResponseContent() 
	{
		return responseContent;
	}

	public void setResponseContent(String responseContent) 
	{
		this.responseContent = responseContent;
	}
}
