package doc.expression_generators;

import java.util.UUID;
import java.util.Vector;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.ProblemGenerator;
import expression.Expression;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Operator;

public class AdditionPropertyOfEquality extends ExpressionGenerator{


	@Override
	protected Node[] generateExpression(int difficulty) throws NodeException {
		Node[] n = new Node[2];

		if ( difficulty == ProblemGenerator.EASY) {
			double[] numPair = ExUtil.pairOfCleanAddingNumbers(100);
			if ( ExUtil.randomBoolean()){
				numPair[1] = -1 * numPair[1];
			}
			n[1] = new Expression(new Operator.Equals(), ExUtil.randomVar(), new Number(numPair[0]));
			n[0] = ((Expression)n[1]).applyOpToBothSides(new Operator.Addition(), new Number(numPair[1]), true);
			n[0] = n[0].smartNumericSimplify();
		}
		else if ( difficulty == ProblemGenerator.MEDIUM){
			n[1] = new Expression(new Operator.Equals(), ExUtil.randomVar(),
					new Number(ExUtil.randomInt(5, 30, true)));
			n[0] = ((Expression)n[1]).applyOpToBothSides(new Operator.Addition(),
					new Number(ExUtil.randomInt(5, 30, true)), true);
			n[0] = n[0].smartNumericSimplify();
		}
		else if ( difficulty == ProblemGenerator.HARD){
			n[1] = new Expression(new Operator.Equals(), ExUtil.randomVar(),
					new Number(ExUtil.randomInt(5, 30, true)));
			Expression sumNode = ExUtil.randomAdditionOrSubtraction(5, 30);
			Node sum = sumNode.numericSimplify();
			n[0] = n[1].cloneNode();
			Node newLeft = null, newRight = null;
			if( ExUtil.randomBoolean()){
				newLeft = ((Expression)n[0]).getChild(0).addNodeToExpression(sumNode);
				newRight = ((Expression)n[0]).getChild(1).addNodeToExpression(sum);
			}
			else{
				newLeft = ((Expression)n[0]).getChild(0).addNodeToExpression(sum);
				newRight = ((Expression)n[0]).getChild(1).addNodeToExpression(sumNode);
			}
			n[0] = new Expression(new Operator.Equals(), newLeft, newRight);
		}
		n[0] = ExUtil.flipSides( (Expression) n[0]);
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("One Step Equations, Addition/Subtraction");
		setAuthor("Open Notebook Staff");
		setDirections("Solve for the unknown variable.");
		try {
			setAttributeValue(UUID_STR, new UUID(4190756403957524305L, 3562395472104360419L));
			addTags("Solve", "Addition", "Subtraction", "Equation");
		} catch (AttributeException e) {
			// should not be thrown
			throw new RuntimeException(e);
		}
		setDate(new Date(2,1,2011));
	}

}
