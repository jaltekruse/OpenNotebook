package doc.expression_generators;

import java.util.UUID;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.ProblemGenerator;
import expression.Expression;
import expression.Node;
import expression.NodeException;
import expression.Operator;
import expression.Number;

public class BasicDistributiveProperty extends ExpressionGenerator {

	@Override
	protected Node[] generateExpression(int difficulty) throws NodeException {
		Node[] n = new Node[2];
		if ( difficulty == ProblemGenerator.EASY) {
			double bigNum = ExUtil.randomInt(2, 10, true) * 10, smallNum = ExUtil.randomInt(1, 5, true),
					multiplier = ExUtil.randomInt(2, 9, true);
			if ( ExUtil.randomBoolean()){
				n[0] = new Expression(new Operator.Addition(), new Number(bigNum), new Number(smallNum));
			}
			else{
				n[0] = new Expression(new Operator.Subtraction(), new Number(bigNum), new Number(smallNum));
			}
			n[0].setDisplayParentheses(true);
			n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), n[0], new Number(multiplier));
		}
		else if ( difficulty == ProblemGenerator.MEDIUM) {
			double multiplier = ExUtil.randomInt(2, 9, true);
			n[0] = ExUtil.randomLinearExpression( ExUtil.randomVarName(), 1, 8);
			n[0].setDisplayParentheses(true);
			n[0].setDisplayParentheses(true);
			n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), n[0], new Number(multiplier));
		}
		else{
			double multiplier = ExUtil.randomInt(2, 14, true);
			String varName = ExUtil.randomVarName();
			n[0] = ExUtil.randomLinearExpression(varName, 1, 12);
			Node newNode = null;
			if ( ExUtil.randomBoolean()) newNode = new Number(ExUtil.randomInt(2, 15, true));
			else newNode = ExUtil.randomTerm(1, varName, 2, 9);
			
			if ( ExUtil.randomBoolean()){
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), n[0], newNode);
			}
			else{
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Subtraction(), n[0], newNode);
			}
			
			n[0].setDisplayParentheses(true);
			n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), n[0], new Number(multiplier));
		}
		n[1] = n[0].cloneNode().smartNumericSimplify().standardFormat();
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("Basic Distribution");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify.");
		try {
			setAttributeValue(UUID_STR, new UUID(41209481357524305L, 3562391204983210319L));
			addTags("Distribution", "multiplication", "linear factor", "simplify");
		} catch (AttributeException e) {
			// should not be thrown
			throw new RuntimeException(e);
		}
		setDate(new Date(2,1,2011));

	}

}
