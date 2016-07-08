/* JToolbarGrid.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.imageio.ImageIO;

import cz.natur.cuni.mirai.math.controller.MathInputController;
import cz.natur.cuni.mirai.math.editor.swt.ToolbarFolder;
import cz.natur.cuni.mirai.math.icon.SymbolIcon;
import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaFunction;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.meta.MetaSymbol;

/**
 * This class is a toolbar grid.
 * 
 * @author Bea Petrovicova
 */
public class JToolbarGrid extends JPanel {

	private MathInputController currentController;
	private Border activeBorder, passiveBorder; 
	private JLabel firstLabel, currentLabel;
	private int next = 0, type = 0;

	private FocusAdapter focusListener = new FocusAdapter() {
		public void focusGained(FocusEvent e) {
			//System.out.println("gained");
			currentLabel = (JLabel)e.getComponent();
			currentLabel.setBorder(activeBorder);
			ToolTipManager.sharedInstance().setInitialDelay(0);			
			ToolTipManager.sharedInstance().mouseMoved(
				new MouseEvent(currentLabel, 0, 0, 0, 0, 
				currentLabel.getHeight()-20, 0, false));

		}
		public void focusLost(FocusEvent e) {
			JLabel previousLabel = (JLabel)e.getComponent();
			previousLabel.setBorder(passiveBorder);
			ToolTipManager.sharedInstance().mouseExited(
				new MouseEvent(currentLabel, 0, 0, 0, 0, 
				currentLabel.getHeight()-20, 0, false));
		}
	};
	
	private MouseAdapter mouseListener = new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
			//System.out.println("requested");
			currentLabel = (JLabel)e.getComponent();
			currentLabel.requestFocus();
			MetaComponent input = (MetaComponent)currentLabel.getClientProperty("input");
			currentController.toolbarInput(input);
	    }
	};

	private KeyAdapter keyListener = new KeyAdapter() {

		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			//int modifiers = e.getModifiersEx();

			FocusTraversalPolicy policy = 
				getFocusCycleRootAncestor().getFocusTraversalPolicy();
			JLabel label = null;

			switch(key) {
				case KeyEvent.VK_LEFT:
					try {
						label = (JLabel)policy.getComponentBefore(
								getFocusCycleRootAncestor(), currentLabel);
					} catch(ClassCastException x) {}
					break;
				case KeyEvent.VK_RIGHT:
					try {
						label = (JLabel)policy.getComponentAfter(
								getFocusCycleRootAncestor(), currentLabel);
					} catch(ClassCastException x) {}
					break;
				case KeyEvent.VK_UP:
					if(currentLabel !=null && currentLabel.getParent().getLayout() instanceof GridLayout) {
						GridLayout layout = (GridLayout)currentLabel.getParent().getLayout();
						int columns = layout.getColumns();
						label = currentLabel;
						for(int i=0;i<columns;i++) {
							try {
								label = (JLabel)policy.getComponentBefore(
										getFocusCycleRootAncestor(), label);
							} catch (ClassCastException x) { return; }
						}
					} else {
						label = currentLabel;
						while(true) {
							try {
								label = (JLabel)policy.getComponentBefore(
										getFocusCycleRootAncestor(), label);
								if(label.getParent()!=currentLabel.getParent()) {
									label = (JLabel)label.getParent().getComponent(0);
									break;
								}
							} catch (ClassCastException x) { return; }
						}
					}

					break;
				case KeyEvent.VK_DOWN:
					if(currentLabel !=null && currentLabel.getParent().getLayout() instanceof GridLayout) {
						GridLayout layout = (GridLayout)currentLabel.getParent().getLayout();
						int columns = layout.getColumns();
						label = currentLabel;
						for(int i=0;i<columns;i++) {
							try {
								label = (JLabel)policy.getComponentAfter(getFocusCycleRootAncestor(), label);
							} catch (ClassCastException x) { return; }
						}
					} else {
						label = currentLabel;
						while(true) {
							try {
								label = (JLabel)policy.getComponentAfter(getFocusCycleRootAncestor(), label);
								if(label.getParent()!=currentLabel.getParent()) { 
									break; 
								}
							} catch (ClassCastException x) { return; }
						}
					}
					break;
			}

			if(label!=null) { 
				label.requestFocus(); 
			}
		}

		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			//int modifiers = e.getModifiersEx();

			FocusTraversalPolicy policy = 
				getFocusCycleRootAncestor().getFocusTraversalPolicy();
			JLabel label = null;

			switch(key) {
				case KeyEvent.VK_HOME: 
					Container firstLabel = (Container)policy.getFirstComponent(
							getFocusCycleRootAncestor());
					label = (JLabel)policy.getComponentAfter(
							getFocusCycleRootAncestor(), firstLabel);
					break;
				case KeyEvent.VK_END:  
					label = (JLabel)policy.getLastComponent(
							getFocusCycleRootAncestor());
					break;
				case KeyEvent.VK_ENTER:
					MetaComponent input = (MetaComponent)currentLabel.getClientProperty("input");
					currentController.toolbarInput(input);
					break;
				case KeyEvent.VK_F1:
					currentController.setToolbarVisible(next);
					break;
				case KeyEvent.VK_ESCAPE:
					currentController.setToolbarVisible(-1);
				default:
			}

			if(label!=null) { 
				label.requestFocus(); 
			}
		}

		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			//System.out.println("keyTyped " + ch);

			FocusTraversalPolicy policy = 
					getFocusCycleRootAncestor().getFocusTraversalPolicy();
			Container firstLabel = (Container)policy.getFirstComponent(getFocusCycleRootAncestor());
			JLabel label = (JLabel)policy.getComponentAfter( getFocusCycleRootAncestor(), firstLabel);

			while(true) {
				MetaComponent input = (MetaComponent)label.getClientProperty("input");
				if(input==null) {
					return;
				}
				if(input.getKey() == ch) {
					currentController.toolbarInput(input);
					break;
				}
				try {
					label = (JLabel)policy.getComponentAfter(
							getFocusCycleRootAncestor(), label);
				} catch(ClassCastException x) {
					return;
				}
			}
		}
	};

	public JToolbarGrid(MetaModel metaModel, MetaGroup groups[], int next, int type) {
		this.next = next;
		this.type = type;
		SymbolIcon.setType(type);
		
		activeBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		passiveBorder = BorderFactory.createLineBorder(getBackground(), 1);

		BoxLayout itemLayout = new BoxLayout(this, BoxLayout.Y_AXIS); 
		setLayout(itemLayout);

		for(int i=0;i<groups.length;i++) {
			if(groups[i].getComponents().length>0) {
				String tab = groups[i].getGroup().toLowerCase();
				MetaComponent components[] = groups[i].getComponents();
				JPanel toolbarGroup = new JPanel();
				int fillerCount = 0;
				if(groups[i].getColumns()==0) {
					FlowLayout layout = new FlowLayout(FlowLayout.LEADING,0,0); 
					toolbarGroup.setLayout(layout); 
				} else {
					int columns = groups[i].getColumns();
					if(components.length>0 && components[0].getIcon()==null && type==ToolbarFolder.SMALL) {
						columns = columns/2;
					}
					int rows = components.length/columns + ((components.length%columns>0) ? 1 : 0);
					GridLayout layout = new GridLayout(rows, columns);
					toolbarGroup.setLayout(layout);
					fillerCount = (components.length%columns>0) ? columns - components.length%columns : 0;
				}
				for(int j=0;j<components.length;j++) {
					if(components[i] instanceof MetaSymbol) {
						addSymbolIcon(toolbarGroup, (MetaSymbol)components[j]);				
					} else if(components[j].getIcon() != null) {
						addImageIcon(toolbarGroup, tab, components[j]);
					} else {
						addLabel(toolbarGroup, components[j]);
					}
				}
				for(int j=0;j<fillerCount;j++) {
					toolbarGroup.add(new JLabel());
				}
				add(toolbarGroup);
			}
		}
	}
	
	public Component getFirstComponent() {
		return firstLabel;
	}
	
	public void requestFocus() {
		Component label = getFirstComponent();
		label.requestFocus();		
	}

	public void setCurrentController(MathInputController controller) {
		this.currentController = controller;
	}

	private void addImageIcon(JPanel toolbarGroup, String group, MetaComponent meta) {
		JLabel label = new JLabel() {
			public JToolTip createToolTip() {
				JToolTip tip = super.createToolTip();
				tip.setBackground(Color.yellow);
				return tip;
			}
			private static final long serialVersionUID = 1L;
		};
		if(firstLabel==null) {
			firstLabel = label;
		}
		try {
			InputStream stream = SymbolIcon.class.getClassLoader().getResourceAsStream(
				(type==ToolbarFolder.LARGE?"large/":"small/")+group+"/"+meta.getIcon());
			Image image = ImageIO.read(stream);
			ImageIcon icon = new ImageIcon(image);
			label.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(meta.getName().length()>1) {
			label.setToolTipText(meta.getName()+"(");
		} else {
			label.setToolTipText("a"+meta.getName()+"b");
		}
		label.setBorder(passiveBorder);
		label.addFocusListener(focusListener);
		label.addMouseListener(mouseListener);
		label.addKeyListener(keyListener);
		label.putClientProperty("input", meta);
		label.setFocusable(true);
		toolbarGroup.add(label);
	}

	private void addSymbolIcon(JPanel toolbarGroup, MetaSymbol meta) {
		JLabel label = new JLabel() {
			public JToolTip createToolTip() {
				JToolTip tip = super.createToolTip();
				tip.setBackground(Color.YELLOW);
				return tip;
			}
			private static final long serialVersionUID = 1L;
		};
		if(firstLabel==null) {
			firstLabel = label;
		}
		Icon icon = new SymbolIcon(meta);
		label.setIcon(icon);
		if(meta.getName().length()>1) {
			label.setToolTipText(meta.getName()+" [Esc]");
		} else {
			label.setToolTipText("a"+meta.getName()+"b");
		}
		label.setToolTipText(meta.getName()+" [Esc]");
		label.setBorder(passiveBorder);
		label.addFocusListener(focusListener);
		label.addMouseListener(mouseListener);
		label.addKeyListener(keyListener);
		label.putClientProperty("input", meta);
		label.setFocusable(true);
		toolbarGroup.add(label);
	}

	private void addLabel(JPanel toolbarGroup, MetaComponent meta) {
		JLabel label = new JLabel() {
			public JToolTip createToolTip() {
				JToolTip tip = super.createToolTip();
				tip.setBackground(Color.YELLOW);
				return tip;
			}
			private static final long serialVersionUID = 1L;
		};
		if(firstLabel==null) {
			firstLabel = label;
		}
		label.setText(((MetaFunction)meta).getSignature()+" "+(meta.getKey()!=0?""+meta.getKey():""));
		label.setToolTipText(meta.getName()+"(");
		label.setFont(label.getFont().deriveFont(8));
		label.setBorder(passiveBorder);
		label.addFocusListener(focusListener);
		label.addMouseListener(mouseListener);
		label.addKeyListener(keyListener);
		label.putClientProperty("input", meta);
		label.setFocusable(true);
		toolbarGroup.add(label);
	}

	private static final long serialVersionUID = 1L;
}
