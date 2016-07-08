/* SwtResourceCache.java
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
package cz.natur.cuni.mirai.math.icon;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class SwtResourceCache {
	private Display display;
	private HashMap<String, Object> resourceMap = new HashMap<String, Object>();

	public SwtResourceCache(Display display) {
		this.display = display;
	}

	public Image getImage(String idTag) {
		if(resourceMap.containsKey(idTag)) {
			return (Image)resourceMap.get(idTag);
		} else {
			// lazy image loading
			InputStream stream = SwtResourceCache.class.getClassLoader().getResourceAsStream(idTag);
			Image icon = new Image(display, stream);
			resourceMap.put(idTag, icon);
			return icon;
		}
	}
	
	public void dispose() {
		Iterator<String> iterator = resourceMap.keySet().iterator();
		while(iterator.hasNext()) {
			String idTag = iterator.next();
			Image image = getImage(idTag);
			image.dispose();
		}
	}
}
