package apps.base.app.views.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerPresentersComponent;
import apps.base.app.models.DefaultDNS;
import apps.base.app.models.SettingsSwitches;
import apps.base.app.models.User;
import apps.base.app.presenters.SettingsFragmentPresenter;
import apps.base.app.utils.ConfigManager;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;

import static apps.base.app.utils.Utils.API_DAY_MONTH_YEAR;
import static apps.base.app.utils.Utils.MONTH_DAY_YEAR;

public class SettingsFragment extends BaseFragment {

    public static final String TAG = "SettingsFragment";

    public static final int KILL_INTERNET = 0;
    public static final int AUTO_CONNECT_WIFI = 1;
    public static final int AUTO_CONNECT_LAUNCH = 2;
    public static final int DISPLAY_ALL_SERVERS = 3;

    @BindView(R2.id.backIcon) View backIcon;
    @BindView(R2.id.infoIcon) View infoIcon;
    @BindView(R2.id.killSwitchContainer) View killSwitchContainer;
    @BindView(R2.id.autoConnectOnPublicWiFiContainer) View autoConnectOnPublicWiFiContainer;
    @BindView(R2.id.autoConnectOnLaunchContainer) View autoConnectOnLaunchContainer;
    @BindView(R2.id.displayAllServersContainer) View displayAllServersContainer;
    @BindView(R2.id.defaultDNSContainer) View defaultDNSContainer;
    @BindView(R2.id.defaultEncryptionContainer) View defaultEncryptionContainer;
    @BindView(R2.id.logoutContainer) View logoutContainer;

    @BindView(R2.id.emailText) TextView emailText;
    @BindView(R2.id.planText) TextView planText;
    @BindView(R2.id.encryptionValue) TextView encryptionValue;
    @BindView(R2.id.expirationText) TextView expirationText;
    @BindView(R2.id.manageYourAccountButton) Button manageYourAccountButton;

    @BindView(R2.id.killInternetSwitch) Switch killInternetSwitch;
    @BindView(R2.id.autoConnectSwitchOnPublicWiFi) Switch autoConnectSwitchOnPublicWiFi;
    @BindView(R2.id.autoConnectOnLaunchSwitch) Switch autoConnectOnLaunchSwitch;
    @BindView(R2.id.displayAllServersSwitch) Switch displayAllServersSwitch;

    @BindView(R2.id.primaryDNSText) TextView primaryDNSText;
    @BindView(R2.id.secondaryDNSText) TextView secondaryDNSText;
    @BindView(R2.id.emailTitle) TextView emailTitle;

    @BindColor(R2.color.buttonGreen) int buttonGreen;
    @BindColor(R2.color.switchGray) int switchGray;

    @BindString(R2.string.numberOptionsAvailable) String numberOptionsAvailable;
    @BindString(R2.string.username) String username;

    @Inject SettingsFragmentPresenter presenter;

    @Override protected int resource() {
        return R.layout.fragment_settings;
    }

    @Override protected void initUI() {

        encryptionValue.setText(String.format(numberOptionsAvailable, ConfigManager.getEncryptionType().size()));

        backIcon.setOnClickListener(v -> {
            presenter.onBackPressed();
        });

        infoIcon.setOnClickListener(v -> {
            presenter.onInfoAction();
        });

        logoutContainer.setOnClickListener(v -> {
            presenter.onLogoutAction();
        });

        manageYourAccountButton.setOnClickListener(v -> {
            presenter.onManageAccountAction();
        });

        killInternetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.onSwitchChanged(KILL_INTERNET, isChecked);
            switchColor((Switch) buttonView, isChecked);
        });

        killSwitchContainer.setOnClickListener(v -> {
            killInternetSwitch.toggle();
        });

        autoConnectSwitchOnPublicWiFi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.onSwitchChanged(AUTO_CONNECT_WIFI, isChecked);
            switchColor((Switch) buttonView, isChecked);
        });

        autoConnectOnPublicWiFiContainer.setOnClickListener(v -> {
            autoConnectSwitchOnPublicWiFi.toggle();
        });

        autoConnectOnLaunchSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.onSwitchChanged(AUTO_CONNECT_LAUNCH, isChecked);
            switchColor((Switch) buttonView, isChecked);
        });

        autoConnectOnLaunchContainer.setOnClickListener(v -> {
            autoConnectOnLaunchSwitch.toggle();
        });

        if (ConfigManager.<String>getField("hubsUri").length() > 0) {
            displayAllServersSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                presenter.onSwitchChanged(DISPLAY_ALL_SERVERS, isChecked);
                switchColor((Switch) buttonView, isChecked);
            });

            displayAllServersContainer.setOnClickListener(v -> {
                displayAllServersSwitch.toggle();
            });
        } else {
            displayAllServersContainer.setVisibility(View.GONE);
        }


        defaultDNSContainer.setOnClickListener(v -> {
            presenter.onEditDefaultDNSAction();
        });

        defaultEncryptionContainer.setOnClickListener(v -> {
            presenter.onEditEncryptionTypeAction();
        });
    }

    private void switchColor(Switch buttonView, boolean checked) {
        buttonView.getTrackDrawable().setColorFilter(checked ? buttonGreen : switchGray, PorterDuff.Mode.SRC);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ApplicationComponent appComponent = ((BaseAppApplication) getActivity().getApplication()).getComponent();
        DaggerPresentersComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);

        presenter.onFragmentReady(this);
    }

    public void updateUserData(User user) {
        emailText.setText(user.getEmail());
        planText.setText(ConfigManager.getUserPlanType(Integer.parseInt(user.getType())));

        DateFormat apiDateFormatter = new SimpleDateFormat(API_DAY_MONTH_YEAR, Locale.getDefault());
        DateFormat settingsDateFormatter = new SimpleDateFormat(MONTH_DAY_YEAR, Locale.getDefault());
        try {
            Date date = apiDateFormatter.parse(user.getExpirationDate());
            String settingsDate = settingsDateFormatter.format(date);
            expirationText.setText(settingsDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateDefaultDNS(DefaultDNS defaultDNS) {
        primaryDNSText.setText(defaultDNS.getPrimaryDNS());
        secondaryDNSText.setText(defaultDNS.getSecondaryDNS());
    }

    public void updateSwitches(SettingsSwitches switches) {
        killInternetSwitch.setChecked(switches.isKillInternet());
        autoConnectSwitchOnPublicWiFi.setChecked(switches.isAutoConnectOnPublicWiFi());
        autoConnectOnLaunchSwitch.setChecked(switches.isAutoConnectOnLaunch());
        displayAllServersSwitch.setChecked(switches.isDisplayAllServers());
    }
}
