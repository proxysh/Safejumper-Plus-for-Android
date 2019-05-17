package apps.base.app.views.dialogs;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.models.Logs;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;

public class LogsDialogFragment extends BaseDialogFragment {

    public static final String TAG = "LogsDialogFragment";

    @BindView(R2.id.logsTextView) TextView logsTextView;
    @BindView(R2.id.copyLogs) Button copyLogs;
    @BindView(R2.id.close) Button close;

    @BindView(R2.id.appLogs) TextView appLogs;
    @BindView(R2.id.vpnLogs) TextView vpnLogs;

    @BindColor(R2.color.mainColor) int mainColor;
    @BindColor(R2.color.switchGray) int switchGray;

    @BindString(R2.string.copiedToClipboard) String copiedToClipboard;
    @BindString(R2.string.noLogs) String noLogs;

    private Logs logs;

    @Override protected int resource() {
        return R.layout.dialog_logs;
    }

    @Override protected void initUI() {
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);

        appLogs.setOnClickListener(v -> {
            ColorDrawable background = (ColorDrawable) v.getBackground();
            if(background.getColor() != mainColor) {
                v.setBackgroundColor(mainColor);
                vpnLogs.setBackgroundColor(switchGray);
                logsTextView.setText(logs.getAppLogs().length() > 0 ? logs.getAppLogs() : noLogs);
            }
        });



        vpnLogs.setOnClickListener(v -> {
            ColorDrawable background = (ColorDrawable) v.getBackground();
            if(background.getColor() != mainColor) {
                v.setBackgroundColor(mainColor);
                appLogs.setBackgroundColor(switchGray);
                logsTextView.setText(logs.getVpnLogs().length() > 0 ? logs.getVpnLogs() : noLogs);
            }
        });

        logsTextView.setText(logs.getAppLogs().length() > 0 ? logs.getAppLogs() : noLogs);

        copyLogs.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("shieldtra-log", logsTextView.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), copiedToClipboard, Toast.LENGTH_SHORT).show();
        });

        close.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setLogs(Logs logs) {
        this.logs = logs;
    }
}
