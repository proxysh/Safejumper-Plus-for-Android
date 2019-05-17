package apps.base.app.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.models.Server;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static apps.base.app.views.activities.MainActivity.activeConnectionCountry;
import static apps.base.app.views.activities.MainActivity.connectionState;
import static apps.base.app.views.fragment.MainFragment.CONNECTED;
import static apps.base.app.views.fragment.MainFragment.CONNECTING;
import static apps.base.app.views.fragment.MainFragment.NOT_CONNECTED;


public class AllServerListRecyclerViewAdapter extends RecyclerView.Adapter<AllServerListRecyclerViewAdapter.ServerHolder> {

    public static final int CONNECT_DISCONNECT = 0;
    public static final int ENCRYPTION = 1;
    public static final int PORT_NO = 2;
    public static final int ADD_TO_FAVOURITE = 3;
    public static final int UPDATE_PING = 4;

    private List<Server> serverList;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private BaseRecyclerViewItemClickListener<Server> baseRecyclerViewItemClickListener;

    private ExpandableLinearLayout readyToCloseExpandableLayout;

    public AllServerListRecyclerViewAdapter(List<Server> serverList) {
        this.serverList = serverList;
        for (int i = 0; i < serverList.size(); i++) {
            expandState.append(i, false);
        }
    }

    @NonNull @Override public ServerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server, parent, false);
        return new ServerHolder(itemView);
    }

    @Override public void onBindViewHolder(@NonNull ServerHolder holder, int position) {
        Server server = serverList.get(position);

        holder.expandableLayout.setInRecyclerView(true);
        holder.expandableLayout.setExpanded(expandState.get(position));

        holder.expandableLayout.setDuration(300);
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override public void onPreOpen() {
                expandState.put(holder.getAdapterPosition(), true);
            }

            @Override public void onPreClose() {
                expandState.put(holder.getAdapterPosition(), false);
            }

            @Override public void onOpened() {
                if(readyToCloseExpandableLayout != null && !Objects.equals(readyToCloseExpandableLayout, holder.expandableLayout)) {
                    readyToCloseExpandableLayout.collapse();
                }
                readyToCloseExpandableLayout = holder.expandableLayout;
            }
        });

        holder.likeIcon.setImageResource(server.isFavourite() ? R.drawable.ic_heart_full : R.drawable.ic_heart);
        holder.headCardView.setOnClickListener(v -> holder.expandableLayout.toggle());

        holder.pingText.setText(String.format(Locale.getDefault(), "%.0f ms", server.getServerPing()));

        holder.encryptionType.setText(server.getEncryptionType());
        holder.portNo.setText(server.getPort());

        holder.countryName.setText(server.getName());
        holder.serverLoadText.setText(String.format(Locale.getDefault(), "%.0f%%", server.getServerLoad()));

        holder.countryImage.setImageResource(holder.itemView.getContext().getResources().getIdentifier("drawable/" + new Locale("", server.getIsoCode().toUpperCase()).getDisplayCountry().toLowerCase().replaceAll(" ", "_"), null, holder.itemView.getContext().getPackageName()));

        if (Objects.equals(activeConnectionCountry, server.getName())) {
            switch (connectionState) {
                default:
                case NOT_CONNECTED:
                    holder.connectDisconnectButton.getBackground().setTint(holder.buttonGreen);
                    holder.connectDisconnectButton.setText(holder.connect);
                    holder.countryImage.setBorderColor(holder.notConnectedCountry);
                    activeConnectionCountry = "";
                    break;
                case CONNECTED:
                    if(!holder.expandableLayout.isExpanded()) {
                        holder.expandableLayout.toggle();
                    }
                    holder.connectDisconnectButton.getBackground().setTint(holder.buttonRed);
                    holder.connectDisconnectButton.setText(holder.disconnect);
                    holder.countryImage.setBorderColor(holder.connectedCountry);
                    break;
                case CONNECTING:
                    holder.connectDisconnectButton.getBackground().setTint(holder.buttonOrange);
                    holder.connectDisconnectButton.setText(holder.stateConnectingText);
                    holder.countryImage.setBorderColor(holder.connectingCountry);
                    break;
            }
        } else {
            holder.countryImage.setBorderColor(holder.notConnectedCountry);
//            holder.expandableLayout.setExpanded(false);
        }
    }

    @Override public int getItemCount() {
        return serverList.size();
    }

    public void updateData(List<Server> serverList) {
        this.serverList = new ArrayList<>(serverList.size());
        this.serverList.addAll(serverList);
        for (int i = 0; i < serverList.size(); i++) {
            expandState.append(i, false);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(BaseRecyclerViewItemClickListener<Server> baseRecyclerViewItemClickListener) {
        this.baseRecyclerViewItemClickListener = baseRecyclerViewItemClickListener;
    }

    class ServerHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.expandableLayout) ExpandableLinearLayout expandableLayout;
        @BindView(R2.id.encryptionContainer) View encryptionContainer;
        @BindView(R2.id.portNoContainer) View portNoContainer;
        @BindView(R2.id.connectDisconnectButton) Button connectDisconnectButton;
        @BindView(R2.id.connectedStatus) View connectedStatus;
        @BindView(R2.id.headCardView) View headCardView;
        @BindView(R2.id.countryImage) CircleImageView countryImage;
        @BindView(R2.id.encryptionType) TextView encryptionType;
        @BindView(R2.id.portNo) TextView portNo;
        @BindView(R2.id.countryName) TextView countryName;
        @BindView(R2.id.serverLoadText) TextView serverLoadText;
        @BindView(R2.id.likeIcon) ImageView likeIcon;
        @BindView(R2.id.pingText) TextView pingText;

        @BindColor(R2.color.notConnectedCountry) int notConnectedCountry;
        @BindColor(R2.color.connectedCountry) int connectedCountry;
        @BindColor(R2.color.connectingCountry) int connectingCountry;
        @BindColor(R2.color.buttonRed) int buttonRed;
        @BindColor(R2.color.buttonOrange) int buttonOrange;
        @BindColor(R2.color.buttonGreen) int buttonGreen;

        @BindString(R2.string.stateConnectingText) String stateConnectingText;
        @BindString(R2.string.disconnect) String disconnect;
        @BindString(R2.string.connect) String connect;

        ServerHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            connectedStatus.setVisibility(View.GONE);

            connectDisconnectButton.setOnClickListener(view -> {
                if(getAdapterPosition() != -1) {
                    baseRecyclerViewItemClickListener.onItemClick(
                            view,
                            getAdapterPosition(),
                            serverList.get(getAdapterPosition()),
                            CONNECT_DISCONNECT
                    );
                }
            });

            encryptionContainer.setOnClickListener(view -> {
                if(getAdapterPosition() != -1) {
                    baseRecyclerViewItemClickListener.onItemClick(
                            view,
                            getAdapterPosition(),
                            serverList.get(getAdapterPosition()),
                            ENCRYPTION
                    );
                }
            });

            portNoContainer.setOnClickListener(view -> {
                if(getAdapterPosition() != -1) {
                    baseRecyclerViewItemClickListener.onItemClick(
                            view,
                            getAdapterPosition(),
                            serverList.get(getAdapterPosition()),
                            PORT_NO
                    );
                }
            });

            likeIcon.setOnClickListener(view -> {
                if(getAdapterPosition() != -1) {
                    baseRecyclerViewItemClickListener.onItemClick(
                            view,
                            getAdapterPosition(),
                            serverList.get(getAdapterPosition()),
                            ADD_TO_FAVOURITE
                    );
                }
            });

            pingText.setOnClickListener(view -> {
                if(getAdapterPosition() != -1) {

                    baseRecyclerViewItemClickListener.onItemClick(
                            view,
                            getAdapterPosition(),
                            serverList.get(getAdapterPosition()),
                            UPDATE_PING
                    );
                }
            });
        }
    }
}
