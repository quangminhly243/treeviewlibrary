package vnpt.it3.treeview_library;

import android.view.View;
import android.widget.TextView;

import vnpt.it3.treeview_library.R;
import vnpt.it3.treeviewlib.TreeNode;
import vnpt.it3.treeviewlib.base.ClickIconNodeViewBinder;

/**
 * Created by zxy on 17/4/23.
 */

public class ThirdLevelNodeViewBinder extends ClickIconNodeViewBinder {
    TextView textView;
    public ThirdLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.node_name_view);
    }


    @Override
    public int getCheckableViewId() {
        return R.id.arrow_img;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_third_level;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        textView.setText(treeNode.getValue().toString());
    }
}
