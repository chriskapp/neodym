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

package com.k42b3.neodym.oauth;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.Response;
import com.k42b3.neodym.TrafficListenerInterface;

/**
 * Oauth
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Oauth 
{
	private Http http;
	private OauthProvider provider;

	private String token;
	private String tokenSecret;
	private boolean callbackConfirmed;
	private String verificationCode;

	private boolean authed = false;

	private Logger logger = Logger.getLogger("com.k42b3.neodym");

	public Oauth(Http http, OauthProvider provider)
	{
		this.http = http;
		this.provider = provider;

		if((provider.getToken() != null && !provider.getToken().isEmpty()) && (provider.getTokenSecret() != null && !provider.getTokenSecret().isEmpty()))
		{
			this.auth(provider.getToken(), provider.getTokenSecret());
		}
	}

	public void auth(String token, String tokenSecret)
	{
		this.setToken(token);
		this.setTokenSecret(tokenSecret);

		this.authed = true;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
	
	public void setTokenSecret(String tokenSecret)
	{
		this.tokenSecret = tokenSecret;
	}

	public String getToken()
	{
		return token;
	}

	public String getTokenSecret()
	{
		return tokenSecret;
	}

	public boolean isAuthed()
	{
		return authed;
	}

	public boolean requestToken() throws Exception
	{
		// add values
		HashMap<String, String> values = new HashMap<String, String>();

		String requestMethod = "POST";

		values.put("oauth_consumer_key", this.provider.getConsumerKey());
		values.put("oauth_signature_method", provider.getMethod());
		values.put("oauth_timestamp", this.getTimestamp());
		values.put("oauth_nonce", this.getNonce());
		values.put("oauth_version", this.getVersion());
		values.put("oauth_callback", "oob");

		// add get vars to values
		URL requestUrl = new URL(provider.getRequestUrl());
		values.putAll(parseQuery(requestUrl.getQuery()));

		// build base string
		String baseString = this.buildBaseString(requestMethod, provider.getRequestUrl(), values);

		// get signature
		SignatureInterface signature = this.getSignature();

		if(signature == null)
		{
			throw new Exception("Invalid signature method");
		}

		// build signature
		values.put("oauth_signature", signature.build(baseString, provider.getConsumerSecret(), ""));

		// add header to request
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("Authorization", "OAuth realm=\"neodym\", " + this.buildAuthString(values));

		Response resp = http.request(Http.POST, provider.getRequestUrl(), header);

		// parse response
		this.token = null;
		this.tokenSecret = null;
		this.callbackConfirmed = false;

		HashMap<String, String> response = parseQuery(resp.getContent());
		Set<String> keys = response.keySet();

		for(String key : keys)
		{
			if(key.equals("oauth_token"))
			{
				this.token = response.get(key);

				logger.info("Received token: " + this.token);
			}

			if(key.equals("oauth_token_secret"))
			{
				this.tokenSecret = response.get(key);

				logger.info("Received token secret: " + this.tokenSecret);
			}

			if(key.equals("oauth_callback_confirmed"))
			{
				this.tokenSecret = response.get(key);

				this.callbackConfirmed = response.get(key).equals("1");
			}
		}

		if(this.token == null)
		{
			throw new Exception("No oauth token received");
		}

		if(this.tokenSecret == null)
		{
			throw new Exception("No oauth token secret received");
		}

		if(this.callbackConfirmed != true)
		{
			throw new Exception("Callback was not confirmed");
		}

		return true;
	}

	public boolean authorizeToken() throws Exception
	{
		String url;

		if(this.provider.getAuthorizationUrl().indexOf('?') == -1)
		{
			url = this.provider.getAuthorizationUrl() + "?oauth_token=" + this.token;
		}
		else
		{
			url = this.provider.getAuthorizationUrl() + "&oauth_token=" + this.token;
		}

		URI authUrl = new URI(url);

		if(Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();

			if(desktop.isSupported(Desktop.Action.BROWSE))
			{
				desktop.browse(authUrl);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Visit the following URL: " + authUrl);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Visit the following URL: " + authUrl);
		}

		verificationCode = JOptionPane.showInputDialog("Please enter the verification Code");

		return true;
	}

	public boolean accessToken() throws Exception
	{
		// add values
		HashMap<String, String> values = new HashMap<String, String>();

		String requestMethod = "POST";

		values.put("oauth_consumer_key", this.provider.getConsumerKey());
		values.put("oauth_token", this.token);
		values.put("oauth_signature_method", provider.getMethod());
		values.put("oauth_timestamp", this.getTimestamp());
		values.put("oauth_nonce", this.getNonce());
		values.put("oauth_verifier", this.verificationCode);

		// add get vars to values
		URL accessUrl = new URL(provider.getAccessUrl());
		values.putAll(parseQuery(accessUrl.getQuery()));

		// build base string
		String baseString = this.buildBaseString(requestMethod, provider.getAccessUrl(), values);

		// get signature
		SignatureInterface signature = this.getSignature();

		if(signature == null)
		{
			throw new Exception("Invalid signature method");
		}

		// build signature
		values.put("oauth_signature", signature.build(baseString, provider.getConsumerSecret(), this.tokenSecret));

		// add header to request
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("Authorization", "OAuth realm=\"neodym\", " + this.buildAuthString(values));

		Response resp = http.request(Http.POST, provider.getAccessUrl(), header);

		// parse response
		this.token = null;
		this.tokenSecret = null;

		HashMap<String, String> response = parseQuery(resp.getContent());
		Set<String> keys = response.keySet();

		for(String key : keys)
		{
			if(key.equals("oauth_token"))
			{
				this.token = response.get(key);

				logger.info("Received token: " + this.token);
			}

			if(key.equals("oauth_token_secret"))
			{
				this.tokenSecret = response.get(key);

				logger.info("Received token secret: " + this.tokenSecret);
			}
		}

		if(this.token == null)
		{
			throw new Exception("No oauth token received");
		}

		if(this.tokenSecret == null)
		{
			throw new Exception("No oauth token secret received");
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public void signRequest(HttpRequestBase request) throws Exception
	{
		// add values
		HashMap<String, String> values = new HashMap<String, String>();
		HashMap<String, String> auth;

		values.put("oauth_consumer_key", this.provider.getConsumerKey());
		values.put("oauth_token", this.token);
		values.put("oauth_signature_method", provider.getMethod());
		values.put("oauth_timestamp", this.getTimestamp());
		values.put("oauth_nonce", this.getNonce());

		auth = (HashMap<String, String>) values.clone();

		// add get vars
		List<NameValuePair> gets = URLEncodedUtils.parse(request.getURI(), "UTF-8");
		for(int i = 0; i < gets.size(); i++)
		{
			values.put(gets.get(i).getName(), gets.get(i).getValue());
		}

		// add post vars
		if(request instanceof HttpPost)
		{
			Header contentType = request.getFirstHeader("Content-Type");

			if(contentType != null && contentType.getValue().toLowerCase().equals("application/x-www-form-urlencoded"))
			{
				HttpPost postRequest = (HttpPost) request;
				HttpEntity entity = postRequest.getEntity();
				List<NameValuePair> posts = URLEncodedUtils.parse(entity);

				for(int i = 0; i < posts.size(); i++)
				{
					values.put(posts.get(i).getName(), posts.get(i).getValue());
				}
			}
		}

		// build base string
		String baseString = this.buildBaseString(request.getMethod(), request.getURI().toString(), values);

		// get signature
		SignatureInterface signature = this.getSignature();

		if(signature == null)
		{
			throw new Exception("Invalid signature method");
		}

		// build signature
		auth.put("oauth_signature", signature.build(baseString, provider.getConsumerSecret(), this.tokenSecret));

		// add header to request
		request.addHeader("Authorization", "OAuth realm=\"neodym\", " + this.buildAuthString(auth));
	}

	private String buildAuthString(HashMap<String, String> values)
	{
		StringBuilder authString = new StringBuilder();

		Iterator<Entry<String, String>> it = values.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			authString.append(urlEncode(e.getKey()) + "=\"" + urlEncode(e.getValue()) + "\", ");
		}

		String str = authString.toString();

		// remove ", " from string
		str = str.substring(0, str.length() - 2);

		return str;
	}

	private String buildBaseString(String requestMethod, String url, HashMap<String, String> params) throws Exception
	{
		StringBuilder base = new StringBuilder();

		base.append(urlEncode(this.getNormalizedMethod(requestMethod)));

		base.append('&');
		
		base.append(urlEncode(this.getNormalizedUrl(url)));

		base.append('&');
		
		base.append(urlEncode(this.getNormalizedParameters(params)));

		logger.fine("BaseString: " + base.toString());

		return base.toString();
	}

	private String getNormalizedParameters(HashMap<String, String> params)
	{
		Iterator<Entry<String, String>> it = params.entrySet().iterator();

		List<String> keys = new ArrayList<String>();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			keys.add(e.getKey());
		}

		// sort params
		Collections.sort(keys);

		// build normalized params
		StringBuilder normalizedParams = new StringBuilder();

		for(int i = 0; i < keys.size(); i++)
		{
			normalizedParams.append(urlEncode(keys.get(i)) + "=" + urlEncode(params.get(keys.get(i))) + "&");
		}

		String str = normalizedParams.toString();

		// remove trailing &
		str = str.substring(0, str.length() - 1);


		return str;
	}

	private String getNormalizedUrl(String rawUrl) throws Exception
	{
		rawUrl = rawUrl.toLowerCase();

		URL url = new URL(rawUrl);

		int port = url.getPort();

		if(port == -1 || port == 80 || port == 443)
		{
			return url.getProtocol() + "://" + url.getHost() + url.getPath();
		}
		else
		{
			return url.getProtocol() + "://" + url.getHost() + ":" + port + url.getPath();
		}
	}

	private String getNormalizedMethod(String method)
	{
		return method.toUpperCase();
	}

	private String getTimestamp()
	{
		return "" + (System.currentTimeMillis() / 1000);
	}

	private String getNonce()
	{
		try
		{
			byte[] nonce = new byte[32];

			Random rand;

			rand = SecureRandom.getInstance("SHA1PRNG");

			rand.nextBytes(nonce);


			return DigestUtils.md5Hex(rand.toString());
		}
		catch(Exception e)
		{
			return DigestUtils.md5Hex("" + System.currentTimeMillis());
		}
	}

	private String getVersion()
	{
		return "1.0";
	}

	private SignatureInterface getSignature() throws Exception
	{
		String cls;

		if(provider.getMethod().equals("HMAC-SHA1"))
		{
			cls = "com.k42b3.neodym.oauth.HMACSHA1";
		}
		else if(provider.getMethod().equals("PLAINTEXT"))
		{
			cls = "com.k42b3.neodym.oauth.PLAINTEXT";
		}
		else
		{
			throw new Exception("Invalid signature method");
		}

		return (SignatureInterface) Class.forName(cls).newInstance();
	}

	public static String urlEncode(String content)
	{
		try
		{
			if(!content.isEmpty())
			{
				String encoded = URLEncoder.encode(content, "UTF8");

				encoded = encoded.replaceAll("%7E", "~");

				return encoded;
			}
			else
			{
				return "";
			}
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public static HashMap<String, String> parseQuery(String query) throws Exception
	{
		HashMap<String, String> map = new HashMap<String, String>();

		if(query != null)
		{
			String[] params = query.split("&");

			for(int i = 0; i < params.length; i++)
			{
				String[] pair = params[i].split("=");

				if(pair.length >= 1)
				{
					String name  = URLDecoder.decode(pair[0], "UTF-8");
					String value = pair.length == 2 ? URLDecoder.decode(pair[1], "UTF-8") : "";

					map.put(name, value);
				}
			}
		}

		return map;
	}
}
