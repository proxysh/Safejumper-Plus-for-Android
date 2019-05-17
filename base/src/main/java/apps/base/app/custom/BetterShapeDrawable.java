package apps.base.app.custom;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class BetterShapeDrawable extends GradientDrawable {

    public BetterShapeDrawable(int strokeWidth, int strokeColor, float cornerRadius) {
        super(Orientation.BOTTOM_TOP,new int[]{Color.WHITE, Color.WHITE});
        setStroke(strokeWidth, strokeColor);
        setShape(GradientDrawable.RECTANGLE);
        setCornerRadius(cornerRadius);
    }

}
