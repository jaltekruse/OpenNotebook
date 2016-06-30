package expression;

import doc.expression_generators.*;

public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		NodeParser parser = new NodeParser();
//		Node p = parser.parseNode("4q+3");
//		Variable x = new Variable("x");
//		p = p.replace(new Identifier("q"), x);
//		Function f = new Function(p, x);
//		System.out.println(p);
//		Node a = f.apply(Number.get(5));
//		System.out.println(a.simplify());
		
//		ProblemGenerator g = new ProblemGenerator();
//		System.out.println(g.generateLinear());
		


		try {
			System.out.println(Expression.parseNode("(-4-5)").smartNumericSimplify().toStringRepresentation());
			Expression ex = new Expression(new Operator.Subtraction());
			ex = (Expression) ExUtil.addChild(ex, new Number(5));
		
			ex = (Expression) ExUtil.addChild(ex, new Expression(new Operator.Subtraction(), 3.0, 5.0));
			Expression.parseNode("5--3");
			System.out.println("Expression: " + ex.toStringRepresentation());
			System.out.println(ex.smartNumericSimplify().toStringRepresentation());
		}catch (NodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}