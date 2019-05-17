package apps.base.app.views.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getContentView(inflater, container);
    }

    private View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View rootView = inflater.inflate(resource(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
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
