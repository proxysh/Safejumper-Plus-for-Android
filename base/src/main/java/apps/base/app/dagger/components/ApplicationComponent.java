package apps.base.app.dagger.components;


import apps.base.app.utils.SharedPrefs;
import apps.base.app.dagger.modules.AppModule;
import apps.base.app.dagger.modules.SharedPreferenceModule;
import apps.base.app.dagger.scope.PerApplication;
import dagger.Component;

@PerApplication
@Component(modules = {AppModule.class, SharedPreferenceModule.class})
public interface ApplicationComponent {
    SharedPrefs sharedPreferences();
}
