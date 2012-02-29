package expression;

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
			VarList varList = new VarList();
			varList.setIdentifierValue("x", new Number(5));
			varList.setIdentifierValue("y", new Number(2));
			Node node = Node.parseNode("3x*3*x");
			System.out.println(node.smartNumericSimplify().standardFormat().toStringRepresentation());
			Node node1 = Node.parseNode("2(x+5)");
			System.out.println(node.equals(node1));
			System.out.println(node1.smartNumericSimplify().toStringRepresentation());
			Node node2 = Node.parseNode("2");
			System.out.println(node.multiplyByNode(node2).toStringRepresentation());
			System.out.println(Expression.staggerAddition(node.splitOnAddition()).toStringRepresentation());
			System.out.println(varList.evaluate(node));
			node = Node.parseNode("3+5");
			System.out.println(node.numericSimplify().toStringRepresentation());
			System.out.println(node.numericSimplify().toStringRepresentation());
			Node n = Node.parseNode("1/a");
			n = n.replace("a", new Number(-10));
			System.out.println(n.toStringRepresentation());
			System.out.println(n.numericSimplify().toStringRepresentation());
			System.out.println(
					Node.parseNode("a(x+c)^2+b").replace("a", new Number(0)).toStringRepresentation());

			n = Node.parseNode("3+5+4");
			System.out.println( ((Expression)n).getChildren().size() );
		}catch (NodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}