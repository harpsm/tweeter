package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.view.main.feed.FeedFragment;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagesPresenter<Status> {

    public interface FeedView extends PagesView<Status> {}

    public FeedPresenter(FeedView view) {
        super(view);
    }

    @Override
    protected void getItemsFromServer(User user) {
        Cache.getInstance().getStatusService().getFeed(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, getLastObject(), new GetFeedObserver());
    }

    public class GetFeedObserver extends GetPagesObserver implements StatusService.GetFeedObserver {}

}
