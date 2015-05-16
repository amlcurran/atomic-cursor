package uk.co.amlcurran;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class AtomicCursorTest {

    @Test
    public void testOneIsOne() {
        assertThat(1 + 1).isEqualTo(2);
    }

}