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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import doc.Document;
import doc.ProblemDatabase;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GraphObject;
import doc.xml.DocReader;
import doc_gui.attribute_panels.ObjectPropertiesFrame;

/**
 * An OpenNotebook object keeps track of all of the background data needed for
 * the entire user interface. Actual display is handled by the classes created
 * inside of the associated NotebookFrame.
 * 
 * @author jason
 * 
 */

public class OpenNotebook extends JApplet {

	public static final String ERROR = "Error";

	public enum Mode {
		STUDENT,
		GRADING,
		// was previously teacher mode, will now be used by students to
		// complete their assignments, so that they can add new content like
		// diagrams, graphs, etc. (TODO think about how to disable graphs when
		// students are supposed to be graphing things themselves)

		// teachers will use this mode to create worksheets as well
		EDIT
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5614406937717777249L;
	private static OpenNotebook application;
	private static JFrame frame;
	private static Mode mode;
	private static JFileChooser fileChooser;
	private static DocReader reader;
	private static ProblemDatabase database;
	public static NotebookPanel notebookPanel;
	public static final int ALIGN_DOCS_LEFT = 1, ALIGN_DOCS_RIGHT = 2,
			ALIGN_DOCS_CENTER = 3;
	private static String ourNodeName = "/doc_gui";
	private static Preferences prefs = Preferences.userRoot().node(ourNodeName);
	private static final String DATABASE_PATH = "databasePath",
			USER_NAME = "userName";
	private static final String BACKING_STORE_AVAIL = "BackingStoreAvail", DATABASE_FILENAME = "ProblemDatabase";
	private int docAlignment = ALIGN_DOCS_CENTER;
	private static boolean minimalGraphicsMode = false;
	private static String cookie;
	
	public long timeAtStart = new Date().getTime();
	
	// this is a parallel array that relates to the list of objects stored in the MathObject class

	public OpenNotebook() {
		this(true, true, false);
	}
	
	public OpenNotebook(boolean askMode, boolean blankDoc, boolean studentMode) {
		// create background resources here
		// GUI elements will be created in NotebookInterface
		// notebook will be created first and then passed into the GUI

		// a similar structure for file creation and then actual gui
		// representation will likely
		// be used, so users will enter data like subjects covered, author, and
		// modification date into a dialog
		// this data can be stored on the back-end and then the back-end data
		// can be passed into the classes to
		// actually render the document
		// addContents(this.getContentPane(), this);
		setFileReader(new DocReader());
		application = this;
		boolean openTutorial = false;
		// For debugging, this statement will clear the
		// preferences stored locally to bring it into
		// the state experienced by new users of the
		// application
		boolean DEBUG_FIRST_STARTUP = false;
		if (DEBUG_FIRST_STARTUP) {
			try {
				application.prefs.clear();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		if (preferencesDirectorySet()) {
			readProblemDatabase();
		} else {
			setPreferencesDirectory();
			readDefaultDatabase();
			openTutorial = true;
		}
		if (!userNameSet()) {
			setUserName();
		}
		Document.setProblemDatabase(database);
		if (askMode){
			// this sets the application to student mode
			// the main interface is added in the setter
			Object[] options = { "Student", "Teacher" };
			int n = JOptionPane.showOptionDialog(null,
					"Which mode would you like to run?", "Mode Selection",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[1]);
			if (n == -1) {
				quit();
				return;
			} else if (n == 1) {
				application.setMode(Mode.EDIT);
			} else {
				application.setMode(Mode.STUDENT);
			}
		}
		else{
			if (frame == null) {
				addContents(this.getContentPane());
			}
			else {
				Mode mode = studentMode ? Mode.STUDENT : Mode.EDIT;
				application.setMode(mode);
			}
		}
		if (openTutorial) {
			if (application.isInStudentMode()) {
				notebookPanel.openSample(SampleListPanel.STUDENT_MODE_TUTORIAL_FILE + ".mdoc");
			} else {
				notebookPanel.openSample(SampleListPanel.TEACHER_MODE_TUTORIAL_FILE + ".mdoc");
			}
		}
	}

	public static void makeErrorDialog(String s) {
		JOptionPane.showMessageDialog(null, s, ERROR,
				JOptionPane.ERROR_MESSAGE);
	}

	private static void openRemoteDoc(String... args) {
		if (args[0].equals("teacher")){
			createAndShowGUI(false, false, false);
		}
		if (args[0].equals("student")){
			createAndShowGUI(false, false, true);
		}
		URL url;
		HttpURLConnection urlConn;
		InputStream input = null;
		
		try {
			url = new URL(args[2]);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("Cookie", getCookie());
			urlConn.connect();
			input = urlConn.getInputStream();
			notebookPanel.addDoc(notebookPanel.readDoc(new InputStreamReader(
					input), "Doc"));
		} catch (IOException e) {
			makeErrorDialog("Error opening document.");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			makeErrorDialog("Error reading document.");
		} finally {
			if ( input == null ){
				makeErrorDialog("Error making site connection to download document.");
			}
			else{
				try {
					input.close();
				} catch (Exception e) {
					makeErrorDialog("Error closing connection.");
				}
			}
		}
		
	}

	private static void createAndShowGUI(boolean askMode, boolean blankDoc, boolean studentMode) {

		frame = new JFrame("OpenNotebook - Teacher");
		application = new OpenNotebook(askMode, blankDoc, studentMode);
		if (frame == null) {// the user hit close on the dialog in the
							// OpenNotebook constructor
			return;
		}
		frame.getContentPane().add(application);

		Dimension frameDim = new Dimension(1200, 720);
		frame.setPreferredSize(frameDim);
		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
//				System.out.println("resized!!!");
//				if (frame.getWidth() > 1100) {
//					application.setDocAlignment(ALIGN_DOCS_CENTER);
//				}
//				if (frame.getWidth() <= 1100) {
//					application.setDocAlignment(ALIGN_DOCS_RIGHT);
//				}

			}

			@Override
			public void componentShown(ComponentEvent arg0) {
			}
		});
		
		application.addComponentListener(new ComponentListener() {
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
//				System.out.println("resized!!!");
//				if (application.getWidth() > 1100) {
//					application.setDocAlignment(ALIGN_DOCS_CENTER);
//				}
//				if (application.getWidth() <= 1100) {
//					application.setDocAlignment(ALIGN_DOCS_RIGHT);
//				}

			}

			@Override
			public void componentShown(ComponentEvent arg0) {
			}
		});

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(createWindowListener());

		frame.pack();
		frame.setVisible(true);
	}

	public static boolean isInStudentMode() {
		return mode == Mode.STUDENT;
	}

	private static WindowListener createWindowListener() {
		return new WindowListener() {
			@Override public void windowActivated(WindowEvent arg0) {}
			@Override public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				notebookPanel.quit();
			}
			@Override public void windowDeactivated(WindowEvent arg0) {}
			@Override public void windowDeiconified(WindowEvent arg0) {}
			@Override public void windowIconified(WindowEvent arg0) {}
			@Override public void windowOpened(WindowEvent arg0) {}
		};
	}

	public static void quit() {

		if (getPreferencesPath() != null) {
			File export = new File(getPreferencesPath());
			if (!export.exists()) {
				export.mkdir();
			}
			exportDatabase();
		}
		if (notebookPanel != null) {
			notebookPanel.disposeOfChildDialogs();
		}
		if ( frame != null){
			frame.dispose();
			frame = null;
		}
	}

	private static void readProblemDatabase() {
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File(getPreferencesPath()
					+ DATABASE_FILENAME));
			database = reader.readDatabase(fileReader);
		} catch (Exception e) {
			e.printStackTrace();
			database = new ProblemDatabase();
			JOptionPane
					.showMessageDialog(
							null,
							"An error occured finding problem Database,\n"
									+ "try using the \"Set Preferences Directory\" option\n"
									+ "in the Edit menu to resolve the issue.",
							"Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private static void readDefaultDatabase() {

		String docName = "ProblemDatabase";
		InputStream inputStream = OpenNotebook.class.getClassLoader()
				.getResourceAsStream("samples/" + docName);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		try {
			database = getFileReader().readDatabase(inputStreamReader);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error opening problem databse.", ERROR,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void exportDatabase() {
		BufferedWriter f = null;
		try {

			File file = new File(getPreferencesPath() + DATABASE_FILENAME);
			f = new BufferedWriter(new FileWriter(file));
			f.write(database.exportToXML());
			f.flush();
			f.close();

		} catch (Exception e) {
			try {
				if (f != null) {
					f.flush();
					f.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null,
					"Error saving problem database.", ERROR,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addContents(Container c) {
		notebookPanel = new NotebookPanel(this);
//		propertiesFrames = new ObjectPropertiesFrame[MathObject.objects.length];
//		for (int i = 0; i < MathObject.objects.length; i++){
//			//System.out.println("before:" + (new java.util.Date().getTime() - timeAtStart));
//			//propertiesFrames[i] = new ObjectPropertiesFrame(MathObject.objects[i], notebookPanel);
//			//System.out.println("after:" + (new java.util.Date().getTime() - timeAtStart));
//		}
		new ObjectPropertiesFrame(new GraphObject(), notebookPanel);
		new ObjectPropertiesFrame(new ExpressionObject(), notebookPanel);
		c.removeAll();
		c.setLayout(new GridBagLayout());
		GridBagConstraints bCon = new GridBagConstraints();
		bCon.fill = GridBagConstraints.BOTH;
		bCon.weightx = 1;
		bCon.weighty = 1;
		c.add(notebookPanel, bCon);
	}

	public static boolean preferencesDirectorySet() {
		String path = prefs.get(DATABASE_PATH, null);
		if (path == null) {
			return false;
		}
		return true;
	}

	public static boolean userNameSet() {
		String userName = prefs.get(USER_NAME, null);
		if (userName == null) {
			return false;
		}
		return true;
	}

	public String getUserName() {
		return prefs.get(USER_NAME, null);
	}

	private static boolean backingStoreAvailable() {
		try {
			boolean oldValue = prefs.getBoolean(BACKING_STORE_AVAIL, false);
			prefs.putBoolean(BACKING_STORE_AVAIL, !oldValue);
			prefs.flush();
		} catch (BackingStoreException e) {
			return false;
		}
		return true;
	}

	public static void setPreferencesDirectory() {
		if (!backingStoreAvailable()) {
			JOptionPane.showMessageDialog(
							application,
							"An error occured finding preferences on your system,\n"
									+ "your application will run with default settings.",
							"Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int input = JOptionPane.showConfirmDialog(null,
				"Open Notebook requires a directory to store your\n"
						+ "preferences, please select one in the next dialog.",
				"Set Preferences Directory", JOptionPane.WARNING_MESSAGE);
		if (input == -1 || input == 2) {
			return;
		}
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int success = fileChooser.showOpenDialog(application);
		if (success == JFileChooser.APPROVE_OPTION) {
			String newPath = fileChooser.getSelectedFile().getPath();
			prefs.put(DATABASE_PATH, newPath);
		}
	}

	public static void setUserName() {
		String name = (String) JOptionPane
				.showInputDialog(
						null,
						"Enter you name as you would like it to appear in the\n"
								+ "database if you publish problems. If you do not\n"
								+ "want your name shown leave it blank.",
						"Enter your name.", JOptionPane.PLAIN_MESSAGE, null,
						null, null);
		if (name == null) {
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

	public static String getPreferencesPath() {
		return prefs.get(DATABASE_PATH, null) + "/OpenNotebook/";
	}

	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if ( args.length > 0 ){
					System.out.println(args[1]);
					
					if ( args.length > 1){
						setCookie(args[1]);
						// TODO - logging
//						System.out.println("open remote doc");
						openRemoteDoc(args);
					}
					else{// only one argument
						if (args[0].equals("teacher")){
							createAndShowGUI(false, false, false);
						}
						if (args[0].equals("student")){
							createAndShowGUI(false, false, true);
						}
					}
				}
				else{
					createAndShowGUI(false, false, false);
//                    notebookPanel.openSample("teacher_overview.mdoc");
				}
			}
		});
	}

	public NotebookPanel setMode(Mode mode) {
		this.mode = mode;
		if ( notebookPanel != null && notebookPanel.sampleDialog != null &&
				notebookPanel.problemDialog != null){
			notebookPanel.sampleDialog.dispose();
			notebookPanel.problemDialog.dispose();
		}
		if (frame == null) {
			application.getContentPane().removeAll();
			application.addContents(this.getContentPane());
			application.setJMenuBar(new NotebookMenuBar(this));
			application.getContentPane().invalidate();
			application.validate();
		} else {
			if (isInStudentMode())
				frame.setTitle("OpenNotebook - Student");
			else
				frame.setTitle("OpenNotebook - Edit");
			this.getContentPane().removeAll();
			this.addContents(frame.getContentPane());
			frame.setJMenuBar(new NotebookMenuBar(this));
			frame.getContentPane().invalidate();
			frame.validate();
		}
		application.repaint();
		return notebookPanel;
	}

	public ProblemDatabase getDatabase() {
		return OpenNotebook.database;
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
		if (docAlignment == ALIGN_DOCS_LEFT || docAlignment == ALIGN_DOCS_RIGHT
				|| docAlignment == ALIGN_DOCS_CENTER) {
			this.docAlignment = docAlignment;
		}

	}

	public static DocReader getFileReader() {
		return reader;
	}

	public void setFileReader(DocReader reader) {
		this.reader = reader;
	}

	public static boolean isMinimalGraphicsMode() {
		return minimalGraphicsMode;
	}

	public static void setMinimalGraphicsMode(boolean minimalGraphicsMode) {
		OpenNotebook.minimalGraphicsMode = minimalGraphicsMode;
	}

	public static String getCookie() {
		return cookie;
	}

	public static void setCookie(String c) {
		cookie = c;
	}
}
