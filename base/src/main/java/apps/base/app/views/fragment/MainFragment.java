package apps.base.app.views.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerPresentersComponent;
import apps.base.app.models.PortProtocol;
import apps.base.app.models.PortProtocolMap;
import apps.base.app.models.Server;
import apps.base.app.models.User;
import apps.base.app.presenters.MainFragmentPresenter;
import apps.base.app.presenters.SimpleModelListener;
import apps.base.app.utils.ConfigManager;
import apps.base.app.views.adapters.AllServerListRecyclerViewAdapter;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static apps.base.app.utils.Utils.getFormattedServerName;
import static apps.base.app.views.activities.MainActivity.activeConnectionCountry;
import static apps.base.app.views.activities.MainActivity.connectionState;
import static apps.base.app.views.adapters.AllServerListRecyclerViewAdapter.ADD_TO_FAVOURITE;
import static apps.base.app.views.adapters.AllServerListRecyclerViewAdapter.CONNECT_DISCONNECT;
import static apps.base.app.views.adapters.AllServerListRecyclerViewAdapter.ENCRYPTION;
import static apps.base.app.views.adapters.AllServerListRecyclerViewAdapter.PORT_NO;
import static apps.base.app.views.adapters.AllServerListRecyclerViewAdapter.UPDATE_PING;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;

public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";

    public static final int NOT_CONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;

    @BindView(R2.id.navigationView) NavigationView navigationView;
    @BindView(R2.id.menuMapIcon) ImageView menuMapIcon;
    @BindView(R2.id.menuServersIcon) ImageView menuServersIcon;
    @BindView(R2.id.menuServersText) TextView menuServersText;
    @BindView(R2.id.menuMapText) TextView menuMapText;
    @BindView(R2.id.menuIcon) View menuIcon;
    @BindView(R2.id.settingsIcon) View settingsIcon;
    @BindView(R2.id.titleBarText) TextView titleBarText;
    @BindView(R2.id.connectedTitleIcon) ImageView connectedTitleIcon;
    @BindView(R2.id.loginLogoutImage) ImageView loginLogoutImage;
    @BindView(R2.id.loginLogoutText) TextView loginLogoutText;
    @BindView(R2.id.profileContainer) View profileContainer;
    @BindView(R2.id.userEmail) TextView userEmail;
    @BindView(R2.id.expirationDate) TextView expirationDate;
    @BindView(R2.id.accountType) TextView accountType;
    @BindView(R2.id.countryBackground) ImageView countryBackground;
    @BindView(R2.id.favouriteCountryName) TextView favouriteCountryName;
    @BindView(R2.id.favoriteCardViewContainer) View favoriteCardViewContainer;
    @BindView(R2.id.slidingContainer) View slidingContainer;
    @BindView(R2.id.slidingLayout) SlidingUpPanelLayout slidingLayout;
    @BindView(R2.id.countryName) TextView countryName;
    @BindView(R2.id.serverLoadText) TextView serverLoadText;
    @BindView(R2.id.connectedStatus) TextView connectedStatus;
    @BindView(R2.id.countryImage) CircleImageView countryImage;
    @BindView(R2.id.likeIcon) ImageView likeIcon;
    @BindView(R2.id.pingText) TextView pingText;
    @BindView(R2.id.favouriteCountryImage) CircleImageView favouriteCountryImage;
    @BindView(R2.id.favouriteServerContainer) View favouriteServerContainer;
    @BindView(R2.id.previousCountry) ImageView previousCountry;
    @BindView(R2.id.nextCountry) ImageView nextCountry;
    @BindView(R2.id.slidingServerContainer) View slidingServerContainer;
    @BindView(R2.id.connectDisconnectButton) Button connectDisconnectButton;
    @BindView(R2.id.showAllServers) Button showAllServers;
    @BindView(R2.id.recyclerViewContainer) View recyclerViewContainer;
    @BindView(R2.id.recyclerView) RecyclerView recyclerView;

    @BindString(R2.string.logout) String logout;
    @BindString(R2.string.login) String login;
    @BindString(R2.string.expiration) String expiration;
    @BindString(R2.string.stateConnectedDotText) String stateConnectedDotText;
    @BindString(R2.string.stateNotConnectedDotText) String stateNotConnectedDotText;
    @BindString(R2.string.stateConnectingDotText) String stateConnectingDotText;
    @BindString(R2.string.stateConnectedText) String stateConnectedText;
    @BindString(R2.string.stateNotConnectedText) String stateNotConnectedText;
    @BindString(R2.string.stateConnectingText) String stateConnectingText;
    @BindString(R2.string.disconnect) String disconnect;
    @BindString(R2.string.connect) String connect;

    @BindColor(R2.color.notConnectedCountry) int notConnectedCountry;
    @BindColor(R2.color.connectingCountry) int connectingCountry;
    @BindColor(R2.color.connectedCountry) int connectedCountry;
    @BindColor(R2.color.buttonOrange) int buttonOrange;
    @BindColor(R2.color.buttonRed) int buttonRed;
    @BindColor(R2.color.buttonGreen) int buttonGreen;
    @BindColor(R2.color.landColor) int landColor;
    @BindColor(R2.color.mainColor) int mainColor;
    @BindColor(R2.color.mainWhite) int mainWhite;

    @Inject MainFragmentPresenter presenter;
    private Server currentServer;
    private Server currentFavoriteServer;

    @Override protected int resource() {
        return R.layout.fragment_main;
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

    @Override protected void initUI() {

        menuIcon.setOnClickListener(v -> {
            DrawerLayout drawerLayout = (DrawerLayout) getView();
            if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        settingsIcon.setOnClickListener(v -> {
            presenter.onSettingsAction();
        });

        initNavigationMenu();
        initSlidingLayout();

        previousCountry.setOnClickListener(v -> {
            presenter.onPreviousCountryAction();
        });

        nextCountry.setOnClickListener(v -> {
            presenter.onNextCountryAction();
        });

        favoriteCardViewContainer.setOnClickListener(v -> {
            presenter.onFavoriteServerClick();
        });

        likeIcon.setOnClickListener(view -> {
            if (currentServer != null) {
                presenter.updateFavouriteServerList(currentServer);
            }
        });

        pingText.setOnClickListener(view -> {
            presenter.onServerPingAction(currentServer);
        });

        showAllServers.setOnClickListener(v -> {
            if (slidingLayout.getPanelState() == COLLAPSED) {
                slidingLayout.setPanelState(EXPANDED);
            }
        });

        connectDisconnectButton.setOnClickListener(v -> {
            presenter.onConnectDisconnectAction(currentServer);
        });

        initRecyclerView();
    }

    private void initNavigationMenu() {
        highlightMenuItem(menuMapText, menuMapIcon, R.id.menuMap);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels;
        navigationView.setLayoutParams(params);
        navigationView.findViewById(R.id.icClose).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuMap).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuServerList).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuNotification).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuShowLogs).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuShop).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuSettings).setOnClickListener(navigationViewOnClickListener);
        navigationView.findViewById(R.id.menuLoginLogout).setOnClickListener(navigationViewOnClickListener);
    }

    private View.OnClickListener navigationViewOnClickListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            DrawerLayout drawerLayout = (DrawerLayout) getView();
            int i = view.getId();
            if (i == R.id.menuLoginLogout) {
                presenter.onLoginLogoutAction();
                if (drawerLayout != null) {
                    drawerLayout.postDelayed(() -> drawerLayout.closeDrawer(GravityCompat.START), 500);
                }

            } else if (i == R.id.menuSettings) {
                presenter.onSettingsAction();
                if (drawerLayout != null) {
                    drawerLayout.postDelayed(() -> drawerLayout.closeDrawer(GravityCompat.START), 500);
                }

            } else if (i == R.id.menuShop) {
                presenter.onShopAction();
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            } else if (i == R.id.menuShowLogs) {
                presenter.onShowLogsAction();
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            } else if (i == R.id.menuNotification) {
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            } else if (i == R.id.menuServerList) {
                if (slidingLayout.getPanelState() == COLLAPSED) {
                    slidingLayout.setPanelState(EXPANDED);
                }
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            } else if (i == R.id.menuMap) {
                if (slidingLayout.getPanelState() == EXPANDED) {
                    slidingLayout.setPanelState(COLLAPSED);
                }
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            } else if (i == R.id.icClose) {
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            }
        }
    };

    private void initSlidingLayout() {
        slidingContainer.setClickable(false);
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                recyclerViewContainer.setAlpha(slideOffset);
                slidingServerContainer.setAlpha(1.0f - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case EXPANDED:
                        slidingServerContainer.setVisibility(View.INVISIBLE);
                        recyclerViewContainer.setVisibility(View.VISIBLE);
                        removeHighlighting(menuMapText, menuMapIcon, R.id.menuMap);
                        highlightMenuItem(menuServersText, menuServersIcon, R.id.menuServerList);
                        break;
                    case DRAGGING:
                        slidingServerContainer.setVisibility(View.VISIBLE);
                        recyclerViewContainer.setVisibility(View.VISIBLE);
                        break;
                    case COLLAPSED:
                        slidingServerContainer.setVisibility(View.VISIBLE);
                        recyclerViewContainer.setVisibility(View.INVISIBLE);
                        highlightMenuItem(menuMapText, menuMapIcon, R.id.menuMap);
                        removeHighlighting(menuServersText, menuServersIcon, R.id.menuServerList);
                        break;
                }
            }
        });
    }

    private void highlightMenuItem(TextView menuItemText, ImageView menuItemIcon, int itemViewId) {
        menuItemText.setTextColor(mainColor);
        menuItemIcon.setColorFilter(mainColor);
        navigationView.findViewById(itemViewId).setBackgroundColor(mainWhite);
    }

    private void removeHighlighting(TextView menuItemText, ImageView menuItemIcon, int itemViewId) {
        menuItemText.setTextColor(mainWhite);
        menuItemIcon.setColorFilter(mainWhite);
        navigationView.findViewById(itemViewId).setBackground(ContextCompat.getDrawable(menuItemIcon.getContext(), R.drawable.selector_menu_item));
    }


    private void initRecyclerView() {
        AllServerListRecyclerViewAdapter adapter = new AllServerListRecyclerViewAdapter(new ArrayList<>(0));
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter.setOnItemClickListener((itemView, position, server, clickType) -> {
            switch (clickType) {
                case CONNECT_DISCONNECT:
                    presenter.onConnectDisconnectAction(server);
                    break;
                case UPDATE_PING:
                    presenter.onServerPingAction(server);
                    break;
                case ENCRYPTION:
                    presenter.onEncryptionAction(
                            updatedServer -> adapter.notifyDataSetChanged(),
                            server
                    );
                    break;
                case PORT_NO:

//                    List<PortProtocol> portProtocols = server.getPorts().get(server.getEncryptionType());
//                    presenter.onPortNoAction(
//                            chosenPortNo -> {
//                                server.setPort(chosenPortNo);
//                                adapter.notifyDataSetChanged();
//                            },
//                            server.getPort(),
//                            portProtocols
//                    );

                    presenter.onPortNoAction(
                            updatedServer -> adapter.notifyDataSetChanged(),
                            server
                    );
                    break;

                case ADD_TO_FAVOURITE:
                    presenter.updateFavouriteServerList(server);
                    break;
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void updateServerList(List<Server> serverList) {
        updateSlidingServerContainer(serverList, 0);
        updateRecyclerView(serverList);
    }

    public void updateRecyclerView(List<Server> serverList) {
        if (recyclerView != null) {
            AllServerListRecyclerViewAdapter adapter = (AllServerListRecyclerViewAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.updateData(serverList);
            }
        }
    }

    public void updateAllServersState() {
        if (recyclerView != null) {
            AllServerListRecyclerViewAdapter adapter = (AllServerListRecyclerViewAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void updateFavoriteContainer(List<Server> serverList, int currentServerIndex) {
        List<Server> favouriteServers = new ArrayList<>();
        for (Server nextServer : serverList) {
            if (nextServer.isFavourite()) {
                favouriteServers.add(nextServer);
            }
        }

        if (currentServerIndex == 0) {
            previousCountry.setVisibility(View.INVISIBLE);
        } else {
            previousCountry.setVisibility(View.VISIBLE);
        }
        if (currentServerIndex == favouriteServers.size() - 1) {
            nextCountry.setVisibility(View.INVISIBLE);
        } else {
            nextCountry.setVisibility(View.VISIBLE);
        }


        if (favouriteServers.isEmpty()) {
            favouriteServerContainer.setVisibility(View.INVISIBLE);
        } else {
            favouriteServerContainer.setVisibility(View.VISIBLE);
            currentFavoriteServer = favouriteServers.get(currentServerIndex);
            favouriteCountryName.setText(currentFavoriteServer.getName());
            String countryFlagIcon = getFormattedServerName("drawable/" + new Locale("", currentFavoriteServer.getIsoCode().toUpperCase()).getDisplayCountry());
            String packageName = getActivity().getPackageName();
            favouriteCountryImage.setImageResource(getResources().getIdentifier(countryFlagIcon, null, packageName));

            updateFavoriteFlagBorder(currentFavoriteServer);
        }

    }

    private void updateFavoriteFlagBorder(Server server) {
        if (server != null) {
            switch (connectionState) {
                case NOT_CONNECTED:

                    if (currentServer != null && server.isValid() && !Objects.equals(activeConnectionCountry, server.getName())) {
                        favouriteCountryImage.setBorderColor(notConnectedCountry);
                        return;
                    }
                    favouriteCountryImage.setBorderColor(notConnectedCountry);
                    break;
                case CONNECTED:

                    if (currentServer != null && server.isValid() && !Objects.equals(activeConnectionCountry, server.getName())) {
                        favouriteCountryImage.setBorderColor(notConnectedCountry);
                        return;
                    }
                    favouriteCountryImage.setBorderColor(connectedCountry);
                    break;
                case CONNECTING:

                    if (currentServer != null && server.isValid() && !Objects.equals(activeConnectionCountry, server.getName())) {
                        favouriteCountryImage.setBorderColor(notConnectedCountry);
                        return;
                    }
                    favouriteCountryImage.setBorderColor(connectingCountry);
                    break;
            }
        }
    }

    public void updateSlidingServerContainer(List<Server> serverList, int currentServerIndex) {
        if (serverList.isEmpty()) {
            return;
        }
        List<Server> favouriteServers = new ArrayList<>();
        for (Server nextServer : serverList) {
            if (nextServer.isFavourite()) {
                favouriteServers.add(nextServer);
            }
        }

        if (favouriteServers.isEmpty()) {
            favouriteServerContainer.setVisibility(View.INVISIBLE);

            if(connectionState == CONNECTED && activeConnectionCountry.length() > 0) {
                for(Server nextServer : serverList) {
                    if (Objects.equals(activeConnectionCountry, nextServer.getName())) {
                        currentServer = nextServer;
                        break;
                    }
                }
            } else {
                currentServer = serverList.get(new Random().nextInt(serverList.size()));
            }
        } else {
            currentFavoriteServer = currentServer = favouriteServers.get(currentServerIndex);
            favouriteServerContainer.setVisibility(View.VISIBLE);
            if (currentServerIndex == 0) {
                previousCountry.setVisibility(View.INVISIBLE);
            } else {
                previousCountry.setVisibility(View.VISIBLE);
            }
            if (currentServerIndex == favouriteServers.size() - 1) {
                nextCountry.setVisibility(View.INVISIBLE);
            } else {
                nextCountry.setVisibility(View.VISIBLE);
            }
        }

        favouriteCountryName.setText(currentServer.getName());
        String countryFlagIcon = getFormattedServerName("drawable/" + new Locale("", currentServer.getIsoCode().toUpperCase()).getDisplayCountry());
        String packageName = getActivity().getPackageName();
        favouriteCountryImage.setImageResource(getResources().getIdentifier(countryFlagIcon, null, packageName));

        updateFavoriteFlagBorder(currentFavoriteServer);
        updateUIWithServer(currentServer);


    }

    public void updateUIWithServer(Server server) {
        currentServer = server;
        updateLikeIcon(currentServer);

        pingText.setText(String.format(Locale.getDefault(), "%.0f ms", currentServer.getServerPing()));
        countryName.setText(currentServer.getName());
        serverLoadText.setText(String.format(Locale.getDefault(), "%.0f%%", currentServer.getServerLoad()));


        String countryFlagIcon = getFormattedServerName("drawable/" + new Locale("", currentServer.getIsoCode().toUpperCase()).getDisplayCountry());
        String packageName = getActivity().getPackageName();
        countryImage.setImageResource(getResources().getIdentifier(countryFlagIcon, null, packageName));

        String countryBackgroundImage = "drawable/" + currentServer.getIsoCode() + "_" + getFormattedServerName(stateNotConnectedText);
        countryBackground.setImageResource(getResources().getIdentifier(countryBackgroundImage, null, packageName));
        updateState();
    }

    public void updateLikeIcon(Server server) {
        if (Objects.equals(server, currentServer)) {
            if (server.isFavourite()) {
                likeIcon.setImageResource(R.drawable.ic_heart_full);
            } else {
                likeIcon.setImageResource(R.drawable.ic_heart);
            }
        }
    }

    public void updateUserData(User user) {
        profileContainer.setVisibility(View.VISIBLE);
        userEmail.setText(user.getEmail());
        expirationDate.setText(String.format("%s: %s", expiration, user.getExpirationDate()));
        accountType.setText(ConfigManager.getUserPlanType(Integer.parseInt(user.getType())));
        loginLogoutImage.setRotation(0);
        loginLogoutText.setText(logout);


    }

    public void loggedOut() {
        loginLogoutImage.setRotation(180);
        loginLogoutText.setText(login);
        profileContainer.setVisibility(View.INVISIBLE);
    }

    public void updateState() {
        updateAllServersState();
        switch (connectionState) {
            case NOT_CONNECTED:
                connectedTitleIcon.setImageResource(R.drawable.ic_lock_unlock);
                titleBarText.setText(stateNotConnectedText);

                if (currentServer != null && !Objects.equals(activeConnectionCountry, currentServer.getName())) {
                    connectDisconnectButton.getBackground().setTint(buttonGreen);
                    connectDisconnectButton.setText(connect);
                    connectedStatus.setTextColor(buttonRed);
                    connectedStatus.setText(stateNotConnectedDotText);

                    countryBackground.setImageResource(getResources().getIdentifier("drawable/" + currentServer.getIsoCode() + "_" + stateNotConnectedText.toLowerCase().replaceAll(" ", "_"), null, getActivity().getPackageName()));
                    countryImage.setBorderColor(notConnectedCountry);
//                    favouriteCountryImage.setBorderColor(notConnectedCountry);
                    return;
                }
                connectDisconnectButton.getBackground().setTint(buttonGreen);
                connectDisconnectButton.setText(connect);
                connectedStatus.setTextColor(buttonRed);
                connectedStatus.setText(stateNotConnectedDotText);

                countryBackground.setImageResource(getResources().getIdentifier("drawable/" + currentServer.getIsoCode() + "_" + stateNotConnectedText.toLowerCase().replaceAll(" ", "_"), null, getActivity().getPackageName()));
                countryImage.setBorderColor(notConnectedCountry);
//                favouriteCountryImage.setBorderColor(notConnectedCountry);
                break;
            case CONNECTED:
                connectedTitleIcon.setImageResource(R.drawable.ic_lock);
                titleBarText.setText(stateConnectedText);

                if (currentServer != null && !Objects.equals(activeConnectionCountry, currentServer.getName())) {
                    connectDisconnectButton.getBackground().setTint(buttonGreen);
                    connectDisconnectButton.setText(connect);
                    connectedStatus.setTextColor(buttonRed);
                    connectedStatus.setText(stateNotConnectedDotText);

                    countryBackground.setImageResource(getResources().getIdentifier("drawable/" + currentServer.getIsoCode() + "_" + stateNotConnectedText.toLowerCase().replaceAll(" ", "_"), null, getActivity().getPackageName()));
                    countryImage.setBorderColor(notConnectedCountry);
//                    favouriteCountryImage.setBorderColor(notConnectedCountry);
                    return;
                }

                connectDisconnectButton.getBackground().setTint(buttonRed);
                connectDisconnectButton.setText(disconnect);
                connectedStatus.setTextColor(buttonGreen);
                connectedStatus.setText(stateConnectedDotText);

                countryBackground.setImageResource(getResources().getIdentifier("drawable/" + currentServer.getIsoCode() + "_" + stateConnectedText.toLowerCase(), null, getActivity().getPackageName()));
                countryImage.setBorderColor(connectedCountry);
//                favouriteCountryImage.setBorderColor(connectedCountry);
                break;
            case CONNECTING:
                connectedTitleIcon.setImageResource(R.drawable.ic_loop);
                titleBarText.setText(stateConnectingText);

                Log.d(TAG, "updateState: CONNECTING");

                if (currentServer != null && !Objects.equals(activeConnectionCountry, currentServer.getName())) {

                    connectDisconnectButton.getBackground().setTint(buttonGreen);
                    connectDisconnectButton.setText(connect);
                    connectedStatus.setTextColor(buttonRed);
                    connectedStatus.setText(stateNotConnectedDotText);

                    countryBackground.setImageResource(getResources().getIdentifier("drawable/" + currentServer.getIsoCode() + "_" + stateNotConnectedText.toLowerCase().replaceAll(" ", "_"), null, getActivity().getPackageName()));
                    countryImage.setBorderColor(notConnectedCountry);
//                    favouriteCountryImage.setBorderColor(notConnectedCountry);
                    return;
                }

                connectDisconnectButton.getBackground().setTint(buttonOrange);
                connectDisconnectButton.setText(stateConnectingText);
                connectedStatus.setTextColor(buttonOrange);
                connectedStatus.setText(stateConnectingDotText);

                countryBackground.setImageResource(getResources().getIdentifier("drawable/" + currentServer.getIsoCode() + "_" + stateConnectingText.toLowerCase(), null, getActivity().getPackageName()));
                countryImage.setBorderColor(connectingCountry);
//                favouriteCountryImage.setBorderColor(connectingCountry);
                break;
        }
    }

    public void updateServerPing() {
        if (pingText != null) {
            pingText.setText(String.format(Locale.getDefault(), "%.0f ms", currentServer.getServerPing()));
            updateAllServersState();
        }
    }

    public void collapseSlidingLayout() {
        if (slidingLayout != null && slidingLayout.getPanelState() == EXPANDED) {
            slidingLayout.setPanelState(COLLAPSED);
        }
    }

    public void updateAllServersEncryptionType() {
        presenter.updateServersEncryptionType();
    }

    public void onNewServerList() {
        if (presenter != null) {
            presenter.provideServerList();
            presenter.onServerListDataUpdated();
        }
    }
}
