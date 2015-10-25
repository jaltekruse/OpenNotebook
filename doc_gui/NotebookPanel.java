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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.awt.datatransfer.DataFlavor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import doc.PointInDocument;
import org.xml.sax.SAXException;

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
import doc_gui.attribute_panels.ObjectPropertiesFrame;
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
	private DocViewerPanel workSpace;
	private JTabbedPane docTabs;
	private Vector<DocTabClosePanel> tabLabels;

    private static PageGUI pageGUI = new PageGUI();

	private MathObject objToPlace;

	public enum ObjectCreationMode {
		NOT_PLACING_OBJECT,
		PLACING_SINGLE_OBJECT,
		MULTIPLE_OBJECTS
	}
	private ObjectCreationMode objectCreationMode = ObjectCreationMode.NOT_PLACING_OBJECT;

	//might want to make workspace a list of frames, to allow multiple open at once
	// would also have to add another list of doc viewers
	private JFrame textFrame, workspaceFrame;
	private DocViewerPanel workspaceDoc;
	private ObjectToolBar objectToolbar;
	private ProblemListPanel problemListPanel;

	// flag to track if the last action that was performed was closing a
	// tab, if the user closes the tab just before the "+" (add new doc)
	// tab the "+" tab gains focus, without this flag, that would prompt
	// a new document to be opened immediately after closing one
	private boolean justClosedTab;
	private MathObject clipBoardContents;
	JDialog sampleDialog, problemDialog;
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
		// TODO - stopwatch and logging
//		System.out.println("start making notebookPanel:" + (new java.util.Date().getTime() - openbook.timeAtStart));
		setOpenNotebook(openbook);

		justClosedTab = false;

		setFileChooser(new JFileChooser());
		createSampleDialog();
//		System.out.println("after sample dialog:" + (new java.util.Date().getTime() - openbook.timeAtStart));
		createProbelmDialog();
//		System.out.println("after problem dialog:" + (new java.util.Date().getTime() - openbook.timeAtStart));
		this.setLayout(new BorderLayout());

		KeyboardShortcuts.addKeyboardShortcuts(this, this);

//		System.out.println("checkpoint 1:" + (new java.util.Date().getTime() - openbook.timeAtStart));
		docTabs = new JTabbedPane();
		docTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		JSplitPane splitPane = new JSplitPane();
		problemListPanel = new ProblemListPanel(this);
		
		//splitPane.setRightComponent(docTabs);
		//splitPane.setLeftComponent(problemListPanel);
		//splitPane.setDividerLocation(450);

		add(docTabs, BorderLayout.CENTER);
		//add(docTabs, BorderLayout.CENTER);
//		System.out.println("after docTabs:" + (new java.util.Date().getTime() - openbook.timeAtStart));

		JPanel topToolBars = new JPanel();
		ModifiedFlowLayout layout = new ModifiedFlowLayout(FlowLayout.LEFT);
		topToolBars.setLayout(layout);

		JToolBar fileActions = new FileActionsToolBar(this);
		topToolBars.add(fileActions);

		add(topToolBars, BorderLayout.NORTH);
//		System.out.println("added fileactions toolbar:" + (new java.util.Date().getTime() - openbook.timeAtStart));

		if (!getOpenNotebook().isInStudentMode()) {
			JToolBar objectActions = new ObjectActionsToolBar(this);
			topToolBars.add(objectActions);

			objectToolbar = new ObjectToolBar(this);
			add(objectToolbar, BorderLayout.SOUTH);
		}

		tabLabels = new Vector<DocTabClosePanel>();
		openDocs = new Vector<DocViewerPanel>();
		Document newDoc = new Document(UNTITLED_DOC);
		newDoc.addBlankPage();
		openDocs.add(new DocViewerPanel(newDoc, getTopLevelContainer(), this));

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
				//check to see if the user is adding a tab
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
				else{//force the properties frame to move to the new tab if it is currently somewhere else
					openDocs.get(selected).propertiesFrameRefacoringNeeded = true;
					openDocs.get(selected).repaintDoc();
				}

			}
		});
//		System.out.println("finish making notebookPanel:" + (new java.util.Date().getTime() - openbook.timeAtStart));
	}


	public void setDocAlignment(int alignment) {
		getOpenNotebook().setDocAlignment(alignment);
		getCurrentDocViewer().repaint();
	}

	public void setPreferencesDirectory() {
		getOpenNotebook().setPreferencesDirectory();
	}

	public void cut() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			// application specific clipboard
			setClipBoardContents(mObj.clone());

			addToSystemClipboard(mObj);

			mObj.getParentContainer().removeObject(mObj);
			getCurrentDocViewer().setFocusedObject(null);
			if (mObj == getCurrentDocViewer().getTempGroup()) {
				getCurrentDocViewer().removeTempGroup();
			}
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();
		}
	}
	public static Image drawObjectToImage(MathObject mObj){
		Image image = new BufferedImage(mObj.getWidth() + 20, mObj.getHeight() + 20, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		pageGUI.drawObject(mObj, g,
				mObj.getParentPage(), new Point(-1 * mObj.getxPos() + 10, -1 * mObj.getyPos() + 10),
				new Rectangle(),  1);
		g.dispose();
		return image;
	}

	public void addToSystemClipboard(MathObject mObj){
		// system clipboard

		ImageSelection imgSel = new ImageSelection(drawObjectToImage(mObj));
		if (imgSel.isDataFlavorSupported(DataFlavor.imageFlavor)){
			//System.out.println("data type correct");
			try{
//				new SecurityManager().checkSystemClipboardAccess();
//				JOptionPane
//				.showMessageDialog(
//						null,
//						"Clipboard access NOT denied.",
//						"Clipboard Error",
//						JOptionPane.WARNING_MESSAGE);
			}catch( Exception ex){
//				JOptionPane
//				.showMessageDialog(
//						null,
//						"Clipboard access denied.",
//						"Clipboard Error",
//						JOptionPane.WARNING_MESSAGE);
			}
		}
		else{
			JOptionPane
			.showMessageDialog(
					null,
					"Your system does not support copying to the system clipboard.\n" +
					"You can still copy and paste within the application, just not\n" +
					"to other programs.",
					"Clipboard Error",
					JOptionPane.WARNING_MESSAGE);
		}
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
	}

	public void copy() {
		MathObject mObj = getCurrentDocViewer().getFocusedObject();
		if (mObj != null) {
			// application internal clipboard
			setClipBoardContents(mObj.clone());

			addToSystemClipboard(mObj);
		}
	}

	// This class is used to hold an image while on the clipboard.
	private static class ImageSelection implements Transferable {
		private Image image;

		public ImageSelection(Image image) {
			this.image = image;
		}

		// Returns supported flavors
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{DataFlavor.imageFlavor};
		}

		// Returns true if flavor is supported
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		// Returns image
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!DataFlavor.imageFlavor.equals(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return image;
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
			getOpenNotebook().quit();
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
		int n = JOptionPane.showOptionDialog(null,
				message + "Are you sure you want to quit?\n" + tabs,
				"Important Data May Be Lost",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null, options, options[1]);

		if (n == 0){ 
			getOpenNotebook().quit();
		}
		else{} // user clicked cancel, don't do anything
	}
	
	public void login(){
		JTextField email = new JTextField();
		JTextField password = new JPasswordField();

		final JComponent[] inputs = new JComponent[] {
				new JLabel("E-mail"),
				email,
				new JLabel("Password"),
				password,
				new OnScreenMathKeypad(this),
		};
		while (true){
			int input = JOptionPane.showConfirmDialog(null, 
				inputs, "Sign In", JOptionPane.PLAIN_MESSAGE);
			if ( input == -1){
				return;
			}
			if ( password.getText().equals("") || email.getText().equals("")){
				JOptionPane.showMessageDialog(null, "Both fields must be filled out.",
						"Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}
			else{
				break;
			}
		}
		URL url;
		HttpURLConnection   urlConn;
		DataOutputStream    printout = null;
		DataInputStream     dataInput = null;
		try {
			url = new URL ("http://open-math.com/index.php?/auth/login");
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);
			urlConn.setRequestMethod("POST");
			urlConn.setInstanceFollowRedirects(false);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			printout = new DataOutputStream (urlConn.getOutputStream ());
			String content =
					"login=" + URLEncoder.encode(email.getText(), "utf-8") +
					"&password=" + URLEncoder.encode(password.getText(), "utf-8");
			//urlConn.setRequestProperty("Content-Length", ""+ content.length());
			printout.writeBytes (content);
			printout.flush ();
			printout.close ();
			if ( urlConn.getResponseCode() == 302 || urlConn.getResponseCode() == 303){
				// redirect sent back, need to send out a new request
				String cookie = urlConn.getHeaderField("Set-Cookie");
				getOpenNotebook().setCookie(cookie);
				JOptionPane.showMessageDialog(null, "Connection to Open-Math was successful.",
						"Connection Success", JOptionPane.WARNING_MESSAGE);
			}
			// Get response data.
			dataInput = new DataInputStream (urlConn.getInputStream());
			String str;
			while (null != ((str = dataInput.readLine())))
			{
				System.out.println (str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
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
			JOptionPane.showMessageDialog(null,
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
				boolean problemsGenerated = getCurrentDocViewer().getDoc().generateNewVersion();
				if (!problemsGenerated) {
					JOptionPane
					.showMessageDialog(
							null,
							"No generated problems were found to replace. To see some" +
									"\ngenerated problems try opening a sample document.",
									"No Problems Generated",
									JOptionPane.WARNING_MESSAGE);
				}
				else{
					getCurrentDocViewer().addUndoState();
					getCurrentDocViewer().repaint();
				}
			}
		});

	}

	public void group() {
		MathObject focusedObj = getCurrentDocViewer().getFocusedObject();
		if (focusedObj != null && focusedObj == getCurrentDocViewer().getTempGroup()) {
			getCurrentDocViewer().getTempGroup().getParentContainer().removeObject(focusedObj);
			MathObject newGroup = getCurrentDocViewer().getTempGroup().clone();
			focusedObj.getParentContainer().addObject(newGroup);
			getCurrentDocViewer().resetTempGroup();
			// make sure this comes after the call to resetTempGroup, when a
			// new focused object is set it places the objects in the temp group
			// back on the page, which is not what is needed here
			getCurrentDocViewer().setFocusedObject(newGroup);
			getCurrentDocViewer().addUndoState();
			getCurrentDocViewer().repaint();

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


    private String createPopupBelowCurrObject(String message, String defaultInput) {

        MathObject currFocused = this.getCurrentDocViewer().getFocusedObject();
        PointInDocument ptInDoc = new PointInDocument(
                // TODO - HAX - fix this
                this.getCurrentDocViewer().getDoc().getPageIndex(currFocused.getParentPage()),
                currFocused.getxPos(), currFocused.getyPos() + currFocused.getHeight());
        JOptionPane optionPane = new JOptionPane(message
                , JOptionPane.PLAIN_MESSAGE
                , JOptionPane.DEFAULT_OPTION
                , null, null, defaultInput);
        optionPane.setWantsInput(true);
        optionPane.setInitialSelectionValue(defaultInput);
        JDialog dialog = optionPane.createDialog(this, "");
        Point p = getCurrentDocViewer().docPt2AbsoluteScreenPos(ptInDoc);
        optionPane.selectInitialValue();
        dialog.setBounds((int) p.getX(), (int)p.getY() + 15, 250, 200);
        dialog.setVisible(true);
        dialog.dispose();
        String result = (String) optionPane.getInputValue();
        return result.equals("uninitializedValue") ? null : result;
    }

	public Node getExpressionFromUser(String message, String defaultInput){
		String lastEx = defaultInput;
		Node newNode = null;
		while( true ){
			lastEx = createPopupBelowCurrObject(message, lastEx);
			if ( lastEx == null ){
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
		JTextField author = new JTextField(getOpenNotebook().getUserName());
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
			int input = JOptionPane.showConfirmDialog(null,
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

		getOpenNotebook().getDatabase().addProblem(problem);
	}

	public void adjustMargins(){
		JTextField xMargin = new JTextField(String.format("%.2G", getCurrentDocViewer().getDoc().getxMargin()/72.0));
		JTextField yMargin = new JTextField(String.format("%.2G", getCurrentDocViewer().getDoc().getyMargin()/72.0));
		JTextField date = new JTextField(new Date().toString());
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Horizontal margins (Inches)"),
				xMargin,
				new JLabel("Vertical Margins (Inches)"),
				yMargin,
		};
		while (true){
			int input = JOptionPane.showConfirmDialog(null, 
					inputs, "Document Margins", JOptionPane.PLAIN_MESSAGE);
			if ( input == -1){
				return;
			}
			try{
				if ( ! xMargin.getText().equals("")){
					getCurrentDocViewer().getDoc().setxMargin(xMargin.getText());
				}
				if ( ! yMargin.getText().equals("")){
					getCurrentDocViewer().getDoc().setyMargin(yMargin.getText());
				}
				break;
			}catch( Exception ex){
				JOptionPane.showMessageDialog(null, ex.getMessage(),
						"Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}
		}
		getCurrentDocViewer().repaintDoc();
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

	public static String addFileExtension(String filename){
		Pattern pattern = Pattern.compile(".+(\\.mdoc)$");
		Matcher matcher;
		matcher = pattern.matcher(filename);
		if (! matcher.matches()){
			return filename + ".mdoc";
		}
		return filename;
	}

	public void createTextViewer(String string) {
		textFrame = new JFrame(string);
		textFrame.setPreferredSize(new Dimension(640, 400));
		JTextArea terminal = new JTextArea(14, 20);

		Font terminalFont = new Font("newFont", 1, 12);
		terminal.setFont(terminalFont);
		terminal.setEditable(false);
		JScrollPane termScrollPane = new JScrollPane(terminal);
		termScrollPane.setWheelScrollingEnabled(true);

		textFrame.add(termScrollPane);
		textFrame.pack();

		textFrame.setVisible(true);
		//		terminal.append(readTextDoc(string));
		terminal.append(string);
		termScrollPane.revalidate();
		JScrollBar tempScroll = termScrollPane.getVerticalScrollBar();
		tempScroll.setValue(0);
		textFrame.pack();
		textFrame.repaint();
		textFrame.setVisible(true);
	}

	public void createWorkspace(MathObject mObj){
		workspaceFrame = new JFrame("Workspace");
		workspaceFrame.setPreferredSize(new Dimension(750, 400));

		Document newDoc = new Document("");
		newDoc.addBlankPage();
		newDoc.setxMargin(0);
		newDoc.setyMargin(0);
		newDoc.setWidth(400);
		newDoc.setHeight(300);
		workspaceDoc = new DocViewerPanel(newDoc, null, this);
		JPanel workspace = new JPanel(new BorderLayout());
		KeyboardShortcuts.addKeyboardShortcuts(workspace, this);
		workspace.add(workspaceDoc, BorderLayout.CENTER);
		workspace.add(new ObjectToolBar(this), BorderLayout.SOUTH);
		workspaceFrame.getContentPane().add(workspace);
		workspaceFrame.setVisible(true);
		workspaceFrame.pack();
		workspaceFrame.repaint();
		workspaceFrame.setVisible(true);
	}

	public void save() {
		BufferedWriter f = null;
		try {
			getFileChooser().setSelectedFile(
					new File(getCurrentDocViewer().getDoc().getName()));
			int value = getFileChooser().showSaveDialog(this);
			if (value == JFileChooser.APPROVE_OPTION) {
				java.io.File file = getFileChooser().getSelectedFile();

				file = new File(file.getParentFile() + File.separator + addFileExtension(file.getName()));

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
				e.printStackTrace();
			} finally {
				try {
					f.flush();
					f.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
				addDoc(getOpenNotebook().getFileReader().readDoc(fileReader, getFileChooser()
						.getSelectedFile().getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error opening file.",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Document readDoc(InputStreamReader file, String docName) throws SAXException, IOException{
		return getOpenNotebook().getFileReader().readDoc(file, docName);
	}

	private void createSampleDialog() {
		sampleDialog = new JDialog();
		sampleDialog.add(new SampleListPanel(this));
		sampleDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		sampleDialog.pack();
	}

	public void setKeyboardDialogVisible(boolean b) {
		getCurrentDocViewer().setOnScreenKeyBoardVisible(true);
	}

	public void createProbelmDialog() {
		if (problemDialog != null){
			problemDialog.dispose();
		}
		problemDialog = new JDialog();
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

	public void disposeOfChildDialogs(){
		if ( sampleDialog != null) sampleDialog.dispose();
		if ( problemDialog != null) problemDialog.dispose();
		if ( textFrame != null) textFrame.dispose();
		if ( workspaceFrame != null) workspaceFrame.dispose();
	}

    public void open(String docFilePath) {
        File f = new File(docFilePath);
        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "File not found.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        openHelper(fis, f.getName());
    }

    public void openHelper(InputStream inStream, String docName) {
        InputStreamReader inputStreamReader = new InputStreamReader(inStream);
        try {
            Document tempDoc = getOpenNotebook().getFileReader().readDoc(inputStreamReader, docName);
            tempDoc.setFilename(docName);
            addDoc(tempDoc);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error opening file",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

	public void openSample(String docName) {
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("samples/" + docName);
        openHelper(inputStream, docName);
	}

	public void addPage() {
		Document doc = getCurrentDocViewer().getDoc();
		Page selectedPage = getCurrentDocViewer().getSelectedPage();
		int pageIndex = 0;

		if (doc.getNumPages() > 0 && selectedPage != null) {
			int index = 0;
			doc.getPages().add(index, new Page(doc));
			index = doc.getPageIndex(selectedPage);
			pageIndex = index + 1;
		} else {
			getCurrentDocViewer().getDoc().addBlankPage();
			pageIndex = getCurrentDocViewer().getDoc().getPages().size() + 1; 
		}
		getCurrentDocViewer().addUndoState();
		getCurrentDocViewer().resizeViewWindow();
		getCurrentDocViewer().docScrollPane.getVerticalScrollBar()
		.setValue(getCurrentDocViewer().getPageOrigin(pageIndex).y);
	}

	public void deletePage() {
		if (getCurrentDocViewer().getSelectedPage() == null) {
			JOptionPane.showMessageDialog(null, "Please select a page first.",
					"Error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		getCurrentDocViewer().getDoc().removePage(
				getCurrentDocViewer().getSelectedPage());
		getCurrentDocViewer().addUndoState();
		getCurrentDocViewer().setSelectedPage(null);
		getCurrentDocViewer().resizeViewWindow();
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
		return getOpenNotebook().isInStudentMode();
	}

	public ProblemDatabase getDatabase(){
		return getOpenNotebook().getDatabase();
	}

	public void addDoc(Document doc) {
		int numTabs = docTabs.getTabCount();
		openDocs.add(new DocViewerPanel(doc, getTopLevelContainer(), this));

		// remove the old adding new doc tab
		docTabs.remove(numTabs - 1);

		// add new doc
		docTabs.add(openDocs.get(openDocs.size() - 1),
				openDocs.get(openDocs.size() - 1).getDoc().getName());
		DocTabClosePanel temp = new DocTabClosePanel(this,
				openDocs.get(openDocs.size() - 1));
		tabLabels.add(temp);
		docTabs.setTabComponentAt(openDocs.size() - 1, temp);

		// add back creation tab
		docTabs.add(new JPanel(), "+");
		docTabs.setSelectedIndex(docTabs.getTabCount() - 2);
	}

	public DocViewerPanel getCurrentDocViewer() {
		if ( workspaceFrame != null){
			if ( workspaceFrame.hasFocus()){
				return workspaceDoc;
			}
		}
		return openDocs.get(docTabs.getSelectedIndex());
	}

	public ImageIcon getIcon(String filename) {
		try {
			BufferedImage image = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream(filename));
			return new ImageIcon(image);
		} catch (IOException e) {
			// TODO - logging
//			System.out.println("cannot find image " + filename);
		}
		return null;
	}

	/**
	 * Used to remove an editor from the tabs.
     *
     * TODO - should be modified to ask if user wants to save first.
	 */
	public void closeDocViewer(DocViewerPanel docPanel) {
		if (openDocs.size() == 1) {
			return;
		}

		if ( docPanel.hasBeenModfiedSinceSave() ){
			// open popup to see ask if user wants to save recent changes
			Object[] options = { "Close Anyway", "Cancel" };
			int n = JOptionPane.showOptionDialog(null,
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

	public OpenNotebook getOpenNotebook() {
		return openNotebook;
	}

	public void setOpenNotebook(OpenNotebook openNotebook) {
		this.openNotebook = openNotebook;
	}

	public MathObject getObjToPlace() {
		return objToPlace;
	}

	public void setObjToPlace(MathObject objToPlace) {
		this.objToPlace = objToPlace;
	}

	public ObjectCreationMode getObjectCreationMode() {
		return objectCreationMode;
	}

	public void setObjectCreationMode(ObjectCreationMode objectCreationMode) {
		this.objectCreationMode = objectCreationMode;
	}

	public boolean isPlacingObj(){
		if ( getObjectCreationMode() == ObjectCreationMode.PLACING_SINGLE_OBJECT ||
				getObjectCreationMode() == ObjectCreationMode.MULTIPLE_OBJECTS){
			return true;
		}
		return false;
	}

	/**
	 * Method to handle changing state in response to placed object.
	 * If the user is placing multiple object, queues up another object
	 * to be placed later, if it was only placing a single object, sets the object
	 * to place null.
	 */
	public void objHasBeenPlaced(){
		if ( getObjectCreationMode() == ObjectCreationMode.MULTIPLE_OBJECTS ){
			setObjToPlace(MathObject.newInstanceWithType(getObjToPlace().getType()));
		}
		else{
			setObjectCreationMode(ObjectCreationMode.NOT_PLACING_OBJECT);
			setObjToPlace(null);
			objectToolbar.updateButton();
		}

	}
}
