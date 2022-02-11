package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagesPresenter<User> {

    public interface FollowingView extends PagesView<User> {}

    public FollowingPresenter(FollowingView view) {
        super(view);
    }

    @Override
    protected void getItemsFromServer(User user) {
        Cache.getInstance().getFollowService().getFollowing(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, getLastObject(), new GetFollowingObserver());
    }

    public class GetFollowingObserver extends GetPagesObserver implements FollowService.GetFollowingObserver {}
}
