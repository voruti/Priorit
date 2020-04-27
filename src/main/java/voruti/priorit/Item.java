package voruti.priorit;

import java.util.Date;

/**
 * @author voruti
 *
 */
public class Item implements Cloneable, Comparable<Item> {

	/**
	 * unique name
	 */
	private String uName;
	private String title;
	private String text;
	private Category category;
	private Date etaDate;
	private Priority priority;
	private boolean done;

	public Item() {
		this.title = "";
		this.text = "";
		this.category = new Category();
		long timestamp = System.currentTimeMillis() + 2592000000L; // 30 days
		this.etaDate = new Date(timestamp);
		this.uName = timestamp + "_" + (int) (Math.random() * 899 + 100);
		this.priority = Priority.VERY_LOW;
		this.done = false;
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
		this.uName = uName;
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
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uName == null) ? 0
				: uName.toLowerCase()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		if (uName == null) {
			if (other.uName != null)
				return false;
		} else if (!uName.equalsIgnoreCase(other.uName))
			return false;
		return true;
	}

	public Item copy() {
		Item item = new Item();
		item.uName = this.uName;
		item.category = this.category.copy();
		item.etaDate = (Date) this.etaDate.clone();
		item.priority = this.priority;
		item.text = this.text;
		item.title = this.title;
		item.done = this.done;

		return item;
	}

	@Override
	public String toString() {
		return String.format("Item [uName=%s, title=%s, text=%s, category=%s, etaDate=%s, priority=%s, done=%s]", uName,
				title, text, category, etaDate, priority, done);
	}

	@Override
	public int compareTo(Item i) {
		return this.etaDate.compareTo(i.etaDate);
	}
}
