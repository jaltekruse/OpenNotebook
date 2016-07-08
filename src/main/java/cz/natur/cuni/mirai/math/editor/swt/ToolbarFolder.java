/* ToolbarFolder.java
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import cz.natur.cuni.mirai.math.controller.MathInputController;
import cz.natur.cuni.mirai.math.icon.SymbolIcon;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;

public class ToolbarFolder extends Composite {
	private ResourceBundle i18n = CommonTasks.getBundle();

	public static final int SMALL = 0;
	public static final int LARGE = 1;

	private Display display;
	private TabFolder tabFolder;
	private ArrayList<ToolbarPanel> toolPanels = new ArrayList<ToolbarPanel>();

	protected static ToolbarFolder instance;

	public static ToolbarFolder getInstance() {
		return instance;
	}

	public static void createInstance(Shell parent, MetaModel metaModel, int type) {
		Shell shell = new Shell(parent, SWT.NO_TRIM | SWT.PRIMARY_MODAL);
		GridLayout layout = new GridLayout();
		layout.marginWidth=0;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing=0;
		shell.setLayout(layout);
		instance = new ToolbarFolder(shell, metaModel, type);
		shell.pack();
	}

	private TraverseListener traverseListener = new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if(e.detail == SWT.TRAVERSE_ESCAPE) {
				e.doit = false;
				hideToolbar();
			}
		}
	};

	private ToolbarFolder(Shell shell, MetaModel metaModel, int type) {
		super(shell, SWT.NONE);

		display = shell.getDisplay();

		GridLayout layout = new GridLayout();
		layout.marginWidth=0;
		layout.marginHeight=0;
		setLayout(layout);

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent event) {
				onPaint(event);
			}
		});
		tabFolder.addTraverseListener(traverseListener);

		String names[] = {
			i18n.getString("General"),
			i18n.getString("Operators"),
			i18n.getString("Symbols"),
			i18n.getString("Functions")
		};
		String tools[] = {
			MetaModel.GENERAL,
			MetaModel.OPERATORS,
			MetaModel.SYMBOLS,
			MetaModel.FUNCTIONS
		};

		for(int i=0;i<names.length;i++) {
			TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
			InputStream stream = SymbolIcon.class.getResourceAsStream("small/" +tools[i] +".png");
			Image icon = new Image(getDisplay(), stream);
			tabItem.setImage(icon);				
			if(type == ToolbarFolder.LARGE) {
				tabItem.setText(" "+names[i]+" ");
			}
			MetaGroup groups[] = metaModel.getGroups(tools[i]);

			if(i < 3) {
				ToolbarGrid toolbarGrid = new ToolbarGrid(tabFolder, metaModel, groups, i+1, type);
				tabItem.setControl(toolbarGrid);
				toolPanels.add(toolbarGrid);

			} else {
				FunctionPanel functionPanel = new FunctionPanel(tabFolder, metaModel, groups, i+1, type);
				tabItem.setControl(functionPanel);
				toolPanels.add(functionPanel);				
			}
		}
	}

	public void setToolbarVisible(int index, MathInputController controller, int x, int y) {
		if(index<0) {
			hideToolbar();
		} else {
			for(int i=0;i<toolPanels.size();i++) {
				toolPanels.get(i).setController(controller);				
			}
			showToolbar(index, x , y);
		}	
	}

	private void showToolbar(final int index, final int x, final int y) {
		display.syncExec(new Runnable() {
			public void run() {
				tabFolder.setSelection(index<4?index:0);
				if(!getShell().isVisible()) {
					getShell().setLocation(x, y);
					getShell().setVisible(true);
				}
				setFocus();
			}
		});
	}

	private void hideToolbar() {
		display.syncExec(new Runnable() {
			public void run() {
				getShell().setVisible(false);
			}
		});
	}
	
	private void onPaint(PaintEvent event) {
		Rectangle rect = getClientArea();
		if (rect.width == 0 || rect.height == 0) return;
		
		GC gc = event.gc;
		Display disp= getDisplay();
		Color c = disp.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		gc.setForeground(c);
		gc.drawRectangle(rect.x, rect.y, rect.width-1, rect.height-1);
	}
}
