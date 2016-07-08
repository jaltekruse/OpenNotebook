/* ToolbarGrid.java
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import cz.natur.cuni.mirai.math.controller.MathInputController;
import cz.natur.cuni.mirai.math.icon.SymbolIcon;
import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaFunction;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;

/**
 * This class is a toolbar grid.
 * 
 * @author Bea Petrovicova
 */
class ToolbarGrid extends Composite implements ToolbarPanel {

	private Control toolbarFields[];
	private MathInputController controller;
	private Control currentLabel;
	private int next = 0, type = 0, length = 0;

	private TraverseListener traverseListener = new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if(e.detail == SWT.TRAVERSE_TAB_NEXT ||
			   e.detail == SWT.TRAVERSE_TAB_PREVIOUS ||
			   e.detail == SWT.TRAVERSE_PAGE_NEXT ||
			   e.detail == SWT.TRAVERSE_PAGE_PREVIOUS)
			e.doit = true;
		}
	};
	
	private FocusAdapter focusListener = new FocusAdapter() {
		public void focusGained(FocusEvent e) {
			//System.out.println("gained");
			currentLabel = (Control)e.getSource();
		}
		public void focusLost(FocusEvent e) {
		}
	};
	
	private MouseAdapter mouseListener = new MouseAdapter() {
	    public void mouseDown(MouseEvent e) {
			//System.out.println("requested");
			currentLabel = (Control)e.getSource();
			currentLabel.forceFocus();
			MetaComponent input = (MetaComponent)currentLabel.getData("input");
			controller.toolbarInput(input);
	    }
	};

	private KeyAdapter keyListener = new KeyAdapter() {

		public void keyPressed(KeyEvent e) {
			int key = e.keyCode;
			e.doit = true;
			//System.out.println("keyPressed("+key);
			Control source = (Control) e.getSource();
			int index = ((Integer)source.getData("index")).intValue();
			Control label = null;

			switch(key) {
				case SWT.F1: 
					controller.setToolbarVisible(next);
					return;
				case SWT.F2:
					controller.setToolbarVisible(1);
					return;
				case SWT.F3:
					controller.setToolbarVisible(2);
					return;
				case SWT.F4:
					controller.setToolbarVisible(3);
					return;
				case SWT.ESC:
					controller.setToolbarVisible(-1);
					return;
				case SWT.CR:
					MetaComponent input = (MetaComponent)currentLabel.getData("input");
					controller.toolbarInput(input);
					return;

				case SWT.ARROW_LEFT:
					if(index>0) {
						label = toolbarFields[index-1];
					} 
					break;
				case SWT.ARROW_RIGHT:
					if(index+1<toolbarFields.length) {
						label = toolbarFields[index+1];
					} 
					break;
				case SWT.ARROW_UP:
					if(currentLabel !=null && currentLabel.getParent().getLayout() instanceof GridLayout) {
						GridLayout layout = (GridLayout)currentLabel.getParent().getLayout();
						int columns = layout.numColumns;
						if(index-columns>=0) {
							label = toolbarFields[index-columns];
						} 
					} else {
						for(int i=index;i>0;i--) {
							if(toolbarFields[i].getParent()!=currentLabel.getParent()) {
								int length = toolbarFields[i].getParent().getChildren().length;
								label = toolbarFields[i-length+1];
								break;
							}
						}
					}
					break;
				case SWT.ARROW_DOWN:
					if(currentLabel !=null && currentLabel.getParent().getLayout() instanceof GridLayout) {
						GridLayout layout = (GridLayout)currentLabel.getParent().getLayout();
						int columns = layout.numColumns;
						if(index+columns<toolbarFields.length) {
							label = toolbarFields[index+columns];
						}
					} else {
						for(int i=index;i<toolbarFields.length;i++) {
							if(toolbarFields[i].getParent()!=currentLabel.getParent()) {
								label = toolbarFields[i];
								break;
							}
						}
					}
					break;

				case SWT.HOME: 
					label = toolbarFields[0];
					break;
				case SWT.END:  
					label = toolbarFields[toolbarFields.length-1];
					break;
			}

			if(label!=null) { 
				label.setFocus(); 
				return;
			}

			if(e.character!=0) {
				char ch = e.character;
				//System.out.println("keyTyped " + ch);

				for(int i=0; i<toolbarFields.length; i++) {
					MetaComponent input = (MetaComponent)toolbarFields[i].getData("input");
					if(input.getKey() == ch) {
						controller.toolbarInput(input);
						return;
					}
				}
			}
		}
	};
	
	public ToolbarGrid(Composite parent, MetaModel metaModel, MetaGroup groups[], int next, int type) {
		super(parent, SWT.NONE);
		this.next = next;
		this.type = type;

		RowLayout itemLayout = new RowLayout();
		itemLayout.marginTop = 0;
		itemLayout.marginBottom = 0;
		itemLayout.marginLeft = 0;
		itemLayout.marginRight = 0;				
		itemLayout.marginWidth=0;
		itemLayout.marginHeight=0;
		itemLayout.spacing=0;
		itemLayout.type = SWT.VERTICAL;
		setLayout(itemLayout);

		ArrayList<Control> arrayList = new ArrayList<Control>();
		for(int i=0;i<groups.length;i++) {
			if(groups[i].getComponents().length>0) {
				String group = groups[i].getGroup().toLowerCase();
				MetaComponent components[] = groups[i].getComponents();
				Composite toolbarGroup = new Composite(this, SWT.NONE);
				if(groups[i].getColumns()==0) {
					RowLayout layout = new RowLayout();
					layout.marginTop=0;
					layout.marginBottom=0;
					layout.marginLeft=0;
					layout.marginRight=0;
					layout.marginWidth=0;
					layout.marginHeight=0;
					layout.spacing = 0;
					toolbarGroup.setLayout(layout); 
				} else {
					int columns = groups[i].getColumns();
					if(components.length>0 && components[0].getIcon()==null && type==ToolbarFolder.SMALL) {
						columns = columns/2;
					}
					GridLayout layout = new GridLayout();
					layout.marginTop=0;
					layout.marginBottom=0;
					layout.marginLeft=0;
					layout.marginRight=0;
					layout.marginWidth=0;
					layout.marginHeight=0;
					layout.horizontalSpacing = 0;
					layout.verticalSpacing = 0;
					layout.numColumns = columns;
					toolbarGroup.setLayout(layout);
				}
				for(int j=0;j<components.length;j++) {
					if(components[j].getIcon() != null) {
						addImageIcon(toolbarGroup, group, components[j]);
					} else {
						addLabel(toolbarGroup, components[j]);
					}
				}
				Control children[] = toolbarGroup.getChildren();
				for(int j=0;j<components.length;j++) {
					arrayList.add(children[j]);
				}
			}
		}
		toolbarFields = arrayList.toArray(new Control[arrayList.size()]);
	}
	
	public void setController(MathInputController controller) {
		this.controller = controller;
	}

	private void addImageIcon(Composite toolbarGroup, String group, MetaComponent meta) {
		InputStream stream = SymbolIcon.class.getClassLoader().getResourceAsStream(
			(type==ToolbarFolder.LARGE?"large/":"small/")+group+"/"+meta.getIcon());
		if(stream == null) {
			System.out.println(meta.getName());
		}
		Image icon = new Image(getDisplay(), stream);
		ToolbarIcon label = new ToolbarIcon(toolbarGroup, SWT.SHADOW_OUT);
		label.setImage(icon);
		if(meta.getName().length()>1) {
			if(meta instanceof MetaFunction) {
				label.setToolTipText(meta.getName()+"(");
			} else {
				label.setToolTipText(meta.getName()+" [Esc]");
			}
		} else {
			label.setToolTipText("a"+meta.getName()+"b");
		}
		label.addTraverseListener(traverseListener);
		label.addFocusListener(focusListener);
		label.addMouseListener(mouseListener);
		label.addKeyListener(keyListener);
		label.setData("input", meta);
		label.setData("index", new Integer(length));
		length++;
	}

	private void addLabel(Composite toolbarGroup, MetaComponent meta) {
		ToolbarIcon label = new ToolbarIcon(toolbarGroup, SWT.SHADOW_OUT);
		if(meta.getKey()!=0) {
			label.setText(
				(new StringBuffer(((MetaFunction)meta).getSignature())).insert(
				meta.getName().indexOf(meta.getKey()),'&').toString());
		} else {
			label.setText(((MetaFunction)meta).getSignature());			
		}
		label.setToolTipText(meta.getName()+"(");
		label.addTraverseListener(traverseListener);
		label.addFocusListener(focusListener);
		label.addMouseListener(mouseListener);
		label.addKeyListener(keyListener);
		label.addListener(SWT.KeyDown, new Listener(){
			public void handleEvent(Event arg0) {
				System.out.println("KeyDown!");
			}
		});
		label.addListener(SWT.KeyUp, new Listener(){
			public void handleEvent(Event arg0) {
				System.out.println("KeyUp!");
			}
		});
		label.setData("input", meta);
		label.setData("index", new Integer(length));
		length++;
	}

}
