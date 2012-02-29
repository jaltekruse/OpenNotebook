package doc_gui;


import javax.swing.ImageIcon;
import javax.swing.JToolBar;

public class ObjectActionsToolBar extends JToolBar{
	
	private NotebookPanel notebookPanel;
	
	public ObjectActionsToolBar(NotebookPanel p){
		notebookPanel = p;
		this.setFloatable(false);
		
		if ( ! notebookPanel.isInStudentMode()){
			ImageIcon deleteIcon = notebookPanel.getIcon("img/delete.png");
			
			OCButton deleteButton = new OCButton(deleteIcon, "Delete page or object", 1, 1, 3, 0, this){
				
				//allow this button to also delete page, need to add code to allow page to be
				//selected and have visual feedback (a border) to indicate selection
				@Override
				public void associatedAction(){
					notebookPanel.delete();
				}
			};
			
			ImageIcon bringToFrontIcon = notebookPanel.getIcon("img/bringToFront.png");
			
			OCButton bringToFrontButton = new OCButton(bringToFrontIcon,
					"Bring to Front", 1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.bringToFront();
				}
			};
			
			ImageIcon sendForwardIcon = notebookPanel.getIcon("img/sendForward.png");
			
			OCButton sendForwardButton = new OCButton(sendForwardIcon,
					"Send Forward", 1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.sendForward();
				}
			};
			
			ImageIcon sendBackwardIcon = notebookPanel.getIcon("img/sendBackward.png");
			
			OCButton sendBackwardButton = new OCButton(sendBackwardIcon,
					"Send Backward", 1, 1, 3, 0, this){
				
				//allow this button to also delete page, need to add code to allow page to be
				//selected and have visual feedback (a border) to indicagte selection
				@Override
				public void associatedAction(){
					notebookPanel.sendBackward();
				}
			};
			
			ImageIcon bringToBackIcon = notebookPanel.getIcon("img/bringToBack.png");
			
			OCButton bringToBackButton = new OCButton(bringToBackIcon,
					"Bring to Back", 1, 1, 3, 0, this){
	
				@Override
				public void associatedAction(){
					notebookPanel.bringToBack();
				}
			};
			
			ImageIcon cutIcon = notebookPanel.getIcon("img/cut.png");
			
			OCButton cutButton = new OCButton(cutIcon,
					"Cut [ctrl+x]", 1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.cut();
				}
			};
			
			ImageIcon copyIcon = notebookPanel.getIcon("img/copy.png");
			
			OCButton copyButton = new OCButton(copyIcon,
					"Copy [ctrl+c]", 1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.copy();
				}
			};
			
			ImageIcon pasteIcon = notebookPanel.getIcon("img/paste.png");
			
			OCButton pasteButton = new OCButton(pasteIcon,
					"Paste [ctrl+v]", 1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.paste();
				}
			};
			
			ImageIcon groupIcon = notebookPanel.getIcon("img/group.png");
			
			OCButton groupButton = new OCButton(groupIcon, "Group", 1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.group();
				}
			};
			
			ImageIcon ungroupIcon = notebookPanel.getIcon("img/ungroup.png");
			
			OCButton ungroupButton = new OCButton(ungroupIcon, "Ungroup a Grouping or Problem",
					1, 1, 3, 0, this){
				
				@Override
				public void associatedAction(){
					notebookPanel.ungroup();
				}
			};
			
//			ImageIcon problemGenerationIcon = notebookPanel.getIcon("img/generateListOfProblems.png");
//			
//			OCButton generateProblems = new OCButton(problemGenerationIcon, "Generate Problems",
//					1, 1, 3, 0, this){
//				
//				@Override
//				public void associatedAction(){
//					MathObject mObj = notebookPanel.getCurrentDocViewer().getFocusedObject();
//					if ( mObj != null && mObj instanceof ProblemObject){
//						
//					}
//					notebookPanel.getCurrentDocViewer().showDatabase();
//				}
//			};
			
//			OCButton logiseparatedButton = new OCButton("Login", "Login", 1, 1, 3, 0, this, null){
//				
//				//allow this button to also delete page, need to add code to allow page to be
//				//selected and have visual feedback (a border) to indicate selection
//				public void associatedAction(){
//					String s = (String)JOptionPane.showInputDialog(
//		                    this,
//		                    "give you user name and Password  by a colon",
//		                    "Login",
//		                    JOptionPane.PLAIN_MESSAGE,
//		                    null,
//		                    null,
//		                    null);
//					String userName = s.substring(0, s.indexOf(':'));
//					String password = s.substring(s.indexOf(':') + 1);
//					System.out.println(notebookPanel.getWebPage(userName, password));
//				}
//			};
		}
	}
}
