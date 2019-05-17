package apps.base.app.dagger.modules;

import android.app.Application;

import apps.base.app.dagger.scope.PerApplication;
import apps.base.app.utils.SharedPrefs;
import dagger.Module;
import dagger.Provides;

@Module public class SharedPreferenceModule {

    @Provides @PerApplication SharedPrefs provideSharedPreference(Application application) {
        return SharedPrefs.getSharedPrefs(application);
    }
}
