package expression;

/**
 * Represents an empty or incomplete node.
 * 
 * @author Killian Kvalvik
 *
 */
public class EmptyValue extends Value {

	@Override
	public boolean equals(Object other) {
		return ((other != null) && (other instanceof EmptyValue));
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toStringRepresentation() {
		return "?";
	}

	@Override
	public Node replace(Identifier identifier, Node node) {
		return this;
	}

	@Override
	public EmptyValue cloneNode() {
		EmptyValue e = new EmptyValue();
		e.setDisplayParentheses(displayParentheses());
		return e;
	}

	@Override
	protected int standardCompare(Node other) {
		return 0;
	}

}
