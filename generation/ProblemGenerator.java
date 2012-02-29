package generation;

import java.util.Random;

import expression.Equation;
import expression.Function;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Variable;

public class ProblemGenerator {

	private double ceiling = 50;
	private double floor = -50;
	private boolean allowReals = false;
	
	private static final Function linearFunction;
	
	static {
		Variable a = null;
		Variable b = null;
		Node linear = null;
		try {
			a = new Variable();
			b = new Variable();
			linear = Node.parseNode("ax+b");
			linear = linear.replace("a", a);
			linear = linear.replace("b", b);
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		linearFunction = new Function(linear, a, b);
	}
	
	public Equation generateLinear() {
		Node line= null;
		try {
			line = linearFunction.apply(Number.get(random()), Number.get(random()));
			line = line.simplify();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Equation(line, Number.get(0));
	}

	private double random() {
		Random r = new Random();
		if (allowReals) {
			return (r.nextDouble() * (ceiling - floor) + floor);
		} else {
			return r.nextInt((int) (ceiling - floor + 1)) + (int) floor;
		}
	}
}
