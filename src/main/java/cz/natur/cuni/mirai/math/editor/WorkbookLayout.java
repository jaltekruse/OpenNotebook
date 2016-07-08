/* WorkbookLayout.java
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
package cz.natur.cuni.mirai.math.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.Hashtable;

public class WorkbookLayout implements LayoutManager2 {
	private Hashtable<Component, Object> constraints = new Hashtable<Component, Object>();

	public void addLayoutComponent(String constraint, Component component) {
		if (constraint != null) {
			constraints.put(component, constraint);
		} else {
			constraints.remove(component);
		}
	}

	public void addLayoutComponent(Component component, Object constraint) {
		if (constraint != null) {
			constraints.put(component, constraint);
		} else {
			constraints.remove(component);
		}
	}

	public void removeLayoutComponent(Component container) {
		if (constraints != null)
			constraints.remove(container);
	}

	public Dimension minimumLayoutSize(Container container) {
		return preferredLayoutSize(container);
	}

	public Dimension maximumLayoutSize(Container container) {
		return preferredLayoutSize(container);
	}

	public Dimension preferredLayoutSize(Container container) {
		Dimension dim = new Dimension(0, 0);

		for(int k=0;k<container.getComponentCount();k++) {
			Component component = container.getComponent(k);
			Dimension size = component.getPreferredSize();

			dim.height += 2; 
			size.width += 5; size.height += 2; 

			dim.height += size.height;
			dim.width = dim.width > size.width ? dim.width : size.width;
		}

		dim.height += 50; 
		dim.width += 5;

		return dim;
	}

	public float getLayoutAlignmentX(Container container) {
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container container) {
		return 0.5f;
	}

	public void invalidateLayout(Container container) {
	}

	public void layoutContainer(Container container) {
		Dimension dim = new Dimension(0, 0);

		for(int k=0;k<container.getComponentCount();k++) {
			Component component = container.getComponent(k);
			Dimension size = component.getPreferredSize();

			dim.height += 2; 
			size.width += 5; size.height += 2; 
			component.setBounds(5, dim.height, size.width, size.height);

			dim.height += size.height;
			dim.width = dim.width > size.width ? dim.width : size.width;
		}
	}

}
