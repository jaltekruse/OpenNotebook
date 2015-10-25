package doc.expression_generators;

import doc.attributes.AttributeException;
import doc.attributes.ListAttribute;
import doc.attributes.UUIDAttribute;
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
			newProblem.getListWithName(GeneratedProblem.GEN_LIST).getValues().clear();
			((ListAttribute<UUIDAttribute>)newProblem.getListWithName(GeneratedProblem.GEN_LIST))
					.addValueWithString(getProblemID().toString());
			Node[] n = generateExpression(difficulty);
			expressionObj.setExpression(n[0].toStringRepresentation());
			expressionObj.addCorrectAnswer(n[1].toStringRepresentation());
		} catch (AttributeException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		}
		getProblemHoldingDocument().getDocViewerPanel().drawObjectInBackground(expressionObj);
		expressionObj.setParentContainer(newProblem.getParentContainer());
		newProblem.addObjectFromPage(expressionObj);
		return newProblem;
	}
	
	protected abstract Node[] generateExpression(int difficulty) throws NodeException;
	
	protected abstract void setAttributes();
}