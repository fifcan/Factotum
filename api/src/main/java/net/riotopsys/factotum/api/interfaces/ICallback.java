package net.riotopsys.factotum.api.interfaces;

import net.riotopsys.factotum.api.AbstractRequest;

/**
 * Created by afitzgerald on 12/16/14.
 */
public interface ICallback<T> {

    void onSuccess(final AbstractRequest request, final T result);

    void onFailure(final AbstractRequest request, final Object error);

}
