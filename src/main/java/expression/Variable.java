package expression;

public class Variable extends Identifier {

	private static int indexCount = 0;
	private int index;
	
	public Variable() throws NodeException {
		this("x");
	}
	
	public Variable(String identifier) throws NodeException {
		this(identifier, nextIndex());
	}
	
	private Variable(String identifier, int index) throws NodeException {
		super(identifier);
		this.index = index;
	}
	
	private static int nextIndex() {
		indexCount++;
		return (indexCount - 1);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Variable))
			return false;
		return (this.index == ((Variable) other).index);
	}
	
	@Override
	public int hashCode() {
		return index;
	}
	
	@Override
	public Variable cloneNode() throws NodeException {
		Variable v = new Variable(getIdentifier(), index);
		v.setDisplayParentheses(displayParentheses());
		return v;
	}
}
