package net.riotopsys.factotum.api.concurent;

import net.riotopsys.factotum.api.AbstractRequest;

/**
 * Created by afitzgerald on 12/16/14.
 */
public interface ICallback<T> {

    public void onSuccess(final AbstractRequest request, final T result);

    public void onFailure(final AbstractRequest request, final T result);

}
