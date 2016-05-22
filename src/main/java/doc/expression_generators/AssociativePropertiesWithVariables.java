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

public class AssociativePropertiesWithVariables extends ExpressionGenerator {

	@Override
	protected Node[] generateExpression(int difficulty) throws NodeException {
		Node[] n = new Node[2];
		if ( ExUtil.randomBoolean())
		{// half of the time create a problem to test the associative property of addition

			if ( difficulty == ProblemGenerator.EASY) {
				double[] numPair = ExUtil.pairOfCleanAddingNumbers(100);
				
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
						new Number(numPair[0]), ExUtil.randomTerm(1, ExUtil.randomVarName(), 3, 12),
						new Number(numPair[1]));
			}
			else if ( difficulty == ProblemGenerator.MEDIUM){
				double[] numPair = ExUtil.pairOfCleanAddingNumbers(100);
				double[] numPair2 = ExUtil.pairOfCleanAddingNumbers(200);
				
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
						new Number(numPair[0]), new Number(numPair2[1]),
						ExUtil.randomTerm(1, ExUtil.randomVarName(), 3, 12),
						new Number(numPair[1]), new Number(numPair2[0]));
			}
			else if ( difficulty == ProblemGenerator.HARD){
				double[] numPair = ExUtil.pairOfCleanAddingNumbers(400);
				double[] numPair2 = ExUtil.pairOfCleanAddingNumbers(200);
				Vector<String> varNames = ExUtil.randomUniqueVarNames(2);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
						new Number(numPair[0]), ExUtil.randomTerm(1, varNames.get(0), 2, 12),
						new Number(numPair2[1]),
						ExUtil.randomTerm(1, varNames.get(1), 3, 12),
						new Number(numPair[1]), new Number(numPair2[0]));
			}
		}
		else{// otherwise create a problem testing the associative property of multiplication
			if ( difficulty == ProblemGenerator.EASY) {
				double[] numPair = ExUtil.pairOfCleanFactors(20);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), 
						new Number(numPair[0]), ExUtil.randomTerm(1, ExUtil.randomVarName(), 3, 12),
						new Number(numPair[1]));
			}
			else if ( difficulty == ProblemGenerator.MEDIUM){
				double[] numPair = ExUtil.pairOfCleanFactors(30);
				Vector<String> varNames = ExUtil.randomUniqueVarNames(2);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), 
						new Number(numPair[0]), ExUtil.randomTerm(1, varNames.get(0), 3, 9),
						ExUtil.randomTerm(1, varNames.get(1), 3, 12),
						new Number(numPair[1]));

			}
			else if ( difficulty == ProblemGenerator.HARD){
				double[] numPair = ExUtil.pairOfCleanFactors(40);
				Vector<String> varNames = ExUtil.randomUniqueVarNames(2);
				n[0] = ExUtil.randomlyStaggerOperation(new Operator.Multiplication(), 
						new Number(numPair[0]), ExUtil.randomTerm(1, varNames.get(0), 3, 9),
						ExUtil.randomTerm(1, varNames.get(1), 3, 12),
						new Number(numPair[1]));;
			}
		}
		n[0] = ExUtil.randomlyAddParenthesis((Expression)n[0], 0, 3);
		n[1] = n[0].smartNumericSimplify().standardFormat();
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("Associative Property Problem With Variables");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify. Use the associative and commutative properties of addition and multiplication" +
				" to reorder and regroup the numbers into easier calculations.");
		try {
			this.setAttributeValue(UUID_STR, new UUID(4190756404850524305L, 3560661854204360419L));
			getTags().addValueWithString("Arithmetic");
			getTags().addValueWithString("Addition");
			getTags().addValueWithString("Multiplication");
			getTags().addValueWithString("Integers");
			getTags().addValueWithString("Variables");
			getTags().addValueWithString("Mental Math");
		} catch (AttributeException e) {
			// should not be thrown
			throw new RuntimeException("error that should not happen in BaiscArithmaticExpression");
		}
		setDate(new Date(2,1,2011));
	}

}
