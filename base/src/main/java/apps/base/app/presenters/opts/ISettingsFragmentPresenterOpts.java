package apps.base.app.presenters.opts;

public interface ISettingsFragmentPresenterOpts {

    void onInfoAction();
    void onLogoutAction();
    void onEditEncryptionTypeAction();
    void onEditDefaultDNSAction();
    void onManageAccountAction();
    void onSwitchChanged(int type, boolean isChecked);
}
