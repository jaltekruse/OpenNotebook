/* JArrayDialog.java
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JArrayDialog extends JDialog {
	private boolean result = false;
	private int columns = 1;

    public JArrayDialog(Frame frame, String title) {
        super(frame, title, true);
        setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(1,2));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		JLabel sizeLabel = new JLabel();
		sizeLabel.setText("Size:");
		content.add(sizeLabel);

		final JSpinner sizeField = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
		sizeField.setValue(new Integer(columns));
		content.add(sizeField);
		add(content, BorderLayout.CENTER);

        JPanel strip = new JPanel();

        final JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        strip.add(okButton);

        final JButton cancelButton = new JButton("Cancel");
        strip.add(cancelButton);

        add(strip, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(okButton);


        sizeField.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Integer value = (Integer)sizeField.getValue();
				columns = value;
			} 	
        });

        okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				result = true;
		    	dispose();
			}
        });

        cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
		    	dispose();
			}
        });

        pack();
    }

	public boolean getResult() {
		return result;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getColumns() {
		return columns;
	}

	private static final long serialVersionUID = 1L;
}
