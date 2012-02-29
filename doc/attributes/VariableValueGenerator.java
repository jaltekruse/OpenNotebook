package doc.attributes;


public class VariableValueGenerator extends MathObjectAttribute {
	
	public VariableValueGenerator(String n) {
		super(n);
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		return null;
	}

	@Override
	public void resetValue() {
		setValue(null);
	}

	@Override
	public String getType() {
		return VAR_VAL_GENERATOR;
	}
}
