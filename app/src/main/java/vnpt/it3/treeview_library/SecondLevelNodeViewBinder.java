package vnpt.it3.treeview_library;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vnpt.it3.treeview_library.R;
import vnpt.it3.treeviewlib.TreeNode;
import vnpt.it3.treeviewlib.base.ClickIconNodeViewBinder;

/**
 * Created by zxy on 17/4/23.
 */

public class SecondLevelNodeViewBinder extends ClickIconNodeViewBinder {

    TextView textView;
    ImageView imageView;
    public SecondLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.node_name_view);
        imageView = (ImageView) itemView.findViewById(R.id.arrow_img);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.arrow_img;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_second_level;
    }

    @Override
    public void bindView(final TreeNode treeNode) {
        textView.setText(treeNode.getValue().toString());
        imageView.setRotation(treeNode.isExpanded() ? 90 : 0);
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {
        if (expand) {
            imageView.animate().rotation(90).setDuration(200).start();
        } else {
            imageView.animate().rotation(0).setDuration(200).start();
        }
    }
}
