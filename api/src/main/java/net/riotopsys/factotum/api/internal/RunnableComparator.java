package net.riotopsys.factotum.api.internal;

import java.util.Comparator;

/**
* Created by afitzgerald on 12/30/14.
*/
public class RunnableComparator implements Comparator<Runnable> {
    @Override
    public int compare(Runnable o1, Runnable o2) {
        RequestCallable rhs = (RequestCallable) ((FriendlyCompletionService.QueueingFuture) o2).getOriginalCallable();
        RequestCallable lhs = (RequestCallable) ((FriendlyCompletionService.QueueingFuture) o1).getOriginalCallable();

        int result = lhs.request.getPriority() - rhs.request.getPriority();
        if ( result != 0 ){
            return result;
        }
        return (int) (lhs.serial - rhs.serial);
    }
}
