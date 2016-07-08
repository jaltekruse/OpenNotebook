package doc.attributes;

public class ExpressionAttribute extends MathObjectAttribute<String>{

	public ExpressionAttribute(String n) {
		super(n);
		setValue("");
	}

	public ExpressionAttribute(String n, boolean userEditable) {
		super(n, userEditable);
		setValue("");
	}

	public ExpressionAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue("");
	}

	public ExpressionAttribute(String n, String s) {
		super(n);
		setValue(s);
	}

	public ExpressionAttribute(String n, String s, boolean userEditable) {
		super(n, userEditable);
		setValue(s);
	}

	public ExpressionAttribute(String n, String s, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue(s);
	}

	@Override
	public String getType() {
		return EXPRESSION_ATTRIBUTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		return s;
	}

	@Override
	public void resetValue() {
		setValue("");
	}

	@Override
	public MathObjectAttribute clone() {
		MathObjectAttribute newString = new StringAttribute(getName(), this.getValue());
		copyRootManagedFields(newString);
		return newString;
	}
}
