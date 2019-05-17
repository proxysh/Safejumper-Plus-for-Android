package apps.base.app.views.dialogs;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import apps.base.app.R;
import apps.base.app.R2;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;


public class ModalEncryptionDialogFragment extends BaseDialogFragment {

    public static final String TAG = "ModalEncryptionDialogFragment";
    public static final int SAVE = 0;

    @BindView(R2.id.save) Button save;
    @BindView(R2.id.cancel) Button cancel;
    @BindView(R2.id.itemsContainer) LinearLayout itemsContainer;

    @BindDimen(R2.dimen.dialogItemHeight) int dialogItemHeight;
    @BindDimen(R2.dimen.dialogItemBottomMargin) int dialogItemBottomMargin;
    @BindDimen(R2.dimen.dialogItemStartEndMargin) int dialogItemStartEndMargin;
    @BindDimen(R2.dimen.dialogItemElevation) int dialogItemElevation;
    @BindDimen(R2.dimen.dialogItemTextPadding) int dialogItemTextPadding;

    @BindColor(R2.color.mainWhite) int mainWhite;
    @BindColor(R2.color.mainBlack) int mainBlack;

    private String encryptionType;
    private DialogResultListener<String> baseDialogListener;

    private List<RadioButton> radioButtonList = new ArrayList<>();
    private List<TextView> textViewList = new ArrayList<>();
    private List<String> availableEncryptionTypes;


//    String[] encryptionTypeArray = getEncryptionType();

    @Override protected int resource() {
        return R.layout.dialog_modal_encryption;
    }

    @Override protected void initUI() {

        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);



        for (String nextString : availableEncryptionTypes) {

            CardView nextCardView = new CardView(getContext());
            nextCardView.setCardBackgroundColor(mainWhite);
            nextCardView.setRadius(dialogItemStartEndMargin);
            nextCardView.setCardElevation(dialogItemElevation);
            nextCardView.setContentPadding(dialogItemBottomMargin, dialogItemBottomMargin, dialogItemBottomMargin, dialogItemBottomMargin);
            LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogItemHeight);
            cardViewParams.bottomMargin = dialogItemBottomMargin;
            cardViewParams.leftMargin = dialogItemStartEndMargin;
            cardViewParams.rightMargin = dialogItemStartEndMargin;

            TextView nextTextView = new TextView(getContext());
            nextTextView.setText(nextString);
            nextTextView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_medium.ttf"));
            nextTextView.setTextColor(mainBlack);
            nextTextView.setPadding(dialogItemTextPadding, 0, dialogItemTextPadding, 0);
            nextTextView.setGravity(Gravity.CENTER_VERTICAL);
            textViewList.add(nextTextView);

            RadioButton nextRadioButton = new RadioButton(getContext());
            nextRadioButton.setButtonDrawable(R.drawable.selector_radio_button);

            nextRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    deselectAll();
                    buttonView.setChecked(true);
                }
            });
            radioButtonList.add(nextRadioButton);

            FrameLayout.LayoutParams textViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            nextCardView.addView(nextTextView, textViewLayoutParams);
            nextCardView.addView(nextRadioButton, textViewLayoutParams);
            itemsContainer.addView(nextCardView, cardViewParams);
        }

        setCheckedEncryption(encryptionType);

        save.setOnClickListener(v -> {
            String encryption = getCheckedEncryption();
            if(baseDialogListener != null) {
                baseDialogListener.onDialogResult(encryption);
            }
            dismiss();
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private String getCheckedEncryption() {
        for(byte index = 0; index < radioButtonList.size(); index++) {
            RadioButton nextRadioButton = radioButtonList.get(index);
            if (nextRadioButton.isChecked()) {
                return textViewList.get(index).getText().toString();
            }
        }
        return availableEncryptionTypes.get(0);
    }

    private void setCheckedEncryption(String encryption) {
        for(byte index = 0; index < textViewList.size(); index++) {
            TextView nextTextView = textViewList.get(index);
            if (nextTextView.getText().toString().equals(encryption)) {
                radioButtonList.get(index).setChecked(true);
                return;
            }
        }
        radioButtonList.get(0).setChecked(true);
    }

    private void deselectAll() {
        for (RadioButton nextRadioButton : radioButtonList) {
            nextRadioButton.setChecked(false);
        }
    }

    public void setEncryptionType(String encryptionType, List<String> availableEncryptionTypes) {
        this.availableEncryptionTypes = availableEncryptionTypes;
        if (encryptionType == null) {
            this.encryptionType = availableEncryptionTypes.get(0);
            return;
        }
        this.encryptionType = encryptionType;
    }

    public void setListener(DialogResultListener<String> baseDialogListener) {
        this.baseDialogListener = baseDialogListener;
    }


}
