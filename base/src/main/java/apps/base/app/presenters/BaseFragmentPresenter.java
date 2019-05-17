package apps.base.app.presenters;

import android.support.annotation.CallSuper;

import apps.base.app.views.activities.BaseActivity;

public abstract class BaseFragmentPresenter<Activity extends BaseActivity, Fragment extends android.support.v4.app.Fragment> extends BasePresenter<Activity> {

    protected Fragment fragment;

    @CallSuper public void onFragmentReady(Fragment fragment) {
        onViewReady((Activity) fragment.getActivity());
        this.fragment = fragment;
    }

    @Override public void onDestroy() {}
    @Override public void onPause() {}
    @Override public void onResume() {}
    public void onDestroyView() {}
}
