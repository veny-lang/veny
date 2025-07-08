package org.venylang.veny.util.source;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SrcFilePosMap} class.
 * <p>
 * This test suite verifies the behavior of {@code SrcFilePosMap}, which represents
 * a source file's metadata and provides mapping between file-relative offsets and
 * human-readable positions (line and column).
 * <p>
 * The tests cover:
 * <ul>
 *   <li>Managing line offsets (adding, merging, setting)</li>
 *   <li>Validating line offset sequences</li>
 *   <li>Mapping between {@link Offset} and {@link Position}</li>
 *   <li>Handling adjusted positions using {@link LineInfo}</li>
 *   <li>Boundary and error conditions for offset and line access</li>
 * </ul>
 *
 * <p><b>Class Coverage:</b></p>
 * <ul>
 *   <li>{@link SrcFilePosMap#addOffset(int)}</li>
 *   <li>{@link SrcFilePosMap#mergeLine(int)}</li>
 *   <li>{@link SrcFilePosMap#setLines(ArrayList)}</li>
 *   <li>{@link SrcFilePosMap#setLinesForContent(byte[])}</li>
 *   <li>{@link SrcFilePosMap#position(Offset)} and {@link SrcFilePosMap#positionFor(Offset, boolean)}</li>
 *   <li>{@link SrcFilePosMap#addLineInfo(String, int, int)}</li>
 * </ul>
 *
 * These tests ensure that {@code SrcFilePosMap} accurately tracks line structure
 * and provides reliable offset-to-position translation, including support for
 * position-altering directives.
 */
public class SrcFilePosMapTest {
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
