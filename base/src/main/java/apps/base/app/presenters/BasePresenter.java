package apps.base.app.presenters;

import android.support.annotation.CallSuper;


import java.util.ArrayList;
import java.util.List;

import apps.base.app.views.activities.BaseActivity;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<Activity extends BaseActivity> {

    protected Activity activity;

    protected List<Disposable> activeRequests = new ArrayList<>();

    public abstract void onDestroy();
    public abstract void onResume();
    public abstract void onPause();

    @CallSuper public void onViewReady(Activity activity) {
        this.activity = activity;
    }

    @CallSuper public void onBackPressed() {
        activity.onBackPressed();
    }

    @CallSuper public void cancelRequests() {
        for (Disposable nextRequest : activeRequests) {
            if(!nextRequest.isDisposed()) {
                nextRequest.dispose();
            }
        }
        activeRequests.clear();
    }
}