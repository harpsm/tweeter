package edu.byu.cs.tweeter.client.presenter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagesPresenter<Status> {

    public interface StoryView extends PagesView<Status> {}

    public StoryPresenter(StoryView view) {
        super(view);
    }

    @Override
    protected void getItemsFromServer(User user) {
        Cache.getInstance().getStatusService().getStory(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, getLastObject(), new GetStoryObserver());
    }

    public class GetStoryObserver extends GetPagesObserver implements StatusService.GetStoryObserver {}

}
