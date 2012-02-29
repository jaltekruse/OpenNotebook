package doc.expression_generators;

import java.util.UUID;

import doc.attributes.AttributeException;
import doc.attributes.Date;
import expression.Node;

public class BasicArithmeticExpression extends ExpressionGenerator{

	@Override
	protected Node generateExpression(int difficulty) {
		String[] ops = { ExUtil.ADDITION, ExUtil.SUBTRACTION, ExUtil.MULTIPLICATION, ExUtil.DIVISION };
		String[] vars = {};
		int numOps;
		int maxAbsVal;
		int minGeneratedVal;
		int maxGeneratedVal;
		if ( difficulty  <= 10){
			numOps = 2;
			minGeneratedVal = 1;
			maxGeneratedVal = 8;
			maxAbsVal = 20;
		}
		else if ( difficulty <= 20){
			numOps = 4;
			minGeneratedVal = 1;
			maxGeneratedVal = 10;
			maxAbsVal = 30;
		}
		else {
			numOps = 4;
			minGeneratedVal = 1;
			maxGeneratedVal = 12;
			maxAbsVal = 50;
		}
		return ExUtil.randomExpression(ops, vars, numOps, maxAbsVal, minGeneratedVal, 
				maxGeneratedVal, true, false, false, true);
	}

	@Override
	protected void setAttributes() {
		setName("Basic Arithmetic Expression");
		setAuthor("Open Notebook Staff");
		setDirections("Simplify.");
		try {
			this.setAttributeValue(UUID_STR, new UUID(4234031236056521345L, 2345748762121345701L));
			getTags().addValueWithString("Arithmetic");
			getTags().addValueWithString("Addition");
			getTags().addValueWithString("Subtraction");
			getTags().addValueWithString("Multiplication");
			getTags().addValueWithString("Dividison");
			getTags().addValueWithString("Integers");
		} catch (AttributeException e) {
			// should not be thrown
			System.out.println("error that should not happen in BaiscArithmaticExpression");
		}
		setDate(new Date(2,1,2011));
	}
	
}
