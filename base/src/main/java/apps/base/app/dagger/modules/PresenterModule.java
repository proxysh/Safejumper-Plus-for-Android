package apps.base.app.dagger.modules;


import apps.base.app.dagger.scope.PerActivity;
import apps.base.app.presenters.MainActivityPresenter;
import apps.base.app.presenters.MainFragmentPresenter;
import apps.base.app.presenters.SettingsFragmentPresenter;
import apps.base.app.presenters.SignInFragmentPresenter;
import apps.base.app.presenters.SignUpFragmentPresenter;
import apps.base.app.presenters.SplashActivityPresenter;
import dagger.Module;
import dagger.Provides;

@Module public class PresenterModule {

    @Provides @PerActivity MainActivityPresenter provideMainActivityPresenter() {
        return new MainActivityPresenter();
    }

    @Provides @PerActivity SplashActivityPresenter provideSplashActivityPresenter() {
        return new SplashActivityPresenter();
    }

    @Provides @PerActivity MainFragmentPresenter provideMainFragmentPresenter() {
        return new MainFragmentPresenter();
    }

    @Provides @PerActivity SignInFragmentPresenter provideSignInFragmentPresenter() {
        return new SignInFragmentPresenter();
    }

    @Provides @PerActivity SignUpFragmentPresenter provideSignUpFragmentPresenter() {
        return new SignUpFragmentPresenter();
    }
    @Provides @PerActivity SettingsFragmentPresenter provideSettingsFragmentPresenter() {
        return new SettingsFragmentPresenter();
    }

}
