package net.riotopsys.factotum.api.concurent;

import net.riotopsys.factotum.api.AbstractRequest;

/**
 * Created by afitzgerald on 12/16/14.
 */
public interface ICallback {

    public void onSuccess(AbstractRequest request, Object result);

    public void onFailure(AbstractRequest request, Object result);

}
