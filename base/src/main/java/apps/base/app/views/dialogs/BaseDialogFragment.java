package apps.base.app.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import apps.base.app.R;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseDialogFragment extends DialogFragment {

    private Unbinder unbinder;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getContentView(inflater, container);
    }

    private View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View rootView = inflater.inflate(resource(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            final FrameLayout root = new FrameLayout(getContext());
            root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.setContentView(root);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return dialog;
    }

    @LayoutRes protected abstract int resource();
    protected abstract void initUI();

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // The view will catch the event so that it will not be passed to the bottom fragment
        view.setClickable(true);
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
