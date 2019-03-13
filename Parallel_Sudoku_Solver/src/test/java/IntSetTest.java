import Parallel_Sudoku_Solver.IntSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class IntSetTest {
    @Test
    public void testIntSetAdd() {
        IntSet set = new IntSet();
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertEquals(set.size(), 0);

        set.add(1);
        set.add(3);
        set.add(4);
        set.add(9);
        assertTrue(set.contains(1));
        assertFalse(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertTrue(set.contains(9));
        assertEquals(set.size(), 4);

        set.add(2);
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertTrue(set.contains(9));
        assertEquals(set.size(), 5);

        set.add(5);
        set.add(6);
        set.add(7);
        set.add(8);
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));
        assertTrue(set.contains(6));
        assertTrue(set.contains(7));
        assertTrue(set.contains(8));
        assertTrue(set.contains(9));
        assertEquals(set.size(), 9);

        set.add(1);
        set.add(5);
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));
        assertTrue(set.contains(6));
        assertTrue(set.contains(7));
        assertTrue(set.contains(8));
        assertTrue(set.contains(9));
        assertEquals(set.size(), 9);
    }

    @Test
    public void testIntSetRemove() {
        IntSet set = new IntSet();
        set.add(2);
        set.add(5);
        set.add(6);

        assertFalse(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertTrue(set.contains(5));
        assertTrue(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertEquals(set.size(), 3);

        set.remove(6);
        assertFalse(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertTrue(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertEquals(set.size(), 2);

        set.remove(5);
        assertFalse(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertEquals(set.size(), 1);

        set.remove(2);
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertEquals(set.size(), 0);

        set.remove(9);
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertEquals(set.size(), 0);
    }

    @Test
    public void testIntToArray() {
        IntSet set = new IntSet();
        set.add(1);
        set.add(7);
        set.add(9);

        int[] values = set.toArray();
        assertEquals(Arrays.toString(values), Arrays.toString(new int[]{1, 7, 9}));
    }

    @Test
    public void testGetFirst() {
        IntSet set = new IntSet();
        set.add(4);
        assertEquals(set.getFirst(), 4);

        set.add(5);
        assertEquals(set.getFirst(), 4);

        set.add(3);
        assertEquals(set.getFirst(), 3);
    }
}
