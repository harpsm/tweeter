package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthorizedTask {

    //IsFollowerTask is not a "GetTask" (beacuse it takes 2 input objects)
    // but it's observer acts as a "GetObjectObserver" and so it
    //must have it's return key be equal to GetTask.RETURN_OBJECT_KEY
    public static final String IS_FOLLOWER_KEY = GetTask.RETURN_OBJECT_KEY;

    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;
    /**
     * The return value from talking to the server
     */
    private boolean isFollower;


    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
        setLogTag("IsFollowerTask");
    }

    @Override
    protected void talkToServer() {
        isFollower = new Random().nextInt() > 0;
    }

    @Override
    protected void addBundleData(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
