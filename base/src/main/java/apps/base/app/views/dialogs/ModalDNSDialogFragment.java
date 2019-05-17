package apps.base.app.views.dialogs;

import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;


import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.models.DefaultDNS;
import butterknife.BindView;

public class ModalDNSDialogFragment extends BaseDialogFragment {

    public static final String TAG = "ModalDNSDialogFragment";

    @BindView(R2.id.save) Button save;
    @BindView(R2.id.cancel) Button cancel;
    @BindView(R2.id.primaryDNSTextInputEditText) TextInputEditText primaryDNSTextInputEditText;
    @BindView(R2.id.secondaryDNSTextInputEditText) TextInputEditText secondaryDNSTextInputEditText;

    private DialogResultListener<DefaultDNS> dialogResultListener;
    private DefaultDNS defaultDNSIps;

    @Override protected int resource() {
        return R.layout.dialog_modal_dns;
    }

    @Override protected void initUI() {

        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);

        primaryDNSTextInputEditText.setText(defaultDNSIps.getPrimaryDNS());
        secondaryDNSTextInputEditText.setText(defaultDNSIps.getSecondaryDNS());

        save.setOnClickListener(v -> {
            defaultDNSIps.setPrimaryDNS(primaryDNSTextInputEditText.getText().toString());
            defaultDNSIps.setSecondaryDNS(secondaryDNSTextInputEditText.getText().toString());
            if(dialogResultListener != null) {
                dialogResultListener.onDialogResult(defaultDNSIps);
            }
            dismiss();
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setListener(DialogResultListener<DefaultDNS> dialogResultListener) {
        this.dialogResultListener = dialogResultListener;
    }

    public void setDNSIps(DefaultDNS defaultDNSIps) {
        this.defaultDNSIps = defaultDNSIps;


    }
}
