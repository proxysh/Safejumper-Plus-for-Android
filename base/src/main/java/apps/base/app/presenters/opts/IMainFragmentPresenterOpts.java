package apps.base.app.presenters.opts;

import apps.base.app.models.Server;
import apps.base.app.presenters.SimpleModelListener;

public interface IMainFragmentPresenterOpts {

    void onEncryptionAction(SimpleModelListener<Server> simpleServerListener, Server server);
    void onPortNoAction(SimpleModelListener<Server> simpleServerListener, Server server);

    void onLoginLogoutAction();
    void onShopAction();
    void onConnectDisconnectAction(Server server);
    void onPreviousCountryAction();
    void onNextCountryAction();
    void onSettingsAction();
    void onShowLogsAction();
    void updateFavouriteServerList(Server currentServer);
    void onServerPingAction(Server currentServer);
    void onServerListDataUpdated();
    void onFavoriteServerClick();
}
