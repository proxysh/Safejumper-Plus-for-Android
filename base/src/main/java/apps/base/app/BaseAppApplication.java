package apps.base.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerApplicationComponent;
import apps.base.app.dagger.modules.AppModule;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.StatusListener;
import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseAppApplication extends Application {

    private ApplicationComponent component;
    public static String PACKAGE_NAME;

    private StatusListener mStatus;

    @Override public void onCreate() {
        super.onCreate();

        PACKAGE_NAME = getPackageName();

        Realm.init(this);

        component = DaggerApplicationComponent
                .builder()
                .appModule(new AppModule(BaseAppApplication.this))
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/roboto_medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels();
        }

        mStatus = new StatusListener();
        mStatus.init(getApplicationContext());

    }

    public ApplicationComponent getComponent() {
        return component;
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Background message
        CharSequence name = getString(R.string.channel_name_background);
        NotificationChannel mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_BG_ID,
                name, NotificationManager.IMPORTANCE_MIN);

        mChannel.setDescription(getString(R.string.channel_description_background));
        mChannel.enableLights(false);

        mChannel.setLightColor(Color.DKGRAY);
        mNotificationManager.createNotificationChannel(mChannel);

        // Connection status change messages

        name = getString(R.string.channel_name_status);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_NEWSTATUS_ID,
                name, NotificationManager.IMPORTANCE_LOW);

        mChannel.setDescription(getString(R.string.channel_description_status));
        mChannel.enableLights(true);

        mChannel.setLightColor(Color.BLUE);
        mNotificationManager.createNotificationChannel(mChannel);


        // Urgent requests, e.g. two factor auth
        name = getString(R.string.channel_name_userreq);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_USERREQ_ID,
                name, NotificationManager.IMPORTANCE_HIGH);
        mChannel.setDescription(getString(R.string.channel_description_userreq));
        mChannel.enableVibration(true);
        mChannel.setLightColor(Color.CYAN);
        mNotificationManager.createNotificationChannel(mChannel);
    }

}
