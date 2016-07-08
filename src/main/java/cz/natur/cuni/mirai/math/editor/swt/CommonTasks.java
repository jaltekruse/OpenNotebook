/* CommonTasks.java
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cz.natur.cuni.mirai.math.algebra.AlgebraSystem;
import cz.natur.cuni.mirai.math.algebra.HtmlSerializer;
import cz.natur.cuni.mirai.math.algebra.MiraiParser;
import cz.natur.cuni.mirai.math.algebra.MiraiSerializer;
import cz.natur.cuni.mirai.math.algebra.OctaveSerializer;
import cz.natur.cuni.mirai.math.algebra.PngSerializer;
import cz.natur.cuni.mirai.math.algebra.TeXSerializer;
import cz.natur.cuni.mirai.math.editor.JMathWorkbook;
import cz.natur.cuni.mirai.math.icon.SwtResourceCache;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathModel;

public class CommonTasks {

	protected Display display=null;
	protected Clipboard clipboard=null;
	protected SwtResourceCache resources=null;
	protected Shell shell=null;

	protected JMathWorkbook workbook=null;

	protected static String engine = "Octave";
	private static String command = "/usr/bin/octave";

	private static boolean smallIcons = false;

	private static ResourceBundle i18n;
	private static final String i18nPath =
		"cz.natur.cuni.mirai.math.editor.locale.Messages";

	private static String docRoot="doc";
	private static String docLocale=null;

	private String pathname=null;
	private String path=null;

	public static void parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--small-icons")) {
				smallIcons = true;
			}
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--doc")) {
				docRoot = args[i+1];
			}
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--jmathlib")) {
				engine = "JMathLib";
			}
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--octave")) {
				command = args[i+1];
				engine = "Octave";
			}
		}

	}

	public static boolean useSmallIcons() {
		return smallIcons;
	}

	private static boolean loadBundle(Locale locale) {
		try {
			i18n = ResourceBundle.getBundle(i18nPath, locale);
			System.out.println("I18n Locale: "+locale.toString()+" found ...");
			return true;

		} catch(Exception x){
			System.out.println("I18n Locale: "+locale.toString()+" not found ...");
			return false;
		} 
	}

	public static ResourceBundle getBundle() {
		if(i18n!=null) {
			return i18n;
		} else if(Locale.getDefault().getLanguage().length()==2 &&
			loadBundle(new Locale(Locale.getDefault().getLanguage()))) {
			; //bundle loaded
		} else if(loadBundle(Locale.getDefault())) {
			; //bundle loaded
		} else if(loadBundle(new Locale("en"))) {
			; //bundle loaded
		} else {
			throw new RuntimeException("Fatal: No default i18n bundle found.");
		}
		return i18n;
	}

	private static boolean findManual(String locale) {
		File index = new File(docRoot+"/"+locale.toString()+"/index.html");
		if(index.exists()) {
			docLocale = locale;
			System.out.println("Doc Locale: "+locale.toString()+" found ...");
			return true;

		} else {
			System.out.println("Doc Locale: "+locale.toString()+" not found ...");
			return false;
		}
	}

	public static String getManual() {
		if(docLocale!=null) {
			return docRoot+"/"+docLocale;
		} else if(Locale.getDefault().getLanguage().length()==2 &&
			findManual(Locale.getDefault().getLanguage().toString())) {
			; //index found
		} else if(findManual(Locale.getDefault().toString())) {
			; //index found
		} else if(findManual("en")) {
			; //index found
		} else {
			new Exception("Error: No default documentation found.").toString();
			return null;
		}
		System.out.println("DocRoot: " +docRoot);
		return docRoot+"/"+docLocale;
	}

	public Shell shell() {
		if(display==null) {
			display = new Display();
			resources = new SwtResourceCache(display);
			clipboard = new Clipboard(display);
		}
		Shell shell = new Shell(display);
		shell.setText("Mirai Math for Octave");
		Image icons[] = {
				resources.getImage("mirai16x16.png"), 
				resources.getImage("mirai32x32.png"),
				resources.getImage("mirai48x48.png"),
				resources.getImage("mirai64x64.png")};
		shell.setImages(icons);
		shell.setSize(600, 450);
		return shell;
	}
	
	public int question(String title, String question) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setMessage(question);
			messageBox.setText(title);
			return messageBox.open();
	}

	public int error(String title, String question) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage(question);
			messageBox.setText(title);
			return messageBox.open();
	}

	public void about() {
		final MessageBox messageBox = new MessageBox(shell, SWT.OK);
		String about = "Mirai Math for Octave; Version 0.5 (b12)\n"
				+ "by Bea Petrovicova, 2008-2009, GPLv2\n" + "http://mirai.sf.net/\n"
				+ "\nMath Libraries:\n" + "http://sf.net/projects/jmathtex/\n";
		messageBox.setText(i18n.getString("About"));
		messageBox.setMessage(about);
		messageBox.open();
	}

	public void clean() {
		if(!saveChanges())
			return;
		// new workbook
		MetaModel metaModel = workbook.getModel().getMetaModel();
		MathModel model = MathModel.newModel(metaModel);
		workbook.attachModel(model);
	}

	public boolean open() {
		if(!saveChanges())
			return false;

		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText(i18n.getString("Open"));
		if(path!=null) {
			fd.setFilterPath(path);
		}
		String[] filterExts = {"*.mirai"};
		String[] filterNames = {"Mirai Math (*.mirai)"};
		fd.setFilterExtensions(filterExts);
		fd.setFilterNames(filterNames);
		pathname = fd.open();
		path = fd.getFilterPath();

		if(pathname==null) {
			return false;
		}

		try {
			MetaModel metaModel = workbook.getModel().getMetaModel();
			MathModel model = new MathModel(metaModel);
			MiraiParser.load(model, pathname);
			workbook.attachModel(model);
		} catch (Exception e) {
			e.printStackTrace();
			error(i18n.getString("Error"), e.getMessage());
			return false;
		}

		return true;
	}

	public void save() {
		try {
			MiraiSerializer.save(workbook.getModel(), pathname);
		} catch (IOException e) {
			e.printStackTrace();
			error(i18n.getString("Error"), e.getMessage());
		}
	}

	public boolean saveAs() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText(i18n.getString("Save"));
		if(path!=null) {
			fd.setFilterPath(path);
		}
		String[] filterExts = {"*.mirai"};
		String[] filterNames = {"Mirai Math (*.mirai)"};
		fd.setFilterExtensions(filterExts);
		fd.setFilterNames(filterNames);
		
		pathname = fd.open();
		path = fd.getFilterPath();
		String name = fd.getFileName();
		
		if(pathname==null) {
			return false;
		}
		//int index = fd.getFilterIndex();
		//System.out.println(index);
		
		if(pathname.lastIndexOf('.') < 0 ||
		   pathname.lastIndexOf('.') <= pathname.lastIndexOf(File.separatorChar)) {

			pathname+=".mirai";
		}

		if(new File(pathname).exists()) {
			int result = question(i18n.getString("Overwrite file?"), MessageFormat.format(
				i18n.getString("A file named {0} already exists. Overwrite existing file?"), 
				new Object[] {name}));
			if(result == SWT.NO) {
				return false;
			}
		}

		try {
			MiraiSerializer.save(workbook.getModel(), pathname);
		} catch (IOException e) {
			e.printStackTrace();
			error(i18n.getString("Error"), e.getMessage());
		}

		return true;
	}

	/** @returns whether the choice have been made */
	public boolean saveChanges() {
		if(workbook.getModel().isModified()) {
			int result = question(i18n.getString("Save changes?"),
				i18n.getString("The document has been modified. Save changes?"));
			if(result == SWT.YES) {
				if(pathname!=null) {
					save();
				} else {
					return saveAs();
				}
			} else if(result == SWT.NO) {
				return true;
			}
		}
		return true;
	}

	public void export() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText(i18n.getString("Export"));
		if(path!=null) {
			fd.setFilterPath(path);
		}
		final int PNG = 0;
		final int HTML = 1;
		final int UTEX = 2;
		final int TEX = 3;
		final int TXT = 4;

		final String[] filterTails = {
				".png", 
				".html", 
				".tex", 
				".tex", 
				".txt"
		};

		final String[] filterExts = {
				"*.png", 
				"*.html", 
				"*.tex", 
				"*.tex", 
				"*.*"
		};

		final String[] filterNames = {
				i18n.getString("Formula as PNG Image")+" (*.png)", 
				i18n.getString("HTML with PNG Images")+" (*.html)",
				i18n.getString("LaTeX Document")+" UTF-8 (*.tex)",
				i18n.getString("LaTeX Document")+" (*.tex)", 
				i18n.getString("Plain Text")+" (*.*)"
		};

		fd.setFilterExtensions(filterExts);
		fd.setFilterNames(filterNames);

		String pathname = fd.open();
		path = fd.getFilterPath();
		String name = fd.getFileName();
		
		if(pathname==null) {
			return;
		}
		int i = fd.getFilterIndex();
		//System.out.println(i);
		
		if(pathname.lastIndexOf('.') < 0 ||
		   pathname.lastIndexOf('.') <= pathname.lastIndexOf(File.separatorChar)) {

			pathname+=filterTails[i];
		}
		
		if(new File(pathname).exists()) {
			int result = question(i18n.getString("Overwrite file?"), MessageFormat.format(
					i18n.getString("A file named {0} already exists. Overwrite existing file?"), 
					new Object[] {name}));
			if(result == SWT.NO) {
				return;						
			}
		}

		//System.out.println(pathname);
		try {
			if(i == PNG) {
				MathFormula formula = workbook.getController().getFormula();
				PngSerializer.save(formula, pathname);
			} else if(i==HTML) {
				String enc = System.getProperty("file.encoding");
				HtmlSerializer.save(workbook.getModel(), pathname, enc);
			} else if(i==UTEX) {
				TeXSerializer.save(workbook.getModel(), pathname, "UTF-8");
			} else if(i==TEX) {
				String enc = System.getProperty("file.encoding");
				TeXSerializer.save(workbook.getModel(), pathname, enc);
			} else if(i==TXT) {
				String enc = System.getProperty("file.encoding");
				OctaveSerializer.save(workbook.getModel(), pathname, enc);
			}
		} catch (IOException e) {
			e.printStackTrace();
			error(i18n.getString("Error"), e.getMessage());
		}
	}

	public void cut() {
		if(workbook.getController().getFormula().getRootComponent().size()==0) {
			return; // nothing to do
		}
		MathFormula clipboardContent = workbook.getController().cut();
		// put into system clip-board
		String serializedContent = 
			MiraiSerializer.copy(clipboardContent);
		//System.out.println("Cut: " +serializedContent);
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { serializedContent },
			new Transfer[] { textTransfer });
	}

	public void copy() {
		if(workbook.getController().getFormula().getRootComponent().size()==0) {
			return; // nothing to do
		}
		MathFormula clipboardContent = workbook.getController().getFormula();
		String serializedContent = 
			MiraiSerializer.copy(clipboardContent);
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { serializedContent },
			new Transfer[] { textTransfer });
	}

	public void copyTex() {
		if(workbook.getController().getFormula().getRootComponent().size()==0) {
			return; // nothing to do
		}
		MathFormula formula = workbook.getController().getFormula();
		String serializedContent = 
			TeXSerializer.copy(formula);
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { serializedContent },
			new Transfer[] { textTransfer });
	}

	public boolean canPaste() {
		// get from system clip-board
		TextTransfer transfer = TextTransfer.getInstance();
		String serializedClipboardContent = 
			(String) clipboard.getContents(transfer);
		return MiraiParser.canPaste(serializedClipboardContent);
	}

	public void paste() {
		// get from system clip-board
		TextTransfer transfer = TextTransfer.getInstance();
		String serializedClipboardContent = 
			(String) clipboard.getContents(transfer);

		if(MiraiParser.canPaste(serializedClipboardContent)) {
			MathFormula clipboardContent[] = MiraiParser.paste(
				workbook.getModel(), serializedClipboardContent);

			if(clipboardContent.length>1) {
				MathModel model = workbook.getModel();
				int i = workbook.getController().getFormula().getModelIndex();
				if(workbook.getModel().hasNextFormula(i)) {
					i = model.nextFormula(i) + 1;
				} else {
					i = model.size();
				}
				for(int j=0; j<clipboardContent.length; j++) {
					model.addElement(i+j, clipboardContent[j]);
				}

			} else if(clipboardContent.length==1) {
				workbook.getController().paste(clipboardContent[0]);

			} // else do nothing
		}
	}

	public void delete() {
		MathFormula formula = workbook.getController().getFormula();
		MathModel model = workbook.getModel();

		// has next; select next formula
		if(model.hasNextFormula(formula.getModelIndex())) {
			int i = model.nextFormula(formula.getModelIndex());
			workbook.requestFocus(i);

		// has previous; select previous formula
		} else if(model.hasPrevFormula(formula.getModelIndex())) {
				int i = model.prevFormula(formula.getModelIndex());
				workbook.requestFocus(i);

		} else return;

		// delete current formula
		int i = formula.getModelIndex();
		model.removeResults(i);
		model.removeElements(i, i);

	}

	public void newFormula() {
		MathModel model = workbook.getModel();
		MetaModel metaModel = model.getMetaModel();
		MathFormula formula = MathFormula.newFormula(metaModel);
		int i = workbook.getController().getFormula().getModelIndex();
		model.addElement(i, formula);
	}

	public void newArray() {
		ArrayDialog dialog = new ArrayDialog(shell);
		dialog.setText(i18n.getString("New Array ..."));
		MetaModel meta = workbook.getController().getFormula().getMetaModel();
		int size = meta.getDefaultArraySize();
		dialog.setSize(size);
		boolean result = dialog.open();
		size = dialog.getSize();

		if(result) {
			meta.setDefaultArraySize(size);
			workbook.getController().newArray(size);
			workbook.getController().update();
		}
	}

	public void newVector() {
		ArrayDialog dialog = new ArrayDialog(shell);
		dialog.setText(i18n.getString("New Vector ..."));
		MetaModel meta = workbook.getController().getFormula().getMetaModel();
		int size = meta.getDefaultVectorSize();
		dialog.setSize(size);
		boolean result = dialog.open();
		size = dialog.getSize();

		if(result) {
			meta.setDefaultVectorSize(size);
			workbook.getController().newMatrix(size, 1);
			workbook.getController().update();
		}
	}

	public void newMatrix() {
		MatrixDialog dialog = new MatrixDialog(shell);
		dialog.setText(i18n.getString("New Matrix ..."));
		MetaModel meta = workbook.getController().getFormula().getMetaModel();
		int columns = meta.getDefaultMatrixColumns();
		int rows = meta.getDefaultMatrixRows();
		dialog.setColumns(columns); dialog.setRows(rows);
		boolean result = dialog.open();
		columns = dialog.getColumns(); rows = dialog.getRows();

		if(result) {
			meta.setDefaultMatrixColumns(columns);
			meta.setDefaultMatrixRows(rows);
			workbook.getController().newMatrix(columns, rows);
			workbook.getController().update();
		}
	}

	public void help() {		
		Shell shell = shell();
		shell.setLayout(new FillLayout());
		try {
			Browser browser = new Browser(shell, SWT.NONE);
			File index = new File(getManual()+"/index.html");
			URI uri = index.toURI();
			browser.setUrl(uri.toString());
			shell.open();
		} catch (SWTError x) {
			x.printStackTrace();
		}
	}

	public JMathWorkbook newWorkbook() {
		workbook = new JMathWorkbook();
		MetaModel metaModel = new MetaModel(engine+".xml");
		MathModel model = MathModel.newModel(metaModel);
		try {
			AlgebraSystem as = (AlgebraSystem) Class.forName(
					"cz.natur.cuni.mirai.math.algebra."+engine+"Plugin").newInstance();
			workbook.setAlgebraSystem(as);
			as.run(command);
			model.addElement(0, i18n.getString(
					"Welcome! Read tutorial at http://mirai.sf.net"));

		} catch (Exception e) {
			e.printStackTrace();
			model.addElement(0, i18n.getString(
					"Please, read install instructions http://mirai.sf.net"));
			model.addElement(0, MessageFormat.format(i18n.getString(
					"Error! {0} not found. Installation incomplete."), new Object[] {engine}));
		}
		model.setModified(false);
		workbook.attachModel(model);
		return workbook;
	}
}
