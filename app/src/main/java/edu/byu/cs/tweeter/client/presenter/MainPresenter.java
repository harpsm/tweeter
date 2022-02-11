package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    private static final String LOG_TAG = "MainActivity";

    public interface View {
        void updateFollowButton(Boolean isFollower);
        void displayMessage(String message);
        void unfollowedUser();
        void enableFollowButton();
        void followedUser();
        void logoutSuccessful();
        void statusPostedSuccessful();
        void updateFollowerCount(int followersCount);
        void updateFollowingCount(int followingCount);
    }

    private View view;
    private FollowService followService;
    private UserService userService;
    private StatusService statusService;
    private User selectedUser;

    public MainPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
        statusService = new StatusService();
    }

    public User getSelectedUser() {
        return selectedUser;
    }
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    //ISFOLLOWER
    public void isFollower() {
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerObserver());
    }
    public class IsFollowerObserver implements FollowService.IsFollowerObserver {

        @Override
        public void handleSuccess(Boolean isFollower) {
            view.updateFollowButton(isFollower);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    }

    //UNFOLLOW
    public void unfollow() {
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver());
    }
    public class UnfollowObserver implements FollowService.UnfollowObserver {

        @Override
        public void handleSuccess() {
            view.enableFollowButton();
            view.unfollowedUser();
            updateSelectedUserFollowingAndFollowers();
        }

        @Override
        public void handleFailure(String message) {
            view.enableFollowButton();
            view.displayMessage("Failed to unfollow: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.enableFollowButton();
            view.displayMessage("Failed to unfollow because of exception: " + exception.getMessage());
        }
    }

    //FOLLOW
    public void follow() {
        followService.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver());
    }
    public class FollowObserver implements FollowService.FollowObserver {

        @Override
        public void handleSuccess() {
            view.enableFollowButton();
            view.followedUser();
            updateSelectedUserFollowingAndFollowers();
        }

        @Override
        public void handleFailure(String message) {
            view.enableFollowButton();
            view.displayMessage("Failed to follow: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.enableFollowButton();
            view.displayMessage("Failed to follow because of exception: " + exception.getMessage());
        }
    }

    //LOGOUT
    public void logout() {
        userService.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }
    public class LogoutObserver implements UserService.LogoutObserver {

        @Override
        public void handleSuccess() {
            //Clear user data (cached data).
            Cache.getInstance().clearCache();
            view.logoutSuccessful();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to logout because of exception: " + exception.getMessage());
        }
    }

    //POST STATUS
    public void postStatus(String post){
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            statusService.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }
    public class PostStatusObserver implements StatusService.PostStatusObserver {

        @Override
        public void handleSuccess() {
            view.statusPostedSuccessful();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to post status because of exception: " + exception.getMessage());
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
        //ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowersCountObserver());

        // Get count of most recently selected user's followees (who they are following)
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountObserver());
    }
    public class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver{

        @Override
        public void handleSuccess(Integer followersCount) {
            view.updateFollowerCount(followersCount);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get followers count because of exception: " + exception.getMessage());
        }
    }
    public class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver{

        @Override
        public void handleSuccess(Integer followingCount) {
            view.updateFollowingCount(followingCount);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following count because of exception: " + exception.getMessage());
        }
    }
}
