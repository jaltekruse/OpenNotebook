/* JScrollWorkbook.java
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

import java.awt.Rectangle;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class JScrollWorkbook extends JPanel {
	private JScrollPane sp;

	public void setScrollPane(JScrollPane sp) {
		this.sp = sp;
	}

	public void scrollToBegin() {
		if(sp!=null) {
			sp.getVerticalScrollBar().setValue(0);
			sp.getHorizontalScrollBar().setValue(0);
			sp.getViewport().doLayout();
		}
	}

	public void scrollToVisible(JComponent component) {
		if(sp!=null) {
			BoundedRangeModel model = sp.getVerticalScrollBar().getModel();
			Rectangle rect = component.getBounds();

			// if formula is above visible extent
			if(rect.y < model.getValue()) {
				int y = rect.y -10 >= 0 ? rect.y -10 : 0;
				sp.getVerticalScrollBar().setValue(y);

			// if formula bottom is below visible extent ...
			} else if(rect.y +rect.height > model.getValue()+model.getExtent()) {
				int newValue = rect.y + rect.height + 10 - model.getExtent();

				// ... and if after scrolling, formula top will be visible
				if(rect.y >= newValue) {
					sp.getVerticalScrollBar().setValue(newValue);
	
				// ... and if formula top is below visible extent too
				} else if(rect.y > model.getValue()+model.getExtent()) {
					int y = rect.y -10 >= 0 ? rect.y -10 : 0;
					sp.getVerticalScrollBar().setValue(y);
				}
			}
		}
	}

	private static final long serialVersionUID = 1L;
}
