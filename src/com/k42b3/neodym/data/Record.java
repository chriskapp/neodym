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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Record
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Record extends LinkedHashMap<String, String>
{
	public String getStringField(String key)
	{
		return get(key);
	}
	
	public int getIntField(String key)
	{
		return Integer.parseInt(get(key));
	}
	
	public float getFloatField(String key)
	{
		return Float.parseFloat(get(key));
	}
	
	public Date getDateField(String key)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try
		{
			return sdf.parse(get(key));
		}
		catch (ParseException e)
		{
			return null;
		}
	}
}
