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

import java.util.ArrayList;

/**
 * An service item of the discovered XRDS
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/neodym
 */
public class Service 
{
	private String uri;
	private ArrayList<String> types;

	public Service(String uri, ArrayList<String> types)
	{
		this.setUri(uri);
		this.setTypes(types);
	}

	public String getUri() 
	{
		return uri;
	}

	public void setUri(String uri) 
	{
		this.uri = uri;
	}
	
	public ArrayList<String> getTypes() 
	{
		return types;
	}

	public void setTypes(ArrayList<String> types) 
	{
		this.types = types;
	}

	public String getTypeStartsWith(String prefix)
	{
		for(int i = 0; i < this.types.size(); i++)
		{
			if(this.types.get(i).startsWith(prefix))
			{
				return this.types.get(i);
			}
		}

		return null;
	}

	public boolean hasType(String type)
	{
		return this.types.contains(type);
	}

	public boolean hasTypeStartsWith(String prefix)
	{
		return this.getTypeStartsWith(prefix) != null;
	}

	public String toString()
	{
		return uri;
	}

	public boolean equals(Object o)
	{
		if(o instanceof Service)
		{
			return ((Service) o).getUri().equals(this.getUri());
		}

		return false;
	}
}
