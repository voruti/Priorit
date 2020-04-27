package voruti.priorit;

import java.io.File;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author voruti
 *
 */
public class PrioritManager {

	private static final String CLASS_NAME = PrioritManager.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * A directory to save all data in.
	 */
	private File directory;
	private TreeSet<Item> items;

	/**
	 * Creates a new manager and saves all data in {@code directory}.
	 * 
	 * @param directory the directory to save all data in
	 */
	public PrioritManager(File directory) {
		final String METHOD_NAME = "<init>";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, directory);

		this.directory = directory;
		items = new TreeSet<>();

		LOGGER.exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * @return the directory
	 */
	public File getDirectory() {
		final String METHOD_NAME = "getDirectory";
		LOGGER.exiting(CLASS_NAME, METHOD_NAME, directory);
		return directory;
	}

	/**
	 * Saves/Adds an {@link Item} to the priority list.
	 * 
	 * @param item the {@link Item} to add
	 * @return {@code true}, if the {@link Item} was successfully saved;
	 *         {@code false} otherwise
	 */
	public boolean addItem(Item item) {
		final String METHOD_NAME = "addItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		boolean successful = items.add(item.copy());
		// TODO save to file

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, successful);
		return successful;
	}

	/**
	 * @return all saved {@link Item items}; sorted by priority (most important
	 *         {@link Item} first)
	 */
	public List<Item> getItems() {
		final String METHOD_NAME = "getItems";

		List<Item> items = this.items.stream()
				.map(Item::copy)
				.collect(Collectors.toList());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, items);
		return items;
	}

	/**
	 * @return the next/most important {@link Item} on the priority list;
	 *         {@code null} if the list is empty
	 */
	public Item getNextItem() {
		final String METHOD_NAME = "getNextItem";

		Item item;
		if (!items.isEmpty()) {
			item = items.first()
					.copy();
		} else {
			item = null;
		}

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, item);
		return item;
	}

	/**
	 * Search {@link Item items} by {@code text} in uName, title and text.
	 * 
	 * @param text the keyword or regEx to search for
	 * @return the wanted {@link Item items} as {@link List}; if no {@link Item} is
	 *         found, the {@link List} is empty
	 */
	public List<Item> searchItem(String text) {
		final String METHOD_NAME = "searchItem(String)";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, text);

		List<Item> foundItems = items.stream()
				.filter(item -> item.getTitle()
						.matches(text)
						|| item.getText()
								.matches(text)
						|| item.getuName()
								.matches(text))
				.map(Item::copy)
				.collect(Collectors.toList());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, foundItems);
		return foundItems;
	}

	/**
	 * Search {@link Item items} by {@code category}.
	 * 
	 * @param category the {@link Category} to search for
	 * @return the wanted {@link Item items} as {@link List}; if no {@link Item} is
	 *         found, the {@link List} is empty
	 */
	public List<Item> searchItem(Category category) {
		final String METHOD_NAME = "searchItem(Category)";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, category);

		List<Item> foundItems = items.stream()
				.filter(item -> item.getCategory()
						.equals(category))
				.map(Item::copy)
				.collect(Collectors.toList());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, foundItems);
		return foundItems;
	}

	/**
	 * Updates already present {@link Item} (identified by uName) with the new
	 * information from {@link Item item}. The {@link Item item} will be
	 * {@link #addItem(Item) added/saved} if it was not before.
	 * 
	 * @param item the {@link Item} to update or add
	 */
	public void updateItem(Item item) {
		final String METHOD_NAME = "updateItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		items.remove(item);
		// TODO remove from file? maybe better to just do an update in #addItem(Item) ?
		addItem(item);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME);
	}

}
