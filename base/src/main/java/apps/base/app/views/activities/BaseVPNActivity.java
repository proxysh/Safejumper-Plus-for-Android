package apps.base.app.views.activities;

import de.blinkt.openvpn.VpnProfile;

public abstract class BaseVPNActivity extends BaseActivity {

    public abstract void appLog(final String s);
    public abstract void startVpn(final VpnProfile profile);

}
