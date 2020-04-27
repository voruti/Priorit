package voruti.priorit;

/**
 * @author voruti
 *
 */
public class Category {

	private String name;

	public Category() {
		this.name = "none";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0
				: name.toLowerCase()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Category))
			return false;
		Category other = (Category) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	public Category copy() {
		Category category = new Category();
		category.name = this.name;
		return category;
	}

	@Override
	public String toString() {
		return String.format("Category [name=%s]", name);
	}
}
