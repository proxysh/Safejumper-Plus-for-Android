package apps.base.app.views.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import apps.base.app.R;


public class OnBoardingViewPagerAdapter extends PagerAdapter {

    private int[] imageResources;
    private int[] titleResources;
    private int[] textResources;

    public OnBoardingViewPagerAdapter(int[] imageResources, int[] titleResources, int[] textResources) {
        this.imageResources = imageResources;
        this.titleResources = titleResources;
        this.textResources = textResources;
    }

    @Override public int getCount() {
        return imageResources.length;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public View instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_on_boaring, container, false);

        ImageView imageView = itemView.findViewById(R.id.onBoardingImage);
        imageView.setImageResource(imageResources[position]);
        TextView title = itemView.findViewById(R.id.onBoardingTitle);
        title.setText(titleResources[position]);
        TextView text = itemView.findViewById(R.id.onBoardingText);
        text.setText(textResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}