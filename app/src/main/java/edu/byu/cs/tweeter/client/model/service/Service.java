package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagesTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Task;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class Service {

    public abstract interface ServiceObserver {
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    //Talks to the server, and returns a page of objects
    public interface GetPagesObserver<T> extends ServiceObserver {
        void handleSuccess(List<T> objectList, boolean hasMorePages);
    }
    //Talks to the server, and returns one object
    public interface GetObjectObserver<T> extends ServiceObserver {
        void handleSuccess(T object);
    }
    //Talks to the server, but returns no objects
    public interface SetObjectObserver extends ServiceObserver {
        void handleSuccess();
    }

    //Grandparent Handler Class
    public abstract class ServiceHandler extends Handler {
        protected ServiceObserver observer;
        public ServiceHandler(ServiceObserver observer) { this.observer = observer; }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                handlerHandleSuccess(msg);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.handleFailure(message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
        protected abstract void handlerHandleSuccess(Message msg);
    }

    //Parent Handler Class for tasks returning a single object
    public class GetObjectHandler<T> extends ServiceHandler {
        protected T returnObject;
        protected GetObjectObserver<T> getObjectObserver;

        public GetObjectHandler(GetObjectObserver<T> observer) {
            super(observer);
            this.getObjectObserver = observer;
        }
        @Override
        protected void handlerHandleSuccess(Message msg) {
            try {
                returnObject = (T) msg.getData().getSerializable(GetTask.RETURN_OBJECT_KEY);
            }
            catch (Exception e) {
                getObjectObserver.handleException(e);
            }
            getObjectObserver.handleSuccess(returnObject);
        }
    }

    //Parent Handler Class for tasks returning a page of objects
    public class GetPagesHandler<T> extends ServiceHandler {
        private Pair<List<T>, Boolean> pageOfObjects;
        private GetPagesObserver<T> getPagesObserver;

        public GetPagesHandler(GetPagesObserver<T> observer) {
            super(observer);
            this.getPagesObserver = observer;
        }
        @Override
        protected void handlerHandleSuccess(Message msg) {
            pageOfObjects = retrieveDataFromMessage(msg);
            getPagesObserver.handleSuccess(pageOfObjects.getFirst(), pageOfObjects.getSecond());
        }
        protected Pair<List<T>, Boolean> retrieveDataFromMessage(Message msg) {
            List<T> users = (List<T>) msg.getData().getSerializable(PagesTask.OBJECT_LIST_KEY);
            boolean hasMorePages = msg.getData().getBoolean(PagesTask.MORE_PAGES_KEY);
            return new Pair<>(users, new Boolean(hasMorePages));
        }
    }

    //Parent Handler Class for tasks returning no objects
    public abstract class SetObjectHandler extends ServiceHandler {
        private SetObjectObserver setObjectObserver;

        public SetObjectHandler(SetObjectObserver observer) {
            super(observer);
            this.setObjectObserver = observer;
        }
        @Override
        protected void handlerHandleSuccess(Message msg) {
            setObjectObserver.handleSuccess();
        }
    }


    protected void executeTask(Task task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

}
