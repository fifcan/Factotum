package net.riotopsys.factotum;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.Factotum;
import net.riotopsys.factotum.api.SimpleCancelRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;
import net.riotopsys.factotum.api.interfaces.IOnTaskCompletionCallback;
import net.riotopsys.factotum.api.interfaces.IOnTaskCreationCallback;
import net.riotopsys.factotum.api.internal.ResultWrapper;
import net.riotopsys.factotum.test.MockitoEnabledTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by afitzgerald on 12/29/14.
 */
public class FactotumTest extends MockitoEnabledTest implements IOnTaskCreationCallback, IOnTaskCompletionCallback {

    Object mon;

    private AtomicBoolean created = new AtomicBoolean();
    private AtomicBoolean completed = new AtomicBoolean();

    @Mock
    Object handler;

    @Mock
    ICallback<Object> callback;

    Object obj;

    private ResultWrapper wrapper;

    Boolean isCanceled;

    @Before
    public void reset() {
        created.set(false);
        completed.set(false);
        obj = new Object();
        wrapper = null;
        isCanceled = false;
        mon = new Object();
    }

    @Test
    public void simpleRun() throws Exception {

        AbstractRequest request = mock(AbstractRequest.class);

        when(request.isCanceled()).thenReturn(false);
        when(request.getTaskHandler()).thenReturn(handler);
        when(request.execute(handler)).thenReturn(obj);

        Factotum factotum = getFactotum();

        factotum.addRequest(request);

        synchronized (mon) {
            while (!completed.get()) {
                mon.wait();
            }
        }

        Assert.assertTrue(created.get());
        Assert.assertTrue(completed.get());

        Assert.assertEquals(request, wrapper.request);
        Assert.assertEquals(obj, wrapper.result);

        factotum.shutdown();

    }

    @Test
    public void cancelRun() throws Exception {


        AbstractRequest request = mock(AbstractRequest.class);
        when(request.isCanceled()).thenReturn(false);
        when(request.getTaskHandler()).thenReturn(handler);
        when(request.execute(handler)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(100);
                return obj;
            }
        });
        when(request.getGroup()).thenReturn("waffles");

        AbstractRequest request2 = mock(AbstractRequest.class);
        when(request2.isCanceled()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return isCanceled;
            }
        });
        when(request2.getTaskHandler()).thenReturn(handler);
        when(request2.execute(handler)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(100);
                return obj;
            }
        });
        when(request2.getGroup()).thenReturn("eggs");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                isCanceled = true;
                return null;
            }
        }).when(request2).cancel();


        Factotum factotum = getFactotum();

        factotum.addRequest(request);
        factotum.addRequest(request);
        factotum.addRequest(request);
        factotum.addRequest(request);

        factotum.addRequest(request2);
        factotum.issueCancelation(new SimpleCancelRequest("eggs"));

        synchronized (mon) {
            while (!completed.get()) {
                try {
                    mon.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        verify(request, times(0)).cancel();
        verify(request2, times(1)).cancel();

        factotum.shutdown();

    }

    private Factotum getFactotum() {
        return new Factotum.Builder()
                .setOnTaskCompletionCallback(this)
                .setOnTaskCreationCallback(this)
                .setMaximumPoolSize(1)
                .build();
    }

    @Override
    public void onTaskCompletion(ResultWrapper wrapper) {
        this.wrapper = wrapper;
        completed.set(true);
        synchronized (mon) {
            mon.notify();
        }
    }

    @Override
    public void onTaskCreation(Object task) {
        created.set(true);
    }
}
