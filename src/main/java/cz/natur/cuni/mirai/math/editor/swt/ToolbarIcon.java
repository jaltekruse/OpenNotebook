/* ToolbarIcon.java
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
package cz.natur.cuni.mirai.math.editor.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

class ToolbarIcon extends Canvas {
	
	static final int GAP = 5;
	static final int INDENT = 3;
	static int DRAW_FLAGS = SWT.DRAW_MNEMONIC | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;

	String text;
	Image image;
	int align = SWT.LEFT;
	int hIndent = INDENT;
	int vIndent = INDENT;

	public ToolbarIcon(Composite parent, int style) {
		super(parent, style);

		addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent event) {
				onPaint(event);
			}
		});	
		
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				redraw();
			}
			public void focusLost(FocusEvent e) {
				redraw();
			}			
		});
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		Point e = getTotalSize();
		if (wHint == SWT.DEFAULT){
			e.x += 2*hIndent;
		} else {
			e.x = wHint;
		}
		if (hHint == SWT.DEFAULT) {
			e.y += 2*vIndent;
		} else {
			e.y = hHint;
		}
		return e;
	}

	public void setImage(Image image) {
		checkWidget();
		if (image != this.image) {
			this.image = image;
			redraw();
		}
	}

	public void setText(String text) {
		checkWidget();
		if (text == null) text = ""; 
		if (! text.equals(this.text)) {
			this.text = text;
			redraw();
		}
	}

	private Point getTotalSize() {
		Point size = new Point(0, 0);

		if (image != null) {
			Rectangle r = image.getBounds();
			size.x += r.width;
			size.y += r.height;
		}
			
		GC gc = new GC(this);
		if (text != null && text.length() > 0) {
			Point e = gc.textExtent(text, DRAW_FLAGS);
			size.x += e.x;
			size.y = Math.max(size.y, e.y);
			if (image != null) size.x += GAP;
		} else {
			size.y = Math.max(size.y, gc.getFontMetrics().getHeight());
		}
		gc.dispose();
		return size;
	}

	private void onPaint(PaintEvent event) {
		Rectangle rect = getClientArea();
		if (rect.width == 0 || rect.height == 0) return;
		
		GC gc = event.gc;
		Point extent = getTotalSize();

		// draw border
		int style = getStyle();
		if(this.isFocusControl()) {
			if ((style & SWT.SHADOW_IN) != 0 || (style & SWT.SHADOW_OUT) != 0) {
				paintBorder(gc, rect);
			}
		}

		// determine horizontal position
		int x = rect.x + hIndent; 
		if (align == SWT.CENTER) {
			x = (rect.width - extent.x)/2;
		}
		if (align == SWT.RIGHT) {
			x = rect.width - hIndent - extent.x;
		}

		// draw the image
		if (image != null) {
			Rectangle imageRect = image.getBounds();
			gc.drawImage(image, 0, 0, imageRect.width, imageRect.height, 
			                x, (rect.height-imageRect.height)/2, imageRect.width, imageRect.height);
			x +=  imageRect.width + GAP;
			extent.x -= imageRect.width + GAP;
		}

		// draw the text
		if (text != null) {
			int lineHeight = gc.getFontMetrics().getHeight();
			int lineY = Math.max(vIndent, rect.y + (rect.height - lineHeight) / 2);
			gc.setForeground(getForeground());
			int lineX = x;
			gc.drawText(text, lineX, lineY, DRAW_FLAGS);
			lineY += lineHeight;
		}
	}

	private void paintBorder(GC gc, Rectangle r) {
		Display disp= getDisplay();
		Color c1 = disp.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		Color c2 = disp.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);

		if (c1 != null && c2 != null) {
			gc.setLineWidth(1);
			drawBevelRect(gc, r.x, r.y, r.width-1, r.height-1, c1, c2);
		}
	}

	private void drawBevelRect(GC gc, int x, int y, int w, int h, Color topleft, Color bottomright) {
		gc.setForeground(bottomright);
		gc.drawLine(x+w, y,   x+w, y+h);
		gc.drawLine(x,   y+h, x+w, y+h);
		
		gc.setForeground(topleft);
		gc.drawLine(x, y, x+w-1, y);
		gc.drawLine(x, y, x,     y+h-1);
	}

}
