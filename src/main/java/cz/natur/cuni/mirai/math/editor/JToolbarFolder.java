/* JToolbarFolder.java
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

import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cz.natur.cuni.mirai.math.controller.MathInputController;
import cz.natur.cuni.mirai.math.editor.swt.ToolbarFolder;
import cz.natur.cuni.mirai.math.icon.SymbolIcon;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;

public class JToolbarFolder extends JPanel {

	public static final int SMALL = 0;
	public static final int LARGE = 1;

	private JTabbedPane tabFolder;
	private ArrayList<JToolbarGrid> toolPanels = new ArrayList<JToolbarGrid>();
	private JDialog toolbarDialog;

	protected static JToolbarFolder instance;

	public static JToolbarFolder getInstance() {
		return instance;
	}

	public static void createInstance(Frame parent, MetaModel metaModel, int type) {
		instance = new JToolbarFolder(parent, metaModel, type);
	}

	private JToolbarFolder(Frame parent, MetaModel metaModel, int type) {
		setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        setLayout(new BorderLayout());

		tabFolder = new JTabbedPane();
		add(tabFolder, BorderLayout.CENTER);
		
		String names[] = {
				MetaModel.GENERAL,
				MetaModel.OPERATORS,				
				MetaModel.SYMBOLS,
				MetaModel.FUNCTIONS};

		for(int i=0;i<names.length;i++) {
			MetaGroup groups[] = metaModel.getGroups(names[i]);

			JPanel panel = new JPanel();
			FlowLayout layout = new FlowLayout(FlowLayout.LEADING,0,0); 
			panel.setLayout(layout);

			JToolbarGrid toolbarGrid = new JToolbarGrid(metaModel, groups, i+1, type);
			panel.add(toolbarGrid);

			tabFolder.addTab((type==ToolbarFolder.LARGE?names[i]:""), panel);
			toolPanels.add(toolbarGrid);

			try {
				InputStream stream = SymbolIcon.class.getResourceAsStream(
						(type==ToolbarFolder.SMALL?"large/":"small/")+names[i]+".png");
				Image image = ImageIO.read(stream);
				ImageIcon icon = new ImageIcon(image);
				tabFolder.setIconAt(i, icon);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// label
		JLabel toolbarLabel = new JLabel();
		if(type==LARGE) {
			String labelText = "Use F1, Esc to switch toolbars; cursor keys, Enter to select.";
			toolbarLabel.setText(labelText);
		} else {
			String labelText = "Use F1, Esc to switch toolbars.";
			toolbarLabel.setText(labelText);			
		}
		add(toolbarLabel, BorderLayout.SOUTH);
		
		toolbarDialog = new JDialog(parent, "Toolbar", true);
		/*toolbarDialog.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy() {
			public Component getFirstComponent(Container focusCycleRoot) {
				int i = tabFolder.getSelectedIndex();
				return toolPanels.get(i).getFirstComponent();
			}
			private static final long serialVersionUID = 1L;
		});*/
		toolbarDialog.setLocation(75, 75);
		toolbarDialog.getContentPane().add(this);
		toolbarDialog.setUndecorated(true);
		toolbarDialog.pack();
	}

	public void setToolbarVisible(int index, MathInputController controller, int x, int y) {
		if(index<0) {
			hideToolbar();
		} else {
			for(int i=0;i<toolPanels.size();i++) {
				toolPanels.get(i).setCurrentController(controller);				
			}
			showToolbar(index,x,y);
		}	
	}

	private void showToolbar(final int index, final int x, final int y) {
		if(!toolbarDialog.isVisible()) {
			toolbarDialog.setLocation(x, y);
			toolbarDialog.setVisible(true);
		}
		tabFolder.setSelectedIndex(index<4?index:0);
	}

	private void hideToolbar() {
		toolbarDialog.setVisible(false);
	}

	private static final long serialVersionUID = 1L;
}
