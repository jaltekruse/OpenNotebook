/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import doc.Document;
import doc.Page;
import doc.ProblemDatabase;
import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.GeneratedProblem;
import doc.mathobjects.Grouping;
import doc.mathobjects.MathObject;
import doc.mathobjects.ProblemGenerator;
import doc.mathobjects.TextObject;
import expression.Node;
import expression.NodeException;

/**
 * Used to display the main panel of the interface. Major components include a
 * toolbar at the top, a set of tabs for open documents, a document view window
 * and possibly a side panel for MathObjectGUI utilities.
 * 
 * @author jason
 * 
 */
public class NotebookPanel extends SubPanel {

	private JFileChooser fileChooser;
	private OpenNotebook openNotebook;
	private Vector<DocViewerPanel> openDocs;
	private JTabbedPane docTabs;
	private Vector<DocTabClosePanel> tabLabels;
	private NotebookPanel thisNotebookPanel;

	// flag to track if the last action that was performed was closing a
	// tab, if the user closes the tab just before the "+" (add new doc)
	// tab the "+" tab gains focus, without this flag, that would prompt
	// a new document to be opened immediately after closing one
	private boolean justClosedTab;
	private MathObject clipBoardContents;
	JDialog sampleDialog, problemDialog, keyboardDialog;
	public static final String UNTITLED_DOC = "Untitled Doc",
			VIEW_PROBLEM_FORUMLA_MESSAGE = "This document was created for viewing a problem "
					+ "formula from one of your other documents. "
					+ "You can modify the formula here and then copy it back onto your other document to generate "
					+ "new problems. If you wish to move objects around, or add something new to the formula, "
					+ "you can create a text object to temporarily store its script data, as it is destroyed "
					+ "when you use the \"Remove problem\" function to ungroup the objects in your problem. "
					+ "Keep in mind that if you just update the problem on this document the changes will not "
					+ "be applied to your generated problems. You must delete the old ones from your other "
					+ "document(s) and copy your modified formula over to generate new ones.";

	public NotebookPanel(OpenNotebook openbook) {
		// create individual GUI elements here
		super(null);
		openNotebook = openbook;
		thisNotebookPanel = this;

		justClosedTab = false;

		setFileChooser(new JFileChooser());
		createSampleDialog();
		createProbelmDialog();
		createKeyboardDialog();

		this.setLayout(new BorderLayout());

		KeyboardShortcuts.addKeyboardShortcuts(this);


		docTabs = new JTabbedPane();
		docTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setRightComponent(docTabs);
		splitPane.setLeftComponent(null);
		
		add(splitPane, BorderLayout.CENTER);

		JPanel topToolBars = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		topToolBars.setLayout(layout);

		JToolBar fileActions = new FileActionsToolBar(this);
		topToolBars.add(fileActions);

		add(topToolBars, BorderLayout.NORTH);
		this.addKeyListener(new NotebookKeyboardListener(this));

		if (!openNotebook.isInStudentMode()) {
			JToolBar objectActions = new ObjectActionsToolBar(this);
			topToolBars.add(objectActions);

			JToolBar objectToolbar = new ObjectToolBar(this);
			add(objectToolbar, BorderLayout.SOUTH);
		}

		tabLabels = new Vector<DocTabClosePanel>();
		openDocs = new Vector<DocViewerPanel>();
		Document newDoc = new Document(UNTITLED_DOC);
		newDoc.addBlankPage();
		openDocs.add(new DocViewerPanel(newDoc, getTopLevelContainer(),
				openNotebook.isInStudentMode(), openNotebook));

		int tabIndex = 0;
		for (DocViewerPanel d : openDocs) {
			docTabs.addTab(d.getDoc().getName(), d);
			DocTabClosePanel temp = new DocTabClosePanel(this, d);
			tabLabels.add(temp);
			docTabs.setTabComponentAt(tabIndex, temp);
			tabIndex++;
		}

		docTabs.add(new JPanel(), "+");

		docTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (openDocs.size() == 0) {
					return;
				}
				int selected = docTabs.getSelectedIndex();
				String nameSelected = docTabs.getTitleAt(selected);
				if (nameSelected.equals("+")) {// add a new untitled document
					if (justClosedTab == false) {
						Document tempDoc = new Document(UNTITLED_DOC);
						tempDoc.addBlankPage();
						addDoc(tempDoc);
					} else {// the last tab in the list was closed, set selected
						// tab to new last tab
						docTabs.setSelectedIndex(docTabs.getTabCount() - 2);
					}
				}
			}
		});

	}


	public void setDocAlignment(int alignment) {
		openNotebook.setDocAlignment(alignment);
		getCurrentDocViewer().repaint();
	}

	public void setPreferencesDirectory() {
		openNotebook.setPreferencesDirectory();
	}

	public void cut() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			setClipBoardContents(mObj.clone());
			mObj.getParentContainer().removeObject(mObj);
			getCurrentDocViewer().setFocusedObject(null);
			if (mObj == getCurrentDocViewer().getTempGroup()) {
				getCurrentDocViewer().removeTempGroup();
			}
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		}
	}

	public void copy() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			setClipBoardContents(mObj.clone());
		}
	}

	public void paste() {
		MathObject focusedObj = getCurrentDocViewer().getFocusedObject();
		if (getClipBoardContents() == null) {
			JOptionPane.showMessageDialog(null, "Clipboard is empty.",
					"Empty Clipboard", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (focusedObj != null) {
			MathObject newObj = getClipBoardContents().clone();
			newObj.setxPos(focusedObj.getxPos());
			// offset the new object a little so the user knows that it was added
			newObj.setyPos(focusedObj.getyPos() + 10);
			newObj.setParentContainer(focusedObj.getParentContainer());
			focusedObj.getParentContainer().addObject(newObj);
			getCurrentDocViewer().setFocusedObject(newObj);
		} else if (getCurrentDocViewer().getSelectedPage() != null) {
			MathObject newObj = getClipBoardContents().clone();
			newObj.setParentContainer(getCurrentDocViewer().getSelectedPage());
			getCurrentDocViewer().getSelectedPage().addObject(newObj);
			getCurrentDocViewer().setFocusedObject(newObj);
		} else {
			JOptionPane
			.showMessageDialog(
					null,
					"Please select a page, or an object on the desired page first.",
					"Select Location for Paste",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		getCurrentDocViewer().addUndoState();
		getCurrentDocViewer().repaint();
	}

	public void quit(){
		boolean hasUnsavedChanges = false;
		Vector<Integer> unsavedIndeces = new Vector<Integer>();
		int index = 0;
		for ( DocViewerPanel dvp : this.openDocs){
			if ( dvp.hasBeenModfiedSinceSave()){
				hasUnsavedChanges = true;
				// add this index to a list of unsaved ones
				// add one to each to have the numbers start at
				// 1 instead of 0
				unsavedIndeces.add(index + 1);
			}
			index++;
		}
		if ( ! hasUnsavedChanges){
			// this will close the entire application, since there are no
			// unsaved changes
			openNotebook.quit();
			return;
		}

		String tabs = null;
		String message;
		if ( unsavedIndeces.size() == 1){
			tabs = "Tab: ";
			message = "You have unsaved changes in the tab listed below.\n";
		}
		else{
			tabs = "Tabs: ";
			message = "You have unsaved changes in the tabs listed below.\n";
		}
		for ( int i = 0; i < unsavedIndeces.size() - 1; i++){
			tabs += unsavedIndeces.get(i) + ", ";
		}
		tabs += unsavedIndeces.get(unsavedIndeces.size() - 1);
		Object[] options = {"Quit", "Cancel"};
		int n = JOptionPane.showOptionDialog(openNotebook,
				message + "Are you sure you want to quit?\n" + tabs,
				"Important Data May Be Lost",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null, options, options[1]);

		if (n == 0){ 
			openNotebook.quit();
		}
		else{} // user clicked cancel, don't do anything
	}

	public void delete() {
		if (getCurrentDocViewer().getFocusedObject() != null) {
			MathObject mObj = getCurrentDocViewer().getFocusedObject();
			mObj.getParentContainer().removeObject(mObj);
			if (mObj == getCurrentDocViewer().getTempGroup()) {
				getCurrentDocViewer().resetTempGroup();
			}
			mObj.setParentContainer(null);
			mObj.setJustDeleted(true);
			getCurrentDocViewer().setFocusedObject(null);
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		} else if (getCurrentDocViewer().getSelectedPage() != null) {
			deletePage();
		} else {
			JOptionPane.showMessageDialog(this,
					"Please select a page or object first.", "Error",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	public void bringToFront() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			mObj.getParentPage().bringObjectToFront(mObj);
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		}
	}

	public void bringToBack() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			mObj.getParentPage().bringObjectToBack(mObj);
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		}
	}

	public void sendForward() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			mObj.getParentPage().sendObjectForward(mObj);
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		}
	}

	public void sendBackward() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			mObj.getParentPage().sendObjectBackward(mObj);
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		}
	}

	public void closeCurrentViewer(){
		this.closeDocViewer(getCurrentDocViewer());
	}

	public void switchTab(){
		if ( docTabs.getSelectedIndex() < docTabs.getTabCount() - 2)
		{// needs to be two before tab count because the last tab is the add new tab
			// with the "+" on it, when it gains focus a new document is added
			docTabs.setSelectedIndex(docTabs.getSelectedIndex() + 1);
		}
		else{
			docTabs.setSelectedIndex(0);
		}
	}

	public void switchTabBackward(){
		if ( docTabs.getSelectedIndex() > 0)
		{
			docTabs.setSelectedIndex(docTabs.getSelectedIndex() - 1);
		}
		else
		{// needs to be two before tab count because the last tab is the add new tab
			// with the "+" on it, when it gains focus a new document is added
			docTabs.setSelectedIndex(docTabs.getTabCount() - 2);
		}
	}

	public void generateWorksheet() {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Vector<Page> docPages = getCurrentDocViewer().getDoc().getPages();
				Vector<MathObject> pageObjects;
				Page p;
				MathObject mObj;
				int oldSize;
				boolean problemsGenerated = false;

				for (int i = 0; i < docPages.size(); i++) {
					pageObjects = docPages.get(i).getObjects();
					oldSize = pageObjects.size();
					for (int j = 0; j < oldSize; j++) {
						mObj = pageObjects.get(j);
						if (mObj instanceof GeneratedProblem) {
							((GeneratedProblem) mObj).generateNewProblem();
							problemsGenerated = true;
							j--;
							oldSize--;
						}
					}
				}
				getCurrentDocViewer().addUndoState();
				getCurrentDocViewer().repaint();
				if (!problemsGenerated) {
					JOptionPane
					.showMessageDialog(
							null,
							"No generated problems were found to replace. To see some" +
									"\ngenerated problems try opening a sample document.",
									"No Problems Generated",
									JOptionPane.WARNING_MESSAGE);
				}
			}
		});

	}

	public void group() {
		if (getCurrentDocViewer().getFocusedObject() != null) {
			MathObject mObj = getCurrentDocViewer().getFocusedObject();
			if (mObj == getCurrentDocViewer().getTempGroup()) {
				getCurrentDocViewer().getTempGroup().getParentContainer().removeObject(mObj);
				MathObject newGroup = getCurrentDocViewer().getTempGroup().clone();
				mObj.getParentContainer().addObject(newGroup);
				getCurrentDocViewer().resetTempGroup();
				// make sure this comes after the call to resetTempGroup, when a
				// new focused object is set
				// it places the objects in the temp group back on the page,
				// which is not what is needed here
				getCurrentDocViewer().setFocusedObject(newGroup);
				getCurrentDocViewer().addUndoState();
				getCurrentDocViewer().repaint();
			}
		}
	}

	public void viewProblemGenrator(ProblemGenerator probGen) {
		Document newDoc = new Document("Problem Generator");
		newDoc.addBlankPage();
		Page page = newDoc.getPage(0);
		TextObject textObj = new TextObject(page, 5 + page.getyMargin(),
				5 + newDoc.getPage(0).getxMargin(), page.getWidth() - 2
				* page.getxMargin(), 150, 12,
				VIEW_PROBLEM_FORUMLA_MESSAGE);
		try {
			textObj.setAttributeValue(TextObject.SHOW_BOX, false);
		} catch (AttributeException e) {
			// should not happen
		}
		newDoc.getPage(0).addObject(textObj);
		MathObject newProb = ((MathObject) probGen).clone();
		newProb.setParentContainer(newDoc.getPage(0));
		newProb.setyPos( page.getxMargin() + 165);
		newProb.setxPos( (newDoc.getPage(0).getWidth() - 2 * page.getxMargin()
				- newProb.getWidth() ) / 2 + page.getxMargin());
		newDoc.getPage(0).addObject(newProb);
		this.addDoc(newDoc);
	}

	public Node getExpressionFromUser(String message){
		return getExpressionFromUser(message, "");
	}

	public Node getExpressionFromUser(String message, String defaultInput){
		String lastEx = defaultInput;
		Node newNode = null;
		while( true ){
			lastEx = (String)JOptionPane.showInputDialog(openNotebook, message,
					null, JOptionPane.PLAIN_MESSAGE, null, null, lastEx);
			if ( lastEx == null){
				return null;
			}
			try{
				newNode = Node.parseNode(lastEx);
				return newNode;
			} catch (NodeException e) {
				JOptionPane.showMessageDialog(null, "Error with expression.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}

		}
	}

	public void addProbelmToDatabase(ProblemGenerator problem){
		JTextField problemName = new JTextField();
		JTextField author = new JTextField(openNotebook.getUserName());
		JTextField date = new JTextField(new Date().toString());
		JTextArea directions = new JTextArea(3,10);
		directions.setWrapStyleWord(true);
		directions.setLineWrap(true);
		JTextArea tags = new JTextArea(3,10);
		tags.setWrapStyleWord(true);
		tags.setLineWrap(true);
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Problem Name"),
				problemName,
				new JLabel("Author"),
				author,
				new JLabel("Date (mm/dd/yyyy)"),
				date,
				new JLabel("Directions"),
				new JScrollPane(directions),
				new JLabel("Tags (Seperate with Commas)"),
				new JScrollPane(tags),
		};
		while (true){
			int input = JOptionPane.showConfirmDialog(openNotebook, 
					inputs, "Enter Problem Details", JOptionPane.PLAIN_MESSAGE);
			if ( input == -1){
				return;
			}
			if ( problemName.getText().equals("")){
				JOptionPane.showMessageDialog(null, "Name cannot be blank.",
						"Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}
			problem.setName(problemName.getText());
			problem.setAuthor(author.getText());
			try {
				problem.setAttributeValueWithString(ProblemGenerator.DATE, date.getText());
			} catch (AttributeException e) {
				JOptionPane.showMessageDialog(null, "Improper date format.",
						"Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}
			problem.setDirections(directions.getText());
			problem.getListWithName(ProblemGenerator.TAGS).removeAll();
			for ( String s : tags.getText().split("[,;]+")){
				try {
					problem.getListWithName(ProblemGenerator.TAGS).addValueWithString(s);
				} catch (AttributeException e) {
					// should not be a problem, as it is just storing strings in the list
				}
			}
			break;
		}

		openNotebook.getDatabase().addProblem(problem);
	}

	public void ungroup() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			if (mObj instanceof Grouping) {
				if (mObj == getCurrentDocViewer().getTempGroup()) {
					getCurrentDocViewer().ungroupTempGroup();
				} else {
					((Grouping) mObj).unGroup();
					mObj.getParentContainer().removeObject(mObj);
				}
				getCurrentDocViewer().setFocusedObject(null);
				getCurrentDocViewer().addUndoState();
				getCurrentDocViewer().repaint();
			}

		}
	}

	public void save() {
		BufferedWriter f = null;
		try {
			getFileChooser().setSelectedFile(
					new File(getCurrentDocViewer().getDoc().getName()));
			int value = getFileChooser().showSaveDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				java.io.File file = getFileChooser().getSelectedFile();

				// change the file name if it does not have the proper extension
				Pattern pattern = Pattern.compile(".+(\\.mdoc)$");
				Matcher matcher;
				String filename = file.getName();
				matcher = pattern.matcher(file.getName());
				if (! matcher.matches()){
					file = new File(file.getAbsolutePath() + ".mdoc");
				}

				f = new BufferedWriter(new FileWriter(file));
				f.write(getCurrentDocViewer().getDoc().exportToXML());
				
				getCurrentDocViewer().getDoc().setFilename(file.getName());
				refreshDocNameTabs();

				// tell the current viewer that the document was just saved
				// to avoid unneeded prompts to save when closing
				getCurrentDocViewer().setCurrentStateAsLastSaved();
				f.flush();
				f.close();
			}
		} catch (Exception e) {
			try {
				f.flush();
				f.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Error saving file", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static File createTempDirectory() throws IOException{
		final File temp;

		temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if(!(temp.delete()))
		{
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if(!(temp.mkdir()))
		{
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}


	/**
	 * Method to save the contents of a directory into a zip file.
	 * 
	 * Code found online at:
	 * http://www.java2s.com/Code/Java/File-Input-Output/CompressfilesusingtheJavaZIPAPI.htm
	 * 
	 */
	public static void zipDirectory(File dir, File zipfile)
			throws IOException, IllegalArgumentException {
		// Check that the directory is a directory, and get its contents
		String[] entries = dir.list();
		byte[] buffer = new byte[4096]; // Create a buffer for copying
		int bytesRead;
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

		for (int i = 0; i < entries.length; i++) {
			File f = new File(dir, entries[i]);
			if (f.isDirectory())
				continue;//Ignore directory
			FileInputStream in = new FileInputStream(f); // Stream to read file
			ZipEntry entry = new ZipEntry(f.getPath()); // Make a ZipEntry
			out.putNextEntry(entry); // Store entry
			while ((bytesRead = in.read(buffer)) != -1)
				out.write(buffer, 0, bytesRead);
			in.close(); 
		}
		out.close();
	}

	public void open() {
		try {
			int value = getFileChooser().showOpenDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				FileReader fileReader = new FileReader(getFileChooser()
						.getSelectedFile());
				addDoc(openNotebook.getFileReader().readDoc(fileReader, getFileChooser()
						.getSelectedFile().getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error opening file, please send it to the lead developer at\n"
							+ "dev@open-math.com to help with debugging",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void createSampleDialog() {
		sampleDialog = new JDialog(openNotebook);
		sampleDialog.add(new SampleListPanel(this));
		sampleDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		sampleDialog.pack();
	}
	

	private void createKeyboardDialog() {
		keyboardDialog = new JDialog(openNotebook);
		keyboardDialog.add(new OnScreenMathKeypad(this));
		keyboardDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		sampleDialog.setBounds(this.getX() + 350, this.getY() + 200, 380, 350);
		keyboardDialog.pack();
	}
	
	public void setKeyboardDialogVisible(boolean b) {
		getCurrentDocViewer().setOnScreenKeyBoardVisible(true);
	}

	public void createProbelmDialog() {
		if (problemDialog != null){
			problemDialog.dispose();
		}
		problemDialog = new JDialog(openNotebook);
		ProblemListPanel listPanel = new ProblemListPanel(this);
		problemDialog.add(listPanel);
		problemDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		problemDialog.pack();
		problemDialog.setMinimumSize(new Dimension(300, 450));
		listPanel.adjustProblemList();
		problemDialog.validate();
	}

	public void setSampleDialogVisible(boolean b) {
		sampleDialog.setVisible(b);
		if (b) {
			sampleDialog.setBounds(this.getX() + 350, this.getY() + 200, 300,
					300);
		}
	}

	public void setProblemDialogVisible(boolean b) {
		problemDialog.setBounds( (int) this.getLocationOnScreen().getX(),
				(int) this.getLocationOnScreen().getY(), 450,
				600);
		problemDialog.setVisible(b);
	}

	public void disposeOfSampledialog() {
		sampleDialog.dispose();
	}

	public void disposeOfProblemdialog() {
		problemDialog.dispose();
	}

	public void open(String docName) {

		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("samples/" + docName);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		try {
			Document tempDoc = openNotebook.getFileReader().readDoc(inputStreamReader, docName);
			tempDoc.setFilename(docName);
			addDoc(tempDoc);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error opening file, please send it to the lead developer at\n"
							+ "dev@open-math.com to help with debugging",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void addPage() {
		Document doc = getCurrentDocViewer().getDoc();
		Page p = getCurrentDocViewer().getSelectedPage();

		if (doc.getNumPages() > 0 && p != null) {
			int index = 0;
			index = doc.getPageIndex(p);
			doc.getPages().add(index, new Page(doc));
		} else {
			getCurrentDocViewer().getDoc().addBlankPage();
		}
		getCurrentDocViewer().addUndoState();
		getCurrentDocViewer().resizeViewWindow();
	}

	public void deletePage() {
		if (getCurrentDocViewer().getSelectedPage() == null) {
			JOptionPane.showMessageDialog(this, "Please select a page first.",
					"Error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		getCurrentDocViewer().getDoc().removePage(
				getCurrentDocViewer().getSelectedPage());
		getCurrentDocViewer().setSelectedPage(null);
		getCurrentDocViewer().resizeViewWindow();
	}

	public void showDocProperties() {
		getCurrentDocViewer().toggleDocPropsFrame();
	}

	public void print() {
		DocPrinter docPrinter = new DocPrinter();

		docPrinter.setDoc(getCurrentDocViewer().getDoc());
		PrinterJob job = PrinterJob.getPrinterJob();

		Paper paper = new Paper();
		Document doc = getCurrentDocViewer().getDoc();
		paper.setImageableArea(doc.getxMargin(), doc.getyMargin(),
				doc.getWidth() - 2 * doc.getxMargin(),
				doc.getHeight() - 2 * doc.getyMargin());
		paper.setSize(doc.getWidth(), doc.getHeight());

		PageFormat pf = job.defaultPage();

		pf.setPaper(paper);

		job.setPrintable(docPrinter, pf);
		job.setJobName(getCurrentDocViewer().getDoc().getName());
		boolean ok = job.printDialog();
		if (ok) {
			try {
				job.print();
			} catch (PrinterException ex) {
				/* The job did not successfully complete */
			}
		}
	}

	public void zoomIn() {
		getCurrentDocViewer().zoomIn();
	}

	public void defaultZoom() {
		getCurrentDocViewer().defaultZoom();
	}

	public void zoomOut() {
		getCurrentDocViewer().zoomOut();
	}

	public void undo() {
		getCurrentDocViewer().undo();
	}

	public void redo() {
		getCurrentDocViewer().redo();
	}

	public void refreshDocNameTabs() {
		for (DocTabClosePanel p : tabLabels) {
			p.updateField();
		}
	}

	public boolean isInStudentMode() {
		return openNotebook.isInStudentMode();
	}

	public ProblemDatabase getDatabase(){
		return openNotebook.getDatabase();
	}

	public void addDoc(Document doc) {
		int numTabs = docTabs.getTabCount();
		openDocs.add(new DocViewerPanel(doc, getTopLevelContainer(),
				openNotebook.isInStudentMode(), openNotebook));

		// remove the old adding new doc tab
		docTabs.remove(numTabs - 1);

		// add new doc
		docTabs.add(openDocs.get(openDocs.size() - 1),
				openDocs.get(openDocs.size() - 1).getDoc().getName());
		DocTabClosePanel temp = new DocTabClosePanel(thisNotebookPanel,
				openDocs.get(openDocs.size() - 1));
		tabLabels.add(temp);
		docTabs.setTabComponentAt(openDocs.size() - 1, temp);

		// add back creation tab
		docTabs.add(new JPanel(), "+");
		docTabs.setSelectedIndex(docTabs.getTabCount() - 2);
	}

	public DocViewerPanel getCurrentDocViewer() {
		return openDocs.get(docTabs.getSelectedIndex());
	}

	public ImageIcon getIcon(String filename) {
		try {
			BufferedImage image = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream(filename));
			return new ImageIcon(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("cannot find image " + filename);
		}
		return null;
	}

	/**
	 * Used to remove an editor from the tabs, should be modified to ask if user
	 * wants to save first.
	 */
	public void closeDocViewer(DocViewerPanel docPanel) {
		if (openDocs.size() == 1) {
			return;
		}

		if ( docPanel.hasBeenModfiedSinceSave() ){
			// open popup to see ask if user wants to save recent changes
			Object[] options = { "Close", "Cancel" };
			int n = JOptionPane.showOptionDialog(this,
					"Changes have been made to this document since you last saved.\n"
							+ "Would you like to continue closing this tab?",
							"Important Data May Be Lost", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[1]);

			if (n == 1) {// the user clicked cancel, do not close the tab
				return;
			}
		}

		openDocs.remove(docPanel);
		docPanel.destroyAllUndoStates();
		destroyDoc(docPanel.getDoc());
		justClosedTab = true;
		docTabs.remove(docPanel);
		justClosedTab = false;
	}

	public void destroyDoc(Document doc){
		for (Page p : doc.getPages()){
			p.setParentDoc(null);
			for (MathObject mObj : p.getObjects()){
				mObj.setParentContainer(null);
			}
			p.removeAllObjects();
		}
		doc.removeAllPages();
		doc.setDocViewerPanel(null);
	}

	public void focusDocViewer(DocViewerPanel docPanel) {
		docTabs.setSelectedComponent(docPanel);
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setClipBoardContents(MathObject clipBoardContents) {
		this.clipBoardContents = clipBoardContents;
	}

	public MathObject getClipBoardContents() {
		return clipBoardContents;
	}
}
