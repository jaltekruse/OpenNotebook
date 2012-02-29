package doc.mathobjects;

import java.util.UUID;

import javax.swing.JOptionPane;

import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.UUIDAttribute;

public class GeneratedProblem extends Grouping {

	public static final String GENERATE_NEW_PROBLEM = "Gernerate New Problem",
			UUID_STR = "uuid", VIEW_PROBLEM_FORMULA = "View Problem Formula",
			DIFFICULTY = "difficulty";

	public GeneratedProblem(MathObjectContainer p, UUID genID, Grouping contents) {
		super(p);

		// the attributes of the previous grouping shouldn't need to be
		// cloned as the grouping will likely get destroyed after the
		// creation of this object. However, they are copied here to prevent
		// future problems
		removeAllAttributes();
		for (MathObjectAttribute mAtt : contents.getAttributes()) {
			addAttribute(mAtt.clone());
		}
		removeAllLists();
		for (ListAttribute list : getLists()) {
			addList(list.clone());
		}
		
		addGeneratedProblemAttibutes();
		addGeneratedProblemActions();
		getAttributeWithName(UUID_STR).setValue(genID);

		for (MathObject mObj : contents.getObjects()) {
			mObj.setParentContainer(getParentContainer());
			addObjectFromPage(mObj.clone());
		}
	}

	public GeneratedProblem(MathObjectContainer p) {
		super(p);
		addGeneratedProblemAttibutes();
		addGeneratedProblemActions();
	}

	public GeneratedProblem() {
		super();
		addGeneratedProblemAttibutes();
		addGeneratedProblemActions();
	}
	
	private void addGeneratedProblemAttibutes(){
		addAttribute(new UUIDAttribute(UUID_STR));
		getAttributeWithName(UUID_STR).setUserEditable(false);
		addAttribute(new IntegerAttribute(DIFFICULTY, false));
	}

	private void addGeneratedProblemActions(){
		// remove actions that are added in the Grouping constructor
		removeAction(MathObject.MAKE_INTO_PROBLEM);
		removeAction(STORE_IN_DATABASE);
		removeAction(BRING_TO_LEFT);
		removeAction(BRING_TO_RIGHT);
		removeAction(BRING_TO_TOP);
		removeAction(BRING_TO_BOTTOM);
		removeAction(Grouping.STRETCH_VERTICALLY);
		removeAction(Grouping.STRETCH_HORIZONTALLY);
		addAction(GENERATE_NEW_PROBLEM);
		addAction(VIEW_PROBLEM_FORMULA);
		// addAction(REMOVE_PROBLEM);
	}
	
	public ProblemGenerator getProblemGenerator() {
		return getParentContainer().getParentDoc().getGeneratorWithID(
				(UUID) getAttributeWithName(UUID_STR).getValue());
	}
	
	public int getDifficulty(){
		return ((IntegerAttribute)getAttributeWithName(DIFFICULTY)).getValue();
	}
	
	public void setDifficulty(int i){
		getAttributeWithName(DIFFICULTY).setValue(i);
	}

	/**
	 * Replaces this problems with a new one produced from the problem's
	 * generator.
	 * 
	 * @return
	 */
	public GeneratedProblem generateNewProblem() {
		GeneratedProblem newProb = getProblemGenerator().generateProblem(getDifficulty());
		newProb.setxPos(getxPos());
		newProb.setyPos(getyPos());
		getParentContainer().addObject(newProb);
		newProb.setParentContainer(getParentContainer());
		getParentContainer().removeObject(this);
		return newProb;
	}

	@Override
	public void addDefaultAttributes() {
	}

	@Override
	public void performSpecialObjectAction(String s) {
		if (s.equals(GENERATE_NEW_PROBLEM)) {
			getParentContainer().getParentDoc().getDocViewerPanel()
					.setFocusedObject(generateNewProblem());
		}
		if (s.equals(VIEW_PROBLEM_FORMULA)) {
			if ( getProblemGenerator().isUserEditable()){
			getParentContainer()
					.getParentDoc().getDocViewerPanel()
					.getNotebook().getNotebookPanel()
					.viewProblemGenrator(getProblemGenerator());
			}
			else{
				JOptionPane.showMessageDialog(null,
						"This formula that generated this probelm cannot be edited.\n" +
						"If you would like a different version of the problem use the\n" +
						"\"Generate New\" button.",
						"Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	public GeneratedProblem newInstance(){
		return new GeneratedProblem();
	}

	@Override
	public String getType() {
		return MathObject.GENERATED_PROBLEM;
	}

	public UUID getGeneratorID() {
		return (UUID) getAttributeWithName(UUID_STR).getValue();
	}

}
