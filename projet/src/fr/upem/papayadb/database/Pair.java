package fr.upem.papayadb.database;

/**
 * A class used to store a pair of items<br>
 * This class is thread-safe.
 * @author jason
 *
 * @param <T1> The type of the first value of the pair
 * @param <T2> The type of the second value of the pair
 */
public class Pair<T1, T2> {
	private final T1 value1;
	private final T2 value2;
	
	public Pair(T1 v1, T2 v2){
		value1 = v1;
		value2 = v2;
	}
	
	public T1 getV1(){
		return value1;
	}
	
	public T2 getV2(){
		return value2;
	}
}
