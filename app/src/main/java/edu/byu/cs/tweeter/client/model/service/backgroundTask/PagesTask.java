package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class PagesTask<T> extends AuthorizedTask {

    public static final String OBJECT_LIST_KEY = "object-list";
    public static final String MORE_PAGES_KEY = "more-pages";

    /**
     * The user whose feed is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;
    /**
     * Maximum number of statuses to return (i.e., page size).
     */
    private int limit;
    private T lastObject;
    private Pair<List<T>, Boolean> returnedPageOfObjects;

    public PagesTask(User targetUser, int limit, T lastObject, AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastObject = lastObject;
    }

    public Pair<List<T>, Boolean> getReturnedPageOfObjects() { return returnedPageOfObjects; }
    public void setReturnedPageOfObjects(Pair<List<T>, Boolean> returnedPageOfObjects) { this.returnedPageOfObjects = returnedPageOfObjects; }
    public T getLastObject() { return lastObject; }
    public void setLastObject(T lastObject) { this.lastObject = lastObject; }
    public User getTargetUser() { return targetUser; }
    public int getLimit() { return limit; }

    protected abstract Pair<List<T>, Boolean> fetchPageOfObjects();

    @Override
    protected void talkToServer() {
        setReturnedPageOfObjects(fetchPageOfObjects());
    }

    @Override
    protected void addBundleData(Bundle msgBundle) {
        msgBundle.putSerializable(OBJECT_LIST_KEY, (Serializable) getReturnedPageOfObjects().getFirst());
        msgBundle.putBoolean(MORE_PAGES_KEY, getReturnedPageOfObjects().getSecond());
    }
}
