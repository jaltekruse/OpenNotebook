package doc_gui;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import doc.mathobjects.MathObject;

public class ObjectToolBar extends JToolBar {

	private NotebookPanel notebookPanel;

	public ObjectToolBar(NotebookPanel p) {
		notebookPanel = p;
		this.setFloatable(false);
		
		OCButton objButton;
		ImageIcon objIcon;

		for (final String s : MathObject.objectTypes) {
			if (s.equals(MathObject.GROUPING)
					|| s.equals(MathObject.VAR_INSERTION_PROBLEM)
					|| s.equals(MathObject.GENERATED_PROBLEM)
					|| s.equals(MathObject.PROBLEM_NUMBER_OBJECT)) {
				// all classes that extend grouping cannot be created with a
				// button and mouse drag. They do not need buttons
				continue;
			}
			if (s.equals(MathObject.PYRAMID_OBJECT)) {
				// these objects have not been created yet, they will be added
				// soon
				continue;
			}
			objIcon = notebookPanel.getIcon("img/"
					+ MathObject.getObjectImageName(s));

			objButton = new OCButton(objIcon, "Add " + s, 1, 1, 0, 0, this) {

				@Override
				public void associatedAction() {
					// pass event down to current doc window for placing a
					// mathObj
					notebookPanel.getCurrentDocViewer().createMathObject(
							MathObject.newInstanceWithType(s));
				}
			};
		}
		
		OCButton generateButton = new OCButton("Generate Problems", "Generate Problems",
				1, 1, 3, 0, this){
			
			@Override
			public void associatedAction(){
				notebookPanel.createProbelmDialog();
				notebookPanel.setProblemDialogVisible(true);
			}
		};
		
//		OCButton cloneButton = new OCButton("Clone Doc", "Clone Doc",
//				1, 1, 3, 0, this){
//			
//			@Override
//			public void associatedAction(){
//				notebookPanel.addDoc(notebookPanel.getCurrentDocViewer().getDoc().clone());
//			}
//		};
	}
}
