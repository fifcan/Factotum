package net.riotopsys.factotum.api.internal;

import com.google.common.collect.Lists;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.test.MockitoEnabledTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by afitzgerald on 12/31/14.
 */
public class PriorityComparatorTest extends MockitoEnabledTest {

    @Test
    public void checkForFifo() {

        List<Runnable> runnables = Lists.newArrayList(
                buildRunnable(0, 0),
                buildRunnable(0, 1),
                buildRunnable(0, 2),
                buildRunnable(0, 3),
                buildRunnable(0, 4),
                buildRunnable(0, 5),
                buildRunnable(0, 6),
                buildRunnable(0, 7),
                buildRunnable(0, 8),
                buildRunnable(0, 9)
        );

        check(runnables);

    }

    @Test
    public void checkForPriority() {

        List<Runnable> runnables = Lists.newArrayList(
                buildRunnable(9, 0),
                buildRunnable(8, 1),
                buildRunnable(7, 2),
                buildRunnable(6, 3),
                buildRunnable(5, 4),
                buildRunnable(4, 5),
                buildRunnable(3, 6),
                buildRunnable(2, 7),
                buildRunnable(1, 8),
                buildRunnable(0, 9)
        );

        check(runnables);

    }

    @Test
    public void checkForMix() {

        List<Runnable> runnables = Lists.newArrayList(
                buildRunnable(3, 0),
                buildRunnable(3, 4),
                buildRunnable(3, 7),
                buildRunnable(2, 1),
                buildRunnable(2, 5),
                buildRunnable(2, 8),
                buildRunnable(1, 2),
                buildRunnable(1, 6),
                buildRunnable(1, 9),
                buildRunnable(0, 3)
        );

        check(runnables);

    }

    private void check(List<Runnable> runnables) {
        List<Runnable> shuffled = new ArrayList<>(runnables);

        Collections.shuffle(shuffled);

        Assert.assertNotEquals(runnables, shuffled);

        PriorityComparator priorityComparator = new PriorityComparator();

        Collections.sort(shuffled, priorityComparator);

        Assert.assertEquals(runnables, shuffled);
    }

    private Runnable buildRunnable(int priority, long serial) {


        AbstractRequest request = mock(AbstractRequest.class);
        when(request.getPriority()).thenReturn(priority);

        RequestCallable originalCallable = mock(RequestCallable.class);
        when(originalCallable.getSerial()).thenReturn(serial);
        when(originalCallable.getRequest()).thenReturn(request);

        FriendlyCompletionService.QueueingFuture result = mock(FriendlyCompletionService.QueueingFuture.class);
        when(result.getOriginalCallable()).thenReturn(originalCallable);

        return result;
    }

}
