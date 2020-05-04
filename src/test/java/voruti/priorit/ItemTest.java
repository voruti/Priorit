package voruti.priorit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * @author voruti
 *
 */
class ItemTest {

	/**
	 * Test method for {@link voruti.priorit.Item#Item()}.
	 */
	@Test
	void testItem() {
		Item emptyItem = new Item();

		assertEquals("", emptyItem.getTitle());
		assertEquals("", emptyItem.getText());
		assertNotNull(emptyItem.getCategories());
		assertSame(1, emptyItem.getCategories()
				.size());
		assertEquals("none", emptyItem.getCategories()
				.get(0));
		assertSame(30, Item.daysLeft(emptyItem.getEtaDate()));
		assertTrue(emptyItem.getuName()
				.matches("[0-9]*_[0-9]{3}"));
		assertSame(Priority.VERY_LOW, emptyItem.getPriority());
		assertFalse(emptyItem.isDone());
	}

	/**
	 * Test method for {@link voruti.priorit.Item#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		Item i1 = new Item();
		i1.setuName("Helloo");
		i1.setTitle("Different");
		Item i2 = new Item();
		i2.setuName("Helloo");
		i2.setTitle("Title");

		boolean equals = i1.equals(i2);

		assertTrue(equals);
	}

	/**
	 * Test method for {@link voruti.priorit.Item#copy()}.
	 */
	@Test
	void testCopy() {
		Item original = new Item();

		Item copy = original.copy();

		original.setTitle("Change");
		original.setText("Dif");
		// ...

		assertEquals("", copy.getTitle());
		assertEquals("", copy.getText());
		// ...
	}

	/**
	 * Test method for {@link voruti.priorit.Item#compareTo(voruti.priorit.Item)}.
	 */
	@Test
	void testCompareTo() {
		Item i1 = new Item();
		i1.setuName("i1");
		Item i2 = new Item();
		i2.setDone(true);
		i2.setuName("i2");
		Item i3 = new Item();
		i3.setPriority(Priority.HIGH);
		i3.setuName("i3");
		Item i4 = new Item();
		i4.setuName("i4");

		Item i5 = new Item();
		i5.setuName("i1");
		i5.setDone(true);

		int c1to1 = i1.compareTo(i1);
		int c1to2 = i1.compareTo(i2);
		int c1to3 = i1.compareTo(i3);
		int c1to4 = i1.compareTo(i4);
		int c2to1 = i2.compareTo(i1);
		int c2to2 = i2.compareTo(i2);
		int c2to3 = i2.compareTo(i3);
		int c2to4 = i2.compareTo(i4);
		int c3to1 = i3.compareTo(i1);
		int c3to2 = i3.compareTo(i2);
		int c3to3 = i3.compareTo(i3);
		int c3to4 = i3.compareTo(i4);
		int c4to1 = i4.compareTo(i1);
		int c4to2 = i4.compareTo(i2);
		int c4to3 = i4.compareTo(i3);
		int c4to4 = i4.compareTo(i4);

		int c5to1 = i5.compareTo(i1);
		int c5to2 = i5.compareTo(i2);

		assertEquals(0, c1to1);
		assertTrue(c1to2 <= -1);
		assertTrue(c1to3 >= 1);
		assertTrue(c1to4 <= -1);
		assertTrue(c2to1 >= 1);
		assertEquals(0, c2to2);
		assertTrue(c2to3 >= 1);
		assertTrue(c2to4 >= 1);
		assertTrue(c3to1 <= -1);
		assertTrue(c3to2 <= -1);
		assertEquals(0, c3to3);
		assertTrue(c3to4 <= -1);
		assertTrue(c4to1 >= 1);
		assertTrue(c4to2 <= -1);
		assertTrue(c4to3 >= 1);
		assertEquals(0, c4to4);

		assertEquals(0, c5to1);
		assertTrue(c5to2 <= -1);
	}

	/**
	 * Test method for
	 * {@link voruti.priorit.Item#calculateValue(voruti.priorit.Item)}.
	 */
	@Test
	void testCalculateValue() {
		Item i1 = new Item();
		Item i2 = new Item();
		i2.setPriority(Priority.HIGH);
		Item i3 = new Item();
		Date plusSeventeen = new Date(new Date().getTime() + 1468800000L);
		i3.setEtaDate(plusSeventeen);
		Item i4 = new Item();
		i4.setPriority(Priority.LOW);
		i4.setEtaDate(plusSeventeen);

		int v1 = Item.calculateValue(i1);
		int v2 = Item.calculateValue(i2);
		int v3 = Item.calculateValue(i3);
		int v4 = Item.calculateValue(i4);

		assertEquals(150, v1);
		assertEquals(60, v2);
		assertEquals(85, v3);
		assertEquals(68, v4);
	}

	/**
	 * Test method for {@link voruti.priorit.Item#daysLeft(java.util.Date)}.
	 */
	@Test
	void testDaysLeft() {
		Item i1 = new Item();
		Date plusSeventeen = new Date(new Date().getTime() + 1468800000L);

		int days1 = Item.daysLeft(i1.getEtaDate());
		int days2 = Item.daysLeft(plusSeventeen);

		assertEquals(30, days1);
		assertEquals(17, days2);
	}

}
