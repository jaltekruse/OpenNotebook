package doc.expression_generators;

import java.util.UUID;
import java.util.Vector;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import doc.mathobjects.ProblemGenerator;
import expression.Expression;
import expression.Identifier;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Operator;

public class PropertyOfOppositiesWithVariables extends ExpressionGenerator {

	@Override
	protected Node[] generateExpression(int difficulty) throws NodeException {
		Node n[] = new Node[2];
		if ( difficulty == ProblemGenerator.EASY) {
			double num = ExUtil.randomInt(2, 9, true);
			n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
					new Number(num), ExUtil.randomTerm(1, ExUtil.randomVarName(), 3, 12),
					new Number(-1 * num), new Number(ExUtil.randomInt(2, 9, true)));
		}
		else if ( difficulty == ProblemGenerator.MEDIUM){
			double[] numPair = ExUtil.pairOfCleanAddingNumbers(100);
			Node var = ExUtil.randomTerm(1, ExUtil.randomVarName(), 3, 12);
			
			n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
					new Number(ExUtil.randomInt(2, 9, true)), var,
					new Number(ExUtil.randomInt(2, 9, true)), new Expression(new Operator.Negation(), var));
		}
		else if ( difficulty == ProblemGenerator.HARD){
			double[] numPair2 = ExUtil.pairOfCleanAddingNumbers(200);
			Vector<String> varNames = ExUtil.randomUniqueVarNames(2);
			Node var = ExUtil.randomTerm(1, ExUtil.randomVarName(), 3, 12);
			n[0] = ExUtil.randomlyStaggerOperation(new Operator.Addition(), 
					new Number(ExUtil.randomInt(2, 9, true)), ExUtil.randomTerm(1, varNames.get(1), 3, 12),
					new Number(numPair2[0]), var, new Number(numPair2[0]), 
					new Expression(new Operator.Negation(), var) );
		}
		n[0] = ExUtil.randomlyAddParenthesis( (Expression) n[0], 0, 3);
		n[1] = n[0].collectLikeTerms().smartNumericSimplify().standardFormat();
		return n;
	}

	@Override
	protected void setAttributes() {
		setName("Property of Opposites");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify.");
		try {
			this.setAttributeValue(UUID_STR, new UUID(282395472492378295L, 1905635205832531290L));
			getTags().addValueWithString("Arithmetic");
			getTags().addValueWithString("Addition");
			getTags().addValueWithString("Integers");
			getTags().addValueWithString("Negative Numbers");
			getTags().addValueWithString("Opposities");
			getTags().addValueWithString("Cancel terms");
			getTags().addValueWithString("Variables");
		} catch (AttributeException e) {
			// should not be thrown
			System.out.println("error that should not happen in BaiscArithmaticExpression");
		}
		setDate(new Date(2,1,2011));
	}

}
