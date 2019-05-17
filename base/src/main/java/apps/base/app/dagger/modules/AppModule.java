package apps.base.app.dagger.modules;

import android.app.Application;

import apps.base.app.dagger.scope.PerApplication;
import dagger.Module;
import dagger.Provides;

@Module public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides @PerApplication Application provideApplication() {
        return application;
    }
}
