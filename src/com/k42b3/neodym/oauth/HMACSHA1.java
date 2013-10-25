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

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * HMACSHA1
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class HMACSHA1 implements SignatureInterface
{
	public String build(String baseString, String consumerSecret, String tokenSecret) throws Exception
	{
		String key = Oauth.urlEncode(consumerSecret) + "&" + Oauth.urlEncode(tokenSecret);


		Charset charset = Charset.defaultCharset();

		SecretKey sk = new SecretKeySpec(key.getBytes(charset), "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");

		mac.init(sk);

		byte[] result = mac.doFinal(baseString.getBytes(charset));


		return Base64.encodeBase64String(result);
	}
}
