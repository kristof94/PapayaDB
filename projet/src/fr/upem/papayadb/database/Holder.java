package fr.upem.papayadb.database;

/**
 * Class storing an object<br>
 * It is used in order to modify primitive variables.<br>
 * This class is thread-safe;
 * @author jason
 *
 * @param <T> The type of the object to be used in the Holder
 */
public class Holder<T> {
	private volatile T value;
	
	/**
	 * Creates a value container
	 * @param value The object to be contained
	 */
	public Holder(T value){
		this.value = value;
	}
	
	/**
	 * Retrieves the object contained
	 * @return The object contained in the Holder
	 */
	public T getValue(){
		return value;
	}
	
	/**
	 * Changes the object contained
	 * @param value The new object to be contained
	 */
	public void setValue(T value){
		this.value = value;
	}
}
