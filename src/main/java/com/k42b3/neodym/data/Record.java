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

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	public BigDecimal getBigDecimal(String key)
	{
		return new BigDecimal(get(key));
	}

	public boolean getBoolean(String key)
	{
		return Boolean.parseBoolean(get(key));
	}

	public Date getDate(String key, Calendar cal)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(cal != null)
		{
			sdf.setCalendar(cal);
		}

		try
		{
			return sdf.parse(get(key));
		}
		catch (ParseException e)
		{
			return null;
		}
	}
	
	public Date getDate(String key)
	{
		return getDate(key, null);
	}

	public double getDouble(String key)
	{
		return Double.parseDouble(get(key));
	}
	
	public float getFloat(String key)
	{
		return Float.parseFloat(get(key));
	}

	public int getInt(String key)
	{
		return Integer.parseInt(get(key));
	}

	public long getLong(String key)
	{
		return Long.parseLong(get(key));
	}

	public short getShort(String key)
	{
		return Short.parseShort(get(key));
	}
	
	public String getString(String key)
	{
		return get(key);
	}
	
	public Timestamp getTimestamp(String key)
	{
		return new Timestamp(getLong(key));
	}
	
	public URL getUrl(String key)
	{
		try
		{
			return new URL(getString(key));
		}
		catch(MalformedURLException e)
		{
			return null;
		}
	}
}
