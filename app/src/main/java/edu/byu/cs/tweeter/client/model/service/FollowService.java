package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public class FollowService extends Service {

    public interface GetFollowingObserver extends GetPagesObserver<User> {}
    public interface GetFollowersObserver extends GetPagesObserver<User> {}
    public interface IsFollowerObserver extends GetObjectObserver<Boolean> {}
    public interface UnfollowObserver extends SetObjectObserver {}
    public interface FollowObserver extends SetObjectObserver {}
    public interface GetFollowersCountObserver extends GetObjectObserver<Integer> {}
    public interface GetFollowingCountObserver extends GetObjectObserver<Integer> {}



    //GETFOLLOWINGTASK
    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, GetFollowingObserver getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new GetFollowingHandler(getFollowingObserver));
        executeTask(getFollowingTask);
    }
    //GetFollowingHandler
    private class GetFollowingHandler extends GetPagesHandler<User> {
        public GetFollowingHandler (GetFollowingObserver observer) {
            super(observer);
        }
    }

    //GETFOLLOWERSTASK
    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, FollowersPresenter.GetFollowersObserver getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new GetFollowersHandler(getFollowersObserver));
        executeTask(getFollowersTask);
    }
    //GetFollowersHandler
    private class GetFollowersHandler extends GetPagesHandler<User> {
        public GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
        }
    }

    //ISFOLLOWERTASK
    public void isFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, IsFollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken, currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        executeTask(isFollowerTask);
    }
    // IsFollowerHandler
    private class IsFollowerHandler extends GetObjectHandler<Boolean> {
        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }
    }

    //UNFOLLOWTASK
    public void unfollow(AuthToken currUserAuthToken, User selectedUser, MainPresenter.UnfollowObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken, selectedUser, new UnfollowHandler(unfollowObserver));
        executeTask(unfollowTask);
    }
    // UnfollowHandler
    private class UnfollowHandler extends SetObjectHandler {
        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
        }
    }

    //FOLLOWTASK
    public void follow(AuthToken currUserAuthToken, User selectedUser, MainPresenter.FollowObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken, selectedUser, new FollowHandler(followObserver));
        executeTask(followTask);
    }
    // FollowHandler
    private class FollowHandler extends SetObjectHandler {
        public FollowHandler(FollowObserver observer) {
            super(observer);
        }
    }

    //GETFOLLOWERSCOUNTTASK
    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, MainPresenter.GetFollowersCountObserver getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken, selectedUser,
                new GetFollowersCountHandler(getFollowersCountObserver));
        executeTask(followersCountTask);
    }
    // GetFollowersCountHandler
    private class GetFollowersCountHandler extends GetObjectHandler<Integer> {
        public GetFollowersCountHandler(GetFollowersCountObserver observer) { super(observer); }
    }

    //GETFOLLOWINGCOUNTTASK
    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, MainPresenter.GetFollowingCountObserver getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken, selectedUser,
                new GetFollowingCountHandler(getFollowingCountObserver));
        executeTask(followingCountTask);
    }
    // GetFollowingCountHandler
    private class GetFollowingCountHandler extends GetObjectHandler<Integer> {
        public GetFollowingCountHandler(GetFollowingCountObserver observer) { super(observer); }
    }
}

