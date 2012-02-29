package doc.expression_generators;

import doc.attributes.AttributeException;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GeneratedProblem;
import doc.mathobjects.MathObjectContainer;
import doc.mathobjects.ProblemGenerator;
import expression.Node;
import expression.NodeException;

public abstract class ExpressionGenerator extends ProblemGenerator implements Cloneable{
	
	public ExpressionGenerator(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
		setAttributes();
		setUserEditable(false);
	}

	public ExpressionGenerator(MathObjectContainer p){
		super(p);
		setAttributes();
		setUserEditable(false);
	}

	public ExpressionGenerator() {
		setAttributes();
		setUserEditable(false);
	}
	
	public GeneratedProblem generateProblem(int difficulty){
		GeneratedProblem newProblem = new GeneratedProblem();
		ExpressionObject expressionObj = new ExpressionObject();
		try {
			newProblem.setDifficulty(difficulty);
			newProblem.setAttributeValue(UUID_STR, this.getProblemID());
			expressionObj.setExpression(generateExpression(difficulty).toStringRepresentation());
		} catch (AttributeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getProblemHoldingDocument().getDocViewerPanel().drawObjectInBackgorund(expressionObj);
		expressionObj.setParentContainer(newProblem.getParentContainer());
		newProblem.addObjectFromPage(expressionObj);
		return newProblem;
	}
	
	protected abstract Node generateExpression(int difficulty);
	
	protected abstract void setAttributes();
}