package apps.base.app.presenters;

import android.app.ProgressDialog;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.dagger.components.DaggerNetworkComponent;
import apps.base.app.dagger.components.NetworkComponent;
import apps.base.app.models.User;
import apps.base.app.presenters.opts.ISignUpFragmentPresenterOpts;
import apps.base.app.rest.RetrofitRepository;
import apps.base.app.utils.ConfigManager;
import apps.base.app.utils.SharedPrefs;
import apps.base.app.views.activities.MainActivity;
import apps.base.app.views.fragment.MainFragment;
import apps.base.app.views.fragment.SignUpFragment;
import io.reactivex.disposables.Disposable;

public class SignUpFragmentPresenter extends BaseFragmentPresenter<MainActivity, SignUpFragment> implements ISignUpFragmentPresenterOpts {

    @Inject SharedPrefs sharedPrefs;
    @Inject RetrofitRepository repository;
    @Inject Gson gson;

    @Override public void onFragmentReady(SignUpFragment fragment) {
        super.onFragmentReady(fragment);

        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .applicationComponent(((BaseAppApplication) activity.getApplication()).getComponent())
                .build();
        networkComponent.inject(this);
    }

    @Override public void onSignUpAction(String email, String password) {

        final String basicAuth = "Basic " + Base64.encodeToString((ConfigManager.getField("BASIC_AUTH_USERNAME") + ":" + ConfigManager.getField("BASIC_AUTH_PASSWORD")).getBytes(), Base64.NO_WRAP);

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please wait...");
        dialog.show();

        String userType = "1";
        String userStatus = "1";
        String userExpirationDate = "2020-01-01";
        Disposable disposable = repository.signUp(
                email,
                password,
                userType,
                userStatus,
                userExpirationDate,
                basicAuth
        )
                .subscribe(response -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (!response.isSuccess()) {
                        Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User user = new User();
                    user.setEmail(email);
                    user.setType(userType);
                    user.setStatus(userStatus);
                    user.setExpirationDate(userExpirationDate);

//                    ConfigManager.activeUserName = email;
//                    ConfigManager.activePasswdOfUser = password;

                    sharedPrefs.clear();
                    sharedPrefs.updateUserPassword(password);
                    sharedPrefs.updateUserData(user);
                    updateMainFragment(user);
                    activity.popBackStack();
                });
        activeRequests.add(disposable);
    }

    private void updateMainFragment(User user) {
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if (fragment != null) {
            fragment.updateUserData(user);
        }
    }

    @Override public void onDestroyView() {
        activity.hideKeyboard();
    }
}
