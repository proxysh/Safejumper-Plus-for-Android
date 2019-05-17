package apps.base.app.dagger.components;

import com.google.gson.Gson;

import apps.base.app.dagger.modules.NetworkModule;
import apps.base.app.dagger.modules.RealmModule;
import apps.base.app.dagger.scope.PerActivity;
import apps.base.app.presenters.MainFragmentPresenter;
import apps.base.app.presenters.SignInFragmentPresenter;
import apps.base.app.presenters.SignUpFragmentPresenter;
import apps.base.app.presenters.SplashActivityPresenter;
import dagger.Component;

@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {NetworkModule.class, RealmModule.class}
)
public interface NetworkComponent {
    Gson gson();
    void inject(SplashActivityPresenter presenter);
    void inject(SignInFragmentPresenter presenter);
    void inject(SignUpFragmentPresenter presenter);
    void inject(MainFragmentPresenter presenter);

}
