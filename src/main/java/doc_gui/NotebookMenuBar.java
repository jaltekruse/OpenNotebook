package doc_gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import doc.Document;

public class NotebookMenuBar extends JMenuBar {

	JMenu mode, view, file, edit;
	OpenNotebook openBook;
	
	public NotebookMenuBar(OpenNotebook book){
		super();
		
		openBook = book;
		file = new JMenu("File");
		this.add(file);
		
		JMenuItem item;
		item = new JMenuItem("New Document");
		file.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Document tempDoc = new Document("Untitled Document");
				tempDoc.addBlankPage();
				openBook.getNotebookPanel().addDoc(tempDoc);
			}
		});
		item = new JMenuItem("Open");
		file.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().open();
			}
		});
		item = new JMenuItem("Save");
		file.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().save();
			}
		});
		
		item = new JMenuItem("Quit");
		file.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				OpenNotebook.quit();
			}
		});
		
		edit = new JMenu("Edit");
		this.add(edit);
		
		item = new JMenuItem("Add Page");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().addPage();
			}
		});
		
		item = new JMenuItem("Adjust Margins");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().adjustMargins();
			}
		});
		
		final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Minimal Graphics Mode (for older machines)");
		cbMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cbMenuItem.isSelected()){
					OpenNotebook.setMinimalGraphicsMode(true);
				}
				else{
					OpenNotebook.setMinimalGraphicsMode(false);
				}
			}

		});
		
		edit.add(cbMenuItem);
		
		edit.addSeparator();
		
		item = new JMenuItem("Cut");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().cut();
			}
		});
		
		item = new JMenuItem("Copy");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().copy();
			}
		});
		
		item = new JMenuItem("Paste");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().paste();
			}
		});
		
		item = new JMenuItem("Delete");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().delete();
			}
		});
		
		edit.addSeparator();
		
		item = new JMenuItem("Send Forward");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().sendForward();
			}
		});
		
		item = new JMenuItem("Bring to Front");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().bringToFront();
			}
		});
		
		item = new JMenuItem("Send Backward");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().sendBackward();
			}
		});
		
		item = new JMenuItem("Bring to Back");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().bringToBack();
			}
		});
		
		edit.addSeparator();
		
		
		item = new JMenuItem("Set Preferences Directory");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				OpenNotebook.setPreferencesDirectory();
			}
		});
		
		item = new JMenuItem("Set User Name");
		edit.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				OpenNotebook.setUserName();
			}
		});
		
		view = new JMenu("View");
		this.add(view);
		
		item = new JMenuItem("Zoom In");
		view.add(item);
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().zoomIn();
			}
		});
		
		item = new JMenuItem("Zoom Out");
		view.add(item);
		item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().zoomOut();
			}
		});
		
		view.addSeparator();
		
		ButtonGroup group = new ButtonGroup();
		
		JRadioButtonMenuItem radioItem = new JRadioButtonMenuItem("Align Pages Right");
		radioItem.setSelected(true);
		group.add(radioItem);
		view.add(radioItem);
		radioItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().setDocAlignment(OpenNotebook.ALIGN_DOCS_RIGHT);
			}
		});
		
		radioItem = new JRadioButtonMenuItem("Align Pages Left");
		group.add(radioItem);
		view.add(radioItem);
		radioItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().setDocAlignment(OpenNotebook.ALIGN_DOCS_LEFT);
			}
		});
		
		radioItem = new JRadioButtonMenuItem("Align Pages Center");
		group.add(radioItem);
		view.add(radioItem);
		radioItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openBook.getNotebookPanel().setDocAlignment(OpenNotebook.ALIGN_DOCS_CENTER);
			}
		});
	
		mode = new JMenu("Mode");
		this.add(mode);

		if (openBook.isInStudentMode()){
			// TODO - change to edit when I rename it elsewhere
			item = new JMenuItem("Teacher");
			mode.add(item);
			item.addActionListener(new ActionListener(){
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Object[] options = {"Switch Now", "Cancel"};
					int n = JOptionPane.showOptionDialog(null,
					    "Switching modes will discard any unsaved changes.",
					    "Information may be Lost",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.WARNING_MESSAGE,
					    null,
					    options,
					    options[1]);
	
					if (n == 0){ 
						openBook.setMode(OpenNotebook.Mode.EDIT);
					}
				}
			});
		}

		else{
			item = new JMenuItem("Student");
			mode.add(item);
			item.addActionListener(new ActionListener(){
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Object[] options = {"Switch Now", "Cancel"};
					int n = JOptionPane.showOptionDialog(null,
					    "Switching modes will discard any unsaved changes.",
					    "Information may be Lost",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.WARNING_MESSAGE,
					    null,
					    options,
					    options[1]);
	
					if (n == 0){ 
						openBook.setMode(OpenNotebook.Mode.STUDENT);
					}
				}
			});
		}
	}
}
