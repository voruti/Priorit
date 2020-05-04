package voruti.priorit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author voruti
 *
 */
class PrioritManagerTest {

	private static final String TEST_DIR = "testItems";

	private PrioritManager prioritManager;

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		deleteDirectory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		deleteDirectory();

		prioritManager = new PrioritManager(new File(TEST_DIR));
	}

	/**
	 * from https://www.baeldung.com/java-delete-directory
	 */
	private static void deleteDirectory() throws IOException {
		File dir = new File(TEST_DIR);
		if (dir.exists()) {
			Path path = dir.toPath();
			Files.walk(path)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		}
	}

	/**
	 * Test method for
	 * {@link voruti.priorit.PrioritManager#PrioritManager(java.io.File)}.
	 * 
	 * @throws IOException
	 */
	@Test
	void testPrioritManager() throws IOException {
		assertEquals(TEST_DIR, prioritManager.getDirectory()
				.toString());

		File dir = new File(TEST_DIR);

		assertTrue(dir.exists());
		assertTrue(dir.isDirectory());
	}

	/**
	 * Test method for
	 * {@link voruti.priorit.PrioritManager#addItem(voruti.priorit.Item, boolean)}.
	 */
	@Test
	void testAddItem() {
		Item itemRandom = new Item();

		boolean r1 = prioritManager.addItem(itemRandom);

		boolean r2 = prioritManager.addItem(itemRandom);

		boolean r3 = prioritManager.addItem(itemRandom, true);

		assertTrue(r1);
		assertEquals(itemRandom, prioritManager.getAllItems()
				.get(0));
		assertEquals(1, prioritManager.getAllItems()
				.size());

		assertFalse(r2);
		assertEquals(1, prioritManager.getAllItems()
				.size());

		assertTrue(r3);
		assertEquals(itemRandom, prioritManager.getAllItems()
				.get(0));
		assertEquals(1, prioritManager.getAllItems()
				.size());
	}

	/**
	 * Test method for {@link voruti.priorit.PrioritManager#getAllItems()}.
	 */
	@Test
	void testGetAllItems() {
		List<Item> l1 = prioritManager.getAllItems();

		prioritManager.addItem(new Item());

		List<Item> l2 = prioritManager.getAllItems();

		prioritManager.addItem(new Item());

		List<Item> l3 = prioritManager.getAllItems();

		assertTrue(l1.isEmpty());

		assertEquals(1, l2.size());

		assertEquals(2, l3.size());
	}

	/**
	 * Test method for {@link voruti.priorit.PrioritManager#getNextItem()}.
	 */
	@Test
	void testGetNextItem() {
		Item i1 = prioritManager.getNextItem();

		Item randomItem = new Item();
		prioritManager.addItem(randomItem);

		Item i2 = prioritManager.getNextItem();

		assertNull(i1);

		assertEquals(randomItem, i2);
	}

	/**
	 * Test method for
	 * {@link voruti.priorit.PrioritManager#searchItem(java.lang.String)}.
	 */
	@Test
	void testSearchItem() {
		Item i1 = new Item();
		i1.setuName("a name");
		Item i2 = new Item();
		i2.setTitle("name-ing things");
		Item i3 = new Item();
		i3.setText("Here a thing is listed.");
		prioritManager.addItem(i1);
		prioritManager.addItem(i2);
		prioritManager.addItem(i3);

		List<Item> l1 = prioritManager.searchItem("(.*)name(.*)");
		List<Item> l2 = prioritManager.searchItem("(.*)lol(.*)");
		List<Item> l3 = prioritManager.searchItem("(.*)thing(.*)");

		assertTrue(l1.contains(i1));
		assertTrue(l1.contains(i2));
		assertEquals(2, l1.size());
		assertTrue(l2.isEmpty());
		assertTrue(l3.contains(i2));
		assertTrue(l3.contains(i3));
		assertEquals(2, l3.size());
	}
}
