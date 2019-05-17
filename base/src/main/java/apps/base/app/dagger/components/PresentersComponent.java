package apps.base.app.dagger.components;

import apps.base.app.views.activities.MainActivity;
import apps.base.app.views.activities.SplashActivity;
import apps.base.app.dagger.modules.PresenterModule;
import apps.base.app.dagger.scope.PerActivity;
import apps.base.app.views.fragment.MainFragment;
import apps.base.app.views.fragment.SettingsFragment;
import apps.base.app.views.fragment.SignInFragment;
import apps.base.app.views.fragment.SignUpFragment;
import dagger.Component;

@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {PresenterModule.class}
)
public interface PresentersComponent {

    void inject(MainFragment fragment);
    void inject(SignInFragment fragment);
    void inject(SignUpFragment fragment);
    void inject(MainActivity activity);
    void inject(SplashActivity activity);
    void inject(SettingsFragment fragment);

}
