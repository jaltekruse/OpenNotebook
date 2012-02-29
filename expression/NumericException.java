package expression;

/**
 * Instances of this class represent errors in numeric calculations.
 * 
 * @author Killian
 *
 */
public class NumericException extends RuntimeException {

	private static final long serialVersionUID = -4503246625847841542L;

	public NumericException() {
		
	}

	public NumericException(String message) {
		super(message);
	}

	public NumericException(Throwable cause) {
		super(cause);
	}

	public NumericException(String message, Throwable cause) {
		super(message, cause);
	}

}
