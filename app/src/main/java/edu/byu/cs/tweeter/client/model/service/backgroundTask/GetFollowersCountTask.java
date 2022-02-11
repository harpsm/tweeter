package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetTask<User, Integer> {

    public static final String COUNT_KEY = "count";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(targetUser, authToken, messageHandler);
        setLogTag("GetFollowersCountTask");
    }

    @Override
    protected void talkToServer() {
        setReturnObject(new Integer(20));
    }

    @Override
    protected void addBundleData(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, getReturnObject());
    }
}
