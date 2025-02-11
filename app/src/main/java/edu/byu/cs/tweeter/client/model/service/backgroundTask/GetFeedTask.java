package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagesTask<Status> {

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(targetUser, limit, lastStatus, authToken, messageHandler);
        setLogTag("GetFeedTask");
    }

    private Pair<List<Status>, Boolean> getFeed() {
        Pair<List<Status>, Boolean> pageOfStatus = getFakeData().getPageOfStatus(getLastObject(), getLimit());
        return pageOfStatus;
    }

    @Override
    protected Pair<List<Status>, Boolean> fetchPageOfObjects() {
        return getFeed();
    }
}
