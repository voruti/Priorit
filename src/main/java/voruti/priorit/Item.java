package voruti.priorit;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author voruti
 *
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
		final int maxLen = 5;
		return String.format("Item [uName=%s, title=%s, text=%s, categories=%s, etaDate=%s, priority=%s, done=%s]",
				uName, title, text.replace("\n", "\\n"), categories != null ? toString(categories, maxLen) : null,
				etaDate, priority, done);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
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
			int valueCompare = calculateValue(this) - calculateValue(i);
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

	public static int calculateValue(Item item) {
		final String METHOD_NAME = "calculateValue";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		int value = daysLeft(item.etaDate) * item.priority.getValue();

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, value);
		return value;
	}

	public static int daysLeft(Date date) {
		final String METHOD_NAME = "daysLeft";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, date);

		int daysBetween = (int) ChronoUnit.DAYS.between(LocalDate.now(),date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, daysBetween);
		return daysBetween;
	}

	public static void iLog() {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		LOGGER.addHandler(consoleHandler);
	}
}
