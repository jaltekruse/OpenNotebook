/* FunctionPanel.java
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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

import cz.natur.cuni.mirai.math.controller.MathInputController;
import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaFunction;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;

class FunctionPanel extends Composite implements ToolbarPanel {

	private MetaFunction currentFunction = null;
	private ArrayList<MetaFunction> functions = new ArrayList<MetaFunction>();
	private MathInputController controller;
	private Browser browser = null;
	private List list = null;
	
	private KeyAdapter keyListener = new KeyAdapter() {

		public void keyPressed(KeyEvent e) {
			int key = e.keyCode;
			Control label = null;
			
			switch(key) {				
				case SWT.F1:
					controller.setToolbarVisible(0);
					break;
				case SWT.F2:
					controller.setToolbarVisible(1);
					break;
				case SWT.F3:
					controller.setToolbarVisible(2);
					break;
				case SWT.F4:
					controller.setToolbarVisible(3);
					break;
				case SWT.ESC:
					controller.setToolbarVisible(-1);
					break;
				case SWT.CR:
					controller.toolbarInput(currentFunction);
					break;

				default:
			}

			if(label!=null) { 
				label.setFocus(); 
				return;
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	};

	MouseListener mouseListener = new MouseListener() {
	    public void mouseDoubleClick(MouseEvent e) {
			controller.toolbarInput(currentFunction);
	    }
	    public void mouseDown(MouseEvent e) { }
	    public void mouseUp(MouseEvent e) { }
	};

	SelectionListener selectionListener = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent event) {
		}

		public void widgetSelected(SelectionEvent event) {
			int i = list.getSelectionIndex();
			if(i>=0 && i<functions.size()) {
				currentFunction =  functions.get(i);
				URI uri = new File(MiraiMath.getManual()+"/functions/"+
					functions.get(i).getName()+".html").toURI();
				if(browser!=null) {
					browser.setUrl(uri.toString());
				}
			}
		}
	};

	public FunctionPanel(Composite parent, MetaModel metaModel, MetaGroup groups[], int next, int type) {
		super(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns=2;
		layout.marginWidth=0;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing=0;
		setLayout(layout);

		list = new List(this, SWT.V_SCROLL | SWT.SINGLE);
		GridData gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.heightHint = type == ToolbarFolder.LARGE ? 34*4 : 18*4;
		list.setLayoutData(gridData);

		try {
			browser = new Browser(this, SWT.NONE);
			browser.setLayoutData(new GridData(GridData.FILL_BOTH));
			browser.addKeyListener(keyListener);
		} catch(SWTError x) {
			x.printStackTrace();
		}

		for(int i=0;i<groups.length;i++) {
			if(groups[i].getComponents().length>0) {
				MetaComponent components[] = groups[i].getComponents();
				for(int j=0;j<components.length;j++) {
					functions.add((MetaFunction)components[j]);
				}
			}
		}
		sort( 0, functions.size()-1);

		for(int k=0;k<functions.size();k++) {
			list.add(((MetaFunction)functions.get(k)).getSignature());
		}

		list.addKeyListener(keyListener);
		list.addMouseListener(mouseListener);
		list.addSelectionListener(selectionListener);
	}

	private void sort(int p, int r) {
		if (p < r) {
			int q = partition(p,r);
			if (q == r) {
				q--;
			}
			sort(p,q);
			sort(q+1,r);
		}
	}

	private int partition (int lo, int hi) {
		MetaFunction pivot = functions.get(lo);

		while(true) {
			while(functions.get(hi).getName().compareTo(pivot.getName()) >= 0 && lo < hi) {
				hi--;
			}
			while(functions.get(lo).getName().compareTo(pivot.getName()) < 0 && lo < hi) {
				lo++;
			}
			if(lo < hi) {
				MetaFunction T = functions.get(lo);
				functions.set(lo, functions.get(hi));
				functions.set(hi, T);
			}
			else return hi;
		}
	}

	public void setController(MathInputController controller) {
		this.controller = controller;
	}

}
