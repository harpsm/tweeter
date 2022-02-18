package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    public static final String LOG_TAG = "MainActivity";
    public static final String IS_FOLLOWER_DESCRIPTION = "determine follow relationship";
    public static final String FOLLOW_DESCRIPTION = "follow";
    public static final String UNFOLLOW_DESCRIPTION = "unfollow";
    public static final String GET_FOLLOWING_COUNT_DESCRIPTION = "get following count";
    public static final String GET_FOLLOWERS_COUNT_DESCRIPTION = "get followers count";
    public static final String POST_STATUS_DESCRIPTION = "post";
    public static final String LOGOUT_DESCRIPTION = "logout";

    //Interfaces and Observer Abstract Classes
    public interface MainView extends View {
        void updateFollowButton(Boolean isFollower);
        void unfollowedUser();
        void enableFollowButton();
        void followedUser();
        void logoutSuccessful();
        void statusPostedSuccessful();
        void updateFollowerCount(int followersCount);
        void updateFollowingCount(int followingCount);
    }
    public abstract class MainObserver implements Service.ServiceObserver {
        protected String taskDescription;
        public MainObserver(String taskDescription) {
            this.taskDescription = taskDescription;
        }
        public void handleFailure(String message) {
            mainView.displayMessage("Failed to " + taskDescription + ": " + message);
        }
        public void handleException(Exception exception) {
            //Log.e(LOG_TAG, ex.getMessage(), ex);
            mainView.displayMessage("Failed to " + taskDescription + " because of exception: " + exception.getMessage());
        }
    }
    public abstract class NoReturnObserver extends MainObserver {
        public NoReturnObserver(String taskDescription) {
            super(taskDescription);
        }
        public void handleSuccess() {
            updateView();
        }
        protected abstract void updateView();
    }
    public abstract class ObjectReturnObserver<T> extends MainObserver {
        public ObjectReturnObserver(String taskDescription) {
            super(taskDescription);
        }
        public void handleSuccess(T obj) {
            updateView(obj);
        }
        protected abstract void updateView(T obj);
    }
    public abstract class FollowButtonObserver extends MainObserver {
        public FollowButtonObserver(String taskDescription) {
            super(taskDescription);
        }
        public void handleSuccess() {
            mainView.enableFollowButton();
            updateView();
            updateSelectedUserFollowingAndFollowers();
        }
        @Override
        public void handleFailure(String message) {
            mainView.enableFollowButton();
            mainView.displayMessage("Failed to " + taskDescription + ": " + message);
        }
        @Override
        public void handleException(Exception exception) {
            //Log.e(LOG_TAG, exception.getMessage(), exception);
            mainView.enableFollowButton();
            mainView.displayMessage("Failed to " + taskDescription + " because of exception: " + exception.getMessage());
        }
        protected abstract void updateView();
    }


    //-------------------------------------
    private MainView mainView;
    private User selectedUser;

    public MainPresenter(MainView view) {
        super(view);
        this.mainView = view;
    }

    public User getSelectedUser() {
        return selectedUser;
    }
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    //ISFOLLOWER
    public void isFollower() {
        Cache.getInstance().getFollowService().isFollower(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerObserver(IS_FOLLOWER_DESCRIPTION));
    }
    public class IsFollowerObserver extends ObjectReturnObserver<Boolean> implements FollowService.IsFollowerObserver {
        public IsFollowerObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView(Boolean isFollower) {
            mainView.updateFollowButton(isFollower);
        }
    }

    //UNFOLLOW
    public void unfollow() {
        Cache.getInstance().getFollowService()
                .unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver(UNFOLLOW_DESCRIPTION));
    }
    public class UnfollowObserver extends FollowButtonObserver implements FollowService.UnfollowObserver {
        public UnfollowObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView() {
            mainView.unfollowedUser();
        }
    }

    //FOLLOW
    public void follow() {
        Cache.getInstance().getFollowService()
                .follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver(FOLLOW_DESCRIPTION));
    }
    public class FollowObserver extends FollowButtonObserver implements FollowService.FollowObserver {
        public FollowObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView() {
            mainView.followedUser();
        }
    }

    //LOGOUT
    public void logout() {
        view.displayMessage("Logging Out...");

        Cache.getInstance().getUserService()
                .logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver(LOGOUT_DESCRIPTION));
    }
    public class LogoutObserver extends NoReturnObserver implements UserService.LogoutObserver {
        public LogoutObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView() {
            //Clear user data (cached data).
            Cache.getInstance().clearCache();
            mainView.logoutSuccessful();
        }
    }

    //POST STATUS
    public void postStatus(String post){
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            Cache.getInstance().getStatusService()
                    .postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver(POST_STATUS_DESCRIPTION));
        }
        catch (Exception ex) {
            //Log.e(LOG_TAG, ex.getMessage(), ex);
            PostStatusObserver observer = new PostStatusObserver(POST_STATUS_DESCRIPTION);
            observer.handleException(ex);
        }
    }
    public class PostStatusObserver extends NoReturnObserver implements StatusService.PostStatusObserver {
        public PostStatusObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView() {
            mainView.statusPostedSuccessful();
        }
    }
    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }
    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    //GETFOLLOWINGCOUNT & GETFOLLOWERSCOUNT
    public void updateSelectedUserFollowingAndFollowers() {
        // Get count of most recently selected user's followers.
        Cache.getInstance().getFollowService()
                .getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser,
                        new GetFollowersCountObserver(GET_FOLLOWERS_COUNT_DESCRIPTION));

        // Get count of most recently selected user's followees (who they are following)
        Cache.getInstance().getFollowService()
                .getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser,
                        new GetFollowingCountObserver(GET_FOLLOWING_COUNT_DESCRIPTION));
    }
    public class GetFollowersCountObserver extends ObjectReturnObserver<Integer> implements FollowService.GetFollowersCountObserver{
        public GetFollowersCountObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView(Integer followersCount) {
            mainView.updateFollowerCount(followersCount);
        }
    }
    public class GetFollowingCountObserver extends ObjectReturnObserver<Integer> implements FollowService.GetFollowingCountObserver{
        public GetFollowingCountObserver(String taskDescription) {
            super(taskDescription);
        }
        @Override
        protected void updateView(Integer followingCount) {
            mainView.updateFollowingCount(followingCount);
        }
    }
}
