package apps.base.app.presenters;


import apps.base.app.BaseAppApplication;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.utils.SharedPrefs;
import apps.base.app.views.activities.MainActivity;

public class MainActivityPresenter extends BasePresenter<MainActivity> {

    private SharedPrefs sharedPrefs;
    @Override public void onViewReady(MainActivity activity) {
        super.onViewReady(activity);

        activity.startMainFragment();

        ApplicationComponent component = ((BaseAppApplication) activity.getApplication()).getComponent();
        sharedPrefs = component.sharedPreferences();

        if(sharedPrefs.isTempUser()) {
            sharedPrefs.clear();
        }

        if(sharedPrefs.getUser().getEmail().length() == 0) {
            activity.startOnBoardingFragment();
        }

    }

    @Override public void onDestroy() {}

    @Override public void onResume() {}

    @Override public void onPause() {}

    public boolean shouldKillInternet() {
        return sharedPrefs.getSwitches().isKillInternet();
    }
}
