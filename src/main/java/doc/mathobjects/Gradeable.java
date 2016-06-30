package doc.mathobjects;

public interface Gradeable<T extends Gradeable> {

	public int[] grade(T answer);
}
