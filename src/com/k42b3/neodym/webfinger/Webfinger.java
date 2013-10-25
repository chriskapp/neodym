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

package com.k42b3.neodym.webfinger;

import org.w3c.dom.Document;

import com.k42b3.neodym.Http;

/**
 * Webfinger
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Webfinger 
{
	private Http http;
	private String baseUrl;

	public Webfinger(Http http, String baseUrl)
	{
		this.http = http;
		this.baseUrl = baseUrl;
	}

	public Document getLrdd(String url) throws Exception
	{
		Document doc = http.requestNotSignedXml(Http.GET, baseUrl + "/.well-known/host-meta");
		HostMeta hm = new HostMeta(doc);

		return http.requestNotSignedXml(Http.GET, hm.getTemplate().replace("{uri}", url));
	}
}
