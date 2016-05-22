package doc.expression_generators;

import java.util.UUID;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.ProblemGenerator;
import expression.Expression;
import expression.Node;
import expression.NodeException;

public class BasicAdditionWithNegatives extends ExpressionGenerator {


	@Override
	protected Node[] generateExpression(int difficulty) throws NodeException {
		String[] ops = { ExUtil.ADDITION, ExUtil.NEGATION};
		String[] vars = {};
		int numOps;
		int maxAbsVal;
		int minGeneratedVal;
		int maxGeneratedVal;
		if ( difficulty == ProblemGenerator.EASY){
			numOps = 2;
			minGeneratedVal = 1;
			maxGeneratedVal = 9;
			maxAbsVal = 20;
		}
		else if ( difficulty == ProblemGenerator.MEDIUM){
			numOps = 4;
			minGeneratedVal = 1;
			maxGeneratedVal = 12;
			maxAbsVal = 60;
		}
		else {
			numOps = 4;
			minGeneratedVal = 2;
			maxGeneratedVal = 25;
			maxAbsVal = 100;
		}
		Node n[] = new Node[2];
		n[0] = ExUtil.randomExpression(ops, vars, numOps, maxAbsVal, minGeneratedVal, 
				maxGeneratedVal, true, false, false, true);
		n[0] = ExUtil.randomlyAddParenthesis( (Expression) n[0], 0, 3);
		n[1] = n[0].smartNumericSimplify().standardFormat();
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("Basic Addition with Negatives");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify.");
		try {
			this.setAttributeValue(UUID_STR, new UUID(28463475628230495L, 3540572839103937290L));
			getTags().addValueWithString("Arithmetic");
			getTags().addValueWithString("Addition");
			getTags().addValueWithString("Integers");
			getTags().addValueWithString("Negative Numbers");
			getTags().addValueWithString("Subtraction");
			getTags().addValueWithString("Negation");
		} catch (AttributeException e) {
			// should not be thrown
			throw new RuntimeException(e);
		}
		setDate(new Date(2,1,2011));
	}
}
