package voruti.priorit;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object to store information of (list) items.
 * 
 * @author voruti
 */
public class Item implements Comparable<Item> {

	private static final String CLASS_NAME = Item.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * unique name
	 */
	private String uName;
	private String title;
	private String text;
	private TreeSet<String> categories;
	private Date etaDate;
	private Priority priority;
	private boolean done;

	/**
	 * Initializes default item with empty or predefined values. The {@link #uName}
	 * is the current time stamp and a random number.
	 */
	public Item() {
		final String METHOD_NAME = "<init>";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		this.title = "";
		this.text = "";
		this.categories = new TreeSet<>();
		this.categories.add("none");
		long timestamp = System.currentTimeMillis() + 2592000000L; // 30 days
		this.etaDate = new Date(timestamp);
		this.uName = timestamp + "_" + (int) (Math.random() * 899 + 100);
		this.priority = Priority.VERY_LOW;
		this.done = false;

		LOGGER.exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * @return the uName
	 */
	public String getuName() {
		return uName;
	}

	/**
	 * @param uName the uName to set
	 */
	public void setuName(String uName) {
		this.uName = uName.toLowerCase();
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the categories
	 */
	public List<String> getCategories() {
		return new ArrayList<>(categories);
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<String> categories) {
		this.categories = new TreeSet<>(categories);
	}

	/**
	 * @return the etaDate
	 */
	public Date getEtaDate() {
		return etaDate;
	}

	/**
	 * @param etaDate the etaDate to set
	 */
	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}

	/**
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * @param done the done to set
	 */
	public void setDone(boolean done) {
		this.done = done;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		return Objects.equals(uName, other.uName);
	}

	/**
	 * Own version of {@code clone()} method. Deep copy.
	 * 
	 * @return a deep copy of the {@link Item}
	 */
	public Item copy() {
		final String METHOD_NAME = "copy";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		Item item = new Item();
		item.uName = this.uName;
		item.categories = new TreeSet<>(this.categories);
		item.etaDate = (Date) this.etaDate.clone();
		item.priority = this.priority;
		item.text = this.text;
		item.title = this.title;
		item.done = this.done;

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, item);
		return item;
	}

	@Override
	public String toString() {
		return String.format("%s%s (%-.10s)", done ? "Done: " : "", title, uName);
	}

	/**
	 * Note to self: This is used as equals-check (sometimes?).
	 */
	@Override
	public int compareTo(Item i) {
		final String METHOD_NAME = "compareTo";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, i);

		LOGGER.log(Level.FINEST,
				"Comparing these items:" + System.lineSeparator() + "{0}" + System.lineSeparator() + "{1}",
				new Object[] { this, i });

		int compareResult;

		if (this.equals(i)) {
			compareResult = 0;
		} else if (this.done && !i.done) {
			compareResult = 1;
		} else if (!this.done && i.done) {
			compareResult = -1;
		} else {
			int valueCompare = calculateValue() - i.calculateValue();
			if (valueCompare != 0) {
				compareResult = valueCompare;
			} else {
				int prioCompare = this.priority.getValue() - i.priority.getValue();
				if (prioCompare != 0) {
					compareResult = prioCompare;
				} else {
					compareResult = this.uName.compareTo(i.uName);
				}
			}
		}

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, compareResult);
		return compareResult;
	}

	/**
	 * Calculates the "value"/"worth" of @ {@code this} {@link Item}.
	 * 
	 * @return the value
	 */
	public int calculateValue() {
		final String METHOD_NAME = "calculateValue";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		int value = daysLeft(etaDate) * priority.getValue();

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, value);
		return value;
	}

	/**
	 * Checks if {@code this} {@link Item} is valid.
	 * 
	 * @return {@code true}, if the {@link Item} is valid
	 */
	public boolean isValid() {
		final String METHOD_NAME = "isValid";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		boolean isValid = (title != null && text != null && categories != null && !categories.isEmpty()
				&& etaDate != null && uName != null && priority != null);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, isValid);
		return isValid;
	}

	/**
	 * Calculates the days left to {@link Date date}.
	 * 
	 * @param date the {@link Date} to count days to
	 * @return the number of days until {@link Date date}
	 */
	public static int daysLeft(Date date) {
		final String METHOD_NAME = "daysLeft";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, date);

		int daysBetween = (int) ChronoUnit.DAYS.between(LocalDate.now(), date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, daysBetween);
		return daysBetween;
	}

}
