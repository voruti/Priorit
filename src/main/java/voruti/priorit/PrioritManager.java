package voruti.priorit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * @author voruti
 *
 */
public class PrioritManager {

	private static final String CLASS_NAME = PrioritManager.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	private static final String ITEM_FILE_ENDING = ".xml";

	private static XStream xstream = null;

	/**
	 * A directory to save all data in.
	 */
	private File directory;
	private TreeSet<Item> items;

	/**
	 * Creates a new manager and saves all data in {@code directory}.
	 * 
	 * @param directory the directory to save all data in
	 * @throws IOException if the {@link File} is no directory, the
	 *                     {@code directory} location cannot be created or the
	 *                     existing {@link Item items} can not be loaded
	 */
	public PrioritManager(File directory) throws IOException {
		final String METHOD_NAME = "<init>";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, directory);

		try {
			directory.mkdirs();
		} catch (SecurityException e) {
			throw new IOException(String.format("File directory=%s could not be created", directory));
		}
		if (!directory.isDirectory())
			throw new IOException(String.format("File directory=%s is no directory", directory));

		this.directory = directory;

		if (xstream == null) {
			xstream = new XStream();
			xstream.alias("item", Item.class);
		}

		this.items = new TreeSet<>();
		if (!loadFromFile())
			throw new IOException(String.format("Error on loading items from directory=%s", directory));

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

		boolean successful = false;

		if (items.add(item.copy())) {
			LOGGER.log(Level.INFO, "Successfully added item={0} to list", item);
			successful = saveToFile(item);
			if (!successful) {
				LOGGER.log(Level.WARNING, "Error at saving item={0} to file; reverting addition of item to list...",
						item);
				items.remove(item);
			}
		} else {
			LOGGER.log(Level.WARNING, "item={0} is already in list", item);
		}

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
								.matches(text)
						|| !item.getCategories()
								.stream()
								.filter(c -> c.matches(text))
								.collect(Collectors.toList())
								.isEmpty())
				.map(Item::copy)
				.collect(Collectors.toList());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, foundItems);
		return foundItems;
	}

	/**
	 * Updates already present {@link Item} (identified by uName) with the new
	 * information from {@link Item item}. The {@link Item item} will be
	 * {@link #addItem(Item) added/saved} if it was not before.<br>
	 * ! Warning: The item first gets removed, then added again. If the addition of
	 * the item fails, the item effectively is deleted (TODO fix this) !
	 * 
	 * @param item the {@link Item} to update or add
	 */
	public void updateItem(Item item) {
		final String METHOD_NAME = "updateItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		items.remove(item);
		addItem(item);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * Saves {@link Item item} to a file in {@link #directory}. Overwrites already
	 * existing files (useful for updates of items).
	 * 
	 * @param item the {@link Item} to save
	 * @return if the file was successfully saved
	 * 
	 * @see #ITEM_FILE_ENDING
	 */
	private boolean saveToFile(Item item) {
		final String METHOD_NAME = "saveToFile";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		boolean successful = false;

		String fileOutput = null;
		try {
			fileOutput = xstream.toXML(item);
		} catch (XStreamException e) {
			LOGGER.log(Level.WARNING, "Error at converting item={0} to XML", item);
			e.printStackTrace();
			successful = false;
		}
		if (fileOutput != null && !fileOutput.equals("")) {

			FileOutputStream fileOutputStream = null;
			PrintWriter printWriter = null;
			try {
				fileOutputStream = new FileOutputStream(
						directory.getPath() + File.separator + item.getuName() + ITEM_FILE_ENDING);
				printWriter = new PrintWriter(fileOutputStream);
				printWriter.println(fileOutput); // here the item is written to disk
				printWriter.flush();

				LOGGER.log(Level.FINE, "item={0} saved to file", item);
				successful = true;
			} catch (FileNotFoundException e) { // should be impossible
				e.printStackTrace();
				successful = false;
			} finally {
				if (printWriter != null)
					printWriter.close();
				if (fileOutputStream != null)
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
						successful = false;
					}
			}

		} else {
			LOGGER.log(Level.WARNING, "Error at converting item={0} to XML: fileOutput={1}",
					new Object[] { item, fileOutput });
			successful = false;
		}

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, successful);
		return successful;
	}

	/**
	 * Loads {@link Item items} from all files in {@link #directory} into
	 * {@link #items}.
	 * 
	 * @return if all {@link Item items} were loaded successfully
	 * 
	 * @see #ITEM_FILE_ENDING
	 */
	private boolean loadFromFile() {
		final String METHOD_NAME = "loadFromFile";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		boolean successful = false;

		try {
			successful = Files.walk(directory.toPath())
					.filter(p -> p.toString()
							.endsWith(ITEM_FILE_ENDING))
					.filter(path -> { // contains only failed paths afterwards
						FileInputStream fileInputStream = null;
						InputStreamReader inputStreamReader = null;
						BufferedReader bufferedReader = null;
						try {
							fileInputStream = new FileInputStream(path.toFile());
							inputStreamReader = new InputStreamReader(fileInputStream);
							bufferedReader = new BufferedReader(inputStreamReader);

							StringBuilder fileInput = new StringBuilder();
							String line;
							while ((line = bufferedReader.readLine()) != null) {
								fileInput.append(line)
										.append(System.lineSeparator());
							}

							Item item = (Item) xstream.fromXML(fileInput.toString());
							if (item != null) {
								items.add(item);
								LOGGER.log(Level.FINE, "Loaded item={0} from file", item);
							} else {
								LOGGER.log(Level.WARNING, "Converting fileInput={0} to XML returns item={1}",
										new Object[] { fileInput, item });
								return true;
							}
						} catch (IOException e) {
							e.printStackTrace();
							return true;
						} catch (XStreamException e) {
							LOGGER.log(Level.WARNING, "Converting fileInput to XML failed (path={0})", path);
							e.printStackTrace();
							return true;
						} finally {
							if (bufferedReader != null)
								try {
									bufferedReader.close();
								} catch (IOException e) {
									e.printStackTrace();
									return true;
								}
							if (inputStreamReader != null)
								try {
									inputStreamReader.close();
								} catch (IOException e) {
									e.printStackTrace();
									return true;
								}
							if (fileInputStream != null)
								try {
									fileInputStream.close();
								} catch (IOException e) {
									e.printStackTrace();
									return true;
								}
						}
						return false;
					})
					.collect(Collectors.toList())
					.isEmpty();
		} catch (IOException e) {
			e.printStackTrace();
			successful = false;
		}

		if (successful)
			LOGGER.log(Level.INFO, "Successfully loaded all items from directory={0}", directory);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, successful);
		return successful;
	}
}
