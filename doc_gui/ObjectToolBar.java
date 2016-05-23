package doc_gui;

import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.xml.sax.SAXException;

import com.sun.org.apache.bcel.internal.generic.NEW;

import doc.mathobjects.MathObject;

public class ObjectToolBar extends JToolBar {

	private static NotebookPanel notebookPanel;
	// array to store the buttons for creating new objects
	// parallel array with the referenced list of objects in MathObject
	private ImageSwitcherButton[] objectButtons = new ImageSwitcherButton[MathObject.objects.length];
	private static ImageSwitcherButton lastHit;
	private static Vector<ObjectToolBar> allToolBars = new Vector<ObjectToolBar>();

	public ObjectToolBar(NotebookPanel p) {
		notebookPanel = p;
		this.setFloatable(false);
		allToolBars.add(this);
		this.setLayout(new ModifiedFlowLayout());
		
		addObjectButtons(this, p);
		
//		new OCButton("Doc", "Print current doc for debugging",
//				1, 1, 3, 0, this){
//
//			@Override
//			public void associatedAction(){
//				System.out.println(notebookPanel.getCurrentDocViewer().getDoc().exportToXML());
//			}
//		};
		new OCButton("Generate Problems", "Generate Problems",
				1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.createProbelmDialog();
				notebookPanel.setProblemDialogVisible(true);
			}
		};

/*
		new OCButton("Login", "Login", 1, 1, 3, 0, this){

			public void associatedAction(){ notebookPanel.login(); }
		};
	*/
	}

	public void finalize(){
		
	}
	
	public void addObjectButtons(JComponent c, final NotebookPanel notebookPanel){

		ImageIcon objIcon;
		int i = 0;
		ImageIcon lockImg = notebookPanel.getIcon("img/small_lock2.png");
		ImageIcon oneImg = notebookPanel.getIcon("img/small_one.png");
		for (MathObject mObj : MathObject.objects) {
			if ( mObj == null){
				continue;
			}
			final String s = mObj.getType();
			if (s.equals(MathObject.GROUPING)
					|| s.equals(MathObject.VAR_INSERTION_PROBLEM)
					|| s.equals(MathObject.GENERATED_PROBLEM)
					|| s.equals(MathObject.PROBLEM_NUMBER_OBJECT)) {
				// all classes that extend grouping cannot be created with a
				// button and mouse drag. They do not need buttons
				i++;
				continue;
			}
			if (s.equals(MathObject.PYRAMID_OBJECT)) {
				// these objects have not been created yet, they will be added
				// soon
				i++;
				continue;
			}
			objIcon = notebookPanel.getIcon("img/"
					+ MathObject.getObjectImageName(s));

			final ImageIcon objImage = objIcon;
			final ImageIcon objCreateImage = notebookPanel.getIcon("img/"
					+ MathObject.getObjectImageName(s));
			objCreateImage.getImage().getGraphics().drawImage(oneImg.getImage(),
					objCreateImage.getIconWidth() - oneImg.getIconWidth(), 
					objCreateImage.getIconHeight() - oneImg.getIconHeight(), null);
			
			final ImageIcon objCreateLockImage = notebookPanel.getIcon("img/"
					+ MathObject.getObjectImageName(s));
			objCreateLockImage.getImage().getGraphics().drawImage(lockImg.getImage(),
					objCreateLockImage.getIconWidth() - lockImg.getIconWidth(), 
					objCreateLockImage.getIconHeight() - lockImg.getIconHeight(), null);
	
			this.objectButtons[i] = new ImageSwitcherButton(objIcon, "Add " + s, 1, 1, 0, 0, c) {

				@Override
				public void associatedAction() {
					// pass event down to current doc window for placing a
					// mathObj
					if (lastHit != null && lastHit != this){
						 notebookPanel.setObjectCreationMode(NotebookPanel.ObjectCreationMode.NOT_PLACING_OBJECT);
						lastHit.updateImage();
					}
					lastHit = this;
					notebookPanel.getCurrentDocViewer().createMathObject(s);
					updateImage();
				}
				
				public void updateImage(){
					if ( notebookPanel.getObjectCreationMode() == NotebookPanel.ObjectCreationMode.NOT_PLACING_OBJECT){
						this.setIcon(objImage);
						lastHit = null;
					}
					else if ( notebookPanel.getObjectCreationMode() == NotebookPanel.ObjectCreationMode.PLACING_SINGLE_OBJECT){
						setIcon(objCreateImage);
					}
					else if ( notebookPanel.getObjectCreationMode() == NotebookPanel.ObjectCreationMode.MULTIPLE_OBJECTS){
						setIcon(objCreateLockImage);
						return;
					}
				}
			};
			i++;
		}
	}

	public static void updateButton(){
		if (notebookPanel.getObjToPlace() == null && lastHit != null){
			lastHit.updateImage();
			return;
		}
		/*
		for (int i = 0; i < MathObject.objects.length; i++){
			if ( MathObject.objects[i].getType().equals(notebookPanel.getObjToPlace().getType())){
				
			}
		}
		*/
	}
}
