package apps.base.app.dagger.modules;

import apps.base.app.dagger.scope.PerActivity;
import apps.base.app.utils.SharedPrefs;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module public class RealmModule {
    @Provides @PerActivity Realm provideRealmInstance(RealmConfiguration configuration) {
        return Realm.getInstance(configuration);
    }

    @Provides @PerActivity RealmConfiguration provideRealmConfiguration(SharedPrefs sharedPrefs) {
        String email = sharedPrefs.getUser().getEmail();
        String userId = email.length() > 0 ? email : "no_login";

        return new RealmConfiguration.Builder()
                .name(String.format("%s.realm", userId))
                .deleteRealmIfMigrationNeeded()
                .build();
    }
}
