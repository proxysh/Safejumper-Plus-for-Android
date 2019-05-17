package apps.base.app.views.dialogs;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import apps.base.app.R;
import apps.base.app.R2;
import butterknife.BindString;
import butterknife.BindView;

public class InAppOfferDialogFragment extends BaseDialogFragment {

    public static final String TAG = "InAppOfferDialogFragment";
    public static final int SHOP_NOW = 0;

    @BindView(R2.id.firstOfferText) TextView firstOfferText;
    @BindView(R2.id.secondOfferText) TextView secondOfferText;
    @BindView(R2.id.shopNow) Button shopNow;
    @BindView(R2.id.cancel) Button cancel;

    @BindString(R2.string.christmasOfferTextFirst) String christmasOfferTextFirst;
    @BindString(R2.string.christmasOfferTextSecond) String christmasOfferTextSecond;
    private BaseDialogListener baseDialogListener;

    @Override protected int resource() {
        return R.layout.dialog_in_app_offer;
    }

    @Override protected void initUI() {

        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);

        String firstBoldText = "50% off";
        String secondBoldText = "“XMAS50”";

        SpannableStringBuilder firstOfferSpannable = new SpannableStringBuilder(christmasOfferTextFirst);
        firstOfferSpannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), christmasOfferTextFirst.indexOf(firstBoldText), christmasOfferTextFirst.indexOf(firstBoldText) + firstBoldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder secondOfferSpannable = new SpannableStringBuilder(christmasOfferTextSecond);
        secondOfferSpannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), christmasOfferTextSecond.indexOf(secondBoldText), christmasOfferTextSecond.indexOf(secondBoldText) + secondBoldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        firstOfferText.setText(firstOfferSpannable);
        secondOfferText.setText(secondOfferSpannable);


        shopNow.setOnClickListener(v -> {
            if(baseDialogListener != null) {
                baseDialogListener.onDialogClickAction(SHOP_NOW);
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
