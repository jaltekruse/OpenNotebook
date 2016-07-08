/* ArrayDialog.java
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

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class ArrayDialog extends Dialog {
	private ResourceBundle i18n = CommonTasks.getBundle();

	private boolean result = false;
	private int size = 1;

	public ArrayDialog(Shell parent) {
		super(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	}

	public boolean open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());

		// widget creation
		shell.setLayout(new GridLayout(2, true));

		Label sizeLabel = new Label(shell, SWT.NULL);
		sizeLabel.setText(i18n.getString("Size:"));

		final Spinner sizeField = new Spinner(shell, SWT.BORDER);
		sizeField.setMinimum(1);
		sizeField.setMaximum(20);
		sizeField.setSelection(size);
		sizeField.setIncrement(1);
		sizeField.setPageIncrement(10);
		sizeField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(i18n.getString("&OK"));
		okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		shell.setDefaultButton(okButton);

		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(i18n.getString("&Cancel"));
		cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		sizeField.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				size = sizeField.getSelection();
			}
		});

		okButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				result = true;
				shell.dispose();
			}
		});

		cancelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		// main event loop
		shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		return result;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}