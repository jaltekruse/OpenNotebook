package expression;

public class Equation {

	private Node lhs;
	private Node rhs;
	
	private static final String equals = " = ";
	
	public Node getLhs() {
		return lhs;
	}

	public void setLhs(Node lhs) {
		this.lhs = lhs;
	}

	public Node getRhs() {
		return rhs;
	}

	public void setRhs(Node rhs) {
		this.rhs = rhs;
	}

	public Equation(Node lhs, Node rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public String toString() {
		return lhs.toString() + equals + rhs.toString();
	}
	
	public Equation apply(Function f) throws NodeException {
		return new Equation(f.apply(lhs), f.apply(rhs));
	}
	
}
