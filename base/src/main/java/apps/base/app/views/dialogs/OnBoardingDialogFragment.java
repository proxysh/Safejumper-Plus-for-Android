package apps.base.app.views.dialogs;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;


import apps.base.app.R;
import apps.base.app.R2;
import apps.base.app.custom.BetterShapeDrawable;
import apps.base.app.custom.CircleIndicators;
import apps.base.app.views.adapters.OnBoardingViewPagerAdapter;
import butterknife.BindColor;
import butterknife.BindView;

public class OnBoardingDialogFragment extends BaseDialogFragment {

    public static final String TAG = "OnBoardingDialogFragment";

    private static final int CREATE_ACCOUNT = 0;
    public static final int LOGIN = 1;

    @BindView(R2.id.onBoardingViewPager) ViewPager onBoardingViewPager;
    @BindView(R2.id.viewPagerCircleIndicator) CircleIndicators viewPagerCircleIndicator;
    @BindView(R2.id.beginTheExperience) Button beginTheExperience;
    @BindView(R2.id.login) Button login;

    @BindColor(R2.color.mainColor) int mainColor;
    private BaseDialogListener baseDialogListener;

    @Override protected int resource() {
        return R.layout.dialog_on_boarding;
    }

    @Override protected void initUI() {

        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);

        float density = getResources().getDisplayMetrics().density;
        BetterShapeDrawable shapeDrawable = new BetterShapeDrawable((int) (2 * density), mainColor, (int) (5 * density));
        login.setBackground(shapeDrawable);

        beginTheExperience.setOnClickListener(v -> {
//            if(baseDialogListener != null) {
//                baseDialogListener.onDialogClickAction(CREATE_ACCOUNT);
//            }
            dismiss();
        });

        login.setOnClickListener(v -> {
            if(baseDialogListener != null) {
                baseDialogListener.onDialogClickAction(LOGIN);
            }
            dismiss();
        });

        onBoardingViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        int[] imageResources = {
                R.drawable.on_boarding_one,
                R.drawable.on_boarding_two,
                R.drawable.on_boarding_three,
                R.drawable.on_boarding_four,
                R.drawable.on_boarding_five,
                R.drawable.on_boarding_six
        };

        int[] titleResources = {
                R.string.onBoardingTitleOne,
                R.string.onBoardingTitleTwo,
                R.string.onBoardingTitleThree,
                R.string.onBoardingTitleFour,
                R.string.onBoardingTitleFive,
                R.string.onBoardingTitleSix
        };

        int[] textResources = {
                R.string.onBoardingTextOne,
                R.string.onBoardingTextTwo,
                R.string.onBoardingTextThree,
                R.string.onBoardingTextFour,
                R.string.onBoardingTextFive,
                R.string.onBoardingTextSix
        };

        OnBoardingViewPagerAdapter adapter = new OnBoardingViewPagerAdapter(imageResources, titleResources, textResources);
        onBoardingViewPager.setAdapter(adapter);
        viewPagerCircleIndicator.setupWithViewPager(onBoardingViewPager);

        onBoardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override public void onPageSelected(int position) {}

            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

    public void setListener(BaseDialogListener baseDialogListener) {
        this.baseDialogListener = baseDialogListener;
    }
}
