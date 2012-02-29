/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import doc.Document;
import doc.ProblemDatabase;
import doc.xml.DocReader;


/**
 * An OpenNotebook object keeps track of all of the background data needed for
 * the entire user interface. Actual display is handled by the classes created
 * inside of the associated NotebookFrame.
 * @author jason
 *
 */

public class OpenNotebook extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5614406937717777249L;
	private static OpenNotebook application;
	private static boolean inStudentMode;
	private static JFileChooser fileChooser;
	private static DocReader reader;
	private static ProblemDatabase database;
	private static NotebookPanel notebookPanel;
	public static final int ALIGN_DOCS_LEFT = 1, ALIGN_DOCS_RIGHT = 2, ALIGN_DOCS_CENTER = 3;
	private static String ourNodeName = "/doc_gui";
	private static Preferences prefs = Preferences.userRoot().node(ourNodeName);;
	private static final String DATABASE_PATH = "databasePath", USER_NAME = "userName";
	private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";
	private static final String DATABASE_FILENAME = "ProblemDatabase";

	private int docAlignment = ALIGN_DOCS_RIGHT;

	public OpenNotebook(String string){
		super(string);
		//create background resources here
		//GUI elements will be created in NotebookInterface
		//notebook will be created first and then passed into the GUI

		//a similar structure for file creation and then actual gui representation will likely
		//be used, so users will enter data like subjects covered, author, and modification date into a dialog
		//this data can be stored on the back-end and then the back-end data can be passed into the classes to
		//actually render the document
		//		addContents(this.getContentPane(), this);
		setFileReader(new DocReader());
	}

	public static void makeErrorDialog(String s){
		JOptionPane.showMessageDialog(null,
				s,
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private static void createAndShowGUI() {

		application = new OpenNotebook("OpenNotebook - Teacher");
//		try {
//			application.prefs.clear();
//		} catch (BackingStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Dimension frameDim = new Dimension(1000, 720);
		application.setPreferredSize(frameDim);
		application.addComponentListener(new ComponentListener(){
			@Override
			public void componentHidden(ComponentEvent arg0) {}
			@Override
			public void componentMoved(ComponentEvent arg0) {}
			@Override
			public void componentResized(ComponentEvent arg0) {
				if ( application.getWidth() > 1100){
					application.setDocAlignment(ALIGN_DOCS_CENTER);
				}
				if ( application.getWidth() <= 1100){
					application.setDocAlignment(ALIGN_DOCS_RIGHT);
				}

			}
			@Override
			public void componentShown(ComponentEvent arg0) {}
		});

		application.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		application.addWindowListener( createWindowListener());

		// this sets the application to student mode
		// the main interface is added in the setter
		Object[] options = {"Student", "Teacher"};
		int n = JOptionPane.showOptionDialog(application,
				"Which mode would you like to run?",
				"Mode Seletion",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);

		database = new ProblemDatabase();
		if (n == -1){
			application.dispose();
			return;
		}
		else if ( n == 1){
			application.setInStudentMode(false);
		}
		else{
			application.setInStudentMode(true);
		}

		application.pack();
		application.setVisible(true);

		if ( preferencesDirectorySet()){
			readProblemDatabase();
		}
		else{
			setPreferencesDirectory();
			readDefaultDatabase();
			if (application.inStudentMode()){
				notebookPanel.open("Student Mode Tutorial");
			}
			else{
				notebookPanel.open("Teacher Mode Tutorial");
			}
		}
		if ( ! userNameSet() ){
			setUserName();
		}
		Document.setProblemDatabase(database);
	}

	private static boolean inStudentMode() {
		return inStudentMode;
	}

	private static WindowListener createWindowListener() {
		return new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				notebookPanel.quit();
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}

		};
	}

	public static void quit(){

		if ( getPreferencesPath() != null){
			File export = new File(getPreferencesPath());
			if ( ! export.exists() ){
				export.mkdir();
			}
			exportDatabase();
		}
		application.dispose();

	}

	private static void readProblemDatabase(){
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File(getPreferencesPath() + DATABASE_FILENAME));
			database = reader.readDatabase(fileReader);
		} catch (Exception e){
			e.printStackTrace();
			database = new ProblemDatabase();
			JOptionPane.showMessageDialog(null, "An error occured finding problem Database,\n" +
					"try using the \"Set Preferences Directory\" option\n" +
					"in the Edit menu to resolve the issue.",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private static void readDefaultDatabase(){

		String docName = "ProblemDatabase";
		InputStream inputStream = OpenNotebook.class.getClassLoader()
				.getResourceAsStream("samples/" + docName);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		try {
			database = getFileReader().readDatabase(inputStreamReader);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error opening problem databse.",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void exportDatabase(){
		BufferedWriter f = null;
		try {

			File file = new File( getPreferencesPath() + DATABASE_FILENAME);
			f = new BufferedWriter(new FileWriter(file));
			f.write(database.exportToXML());
			f.flush();
			f.close();

		} catch (Exception e) {
			try {
				if ( f != null){
					f.flush();
					f.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Error saving problem database.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addContents(Container c, OpenNotebook book){
		notebookPanel = new NotebookPanel(book);
		c.removeAll();

		c.setLayout(new GridBagLayout());
		GridBagConstraints bCon = new GridBagConstraints();
		bCon.fill = GridBagConstraints.BOTH;
		bCon.weightx = 1;
		bCon.weighty = 1;
		c.add(notebookPanel, bCon);
	}

	public static boolean preferencesDirectorySet(){
		String path = prefs.get(DATABASE_PATH, null);
		if ( path == null){
			return false;
		}
		return true;
	}
	
	public static boolean userNameSet(){
		String userName = prefs.get(USER_NAME, null);
		if ( userName == null){
			return false;
		}
		return true;
	}
	
	public String getUserName(){
		return prefs.get(USER_NAME, null);
 	}

	private static boolean backingStoreAvailable() {
		try {
			boolean oldValue = prefs.getBoolean(BACKING_STORE_AVAIL, false);
			prefs.putBoolean(BACKING_STORE_AVAIL, !oldValue);
			prefs.flush();
		} catch(BackingStoreException e) {
			return false;
		}
		return true;
	}

	public static void setPreferencesDirectory(){
		if ( ! backingStoreAvailable()){
			JOptionPane.showMessageDialog(application, "An error occured finding preferences,\n" +
					"your application will run with default settings.",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int input = JOptionPane.showConfirmDialog(null, "Open Notebook requires a directory to store your\n" +
				"preferences, please select one in the next dialog.",
				"Set Preferences Directory", JOptionPane.WARNING_MESSAGE);
		System.out.println(input);
		if ( input == -1 || input == 2 ){
			return;
		}
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int success = fileChooser.showOpenDialog(application);
		if ( success == JFileChooser.APPROVE_OPTION){
			String newPath = fileChooser.getSelectedFile().getPath();
			System.out.println(newPath);
			prefs.put(DATABASE_PATH, newPath);
		}

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setUserName(){
		String name = (String)JOptionPane.showInputDialog(
				null,
				"Enter you name as you would like it to appear in the\n" +
				"database if you publish problems. If you do not\n" +
				"want your name shown leave it blank.",
				"Enter your name.",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				null);
		if (name == null){
			prefs.put(USER_NAME, "");
			return;
		}
		prefs.put(USER_NAME, name);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getPreferencesPath(){
		return (prefs.get(DATABASE_PATH, null) + "/OpenNotebook/");
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public void setInStudentMode(boolean b) {
		inStudentMode = b;
		if (application == null){
			this.getContentPane().removeAll();
			this.addContents(this.getContentPane(), this);
			this.setJMenuBar(new NotebookMenuBar(this));
		}
		else{
			if (b)
				application.setTitle("OpenNotebook - Student");
			else
				application.setTitle("OpenNotebook - Teacher");
			application.getContentPane().removeAll();
			this.addContents(application.getContentPane(), this);
			application.setJMenuBar(new NotebookMenuBar(this));
		}
	}

	public boolean isInStudentMode() {
		return inStudentMode;
	}

	public ProblemDatabase getDatabase(){
		return application.database;
	}

	public void setNotebookPanel(NotebookPanel notebookPanel) {
		this.notebookPanel = notebookPanel;
	}

	public NotebookPanel getNotebookPanel() {
		return notebookPanel;
	}

	public int getDocAlignment() {
		return docAlignment;
	}

	public void setDocAlignment(int docAlignment) {
		if ( docAlignment == ALIGN_DOCS_LEFT || 
				docAlignment == ALIGN_DOCS_RIGHT || 
				docAlignment == ALIGN_DOCS_CENTER){
			this.docAlignment = docAlignment;
		}

	}

	public static DocReader getFileReader() {
		return reader;
	}

	public void setFileReader(DocReader reader) {
		this.reader = reader;
	}
}
