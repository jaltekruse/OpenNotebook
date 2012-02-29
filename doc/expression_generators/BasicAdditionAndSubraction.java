package doc.expression_generators;

import java.util.UUID;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.ProblemGenerator;
import expression.Expression;
import expression.Node;

public class BasicAdditionAndSubraction extends ExpressionGenerator {

	@Override
	protected Node generateExpression(int difficulty) {
		String[] ops = { ExUtil.ADDITION, ExUtil.NEGATION, ExUtil.SUBTRACTION };
		String[] vars = {};
		int numOps;
		int maxAbsVal;
		int minGeneratedVal;
		int maxGeneratedVal;
		if ( difficulty == ProblemGenerator.EASY){
			numOps = 2;
			minGeneratedVal = 2;
			maxGeneratedVal = 9;
			maxAbsVal = 20;
		}
		else if ( difficulty == ProblemGenerator.MEDIUM){
			numOps = 4;
			minGeneratedVal = 2;
			maxGeneratedVal = 12;
			maxAbsVal = 60;
		}
		else {
			numOps = 4;
			minGeneratedVal = 2;
			maxGeneratedVal = 25;
			maxAbsVal = 100;
		}
		Node n = ExUtil.randomExpression(ops, vars, numOps, maxAbsVal, minGeneratedVal, 
				maxGeneratedVal, true, false, false, true);
//		n = ExUtil.randomlyAddParenthesis( (Expression) n, 0, 3);
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("Basic Addition and Subtraction");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify.");
		try {
			this.setAttributeValue(UUID_STR, new UUID(282048723434478295L, 3549720219560301290L));
			getTags().addValueWithString("Arithmetic");
			getTags().addValueWithString("Addition");
			getTags().addValueWithString("Integers");
			getTags().addValueWithString("Negative Numbers");
			getTags().addValueWithString("Subtraction");
			getTags().addValueWithString("Negation");
		} catch (AttributeException e) {
			// should not be thrown
			System.out.println("error that should not happen in BaiscArithmaticExpression");
		}
		setDate(new Date(2,1,2011));
	}
}
