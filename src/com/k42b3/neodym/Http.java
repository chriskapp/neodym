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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.k42b3.neodym.oauth.Oauth;

/**
 * Http
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Http 
{
	public static int GET = 0x1;
	public static int POST = 0x2;

	private Oauth oauth;
	private TrafficListenerInterface trafficListener;

	private HttpRequest lastRequest;
	private Response lastResponse;
	
	private Logger logger = Logger.getLogger("com.k42b3.neodym");

	public Http(TrafficListenerInterface trafficListener)
	{
		this.trafficListener = trafficListener;
	}

	public Http()
	{
		this(null);
	}

	public Response request(int method, String url, Map<String, String> header, HttpEntity body, boolean signed) throws Exception
	{
		// build request
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpRequestBase httpRequest;

		if(method == Http.GET)
		{
			httpRequest = new HttpGet(url);
		}
		else if(method == Http.POST)
		{
			httpRequest = new HttpPost(url);

			if(body != null)
			{
				((HttpPost) httpRequest).setEntity(new BufferedHttpEntity(body));
			}
		}
		else
		{
			throw new Exception("Invalid request method");
		}

		// add headers
		if(header != null)
		{
			Set<String> keys = header.keySet();

			for(String k : keys)
			{
				httpRequest.addHeader(k, header.get(k));
			}
		}

		// sign request
		if(oauth != null && signed)
		{
			oauth.signRequest(httpRequest);
		}

		// execute request
		logger.info("Request: " + httpRequest.getRequestLine().toString());

		HttpResponse httpResponse = httpClient.execute(httpRequest);
		Response response = new Response(httpResponse);
		
		logger.info("Response: " + httpResponse.getStatusLine().toString());

		// log traffic
		if(trafficListener != null)
		{
			trafficListener.handleRequest(new TrafficItem(httpRequest, response));
		}

		// check status code
		int statusCode = httpResponse.getStatusLine().getStatusCode();

		if(!(statusCode >= 200 && statusCode < 300))
		{
			String resp = response.getContent();

			if(!resp.isEmpty())
			{
				throw new Exception(resp.length() > 128 ? resp.substring(0, 128) + "..." : resp);
			}
			else
			{
				throw new Exception("No successful status code");
			}
		}

		// assign last request/response
		lastRequest = httpRequest;
		lastResponse = response;

		return response;
	}

	/*
	public String request(int method, String url, Map<String, String> header, String body, boolean signed) throws Exception
	{
		return this.request(method, url, header, new StringEntity(body), true);
	}
	*/

	public Response request(int method, String url, Map<String, String> header, HttpEntity body) throws Exception
	{
		return this.request(method, url, header, body, true);
	}

	public Response request(int method, String url, Map<String, String> header) throws Exception
	{
		return this.request(method, url, header, null, true);
	}

	public Response request(int method, String url) throws Exception
	{
		return this.request(method, url, null);
	}

	public Response requestNotSigned(int method, String url, Map<String, String> header, HttpEntity body) throws Exception
	{
		return this.request(method, url, header, body, false);
	}

	public Response requestNotSigned(int method, String url, Map<String, String> header) throws Exception
	{
		return this.requestNotSigned(method, url, header, null);
	}

	public Response requestNotSigned(int method, String url) throws Exception
	{
		return this.requestNotSigned(method, url, null);
	}

	public Document requestXml(int method, String url, Map<String, String> header, HttpEntity body, boolean signed) throws Exception
	{
		// request
		if(header == null)
		{
			header = new HashMap<String, String>();
		}

		if(!header.containsKey("Accept"))
		{
			header.put("Accept", "application/xml");
		}

		Response response = this.request(method, url, header, body, signed);
		String xml = response.getContent();

		try
		{
			// parse response
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);

			Element rootElement = (Element) doc.getDocumentElement();
			rootElement.normalize();

			return doc;
		}
		catch(SAXException e)
		{
			String text = xml.length() > 32 ? xml.substring(0, 32) + "..." : xml;

			throw new Exception(text);
		}
	}

	public Document requestXml(int method, String url, Map<String, String> header, HttpEntity body) throws Exception
	{
		return this.requestXml(method, url, header, body, true);
	}
	
	public Document requestXml(int method, String url, Map<String, String> header) throws Exception
	{
		return this.requestXml(method, url, header, null);
	}

	public Document requestXml(int method, String url) throws Exception
	{
		return this.requestXml(method, url, null);
	}

	public Document requestNotSignedXml(int method, String url, Map<String, String> header, HttpEntity body) throws Exception
	{
		return this.requestXml(method, url, header, body, false);
	}

	public Document requestNotSignedXml(int method, String url, Map<String, String> header) throws Exception
	{
		return this.requestNotSignedXml(method, url, header, null);
	}

	public Document requestNotSignedXml(int method, String url) throws Exception
	{
		return this.requestNotSignedXml(method, url, null);
	}

	public HttpRequest getLastRequest()
	{
		return lastRequest;
	}

	public Response getLastResponse()
	{
		return lastResponse;
	}

	public void setOauth(Oauth oauth)
	{
		this.oauth = oauth;
	}

	public Oauth getOauth()
	{
		return oauth;
	}

	public void setTrafficListener(TrafficListenerInterface trafficListener)
	{
		this.trafficListener = trafficListener;
	}

	public TrafficListenerInterface getTrafficListener()
	{
		return trafficListener;
	}

	public static String appendQuery(String url, String query)
	{
		if(url.indexOf('?') == -1)
		{
			return url + '?' + query;
		}
		else
		{
			return url + '&' + query;
		}
	}
}
