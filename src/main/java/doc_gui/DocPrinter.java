/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
 */

package doc_gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import doc.Document;
import doc.Page;

public class DocPrinter implements Printable{

	private Document doc;
	
    public int print(Graphics g, PageFormat pf, int page) throws
    PrinterException {
    	
    	Page p;
		p = doc.getPage(page);
		
		if ( p == null){
			return NO_SUCH_PAGE;
		}

        PageGUI pageDrawer = new PageGUI();
        Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        pageDrawer.drawPage(g, p, new Point(0,0), new Rectangle( p.getWidth(), p.getHeight()), 1);
        
        return PAGE_EXISTS;
    }

	public void setDoc(Document d) {
		doc = d;
	}

	public Document getDoc() {
		return doc;
	}
}
