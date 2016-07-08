package doc.expression_generators;

import java.util.UUID;
import java.util.Vector;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.ProblemGenerator;
import expression.*;
import expression.Number;

public class BasicAssociativeProperty extends ExpressionGenerator {
	
	@Override
	protected Node[] generateExpression(int difficulty) throws NodeException {
		Node n[] = new Node[2];
		if ( ExUtil.randomBoolean())
		{// half of the time create a problem to test the associative property of addition

			if ( difficulty == ProblemGenerator.EASY) {
				double[] numPair = ExUtil.pairOfCleanAddingNumbers(100);
				double[] numPair2 = ExUtil.pairOfCleanAddingNumbers(50);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
						new Number(numPair[0]), new Number(numPair2[1]),
						new Number(numPair[1]), new Number(numPair2[0]));
			}
			else if ( difficulty == ProblemGenerator.MEDIUM){
				double[] numPair = ExUtil.pairOfCleanAddingNumbers(200);
				double[] numPair2 = ExUtil.pairOfCleanAddingNumbers(200);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
						new Number(numPair[0]), new Number(numPair2[1]),
						new Number(ExUtil.randomInt(3, 17, false)),
						new Number(numPair[1]), new Number(numPair2[0]));
			}
			else if ( difficulty == ProblemGenerator.HARD){
				double[] numPair = ExUtil.pairOfCleanAddingNumbers(400);
				double[] numPair2 = ExUtil.pairOfCleanAddingNumbers(200);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
						new Number(numPair[0]), new Number(numPair2[1]),
						new Number(ExUtil.randomInt(31, 79, false)),
						new Number(numPair[1]), new Number(numPair2[0]));
			}
		}
		else{// otherwise create a problem testing the associative property of multiplication
			if ( difficulty == ProblemGenerator.EASY) {
				double[] numPair = ExUtil.pairOfCleanFactors(30);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), 
						new Number(ExUtil.randomInt(2, 6, false)),
						new Number(numPair[0]),
						new Number(ExUtil.randomInt(3, 8, false)),
						new Number(numPair[1]) );
			}
			else if ( difficulty == ProblemGenerator.MEDIUM){
				double[] numPair = ExUtil.pairOfCleanFactors(30);
				double[] numPair2 = ExUtil.pairOfCleanFactors(20);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), 
						new Number(numPair[0]), new Number(numPair2[1]),
						new Number(ExUtil.randomInt(2, 5, false)),
						new Number(numPair[1]), new Number(numPair2[0]));

			}
			else if ( difficulty == ProblemGenerator.HARD){
				double[] numPair = ExUtil.pairOfCleanFactors(50);
				double[] numPair2 = ExUtil.pairOfCleanFactors(30);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), 
						new Number(numPair[0]), new Number(numPair2[1]),
						new Number(ExUtil.randomInt(2, 5, false)),
						new Number(numPair[1]), new Number(numPair2[0]));
			}
		}
		n[0] = ExUtil.randomlyAddParenthesis( (Expression) n[0], 0, 3);
		n[1] = n[0].smartNumericSimplify().standardFormat();
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("Basic Associative Property Problem");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify. Use the associative and commutative properties of addition and multiplication" +
				" to reorder and regroup the numbers into easier calculations.");
		try {
			this.setAttributeValue(UUID_STR, new UUID(4470592837206498305L, 1948027489360475701L));
			getTags().addValueWithString("Arithmetic");
			getTags().addValueWithString("Addition");
			getTags().addValueWithString("Multiplication");
			getTags().addValueWithString("Integers");
			getTags().addValueWithString("Mental Math");
		} catch (AttributeException e) {
			// should not be thrown
			throw new RuntimeException(e);
		}
		setDate(new Date(2,1,2011));
	}
}
