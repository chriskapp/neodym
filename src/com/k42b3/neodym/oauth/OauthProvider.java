/**
 * $Id: OauthProvider.java 205 2011-12-18 18:12:33Z k42b3.x@gmail.com $
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

package com.k42b3.neodym.oauth;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;
import com.k42b3.neodym.Services;

/**
 * OauthProvider
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 205 $
 */
public class OauthProvider 
{
	private String requestUrl;
	private String authorizationUrl;
	private String accessUrl;

	private String consumerKey;
	private String consumerSecret;
	private String token;
	private String tokenSecret;

	private String method = "HMAC-SHA1";

	public OauthProvider(String requestUrl, String authorizationUrl, String accessUrl, String consumerKey, String consumerSecret, String token, String tokenSecret)
	{
		this.setRequestUrl(requestUrl);
		this.setAuthorizationUrl(authorizationUrl);
		this.setAccessUrl(accessUrl);

		this.setConsumerKey(consumerKey);
		this.setConsumerSecret(consumerSecret);
		this.setToken(token);
		this.setTokenSecret(tokenSecret);
	}

	public OauthProvider(String requestUrl, String authorizationUrl, String accessUrl, String consumerKey, String consumerSecret)
	{
		this(requestUrl, authorizationUrl, accessUrl, consumerKey, consumerSecret, null, null);
	}

	public String getRequestUrl() 
	{
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) 
	{
		this.requestUrl = requestUrl;
	}

	public String getAuthorizationUrl() 
	{
		return authorizationUrl;
	}

	public void setAuthorizationUrl(String authorizationUrl) 
	{
		this.authorizationUrl = authorizationUrl;
	}

	public String getAccessUrl() 
	{
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) 
	{
		this.accessUrl = accessUrl;
	}

	public String getConsumerKey() 
	{
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) 
	{
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() 
	{
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) 
	{
		this.consumerSecret = consumerSecret;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public String getTokenSecret()
	{
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret)
	{
		this.tokenSecret = tokenSecret;
	}

	public String getMethod() 
	{
		return method;
	}

	public void setMethod(String method) 
	{
		this.method = method;
	}
	
	public static OauthProvider discoverProvider(Http http, String baseUrl, String consumerKey, String consumerSecret) throws Exception
	{
		// load available services
		Services services = new Services(http, baseUrl);
		services.discover();

		ServiceItem request = services.getItem("http://oauth.net/core/1.0/endpoint/request");
		ServiceItem authorization = services.getItem("http://oauth.net/core/1.0/endpoint/authorize");
		ServiceItem access = services.getItem("http://oauth.net/core/1.0/endpoint/access");

		if(request == null)
		{
			throw new Exception("Could not find request service");
		}

		if(authorization == null)
		{
			throw new Exception("Could not find authorization service");
		}

		if(access == null)
		{
			throw new Exception("Could not find access service");
		}

		return new OauthProvider(request.getUri(), authorization.getUri(), access.getUri(), consumerKey, consumerSecret);
	}
}
