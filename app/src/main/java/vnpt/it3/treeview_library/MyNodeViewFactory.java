package vnpt.it3.treeview_library;

import android.view.View;

import vnpt.it3.treeviewlib.base.BaseNodeViewBinder;
import vnpt.it3.treeviewlib.base.BaseNodeViewFactory;



public class MyNodeViewFactory extends BaseNodeViewFactory {

    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {
        switch (level) {
            case 0:
                return new FirstLevelNodeViewBinder(view);
            case 1:
                return new SecondLevelNodeViewBinder(view);
            case 2:
                return new ThirdLevelNodeViewBinder(view);
            default:
                return null;
        }
    }
}
