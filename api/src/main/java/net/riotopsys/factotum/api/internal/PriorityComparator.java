package net.riotopsys.factotum.api.internal;

import java.io.Serializable;
import java.util.Comparator;

/**
* Created by afitzgerald on 12/30/14.
*/
public class PriorityComparator implements Comparator<Runnable>, Serializable {
    @Override
    public int compare(Runnable o1, Runnable o2) {
        RequestCallable rhs = (RequestCallable) ((FriendlyCompletionService.QueueingFuture) o2).getOriginalCallable();
        RequestCallable lhs = (RequestCallable) ((FriendlyCompletionService.QueueingFuture) o1).getOriginalCallable();

        int result = rhs.getRequest().getPriority() - lhs.getRequest().getPriority();
        if ( result != 0 ){
            return result;
        }
        return (int) (lhs.getSerial() - rhs.getSerial());
    }
}
