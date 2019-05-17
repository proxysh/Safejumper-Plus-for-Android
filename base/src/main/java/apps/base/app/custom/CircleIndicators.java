package apps.base.app.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import apps.base.app.R;


public class CircleIndicators extends FrameLayout {

    private int selectedColor;
    private int unselectedColor;
    private int indicatorsMargin;
    private MarginLayoutParams params;
    private LinearLayout unselectedContainer;
    private ImageView slidingIndicator;

    protected int displayWidth;
    private int indicatorsSize;

    public CircleIndicators(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleIndicators,
                0,
                0
        );
        try {
            selectedColor = a.getColor(
                    R.styleable.CircleIndicators_selectedColor,
                    ContextCompat.getColor(getContext(), R.color.mainColor)
            );

            unselectedColor = a.getColor(
                    R.styleable.CircleIndicators_unselectedColor,
                    ContextCompat.getColor(getContext(), R.color.mainColor)
            );

            indicatorsMargin = a.getDimensionPixelSize(
                    R.styleable.CircleIndicators_indicatorsMargin,
                    context.getResources().getDimensionPixelOffset(R.dimen.viewPagerIndicatorMargin)
            );

            indicatorsSize = a.getDimensionPixelSize(
                    R.styleable.CircleIndicators_indicatorsSize,
                    context.getResources().getDimensionPixelOffset(R.dimen.viewPagerIndicatorSize)
            );
        } finally {
            a.recycle();
        }
        unselectedContainer = new LinearLayout(context, attrs);
        unselectedContainer.setOrientation(LinearLayout.HORIZONTAL);
        params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        ((LayoutParams)params).gravity = Gravity.CENTER_VERTICAL;
        unselectedContainer.setLayoutParams(params);
    }

    public void setupWithViewPager(ViewPager viewPager) {
        removeAllViews();
        Bitmap indicatorBitmap = getStrokedCircleBitmap(indicatorsSize, unselectedColor);
        params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(indicatorsMargin/2, 0, indicatorsMargin/2, 0);
        for(int i=0; i<viewPager.getAdapter().getCount(); i++) {
            slidingIndicator = new ImageView(getContext());
            slidingIndicator.setImageBitmap(indicatorBitmap);
            unselectedContainer.addView(slidingIndicator, params);
        }
        slidingIndicator = new ImageView(getContext());
        int outFit = 3;
        slidingIndicator.setImageBitmap(getFilledCircleBitmap(indicatorsSize + outFit, selectedColor));
        addView(unselectedContainer);
        params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(indicatorsMargin/2 - outFit/2, 0, 0, 0);
        addView(slidingIndicator, params);
        final int indicatorBacklash = indicatorsSize + indicatorsMargin;
        final float coefficient = displayWidth/ indicatorBacklash;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                slidingIndicator.setTranslationX((position * indicatorBacklash) + (positionOffsetPixels / coefficient));
            }

            @Override public void onPageSelected(int position) {

            }

            @Override public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private Bitmap getStrokedCircleBitmap(int indicatorsSize, int color) {
        Bitmap output = Bitmap.createBitmap(indicatorsSize, indicatorsSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int x = indicatorsSize;
        int y = indicatorsSize;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(indicatorsSize/7);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        canvas.drawBitmap(output, 0.0f, 0.0f, paint);
        paint.setColor(color);
        canvas.drawCircle(x/2, y/2, indicatorsSize/6 + indicatorsSize/4, paint);
        return output;
    }

    public Bitmap getFilledCircleBitmap(int dimenSize, int mColor) {
        Bitmap output = Bitmap.createBitmap(dimenSize, dimenSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int x = dimenSize;
        int y = dimenSize;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        canvas.drawBitmap(output, 0.0f, 0.0f, paint);
        paint.setColor(mColor);
        canvas.drawCircle(x/2, y/2, dimenSize/2, paint);
        return output;
    }
}
