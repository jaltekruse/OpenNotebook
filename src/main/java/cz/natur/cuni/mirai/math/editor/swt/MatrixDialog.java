/* MatrixDialog.java
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

public class MatrixDialog extends Dialog {
	private ResourceBundle i18n = CommonTasks.getBundle();

	private boolean result = false;
	private int columns = 2;
	private int rows = 2;

	public MatrixDialog(Shell parent) {
		super(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	}

	public boolean open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());

		// widget creation
		shell.setLayout(new GridLayout(2, false));

		Label columnLabel = new Label(shell, SWT.NULL);
		columnLabel.setText(i18n.getString("Columns:"));

		final Spinner columnField = new Spinner(shell, SWT.BORDER);
		columnField.setMinimum(1);
		columnField.setMaximum(5);
		columnField.setSelection(columns);
		columnField.setIncrement(1);
		columnField.setPageIncrement(5);
		columnField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label rowLabel = new Label(shell, SWT.NULL);
		rowLabel.setText(i18n.getString("Rows:"));

		final Spinner rowField = new Spinner(shell, SWT.BORDER);
		rowField.setMinimum(1);
		rowField.setMaximum(5);
		rowField.setSelection(rows);
		rowField.setIncrement(1);
		rowField.setPageIncrement(5);
		rowField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(i18n.getString("&OK"));
		okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		shell.setDefaultButton(okButton);

		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(i18n.getString("&Cancel"));
		cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		columnField.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				columns = columnField.getSelection();
			}
		});

		rowField.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				rows = rowField.getSelection();
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

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getColumns() {
		return columns;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getRows() {
		return rows;
	}
}