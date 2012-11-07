package doc.mathobjects;

import java.util.UUID;

import javax.swing.JOptionPane;

import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.attributes.UUIDAttribute;

public class GeneratedProblem extends Grouping {

	public static final String GENERATE_NEW_PROBLEM = "Gernerate New Problem",
			UUID_STR = "uuid", VIEW_PROBLEM_FORMULA = "View Problem Formula",
			DIFFICULTY = "difficulty", GEN_LIST = "generator IDs";

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
		((ListAttribute<UUIDAttribute>)getListWithName(GEN_LIST)).getValue(0).setValue(genID);

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
		addList(new ListAttribute<UUIDAttribute>(GEN_LIST,
				new UUIDAttribute(""), 20, false, false));
		// the next line for adding UUID_STR as an attribute was commented out
		// it broke the JAVA based expression problem generators
		// I uncommented it, but I don't remember why I commented it out origionally
		addAttribute(new UUIDAttribute(UUID_STR, false, false));
		addAttribute(new IntegerAttribute(DIFFICULTY, false));
	}

	private void addGeneratedProblemActions(){
		// remove actions that are added in the Grouping constructor
		removeAction(MathObject.MAKE_INTO_PROBLEM);
		removeAction(Grouping.BRING_TO_LEFT);
		removeAction(Grouping.BRING_TO_TOP);
		removeAction(Grouping.BRING_TO_RIGHT);
		removeAction(Grouping.BRING_TO_BOTTOM);
		removeAction(Grouping.STRETCH_HORIZONTALLY);
		removeAction(Grouping.STRETCH_VERTICALLY);
		removeAction(DISTRIBUTE_VERTICALLY);
		removeAction(DISTRIBUTE_HORIZONTALLY);
		removeAction(ALIGN_GROUP_VERTICAL_CENTER);
		removeAction(ALIGN_GROUP_HORIZONTAL_CENTER);
		
		addAction(GENERATE_NEW_PROBLEM);
		addAction(VIEW_PROBLEM_FORMULA);
		// addAction(REMOVE_PROBLEM);
	}
	
	public ProblemGenerator getProblemGenerator() {
		return getParentContainer().getParentDoc().getGeneratorWithID(
				((ListAttribute<UUIDAttribute>)getListWithName(GEN_LIST)).getValue(0).getValue());
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
