package apps.base.app.views.adapters;

import android.view.View;

public interface BaseRecyclerViewItemClickListener<Model> {
    void onItemClick(View itemView, int position, Model model, int clickType);
}