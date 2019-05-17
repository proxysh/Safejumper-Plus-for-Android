package apps.base.app.views.dialogs;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;


import apps.base.app.R;
import apps.base.app.R2;
import butterknife.BindView;

public class ContactUsDialogFragment extends BaseDialogFragment {

    public static final String TAG = "ContactUsDialogFragment";
    public static final int CONTACT_US = 0;

    @BindView(R2.id.contactUs) Button contactUs;
    @BindView(R2.id.messageContainer) View messageContainer;
    private BaseDialogListener baseDialogListener;

    @Override protected int resource() {
        return R.layout.dialog_contact_us;
    }

    @Override protected void initUI() {
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);


        messageContainer.setClickable(true);

        contactUs.setOnClickListener(v -> {
            if(baseDialogListener != null) {
                baseDialogListener.onDialogClickAction(CONTACT_US);
            }
            dismiss();
        });

        if(getView() != null) {
            getView().setOnClickListener(v -> {
                dismiss();
            });
        }

    }

    public void setListener(BaseDialogListener baseDialogListener) {
        this.baseDialogListener = baseDialogListener;
    }
}
