/* MetaGroup.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */
package cz.natur.cuni.mirai.math.meta;

/**
 * Group of abstract Meta Model Components.
 * 
 * @author Bea Petrovicova
 */
public class MetaGroup {

	private MetaComponent components[];
	private String name, group;
	private int columns = 0;

	MetaGroup(String name, String group, MetaComponent components[], int columns) {
		this.name = name;
		this.group = group;
		this.components = components;
		this.columns = columns;
	}
	public MetaComponent getComponent(String name) {
		for(int i=0;i<components.length;i++) {
			if(components[i].getName().equals(name)) {
				return components[i];
			}
		}
		return null;
	}

	public MetaComponent[] getComponents() {
		return components;
	}
	
	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}

	public int getColumns() {
		return columns;
	}

}
