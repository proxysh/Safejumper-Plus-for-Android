package apps.base.app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.R;
import apps.base.app.dagger.components.DaggerPresentersComponent;
import apps.base.app.presenters.SplashActivityPresenter;
import apps.base.app.dagger.components.ApplicationComponent;

public class SplashActivity extends BaseActivity {

    @Inject SplashActivityPresenter presenter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StrictMode.enableDefaults();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            ApplicationComponent appComponent = ((BaseAppApplication) getApplication()).getComponent();
            DaggerPresentersComponent.builder()
                    .applicationComponent(appComponent)
                    .build()
                    .inject(this);
            presenter.onViewReady(this);
        }, 1000);
    }

    @Override protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    public void startMainActivity() {
        Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

}
