package net.riotopsys.factotum.test;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by afitzgerald on 12/29/14.
 */
public class MockitoEnabledTest {

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

}
