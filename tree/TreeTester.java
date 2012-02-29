/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

public class TreeTester {

	public static void main(String[] args){
		
		String expression = "(/)";
		ExpressionParser parser = new ExpressionParser();
		Expression parsedExpression = null;
		if (args.length > 0){
			if (args.length == 1){
				try {
					System.out.println(args[0]);
					parsedExpression = parser.ParseExpression(args[0]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
//				System.out.println("please give one argument, an expression to be evaluated");
			}
		}
		else{
			try {
//				System.out.println("fed in: " + expression);
				parsedExpression = parser.ParseExpression(expression);
//				System.out.println("parsed:" + parsedExpression);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
//			System.out.println(parsedExpression.eval());
			UnaryExpression sqrt = new UnaryExpression(Operator.SQRT);
			Fraction f1 = new Fraction(3, 1);
			Fraction f2 = new Fraction(5, 1);
			BinExpression binEx = new BinExpression(Operator.ADD);
			binEx.setLeftChild(f1);
			binEx.setRightChild(f2);
			sqrt.setChild(binEx);
			System.out.println(sqrt.eval());
		} catch (EvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
