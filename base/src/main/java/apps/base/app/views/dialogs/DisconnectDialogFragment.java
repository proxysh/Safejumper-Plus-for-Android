package apps.base.app.views.dialogs;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import apps.base.app.R;
import apps.base.app.R2;
import butterknife.BindString;
import butterknife.BindView;

import static apps.base.app.views.activities.MainActivity.activeConnectionCountry;


public class DisconnectDialogFragment extends BaseDialogFragment {

    public static final String TAG = "DisconnectDialogFragment";
    public static final int DISCONNECT = 0;

    @BindView(R2.id.disconnect) Button disconnect;
    @BindView(R2.id.cancel) Button cancel;
    @BindView(R2.id.disconnectTextView) TextView disconnectTextView;

    @BindString(R2.string.disconnectText) String disconnectText;

    private BaseDialogListener baseDialogListener;

    @Override protected int resource() {
        return R.layout.dialog_disconect;
    }

    @Override protected void initUI() {

        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);


        disconnectTextView.setText(String.format(disconnectText, activeConnectionCountry));

        disconnect.setOnClickListener(v -> {
            if(baseDialogListener != null) {
                baseDialogListener.onDialogClickAction(DISCONNECT);
            }
            dismiss();
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });

    }

    public void setListener(BaseDialogListener baseDialogListener) {
        this.baseDialogListener = baseDialogListener;
    }
}
