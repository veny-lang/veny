package org.venylang.veny.util.source;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SrcFileSet} class.
 * <p>
 * This test suite verifies the core functionality of SrcFileSet, including:
 * <ul>
 *   <li>Adding source files with or without explicit base offsets</li>
 *   <li>Validating file offset boundaries and error handling</li>
 *   <li>Retrieving files by global offset</li>
 *   <li>Resolving positions from offsets</li>
 *   <li>Iterating over the file set with early termination</li>
 * </ul>
 * These tests ensure that SrcFileSet correctly manages file metadata and offset mapping
 * within a shared offset space.
 */
public class SrcFileSetTest {

    @Test
    void testAddOffsetAndLineCount() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{});
        map.addOffset(0);
        map.addOffset(10);
        map.addOffset(20);
        assertEquals(3, map.lineCount());
    }

    @Test
    void testMergeLineRemovesOffset() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{0, 10, 20});
        map.mergeLine(1);
        assertEquals(2, map.lineCount());
    }

    @Test
    void testSetLinesValid() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{});
        ArrayList<Integer> lines = new ArrayList<>(List.of(0, 10, 20));
        assertTrue(map.setLines(lines));
    }

    @Test
    void testSetLinesInvalid() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{});
        ArrayList<Integer> lines = new ArrayList<>(List.of(0, 20, 10)); // not increasing
        assertFalse(map.setLines(lines));
    }

    @Test
    void testSetLinesForContent() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{});
        map.setLinesForContent("line1\nline2\nline3\n".getBytes());
        assertEquals(3, map.lineCount());
    }

    @Test
    void testPositionForOffset() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{0, 6, 12});
        Offset offset = new Offset(1 + 7); // line 2, column 2
        Position pos = map.position(offset);
        assertEquals(2, pos.line());
        assertEquals(2, pos.column());
    }

    /**TODO @Test
    void testAdjustedPositionWithLineInfo() {
        SrcFilePosMap map = new SrcFilePosMap(new SrcFileSet(), "test.java", 1, 50, new int[]{0, 6, 12});
        map.addLineInfo("virtual.java", 6, 100); // adjust line 2 to 100
        Offset offset = new Offset(1 + 7); // line 2
        Position pos = map.position(offset);
        assertEquals("virtual.java", pos.fileName());
        assertEquals(101, pos.line());
    }*/
}
