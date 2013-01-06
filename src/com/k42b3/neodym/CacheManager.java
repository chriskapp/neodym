/**
 * $Id: CacheManager.java 227 2012-04-29 12:54:26Z k42b3.x@gmail.com $
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

import java.util.ArrayList;
import java.util.Date;

/**
 * Manages the cache objects
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 227 $
 */
public class CacheManager extends ArrayList<Cache>
{
	private boolean disabled = false;

	/**
	 * Returns the cache if an cache object with this key exists and the cache
	 * ist not expired. If no cache object exists null is returned
	 * 
	 * @param String key
	 * @return Cache
	 */
	public Cache get(String key)
	{
		if(this.disabled)
		{
			return null;
		}

		for(int i = 0; i < this.size(); i++)
		{
			if(this.get(i).getKey().equals(key))
			{
				// check whether expired
				if(this.get(i).getExpire().compareTo(new Date()) < 0)
				{
					return null;
				}

				return this.get(i);
			}
		}

		return null;
	}

	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
}
