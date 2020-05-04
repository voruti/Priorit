package voruti.priorit;

/**
 * @author voruti
 *
 */
public enum Priority {

	VERY_LOW(5), LOW(4), MED(3), HIGH(2), VERY_HIGH(1);

	private final int value;

	Priority(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
