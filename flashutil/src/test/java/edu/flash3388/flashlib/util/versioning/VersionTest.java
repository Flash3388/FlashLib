package edu.flash3388.flashlib.util.versioning;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VersionTest {

    @Test
    public void isCompatibleWith_sameVersion_returnsTrue() throws Exception {
        Version CURRENT = new Version(1, 2, 0);
        Version OTHER = new Version(1, 2, 0);

        assertTrue(CURRENT.isCompatibleWith(OTHER));
    }

    @Test
    public void isCompatibleWith_higherMinorVersion_returnsTrue() throws Exception {
        Version CURRENT = new Version(1, 5, 0);
        Version OTHER = new Version(1, 2, 0);

        assertTrue(CURRENT.isCompatibleWith(OTHER));
    }

    @Test
    public void isCompatibleWith_lowerMinorVersion_returnsFalse() throws Exception {
        Version CURRENT = new Version(1, 0, 0);
        Version OTHER = new Version(1, 2, 0);

        assertFalse(CURRENT.isCompatibleWith(OTHER));
    }

    @Test
    public void isCompatibleWith_differentMajorVersion_returnsFalse() throws Exception {
        Version CURRENT = new Version(2, 2, 0);
        Version OTHER = new Version(1, 2, 0);

        assertFalse(CURRENT.isCompatibleWith(OTHER));
    }
}