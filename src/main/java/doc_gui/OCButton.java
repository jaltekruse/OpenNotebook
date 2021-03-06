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

import java.awt.Color;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class OCButton extends JButton {

	/**
	 * An OCButton is the standard object for a button on the interface. The
	 * constructor takes various attributes for using a GridBagConstraints
	 * object. Its action can be changed by overriding the associatedAction
	 * method.
	 */
	private static final long serialVersionUID = 1L;
	private String s;
	
	public static KeyboardFocusManager keyFocusManager;
	
	static{
		try{
			keyFocusManager = new DefaultKeyboardFocusManager();
		}catch(Exception e){
		}
	}
	
	public OCButton(String str, int gridwidth, int gridheight, int gridx,
			int gridy, JComponent comp) {

		super(str);
		this.setFocusable(false);
		s = str;
		addToContainer(gridwidth, gridheight, gridx, gridy, false, comp);
	}

	public OCButton(Icon bi, String tip, int gridwidth, int gridheight,
			int gridx, int gridy, JComponent comp) {

		super(bi);
		this.setFocusable(false);
		this.setToolTipText(tip);
		addToContainer(gridwidth, gridheight, gridx, gridy, false, comp);
	}

	public OCButton(boolean hasInsets, String str, int gridwidth,
			int gridheight, int gridx, int gridy, JComponent comp) {

		super(str);
		this.setFocusable(false);
		addToContainer(gridwidth, gridheight, gridx, gridy, hasInsets, comp);
		s = str;

	}

	public OCButton(String str, String tip, int gridwidth, int gridheight,
			int gridx, int gridy, JComponent comp) {

		super(str);
		this.setFocusable(false);
		s = str;
		this.setToolTipText(tip);
		addToContainer(gridwidth, gridheight, gridx, gridy, false, comp);
	}
	
	private void addToContainer(int gridwidth, int gridheight,
			int gridx, int gridy, boolean hasInsets, JComponent comp){
		setMargin(new Insets(1, 1, 1, 1));
		GridBagConstraints bCon;
		bCon = new GridBagConstraints();
		bCon.fill = GridBagConstraints.BOTH;
		bCon.weightx = .1;
		bCon.weighty = .2;
		bCon.gridheight = gridheight;
		bCon.gridwidth = gridwidth;
		bCon.gridx = gridx;
		bCon.gridy = gridy;
		bCon.insets = new Insets(2, 2, 2, 2);
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				associatedAction();
			}
		});

		comp.add(this, bCon);
	}
	
	public static boolean textComponentHasFocus(){
		if ( keyFocusManager.getFocusOwner() instanceof JTextComponent ){
			return true;
		}
		return false;
	}
	
	public static JTextComponent getFocusedComponent(){
		if ( ! textComponentHasFocus()){
			return null;
		}
		return (JTextComponent) keyFocusManager.getFocusOwner();
	}

	public void associatedAction() {
		
		if ( ! textComponentHasFocus()){
			return;
		}
		JTextComponent currField = getFocusedComponent();
		int caretPos = 0;
		if (currField != null) {
			String currText = currField.getText();
			if (currField.getSelectionStart() == currField.getSelectionEnd()) {
				currText = currField.getText();
				caretPos = currField.getCaretPosition();
				String tempText = currText.substring(0, caretPos);
				tempText += s;
				tempText += currText.substring(caretPos, currText.length());
				currField.setText(tempText);
				currField.requestFocus();
				currField.setCaretPosition(caretPos + s.length());
			}
			else {
				int selectStart = currField.getSelectionStart();
				int selectEnd = currField.getSelectionEnd();
				String tempText = currText.substring(0, selectStart);
				tempText += s;
				tempText += currText.substring(selectEnd, currText.length());
				currField.setText(tempText);
				currField.requestFocus();
				currField.setCaretPosition(selectStart + s.length());
			}
		}
	}
}
