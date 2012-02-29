/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
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
