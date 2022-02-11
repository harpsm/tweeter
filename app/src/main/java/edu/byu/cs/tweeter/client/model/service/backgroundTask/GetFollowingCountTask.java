package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetTask<User, Integer> {
    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(targetUser, authToken, messageHandler);
        setLogTag("GetFollowingCountTask");
    }

    @Override
    protected void talkToServer() {
        setReturnObject(new Integer(20));
    }
}
