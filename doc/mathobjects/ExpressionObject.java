/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.mathobjects;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

import doc.PointInDocument;
import doc.attributes.AttributeException;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import expression.Expression;
import expression.Node;
import expression.NodeException;
import expression.Operator;
import expression.Operator.Multiplication;

public class ExpressionObject extends MathObject {

	public static String	COMBINE_LIKE_TERMS = "Combine like terms",
			SIMPLIFY = "Simplify",
			ADD_TO_BOTH_SIDES = "Add to both sides",
			SUBTRACT_FROM_BOTH_SIDES = "Subtract from both sides",
			MULTIPLY_BOTH_SIDES = "Multiply both sides",
			DIVIDE_BOTH_SIDES = "Divide both sides",
			SUB_IN_VALUE = "(S)ubstitute in value",
			MODIFY_EXPRESSION = "(M)odify Expression",
			MANUALLY_TYPE_STEP = "Manually (t)ype step",
			OTHER_OPERATIONS = "(O)ther operations",
			UNDO_STEP = "(U)ndo Step";

	public static String		EXPRESSION = "expression",
			STEPS = "steps", ALWAYS_SHOW_STEPS = "always show steps",
			CORRECT_ANSWERS = "correct answers";


	public ExpressionObject(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
		setVerticallyResizable(false);
		setHorizontallyResizable(false);
		setExpressionActions();
	}

	public ExpressionObject(MathObjectContainer p){
		super(p);
		setVerticallyResizable(false);
		setHorizontallyResizable(false);
		setExpressionActions();
	}

	public ExpressionObject() {
		setVerticallyResizable(false);
		setHorizontallyResizable(false);
		setExpressionActions();
	}

	private void setExpressionActions(){
		removeAction(MAKE_SQUARE);
		addAction(MathObject.MAKE_INTO_PROBLEM);
		addStudentAction(COMBINE_LIKE_TERMS);
//		addStudentAction(SIMPLIFY);
		addStudentAction(ADD_TO_BOTH_SIDES);
		addStudentAction(SUBTRACT_FROM_BOTH_SIDES);
		addStudentAction(MULTIPLY_BOTH_SIDES);
		addStudentAction(DIVIDE_BOTH_SIDES);
		addStudentAction(SUB_IN_VALUE);
		addStudentAction(OTHER_OPERATIONS);
		addStudentAction(MODIFY_EXPRESSION);
		addStudentAction(MANUALLY_TYPE_STEP);
		addStudentAction(UNDO_STEP);
	}

	@Override
	protected void addDefaultAttributes() {
		addAttribute(new StringAttribute(EXPRESSION));
		addList(new ListAttribute<StringAttribute>(CORRECT_ANSWERS,
				new StringAttribute(""), 20, true, false));
		addList(new ListAttribute<StringAttribute>(STEPS, new StringAttribute("val"), false));
		addAttribute(new IntegerAttribute(FONT_SIZE, 1, 50));
		addAttribute(new ColorAttribute(FILL_COLOR));
		addAttribute(new BooleanAttribute(ALWAYS_SHOW_STEPS, true, true, false));
		getAttributeWithName(EXPRESSION).setValue("");
		getAttributeWithName(FONT_SIZE).setValue(12);
		getAttributeWithName(FILL_COLOR).setValue(null);
	}

	public ExpressionObject getObjectWithAnswer(){
		ExpressionObject o = (ExpressionObject) this.clone();
		try {
			if ( ! o.alswaysShowSteps()){
				o.getListWithName(STEPS).removeAll();
				o.setAttributeValue(ALWAYS_SHOW_STEPS, true);
			}
		} catch (AttributeException e) {
			// should not throw an error, just setting a string attribute
			e.printStackTrace();
		}
		return o;
	}

	public boolean setAttributeValue(String n, Object o) throws AttributeException{
		if (n.equals(EXPRESSION)){
			if ( ! o.equals(getAttributeWithName(EXPRESSION).getValue()))
			{// if the value of the expression is different
				// need to prevent loss of steps, as the text box for setting the
				// expression is selected when an expression object gains focus
				// and it tries to set the value when the input field loses focus
				// thus selecting and de-selecting an expression would always result in
				// a loss of the steps without this check
				getListWithName(STEPS).removeAll();
			}
			else
			{// the expression was attempting to be set, but it matched the previous one
				return false;
			}
		}
		getAttributeWithName(n).setValue(o);
		return true;
	}

	public boolean isStudentSelectable(){
		return true;
	}

	public boolean alswaysShowSteps(){
		return (Boolean) getAttributeWithName(ALWAYS_SHOW_STEPS).getValue();
	}

	public void performSpecialObjectAction(String s){
		// TODO - define an interface for the data that can be asked from the user
		// during one of the multi-step actions
		// TODO - make an implementation of this interface based on dialogs as are
		// hard coded here
		// This refactoring will allow for other possible UIs to interact with these
		// actions in the future
		if ( ((StringAttribute)getAttributeWithName(EXPRESSION)).getValue() == null || 
				((StringAttribute)getAttributeWithName(EXPRESSION)).getValue().equals("") ){
			JOptionPane.showMessageDialog(null,
					"There is no expression to work with, enter one in the box below.",
					"Warning",
					JOptionPane.WARNING_MESSAGE);
			setActionCancelled(true);
			return;
		}
		if (s.equals(SIMPLIFY)){
			try {
				getListWithName(STEPS).addValueWithString(Expression.parseNode(
						getLastStep()).smartNumericSimplify().toStringRepresentation());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Error with expression simplification", "Error",
						JOptionPane.WARNING_MESSAGE);
			}
		}
        if (s.equals(COMBINE_LIKE_TERMS)){
            try {
                getListWithName(STEPS).addValueWithString(Expression.parseNode(
                        getLastStep()).collectLikeTerms().simplify().toStringRepresentation());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error with combining like terms", "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
		if (s.equals(MAKE_INTO_PROBLEM)){
			VariableValueInsertionProblem newProblem = new VariableValueInsertionProblem(getParentContainer(), getxPos(),
					getyPos(), getWidth(), getHeight() );
			this.getParentContainer().getParentDoc().getDocViewerPanel().setFocusedObject(newProblem);
			newProblem.addObjectFromPage(this);
			getParentContainer().addObject(newProblem);
			getParentContainer().removeObject(this);
			return;
		}
		else if (s.equals(UNDO_STEP)){
			int size = getListWithName(STEPS).getValues().size();
			if ( size > 0){
				getListWithName(STEPS).getValues().remove(size - 1);
			}
			else{
				JOptionPane.showMessageDialog(null,
						"No steps to undo.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				setActionCancelled(true);
			}
			return;
		}
		else if (s.equals(SUB_IN_VALUE)){
			String variableStr = "";
			Node substitute = null;
            boolean foundVar;
			do{
				variableStr = (String)JOptionPane.showInputDialog(
						null, "Enter a variable to replace, variables are case sensitive 'a' is not the same as 'A'.", null,
						JOptionPane.PLAIN_MESSAGE, null, null, null);
				if ( variableStr == null){
					setActionCancelled(true);
					return;
				}
				if ( variableStr.length() != 1 || ! Character.isLetter(variableStr.charAt(0))){
					JOptionPane.showMessageDialog(null, "Need to enter a single letter.",
							"Warning", JOptionPane.WARNING_MESSAGE);

				}
                foundVar = Node.parseNode(getLastStep()).containsIdentifier(variableStr);
                if ( ! foundVar) {
                    JOptionPane.showMessageDialog(null, "Variable not found in expression.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
			} while(variableStr.length() != 1 || ! Character.isLetter(variableStr.charAt(0)) && ! foundVar);

			substitute = this.getParentPage().getParentDoc().getDocViewerPanel()
					.getNotebook().getNotebookPanel().getExpressionFromUser("Enter value or expression to substitute.");
			if ( substitute == null){
				setActionCancelled(true);
				return;
			}
			substitute.setDisplayParentheses(true);
			try {
				getListWithName(STEPS).addValueWithString(Node.parseNode(getLastStep()).replace(
						variableStr, substitute).toStringRepresentation());
			} catch (Exception e) {
				// this should not throw an error, as both the expression and the one being
				// Substituted have both been checked for validity
				JOptionPane.showMessageDialog(null, "Error with expression.",
						"Warning", JOptionPane.WARNING_MESSAGE);
				setActionCancelled(true);
			}
			return;
		}
		else if (s.equals(MODIFY_EXPRESSION)){

			Node newNode = this.getParentPage().getParentDoc().getDocViewerPanel()
					.getNotebook().getNotebookPanel().getExpressionFromUser("Modify the expression.",
							getLastStep());
			if ( newNode == null){
				setActionCancelled(true);
				return;
			}
			try {
				getListWithName(STEPS).addValueWithString(newNode.toStringRepresentation());
				return;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error with expression.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
		else if (s.equals(MANUALLY_TYPE_STEP)){

			Node newNode = this.getParentPage().getParentDoc().getDocViewerPanel()
					.getNotebook().getNotebookPanel().getExpressionFromUser("Type the entire next line.");
			if ( newNode == null){
				setActionCancelled(true);
				return;
			}
			try {
				getListWithName(STEPS).addValueWithString(newNode.toStringRepresentation());
				return;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Error with expression.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		}

		// all of the rest of the operations require an equals sign
		Node n = null;
		try{
			String expression = ((StringAttribute)getAttributeWithName(EXPRESSION)).getValue();
			if ( ! expression.equals("")){
				if ( getListWithName(STEPS).getValues().size() == 0){
					n = Node.parseNode( ((StringAttribute)getAttributeWithName(EXPRESSION)).getValue());
				}
				else{
					n = Node.parseNode(getLastStep());
				}
			}
		} catch (Exception e){
			JOptionPane.showMessageDialog(null,
					"Previous expression has an error.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			setActionCancelled(true);
			return;
		}
		if ( ! (n instanceof Expression && ((Expression)n).getOperator() instanceof Operator.Equals) ){
			//the expression does not have an equals sign
			JOptionPane.showMessageDialog(null,
					"Expression requires an equal sign for that operation.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			setActionCancelled(true);
			return;
		}
		Expression ex = (Expression) n;
		Operator o = null;
		if (s.equals(OTHER_OPERATIONS)){
			Object[] operations = {"sqrt", "sin", "cos", "tan"};
			String op = (String)JOptionPane.showInputDialog(
					null,
					"Choose an operation to apply to both sides",
					"Operation Selection",
					JOptionPane.PLAIN_MESSAGE,
					null,
					operations,
					"sqrt");
			if (op == null || op.equals("")){
				setActionCancelled(true);
				return;
			}
			if (op.equals("sqrt"))		o = new Operator.SquareRoot();
			else if (op.equals("sin"))	o = new Operator.Sine();
			else if (op.equals("cos"))	o = new Operator.Cosine();
			else if (op.equals("tan"))	o = new Operator.Tangent();

			Expression newLeft = new Expression(o);
			Vector<Node> left = new Vector<Node>();
			Node newChild = ex.getChild(0);
			if ( ! op.equals("sqrt") ){
				newChild.setDisplayParentheses(true);
			}
			left.add(newChild);
			newLeft.setChildren(left);

			Expression newRight = new Expression(o);
			Vector<Node> right = new Vector<Node>();
			newChild = ex.getChild(1);
			if ( ! op.equals("sqrt") ){
				newChild.setDisplayParentheses(true);
			}
			right.add(newChild);
			newRight.setChildren(right);

			Vector<Node> exChildren = new Vector<Node>();
			exChildren.add(newLeft);
			exChildren.add(newRight);
			ex.setChildren(exChildren);

			try {
				getListWithName(STEPS).addValueWithString(ex.toStringRepresentation());
			} catch (NodeException e) {
				JOptionPane.showMessageDialog(null,
						"Error with expression.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			} catch (AttributeException e) {
				JOptionPane.showMessageDialog(null,
						"Error with expression.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
			return;
		}
		try{
			if( s.equals(ADD_TO_BOTH_SIDES) || s.equals(SUBTRACT_FROM_BOTH_SIDES) ||
					s.equals(DIVIDE_BOTH_SIDES) || s.equals(MULTIPLY_BOTH_SIDES)){
				String message = "";
				if (s.equals(ADD_TO_BOTH_SIDES)){
					o = new Operator.Addition();
					message = "Add to both sides";
				}
				else if (s.equals(SUBTRACT_FROM_BOTH_SIDES)){
					o = new Operator.Subtraction();
					message = "Subtract from both sides";
				}
				else if (s.equals(DIVIDE_BOTH_SIDES)){
					o = new Operator.Division();
					message = "Divide both sides by";
				}
				else if (s.equals(MULTIPLY_BOTH_SIDES)){
					o = new Operator.Multiplication();
					message = "Multiply both sides by";
				}
				Node newNode = this.getParentPage().getParentDoc().getDocViewerPanel()
						.getNotebook().getNotebookPanel().getExpressionFromUser(message);
				if ( newNode == null){
					setActionCancelled(true);
					return;
				}
				ex = ex.applyOpToBothSides(o, newNode, true);
				try {
					getListWithName(STEPS).addValueWithString(ex.toStringRepresentation());
				} catch (AttributeException e) {
					JOptionPane.showMessageDialog(null,
							"Error with expression.",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		}catch (Exception e){
			JOptionPane.showMessageDialog(null,
					"Error with operation.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getLastStep(){
		if ( getListWithName(STEPS).getValues().size() == 0){
			return ((StringAttribute)getAttributeWithName(EXPRESSION)).getValue();
		}
		else{
			return (String) getListWithName(STEPS).getValue(
					getListWithName(STEPS).getValues().size() - 1).getValue();
		}
	}

	public Color getColor(){
		return ((ColorAttribute)getAttributeWithName(FILL_COLOR)).getValue();
	}

	public String getExpression(){
		return ((StringAttribute)getAttributeWithName(EXPRESSION)).getValue();
	}

	public Boolean isAlwaysShowingSteps(){
		return ((BooleanAttribute)getAttributeWithName(ALWAYS_SHOW_STEPS)).getValue();
	}


	public void setExpression(String s) throws AttributeException{
		setAttributeValue(EXPRESSION, s);
	}

	public Vector<StringAttribute> getCorrectAnswers(){
		return (Vector<StringAttribute>) getListWithName(ExpressionObject.CORRECT_ANSWERS).getValues();
	}

	public void addCorrectAnswer(String s) throws AttributeException{
		if ( ! getListWithName(CORRECT_ANSWERS).isEmpty()){
			StringAttribute lastVal = (StringAttribute) getListWithName(CORRECT_ANSWERS).getLastValue();
			if (lastVal != null && lastVal.getValue().equals("")){
				lastVal.setValue(s);
				return;
			}
		}
		getListWithName(CORRECT_ANSWERS).addValueWithString(s);
	}

	public void setFontSize(int fontSize) throws AttributeException {
		setAttributeValue(FONT_SIZE, fontSize);
	}

	public int getFontSize() {
		return ((IntegerAttribute)getAttributeWithName(FONT_SIZE)).getValue();
	}

	@Override
	public String getType() {
		return EXPRESSION_OBJ;
	}

	@Override
	public MathObject newInstance() {
		return new ExpressionObject();
	}
}
