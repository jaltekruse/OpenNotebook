package doc_gui;


import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

public class FileActionsToolBar extends JToolBar {

	private NotebookPanel notebookPanel;

	public FileActionsToolBar(NotebookPanel p){
		this.setFloatable(false);
		notebookPanel = p;

		Icon saveIcon = notebookPanel.getIcon("img/save.png");

		new OCButton(saveIcon, "Save [ctrl+s]", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.save();
			}
		};

		ImageIcon openIcon = notebookPanel.getIcon("img/open.png");

		new OCButton(openIcon, "Open [ctrl+o]", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.open();
			}
		};

		if ( ! notebookPanel.isInStudentMode()){
			ImageIcon addPageIcon = notebookPanel.getIcon("img/newPage.png");

			OCButton addPageButton = new OCButton(addPageIcon,
					"Add Page [ctrl+n](before selected, or at the end if none selected)", 1, 1, 2, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.addPage();
				}
			};

			//			ImageIcon pagePropsIcon = notebookPanel.getIcon("img/pageProperties.png");
			//			
			//			OCButton pagePropsButton = new OCButton(pagePropsIcon, "Doc Properties", 1, 1, 2, 0, this){
			//				
			//				@Override
			//				public void associatedAction(){
			//					notebookPanel.showDocProperties();
			//				}
			//			};
		}

		ImageIcon zoomInIcon = notebookPanel.getIcon("img/zoomIn.png");

		new OCButton(zoomInIcon, "Zoom In [ctrl and '+']", 1, 1, 2, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.zoomIn();
			}
		};

		ImageIcon zoomOutIcon = notebookPanel.getIcon("img/zoomOut.png");

		new OCButton(zoomOutIcon, "Zoom Out [ctrl and '-']", 1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.getCurrentDocViewer().zoomOut();
			}
		};
		ImageIcon undoIcon = notebookPanel.getIcon("img/undo.png");

		OCButton undoButton = new OCButton(undoIcon, "Undo", 1, 1, 3, 0, this){

			public void associatedAction(){
				notebookPanel.undo();
			}
		};

		ImageIcon redoIcon = notebookPanel.getIcon("img/redo.png");

		OCButton redoButton = new OCButton(redoIcon, "Redo", 1, 1, 3, 0, this){

			public void associatedAction(){
				notebookPanel.redo();
			}
		};

		ImageIcon printIcon = notebookPanel.getIcon("img/print.png");

		new OCButton(printIcon, "Print [ctrl+p]", 1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.print();
			}
		};

		if ( ! notebookPanel.isInStudentMode()){
			ImageIcon generateIcon = notebookPanel.getIcon("img/generateWorksheet.png");

			new OCButton(generateIcon, "Generate Worksheet", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.generateWorksheet();
				}
			};

			ImageIcon answerKeyIcon = notebookPanel.getIcon("img/answerKey.png");

			new OCButton(answerKeyIcon, "Show Answer Key", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.addDoc(notebookPanel.getCurrentDocViewer().getDoc().createAnswerKey());
				}
			};
		}
		ImageIcon keyboardIcon = notebookPanel.getIcon("img/keyboard.png");

		new OCButton(keyboardIcon, "Show On-Screen Keyboard", 1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.setKeyboardDialogVisible(true);
			}
		};

		OCButton sampleDocs = new OCButton("<html><font size=-1>Tutorials/<br>Samples</font><html>", "Sample Documents",
				1, 1, 3, 0, this){

			@Override
			public void associatedAction(){
				notebookPanel.setSampleDialogVisible(true);
			}
		};
	}

}
