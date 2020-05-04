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
import java.util.ArrayList;
import java.util.List;
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

		// create directory:
		try {
			directory.mkdirs();
		} catch (SecurityException e) {
			throw new IOException(String.format("File directory=%s could not be created", directory));
		}
		// test if directory:
		if (!directory.isDirectory())
			throw new IOException(String.format("File directory=%s is no directory", directory));

		this.directory = directory;

		// init XStream:
		if (xstream == null) {
			xstream = new XStream();

			// from https://stackoverflow.com/a/45152845 :
			XStream.setupDefaultSecurity(xstream); // to be removed after 1.5
			xstream.allowTypesByWildcard(new String[] { "voruti.priorit.**" });

			xstream.alias("item", Item.class);
		}

		// checking/validating files:
		loadFromFile();

		LOGGER.exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * @return the directory
	 */
	public File getDirectory() {
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
		return addItem(item, false);
	}

	/**
	 * Saves/Adds an {@link Item} to the priority list.
	 * 
	 * @param item                 the {@link Item} to add
	 * @param ignoreAlreadyPresent {@code true}, to overwrite already present item
	 *                             files
	 * @return {@code true}, if the {@link Item} was successfully saved;
	 *         {@code false} otherwise
	 */
	public boolean addItem(Item item, boolean ignoreAlreadyPresent) {
		final String METHOD_NAME = "addItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		boolean successful = false;

		if (ignoreAlreadyPresent || !getFileToItem(item).exists()) {
			successful = saveToFile(item);
			if (!successful)
				LOGGER.log(Level.WARNING, "Error at saving item={0} to file", item);
		} else {
			LOGGER.log(Level.WARNING, "item={0} is already in list", item);
		}

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, successful);
		return successful;
	}

	/**
	 * @return all saved {@link Item items}; sorted by priority (most important
	 *         {@link Item} first); is empty if item can not be loaded
	 */
	public List<Item> getAllItems() {
		final String METHOD_NAME = "getItems";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		List<Item> items = new ArrayList<>();
		try {
			items = loadFromFile();
			LOGGER.log(Level.FINE, "Successfully obtained all items from the files");
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Item list can not be obtained");
			e.printStackTrace();
		}

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, items);
		return items;
	}

	/**
	 * @return the next/most important {@link Item} on the priority list;
	 *         {@code null} if the list is empty
	 */
	public Item getNextItem() {
		final String METHOD_NAME = "getNextItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		List<Item> items = getAllItems();

		Item item;
		if (!items.isEmpty())
			item = items.get(0);
		else
			item = null;

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, item);
		return item;
	}

	/**
	 * Search {@link Item items} by {@code text} in uName, title, text and category.
	 * 
	 * @param text the keyword or regEx to search for
	 * @return the wanted {@link Item items} as {@link List}; if no {@link Item} is
	 *         found, the {@link List} is empty
	 */
	public List<Item> searchItem(String text) {
		final String METHOD_NAME = "searchItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, text);

		List<Item> foundItems = getAllItems().stream()
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
				.collect(Collectors.toList());
		LOGGER.log(Level.FINE, "Searching for text={0} found foundItems={1}", new Object[] { text, foundItems });

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, foundItems);
		return foundItems;
	}

	/**
	 * Updates already present {@link Item} (identified by uName) with the new
	 * information from {@link Item item}. The {@link Item item} will be
	 * {@link #addItem(Item) added/saved} if it was not before.
	 * 
	 * @param item the {@link Item} to update or add
	 * @return {@code true}, if the update or adding was successful, {@code false}
	 *         otherwise
	 */
	public boolean updateItem(Item item) {
		final String METHOD_NAME = "updateItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		LOGGER.log(Level.FINE, "Adding new item");
		boolean successful = addItem(item, true);
		if (successful)
			LOGGER.log(Level.FINE, "Successfully updated item={0}", item);
		else
			LOGGER.log(Level.WARNING, "Error on updating item={0}", item);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, successful);
		return successful;
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
				fileOutputStream = new FileOutputStream(getFileToItem(item));
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
	 * Loads {@link Item items} from all files in {@link #directory} and returns
	 * them as sorted {@link List}.
	 * 
	 * @return all {@link Item items} sorted in a {@link List}; the {@link List} is
	 *         empty, if no items are found
	 * @throws IOException if one occurs while searching and opening the item files
	 *                     or not all files are successfully loaded
	 * 
	 * @see #ITEM_FILE_ENDING
	 */
	private List<Item> loadFromFile() throws IOException {
		final String METHOD_NAME = "loadFromFile";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		boolean successful = false;

		final List<Item> items = new ArrayList<>();

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
						if (item != null && items.add(item)) {
							LOGGER.log(Level.FINE, "Loaded item={0} from file", item);
						} else {
							LOGGER.log(Level.WARNING,
									"Can not load item! Converting fileInput={0} to XML returns item={1}",
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

		if (successful)
			LOGGER.log(Level.FINE, "Successfully loaded all items from directory={0}", directory);
		else
			throw new IOException(String.format("Error on loading items from directory=%s", directory));

		items.sort(null);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, items);
		return items;
	}

	/**
	 * Generates a {@link File} in which the {@link Item item} is saved.
	 * 
	 * @param item the {@link Item} to generate a {@link File} for
	 * @return the generated {@link File}
	 * 
	 * @see #ITEM_FILE_ENDING
	 */
	private File getFileToItem(Item item) {
		final String METHOD_NAME = "getFileToItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME, item);

		File file = new File(directory.getPath() + File.separator + item.getuName() + ITEM_FILE_ENDING);

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, file);
		return file;
	}

	/**
	 * @return all categories which are used in any {@link Item items}
	 */
	public List<String> getAllCategories() {
		final String METHOD_NAME = "getFileToItem";
		LOGGER.entering(CLASS_NAME, METHOD_NAME);

		List<String> categories = getAllItems().stream()
				.map(i -> i.getCategories())
				.flatMap(cl -> cl.stream())
				.distinct()
				.collect(Collectors.toList());

		LOGGER.exiting(CLASS_NAME, METHOD_NAME, categories);
		return categories;
	}

}
