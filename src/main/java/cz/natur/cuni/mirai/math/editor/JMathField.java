/* JMathField.java
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import cz.natur.cuni.mirai.math.algebra.TeXSerializer;
import cz.natur.cuni.mirai.math.controller.MathInputController;
import cz.natur.cuni.mirai.math.editor.swt.ToolbarFolder;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathSequence;

import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.TeXIcon;

/**
 * This class is a Math Field. 
 * Displays and allows to edit single formula.
 * 
 * @author Bea Petrovicova
 */
public class JMathField extends JLabel {

	private TeXIcon renderer;
	private TeXSerializer serializer;

	FocusAdapter focusListener = new FocusAdapter() {
		public void focusGained(FocusEvent e) {
			controller.update();
		}
		public void focusLost(FocusEvent e) {
			update();
		}
	};

	private MouseAdapter mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			//System.out.println("requested");
			requestFocus();
		}
	};

	private KeyAdapter keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			int modifiers = e.getModifiersEx();
			//System.out.println("key_released: "+keyCode+", "+modifiers);
			controller.keyPressed(keyCode,modifiers);
		}
		public void keyReleased(KeyEvent e) {
			//int keyCode = e.getKeyCode();
			//int modifiers = e.getModifiersEx();
			//System.out.println("key_released: "+keyCode+", "+modifiers);
			//controller.keyReleased(keyCode,modifiers);
		}
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			//System.out.println("key_typed: "+ch+", "+modifiers);
			controller.keyTyped(ch);
		}
	};

	private MathInputController controller = new MathInputController() {
		public void setToolbarVisible(int index) {
			if(ToolbarFolder.getInstance()!=null) {
				ToolbarFolder.getInstance().setToolbarVisible(index,this, 
					getLocationOnScreen().x+getHeight(),
					getLocationOnScreen().y+getHeight());
			} else if(JToolbarFolder.getInstance()!=null) {
				JToolbarFolder.getInstance().setToolbarVisible(index,this, 
					getLocationOnScreen().x+getHeight(),
					getLocationOnScreen().y+getHeight());				
			}
		}

		public void update() {
			JMathField.this.update(currentField, currentOffset);
		}
	}; 


	public JMathField() {
		setBackground(Color.white);
		serializer = new TeXSerializer();
		renderer = new TeXIcon(TeXConstants.STYLE_DISPLAY, 18);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		addFocusListener(focusListener);
		addMouseListener(mouseListener);
		addKeyListener(keyListener);		
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
	}

	public MathInputController getController() {
		return controller;
	}

	public MathFormula getFormula() {
		return controller.getFormula();
	}

	public void setFormula(MathFormula formula) {
		controller.setFormula(formula);
		setPreferredSize(getPreferredSize(null, 0));
	}

	private Dimension getPreferredSize(MathSequence currentField, int currentOffset) {
		String serializedFormula = serializer.serialize(
				controller.getFormula(), currentField, currentOffset);
		//System.out.println("TeX> "+serializedFormula);
		TeXFormula texFormula = new TeXFormula(serializedFormula);
		renderer.setTeXFormula(texFormula);
		setIcon(renderer);
		return new Dimension(renderer.getIconWidth(),renderer.getIconHeight());
	}
	
	private void update(MathSequence currentField, int currentOffset) {
		if(getParent()!=null) {
			Dimension dim = getPreferredSize(currentField, currentOffset);
			setPreferredSize(dim);

			if(getSize().width != dim.width || 
				getSize().height != dim.height) {
	
				setSize(dim.width, dim.height);
				getParent().doLayout();
				if(currentField!=null) {
					if(getParent() instanceof JMathWorkbook) {
						((JMathWorkbook)getParent()).sizeContainer();
					}
				}
			}
			repaint();
		}
	}

	public void update() {
		update(null,0);
	}

	private static final long serialVersionUID = 1L;

}
