package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter {

    private static final int PAGE_SIZE = 10;

    public interface View {
        void selectNewUser(User user);
        void displayMessage(String message);
        void setLoadingFooterStatus(boolean activated);
        void addFollowers(List<User> followers);
    }

    public FollowersPresenter(View view) {
        this.view = view;
        userService = new UserService();
        followService = new FollowService();
    }

    private View view;
    private UserService userService;
    private FollowService followService;

    private User lastFollower;

    private boolean hasMorePages;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean isLoading = false;

    //GETUSERTASK
    public void selectUser(String userAlias) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    public class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.selectNewUser(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }
    }

    //GETFOLLOWERSTASK
    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooterStatus(true);

            followService.getFollowers(Cache.getInstance().getCurrUserAuthToken(),
                    user, PAGE_SIZE, lastFollower, new GetFollowersObserver());
        }
    }

    public class GetFollowersObserver implements FollowService.GetFollowersObserver {

        @Override
        public void handleSuccess(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.setLoadingFooterStatus(false);
            view.addFollowers(followers);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooterStatus(false);
            view.displayMessage("Failed to get followers: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.setLoadingFooterStatus(false);
            view.displayMessage("Failed to get followers because of exception: " + exception.getMessage());
        }
    }

}
