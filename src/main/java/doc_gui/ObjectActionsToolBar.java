package doc_gui;


import javax.swing.ImageIcon;
import javax.swing.JToolBar;

public class ObjectActionsToolBar extends JToolBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = -736669225659499204L;
	private NotebookPanel notebookPanel;

	public ObjectActionsToolBar(NotebookPanel p){
		notebookPanel = p;
		this.setFloatable(false);

		if ( ! notebookPanel.isInStudentMode()){
			ImageIcon deleteIcon = notebookPanel.getIcon("img/delete.png");

			new OCButton(deleteIcon, "Delete page or object", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.delete();
				}
			};

			ImageIcon bringToFrontIcon = notebookPanel.getIcon("img/bringToFront.png");

			new OCButton(bringToFrontIcon, "Bring to Front", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.bringToFront();
				}
			};

			ImageIcon sendForwardIcon = notebookPanel.getIcon("img/sendForward.png");

			new OCButton(sendForwardIcon, "Send Forward", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.sendForward();
				}
			};

			ImageIcon sendBackwardIcon = notebookPanel.getIcon("img/sendBackward.png");

			new OCButton(sendBackwardIcon, "Send Backward", 1, 1, 3, 0, this){

				//allow this button to also delete page, need to add code to allow page to be
				//selected and have visual feedback (a border) to indicagte selection
				@Override
				public void associatedAction(){
					notebookPanel.sendBackward();
				}
			};

			ImageIcon bringToBackIcon = notebookPanel.getIcon("img/bringToBack.png");

			new OCButton(bringToBackIcon, "Bring to Back", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.bringToBack();
				}
			};

			ImageIcon cutIcon = notebookPanel.getIcon("img/cut.png");

			new OCButton(cutIcon, "Cut [ctrl+x]", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.cut();
				}
			};

			ImageIcon copyIcon = notebookPanel.getIcon("img/copy.png");

			new OCButton(copyIcon, "Copy [ctrl+c]", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.copy();
				}
			};

			ImageIcon pasteIcon = notebookPanel.getIcon("img/paste.png");

			new OCButton(pasteIcon, "Paste [ctrl+v]", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.paste();
				}
			};

			ImageIcon groupIcon = notebookPanel.getIcon("img/group.png");

			new OCButton(groupIcon, "Group", 1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.group();
				}
			};

			ImageIcon ungroupIcon = notebookPanel.getIcon("img/ungroup.png");

			new OCButton(ungroupIcon, "Ungroup a Grouping or Problem",
					1, 1, 3, 0, this){

				@Override
				public void associatedAction(){
					notebookPanel.ungroup();
				}
			};
		}
	}
}
