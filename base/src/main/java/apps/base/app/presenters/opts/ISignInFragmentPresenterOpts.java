package apps.base.app.presenters.opts;

public interface ISignInFragmentPresenterOpts {

    void onForgotPasswordAction();
    void onSignUpAction();
    void onLoginAction(String email, String password, boolean isTempUser);
}
