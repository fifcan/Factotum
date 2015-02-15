/*
 * Copyright 2015 C. A. Fitzgerald
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.riotopsys.factotum.api.internal;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;
import net.riotopsys.factotum.test.MockitoEnabledTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by afitzgerald on 12/29/14.
 */
public class RequestCallableTest extends MockitoEnabledTest {

    @Mock
    AbstractRequest request;

    @Mock
    Object handler;

    @Mock
    ICallback<Object> callback;

    @Test
    public void happyPath() throws Exception {
        //setup
        Object obj = new Object();

        when(request.isCanceled()).thenReturn(false);
        when(request.execute(handler)).thenReturn(obj);
        when(request.getCallback()).thenReturn(callback);

        RequestCallable requestCallable = new RequestCallable(request, handler, 0L);

        //run
        ResultWrapper result = requestCallable.call();

        //verify
        verify(request, times(1)).execute(handler);
        verify(callback, times(1)).onSuccess(request, obj);
        verify(callback, times(0)).onFailure(eq(request), any());

        Assert.assertNotNull(result);
        Assert.assertEquals(request, result.request);
        Assert.assertEquals(obj, result.result);


    }

    @Test
    public void canceledRequestPath() throws Exception {
        //setup
        Object obj = new Object();

        when(request.isCanceled()).thenReturn(true);
        when(request.execute(handler)).thenReturn(obj);
        when(request.getCallback()).thenReturn(callback);

        RequestCallable requestCallable = new RequestCallable(request, handler, 0L);

        //run
        ResultWrapper result = requestCallable.call();

        //verify
        verify(request, times(0)).execute(handler);
        verify(callback, times(0)).onSuccess(request, obj);
        verify(callback, times(0)).onFailure(eq(request), any());

        Assert.assertNull(result);

    }

    @Test
    public void taskThrowsExceptionPath() throws Exception {
        //setup
        Object obj = new Object();
        Exception exception = new Exception();

        when(request.isCanceled()).thenReturn(false);
        when(request.execute(handler)).thenThrow(exception);
        when(request.getCallback()).thenReturn(callback);

        RequestCallable requestCallable = new RequestCallable(request, handler, 0L);

        //run
        ResultWrapper result = requestCallable.call();

        //verify
        verify(request, times(1)).execute(handler);
        verify(callback, times(0)).onSuccess(request, obj);
        verify(callback, times(1)).onFailure(request, exception);

        Assert.assertNotNull(result);
        Assert.assertEquals(request, result.request);
        Assert.assertEquals(exception, result.result);

    }

}
