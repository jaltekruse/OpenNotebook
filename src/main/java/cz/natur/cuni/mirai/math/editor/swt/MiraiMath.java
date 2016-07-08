/* MiraiMath.java
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class MiraiMath extends CommonTasks {

	public Menu createMenu() {
		Menu menu = new Menu(shell, SWT.BAR);
		MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
		fileItem.setText(getBundle().getString("&File"));
		MenuItem editItem = new MenuItem(menu, SWT.CASCADE);
		editItem.setText(getBundle().getString("&Edit"));
		MenuItem insertItem = new MenuItem(menu, SWT.CASCADE);
		insertItem.setText(getBundle().getString("&Insert"));
		MenuItem helpItem = new MenuItem(menu, SWT.CASCADE);
		helpItem.setText(getBundle().getString("&Help"));

		CoolBar coolBar = new CoolBar(shell, SWT.FLAT);
		coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		class Cool implements Listener {
			public void handleEvent(Event event) {
				shell.layout();
			}
		}
		coolBar.addListener(SWT.Resize, new Cool());

		ToolBar fileBar = new ToolBar (coolBar, SWT.FLAT | SWT.NO_FOCUS);
		ToolBar editBar = new ToolBar (coolBar, SWT.FLAT | SWT.NO_FOCUS);
		ToolBar toolsBar = new ToolBar (coolBar, SWT.FLAT | SWT.NO_FOCUS);

		Menu fileMenu = new Menu(menu);
		fileItem.setMenu(fileMenu);

		MenuItem newItem = new MenuItem(fileMenu, SWT.NONE);
		newItem.setText(getBundle().getString("&New")+"\tCTRL+N"); 
		newItem.setAccelerator(SWT.CTRL + 'N');
		newItem.setImage(resources.getImage("small/New.gif"));
		ToolItem newIcon = new ToolItem (fileBar, SWT.NONE);
		newIcon.setImage(resources.getImage("small/New.gif"));
		newIcon.setToolTipText(getBundle().getString("New")+" CTRL+N");

		class New implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
			public void widgetSelected(SelectionEvent event) {
				clean();
			}
		};
		New nev = new New();
		newItem.addSelectionListener(nev);
		newIcon.addSelectionListener(nev);

		MenuItem openItem = new MenuItem(fileMenu, SWT.PUSH);
		openItem.setText(getBundle().getString("&Open...")+"\tCTRL+O");
		openItem.setAccelerator(SWT.CTRL + 'O');
		openItem.setImage(resources.getImage("small/Open.gif"));
		ToolItem openIcon = new ToolItem (fileBar, SWT.NONE);
		openIcon.setImage(resources.getImage("small/Open.gif"));
		openIcon.setToolTipText(getBundle().getString("Open")+" CTRL+O");

		final MenuItem saveItem = new MenuItem(fileMenu, SWT.PUSH);
		saveItem.setText(getBundle().getString("&Save"));
		saveItem.setEnabled(false);
		saveItem.setImage(resources.getImage("small/Save.gif"));
		final ToolItem saveIcon = new ToolItem (fileBar, SWT.NONE);
		saveIcon.setImage(resources.getImage("small/Save.gif"));
		saveIcon.setEnabled(false);
		saveIcon.setToolTipText(getBundle().getString("Save"));

		class Open implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
			public void widgetSelected(SelectionEvent event) {
				boolean enable = open();
				saveItem.setEnabled(enable);
				saveIcon.setEnabled(enable);
			}
		};
		Open open = new Open();
		openItem.addSelectionListener(open);
		openIcon.addSelectionListener(open);

		class Save implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
			public void widgetSelected(SelectionEvent event) {
				save();
			}
		}
		Save save = new Save();
		saveItem.addSelectionListener(save);
		saveIcon.addSelectionListener(save);

		MenuItem saveAsItem = new MenuItem(fileMenu, SWT.PUSH);
		saveAsItem.setText(getBundle().getString("&Save As ...")+"\tCTRL+S");
		saveAsItem.setAccelerator(SWT.CTRL + 'S');
		saveAsItem.setImage(resources.getImage("small/SaveAs.gif"));

		class SaveAs implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
			public void widgetSelected(SelectionEvent event) {
				boolean enable = saveAs();
				saveItem.setEnabled(enable);
				saveIcon.setEnabled(enable);
			}
		}
		SaveAs saveAs = new SaveAs();
		saveAsItem.addSelectionListener(saveAs);

		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem exportItem = new MenuItem(fileMenu, SWT.PUSH);
		exportItem.setText(getBundle().getString("&Export ..."));
		exportItem.setImage(resources.getImage("small/Export.gif"));
		ToolItem exportIcon = new ToolItem (fileBar, SWT.NONE);
		exportIcon.setImage(resources.getImage("small/Export.gif"));
		exportIcon.setToolTipText(getBundle().getString("Export"));

		class Export implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				export();
			}
		};
		Export export = new Export();
		exportItem.addSelectionListener(export);
		exportIcon.addSelectionListener(export);

		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem exitItem = new MenuItem(fileMenu, SWT.NONE);
		exitItem.setText(getBundle().getString("E&xit"));
		class Exit implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				if(saveChanges()) {
					shell.getDisplay().dispose();
				}
			}
		}
		exitItem.addSelectionListener(new Exit());

		Menu editMenu = new Menu(menu);
		editItem.setMenu(editMenu);

		/* MenuItem undoItem = new MenuItem(editMenu, SWT.PUSH);
		undoItem.setText(getBundle().getString("&Undo")+"\tCTRL+Z");
		undoItem.setAccelerator(SWT.CTRL + 'Z');
		undoItem.setImage(resources.getImage("small/Undo.gif"));
		ToolItem undoIcon = new ToolItem (editBar, SWT.NONE);
		undoIcon.setImage(resources.getImage("small/Undo.gif"));
		undoIcon.setToolTipText(getBundle().getString("Undo")+" CTRL+Z");

		MenuItem redoItem = new MenuItem(editMenu, SWT.PUSH);
		redoItem.setText(getBundle().getString("&Redo")+"\tCTRL+Y");
		redoItem.setAccelerator(SWT.CTRL + 'Y');
		redoItem.setImage(resources.getImage("small/Redo.gif"));
		ToolItem redoIcon = new ToolItem (editBar, SWT.NONE);
		redoIcon.setImage(resources.getImage("small/Redo.gif"));
		redoIcon.setToolTipText(getBundle().getString("Redo")+" CTRL+Y"); 

		new MenuItem(editMenu, SWT.SEPARATOR); */

		MenuItem cutItem = new MenuItem(editMenu, SWT.PUSH);
		cutItem.setText(getBundle().getString("&Cut")+"\tCTRL+X");
		cutItem.setAccelerator(SWT.CTRL + 'X');
		cutItem.setImage(resources.getImage("small/Cut.gif"));
		ToolItem cutIcon = new ToolItem (editBar, SWT.NONE);
		cutIcon.setImage(resources.getImage("small/Cut.gif"));
		cutIcon.setToolTipText(getBundle().getString("Cut")+" CTRL+X");

		MenuItem copyItem = new MenuItem(editMenu, SWT.PUSH);
		copyItem.setText(getBundle().getString("&Copy")+"\tCTRL+C");
		copyItem.setAccelerator(SWT.CTRL + 'C');
		copyItem.setImage(resources.getImage("small/Copy.gif"));
		ToolItem copyIcon = new ToolItem (editBar, SWT.NONE);
		copyIcon.setImage(resources.getImage("small/Copy.gif"));
		copyIcon.setToolTipText(getBundle().getString("Copy")+" CTRL+C");

		MenuItem copyTexItem = new MenuItem(editMenu, SWT.PUSH);
		copyTexItem.setText(getBundle().getString("Copy Te&X"));

		final MenuItem pasteItem = new MenuItem(editMenu, SWT.PUSH);
		pasteItem.setText(getBundle().getString("&Paste")+"\tCTRL+V");
		pasteItem.setAccelerator(SWT.CTRL + 'V');
		pasteItem.setImage(resources.getImage("small/Paste.gif"));
		final ToolItem pasteIcon = new ToolItem (editBar, SWT.NONE);
		pasteIcon.setImage(resources.getImage("small/Paste.gif"));
		pasteIcon.setToolTipText(getBundle().getString("Paste")+" CTRL+V");

		class Cut implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				cut();
	        	boolean enable = canPaste();
				pasteItem.setEnabled(enable);
				pasteIcon.setEnabled(enable);
			}
		};
		Cut cut = new Cut();
		cutItem.addSelectionListener(cut);
		cutIcon.addSelectionListener(cut);

		class Copy implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				copy();
	        	boolean enable = canPaste();
				pasteItem.setEnabled(enable);
				pasteIcon.setEnabled(enable);
			}
		};
		Copy copy = new Copy();
		copyItem.addSelectionListener(copy);
		copyIcon.addSelectionListener(copy);

		class CopyTeX implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				copyTex();
	        	boolean enable = canPaste();
				pasteItem.setEnabled(enable);
				pasteIcon.setEnabled(enable);
			}
		};
		CopyTeX copyTex = new CopyTeX();
		copyTexItem.addSelectionListener(copyTex);

		class Paste implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				paste();
			}
		};
		Paste paste = new Paste();
		pasteItem.addSelectionListener(paste);
		pasteIcon.addSelectionListener(paste);

		shell.addListener(SWT.Activate, new Listener() {
	        public void handleEvent(Event event) {
	        	boolean enable = canPaste();
				pasteItem.setEnabled(enable);
				pasteIcon.setEnabled(enable);	        	
	        }
	    });

		new MenuItem(editMenu, SWT.SEPARATOR);

		MenuItem deleteItem = new MenuItem(editMenu, SWT.NONE);
		deleteItem.setText(getBundle().getString("&Delete"));
		deleteItem.setImage(resources.getImage("small/Delete.gif"));
		class Delete implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				delete();
			}
		}
		deleteItem.addSelectionListener(new Delete());

		Menu insertMenu = new Menu(menu);
		insertItem.setMenu(insertMenu);

		MenuItem newFormulaItem = new MenuItem(insertMenu, SWT.NONE);
		newFormulaItem.setText(getBundle().getString("&Formula"));
		newFormulaItem.setImage(resources.getImage("small/Formula.gif"));
		class NewFormula implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				newFormula();
			}
		}
		newFormulaItem.addSelectionListener(new NewFormula());

		MenuItem newArrayItem = new MenuItem(insertMenu, SWT.NONE);
		newArrayItem.setText(getBundle().getString("&Array ..."));
		newArrayItem.setImage(resources.getImage("small/Vector.gif"));
		class NewArray implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				newArray();
			}
		}
		newArrayItem.addSelectionListener(new NewArray());

		MenuItem newVectorItem = new MenuItem(insertMenu, SWT.NONE);
		newVectorItem.setText(getBundle().getString("&Vector ..."));
		newVectorItem.setImage(resources.getImage("small/Vector.gif"));
		class NewVector implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				newVector();
			}
		}
		newVectorItem.addSelectionListener(new NewVector());

		MenuItem newMatrixItem = new MenuItem(insertMenu, SWT.NONE);
		newMatrixItem.setText(getBundle().getString("&Matrix ..."));
		newMatrixItem.setImage(resources.getImage("small/Matrix.gif"));
		class NewMatrix implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				newMatrix();
			}
		}
		newMatrixItem.addSelectionListener(new NewMatrix());

		Menu helpMenu = new Menu(menu);
		helpItem.setMenu(helpMenu);

		MenuItem docItem = new MenuItem(helpMenu, SWT.NONE);
		docItem.setText(getBundle().getString("&Contents"));
		docItem.setImage(resources.getImage("small/Help.gif"));
		class Doc implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				help();
			}
		}
		docItem.addSelectionListener(new Doc());

		MenuItem aboutItem = new MenuItem(helpMenu, SWT.NONE);
		aboutItem.setText(getBundle().getString("&About ..."));
		aboutItem.setImage(resources.getImage("small/About.gif"));
		class About implements SelectionListener {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				about();
			}
		}
		aboutItem.addSelectionListener(new About());

		String tips[] = {
			getBundle().getString("General")+" F1",
			getBundle().getString("Operators")+" F2",
			getBundle().getString("Symbols")+" F3",
			getBundle().getString("Functions")+" F4"
		};

		String tools[] = {
			"small/General.png",
			"small/Operators.png",
			"small/Symbols.png",
			"small/Functions.png"
		};

		copyIcon.addSelectionListener(copy);

		class Tools implements SelectionListener {
			int i;
			Tools(int i) {
				this.i=i;
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				workbook.getController().setToolbarVisible(i);		
			}
		};

		for (int j=0; j<tools.length; j++) {
			if(tools[j]!=null) {
				ToolItem toolsIcon = new ToolItem (toolsBar, SWT.DROP_DOWN);
				toolsIcon.setImage(resources.getImage(tools[j]));
				toolsIcon.setToolTipText(tips[j]);
				toolsIcon.addSelectionListener(new Tools(j));

			} else {
				new ToolItem(toolsBar, SWT.SEPARATOR);
			}
		}
		
		packToolbar(coolBar, fileBar);
		packToolbar(coolBar, editBar);
		packToolbar(coolBar, toolsBar);

		return menu;
	}

	public void packToolbar(CoolBar coolBar, ToolBar toolBar) {
		CoolItem coolItem = new CoolItem(coolBar, SWT.NONE);
		coolItem.setControl(toolBar);
		Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point coolSize = coolItem.computeSize (size.x, size.y);
		coolItem.setMinimumSize(coolSize.x, coolSize.y);
		coolItem.setPreferredSize(coolSize);
		coolItem.setSize(coolSize);
	}

	public MiraiMath() {
		// initialize bundle
		getBundle();

		shell = shell();
		GridLayout layout = new GridLayout();
		layout.marginWidth=0;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing=0;
		shell.setLayout(layout);

		// create workbook and meta-model
		newWorkbook();

		// create tool-bars and their frames
		ToolbarFolder.createInstance(shell, workbook.getModel().getMetaModel(), 
				useSmallIcons() ? ToolbarFolder.SMALL : ToolbarFolder.LARGE);

		// create menu bar
		Menu menu = createMenu();
		shell.setMenuBar(menu);

		// root composite
		Composite swtParent = new Composite(shell, SWT.NO_BACKGROUND);
		swtParent.setLayoutData(new GridData(GridData.FILL_BOTH));
		swtParent.setLayout(new FillLayout());

		// scroll panel
		final ScrollPanel scrollPanel = new ScrollPanel(swtParent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrollPanel.getJScrollPane().setViewportView(workbook);
		workbook.setScrollPane(scrollPanel.getJScrollPane());

		// display
		try {
			System.setProperty("sun.awt.noerasebackground", "true");
		} catch (NoSuchMethodError error) {
		}

		shell.open();
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				e.doit = saveChanges();
			}
		}); 
		shell.addListener(SWT.Dispose, new Listener() {
	        public void handleEvent(Event event) {
	        	workbook.removeAll();
	        	workbook.detachModel();
				workbook.getAlgebraSystem().exit();
	        }
	    });

		while (!shell.isDisposed()) {
			try {
			if (!display.readAndDispatch())
				display.sleep();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		resources.dispose();
		display.dispose();
	}

	public static void main(String[] args) {
		parseArgs(args);
		new MiraiMath();
	}
	
}
