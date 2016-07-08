/* SymbolIcon.java
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

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

import cz.natur.cuni.mirai.math.editor.JToolbarFolder;
import cz.natur.cuni.mirai.math.meta.MetaSymbol;

import be.ugent.caagt.jmathtex.DefaultTeXFont;

public class SymbolIcon implements Icon {

	private static int size = 32;
	private static float scaleSymbol = 24f;
	private static float scaleInput = 10f;			
	
	private static final Font cmr10 = DefaultTeXFont.getFont(1 );
	private MetaSymbol ich;
	
	public static void setType(int type) {
		if(type==JToolbarFolder.LARGE) {
			size = 32;
			scaleSymbol = 24f;
		} else {
			size = 16;
			scaleSymbol = 16f;
		}		
	}

	public SymbolIcon(MetaSymbol ich) {
		this.ich = ich;
	}

	public int getIconHeight() {
		return size;
	}

	public int getIconWidth() {
		return size;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {

		Graphics2D g2 = (Graphics2D) g;
		// copy graphics settings
		RenderingHints oldHints = g2.getRenderingHints();

		// new settings
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// g2.scale(size, size); // the point size
		g2.setColor(c.getForeground()); // foreground will be used as default painting color
		//g2.setColor(Color.blue); // foreground will be used as default painting color

		// backup
		java.awt.Font f = g2.getFont();
		 
		//g2.setFont(DefaultTeXFont.getFont(0).deriveFont(scaleSymbol));
		g2.setFont(ich.getFont().deriveFont(scaleSymbol));

		if(size > 16) {
			g2.drawString(Character.toString(ich.getCode()), 0.0f*scaleSymbol, 1.0f*scaleSymbol);
		} else {
			g2.drawString(Character.toString(ich.getCode()), 0.1f*scaleSymbol, 0.9f*scaleSymbol);
		}

		if(ich.getKey()!=0 && size > 16) {
			g2.setFont(cmr10.deriveFont(scaleInput));
			g2.drawString(Character.toString(ich.getKey()), 0.9f*scaleSymbol, 1.3f*scaleSymbol);
		}

		// restore
		g2.setFont(f);

		// restore graphics settings
		g2.setRenderingHints(oldHints);
	}

}
