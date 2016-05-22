package expression;

/** A generic class representing literal numbers.
 * Currently represented internally by a {@code double} value.
 * 
 * @author Killian Kvalvik
 */
public class Number extends Value implements Comparable<Number> {
	
	private double value;
	
	public Number(double value) {
		if (Double.isInfinite(value) || Double.isNaN(value))
			throw new NumericException("Invalid number format or operation");
		setValue(value);
	}
	
	public static Number get(double value) {
		return new Number(value);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Number))
			return false;
		return (getValue() == ((Number) other).getValue());
	}
	
	public boolean isInteger(){
		return (value % 1 == 0);
	}
	
	@Override
	public int hashCode() {
		return Double.valueOf(value).hashCode();
	}

	@Override
	public Number cloneNode() {
		Number n =  new Number(getValue());
		n.setDisplayParentheses(displayParentheses());
		return n;
	}

	@Override
	public String toStringRepresentation() {
		String result;
		if (isInteger()){
			result = (int) value + "";
		}
		else{
			result = getValue() + ""; // change this!!
		}
		if (displayParentheses()){
			return "(" + result + ")";
		}
		return result;
	}

	@Override
	public Node replace(Identifier x, Node n) {
		return this;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public Number add(Number o) {
		return new Number(getValue() + o.getValue());
	}
	
	public Number subtract(Number o) {
		return new Number(getValue() - o.getValue());
	}

	public Number multiply(Number o) {
		return new Number(getValue() * o.getValue());
	}
	
	public Number divide(Number o) {
		return new Number(getValue() / o.getValue());
	}

	public Number exponent(Number o) {
		return new Number(Math.pow(getValue(), o.getValue()));
	}

	public Number negate() {
		return new Number(- getValue());
	}
	
	public Number abs() {
		return new Number(Math.abs(getValue()));
	}
	
	public Number log() {
		return new Number(Math.log10(getValue()));
	}
	
	public Number ln() {
		return new Number(Math.log(getValue()));
	}
	
	public Number sin() {
		return new Number(Math.sin(getValue()));
	}
	
	public Number cos() {
		return new Number(Math.sin(getValue()));
	}
	
	public Number tan() {
		return new Number(Math.tan(getValue()));
	}
	
	public Number sqrt() {
		return new Number(Math.sqrt(getValue()));
	}
	
	public Number cbrt() {
		return new Number(Math.cbrt(getValue()));
	}
	
	public static Number root(Number radix, Number argument) {
		return new Number(Math.pow(radix.getValue(), 1 / argument.getValue()));
	}
	
	public static Number log(Number base, Number argument) {
		return new Number(Math.log(argument.getValue()) 
				/ Math.log(base.getValue()));
	}

	public static boolean isNumeric(char c) {
		return Character.isDigit(c) || c == '.';
	}

	public static Number parseNumber(String s) {
		try {
			return new Number(Double.parseDouble(s));
		} catch (NumberFormatException e) {
			throw new NumericException(e);
		}
	}

	public boolean isNegative() {
		return (value < 0);
	}

	@Override
	public int compareTo(Number arg0) {
		double diff = value - arg0.value;
		if (diff > 0)
			return 1;
		if (diff < 0)
			return -1;
		return 0;
	}
	
	@Override
	protected int standardCompare(Node other) {
		if (!(other instanceof Number)) {
			return -1;
		}
		return this.compareTo((Number) other);
	}

}
