package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagesPresenter<User> {

    public interface FollowersView extends PagesView<User> {}

    public FollowersPresenter(FollowersView view) {
        super(view);
    }

    @Override
    protected void getItemsFromServer(User user) {
        Cache.getInstance().getFollowService().getFollowers(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, getLastObject(), new GetFollowersObserver());
    }

    public class GetFollowersObserver extends GetPagesObserver implements FollowService.GetFollowersObserver {}

}
