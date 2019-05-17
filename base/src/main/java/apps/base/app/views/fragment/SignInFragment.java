package apps.base.app.views.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerPresentersComponent;
import apps.base.app.presenters.SignInFragmentPresenter;
import apps.base.app.utils.ConfigManager;
import butterknife.BindString;
import butterknife.BindView;

import static apps.base.app.utils.Utils.isEmailValid;

public class SignInFragment extends BaseFragment {

    public static final String TAG = "SignInFragment";

    @BindView(R2.id.back) View back;
    @BindView(R2.id.passwordEdiText) TextInputEditText passwordEdiText;
    @BindView(R2.id.emailEditText) TextInputEditText emailEditText;
    @BindView(R2.id.passwordTextInputLayout) TextInputLayout passwordTextInputLayout;
    @BindView(R2.id.emailTextInputLayout) TextInputLayout emailTextInputLayout;
    @BindView(R2.id.forgotPassword) View forgotPassword;
    @BindView(R2.id.login) Button login;
    @BindView(R2.id.signUpButton) Button signUpButton;
    @BindView(R2.id.checkboxContainer) View checkboxContainer;
    @BindView(R2.id.rememberMeCheckBox) CheckBox rememberMeCheckBox;

    @BindString(R2.string.username) String username;

    @Inject SignInFragmentPresenter presenter;

    @Override protected int resource() {
        return R.layout.fragment_sign_in;
    }

    @Override protected void initUI() {

        back.setOnClickListener(view -> presenter.onBackPressed());
        checkboxContainer.setOnClickListener(v -> rememberMeCheckBox.setChecked(!rememberMeCheckBox.isChecked()));
        login.setOnClickListener(v -> {
            if (isValidationPassed()) {
                presenter.onLoginAction(
                        emailEditText.getText().toString(),
                        passwordEdiText.getText().toString(),
                        !rememberMeCheckBox.isChecked()

                );
            }
        });
        signUpButton.setOnClickListener(v -> presenter.onSignUpAction());
        forgotPassword.setOnClickListener(v -> presenter.onForgotPasswordAction());
        emailTextInputLayout.setTypeface(Typeface.createFromAsset(emailTextInputLayout.getContext().getAssets(), "fonts/roboto_bold.ttf"));
        passwordTextInputLayout.setTypeface(Typeface.createFromAsset(passwordTextInputLayout.getContext().getAssets(), "fonts/roboto_bold.ttf"));


        int fiveDp = getResources().getDimensionPixelSize(R.dimen.editText);
        int twelveSp = getResources().getDimensionPixelSize(R.dimen.toggleButton);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, fiveDp + twelveSp);
        emailEditText.setLayoutParams(params);
        passwordEdiText.setLayoutParams(params);

        params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END;
        params.setMargins(0, 0, 0, (fiveDp + twelveSp));
        CheckableImageButton togglePasswordButton = findTogglePasswordButton(passwordTextInputLayout);
        togglePasswordButton.setLayoutParams(params);

        if(ConfigManager.getField("useUsername")) {
            emailTextInputLayout.setHint(username);
            emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }

        emailEditText.setOnFocusChangeListener((view, hasFocus) -> onTextInputEditTextFocus(fiveDp, twelveSp, (TextInputEditText) view, hasFocus));
        passwordEdiText.setOnFocusChangeListener((view, hasFocus) -> onTextInputEditTextFocus(fiveDp, twelveSp, (TextInputEditText) view, hasFocus));

    }

    private boolean isValidationPassed() {

        if(!ConfigManager.<Boolean>getField("useUsername")) {
            if (!isEmailValid(emailEditText.getText())) {
                emailTextInputLayout.setError(" ");
            } else {
                emailTextInputLayout.setError(null);
            }
        }

        if(passwordEdiText.getText().length() < 4) {
            passwordTextInputLayout.setError(" ");
        } else {
            passwordTextInputLayout.setError(null);
        }

        if(ConfigManager.getField("useUsername")) {
            return passwordEdiText.getText().length() > 4;
        }
        return isEmailValid(emailEditText.getText()) && passwordEdiText.getText().length() > 4;
    }

    private void onTextInputEditTextFocus(int fiveDp, int twelveSp, TextInputEditText view, boolean hasFocus) {
        TextInputLayout textInputLayout = getTextInputLayout((view));
        if (view.length() > 0 || textInputLayout.getHint() == null) {
            return;
        }

        FrameLayout.LayoutParams tempTextInputEditTextLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        String hintText = textInputLayout.getHint().toString();
        if (hasFocus) {
            tempTextInputEditTextLayoutParams.setMargins(0, 0, 0, fiveDp);
            view.setLayoutParams(tempTextInputEditTextLayoutParams);
            textInputLayout.setHint(hintText.substring(0, 1).concat(hintText.substring(1, hintText.length()).toUpperCase()));
        } else {
            tempTextInputEditTextLayoutParams.setMargins(0, 0, 0, (fiveDp + twelveSp));
            view.setLayoutParams(tempTextInputEditTextLayoutParams);
            textInputLayout.setHint(hintText.substring(0, 1).concat(hintText.substring(1, hintText.length()).toLowerCase()));
        }
    }

    private CheckableImageButton findTogglePasswordButton(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int ind = 0; ind < childCount; ind++) {
            View child = viewGroup.getChildAt(ind);
            if (child instanceof ViewGroup) {
                View togglePasswordButton = findTogglePasswordButton((ViewGroup) child);
                if (togglePasswordButton != null) {
                    return (CheckableImageButton) togglePasswordButton;
                }
            } else if (child instanceof CheckableImageButton) {
                return (CheckableImageButton) child;
            }
        }
        return new CheckableImageButton(viewGroup.getContext());
    }

    private TextInputLayout getTextInputLayout(TextInputEditText view) {
        for (ViewParent parent = view.getParent(); parent instanceof View; parent = parent.getParent()) {
            if (parent instanceof TextInputLayout) {
                return (TextInputLayout) parent;
            }
        }
        return new TextInputLayout(view.getContext());
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

    @Override public void onDestroyView() {
        presenter.onDestroyView();
        super.onDestroyView();
    }
}
