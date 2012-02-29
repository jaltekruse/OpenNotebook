package doc.attributes;
import java.util.UUID;

public class UUIDAttribute extends MathObjectAttribute<UUID> {

	public UUIDAttribute(String n) {
		super(n);
	}
	
	public UUIDAttribute(String n, boolean userEditable) {
		super(n, userEditable);
	}
	
	public UUIDAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
	}
	
	public UUIDAttribute(String n, UUID id) {
		super(n);
		setValue(id);
	}
	
	public UUIDAttribute(String n, UUID id, boolean userEditable) {
		super(n, userEditable);
		setValue(id);
	}
	
	public UUIDAttribute(String n, UUID id, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue(id);
	}
	
	@Override
	public String getType() {
		return UUID_ATTRIBUTE;
	}

	@Override
	public UUID readValueFromString(String s) throws AttributeException {
		try{
			return UUID.fromString(s);
		} catch ( Exception ex){
			throw new AttributeException("Error with format of UUID");
		}
	}

	@Override
	public void resetValue() {
		return;
	}
}
