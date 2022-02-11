package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.presenter.FeedPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {

    public interface GetFeedObserver extends GetPagesObserver<Status> {}
    public interface GetStoryObserver extends GetPagesObserver<Status> {}
    public interface PostStatusObserver extends SetObjectObserver {}

    //GETFEEDTASK
    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, FeedPresenter.GetFeedObserver getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken, user, pageSize, lastStatus, new GetFeedHandler(getFeedObserver));
        executeTask(getFeedTask);
    }
    //GetFeedHandler
    private class GetFeedHandler extends GetPagesHandler<Status> {
        public GetFeedHandler(GetFeedObserver observer) {
            super(observer);
        }
    }

    //STORYTASK
    public void getStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StoryPresenter.GetStoryObserver getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken, user, pageSize, lastStatus, new GetStoryHandler(getStoryObserver));
        executeTask(getStoryTask);
    }
    //GetStoryHandler
    private class GetStoryHandler extends GetPagesHandler<Status> {
        public GetStoryHandler(GetStoryObserver observer) { super(observer); }
    }

    //POSTSTATUSTASK
    public void postStatus(AuthToken currUserAuthToken, Status newStatus, MainPresenter.PostStatusObserver postStatusObserver) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken, newStatus, new PostStatusHandler(postStatusObserver));
        executeTask(statusTask);
    }
    // PostStatusHandler
    private class PostStatusHandler extends SetObjectHandler {
        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
        }
    }
}
