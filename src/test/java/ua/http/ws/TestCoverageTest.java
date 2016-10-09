package ua.http.ws;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestCoverageTest {

    @Test
    public void invokeTestClass() throws Exception {
        TestCoverage test = new TestCoverage();
        assertThat(test.getNumber(), is(42));
    }
}
