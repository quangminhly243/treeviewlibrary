
package vnpt.it3.treeviewlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import vnpt.it3.treeviewlib.base.BaseNodeViewBinder;
import vnpt.it3.treeviewlib.base.BaseNodeViewFactory;
import vnpt.it3.treeviewlib.base.ClickIconNodeViewBinder;
import vnpt.it3.treeviewlib.helper.TreeHelper;


public class TreeViewAdapter extends RecyclerView.Adapter {

    private Context context;

    private TreeNode root;

    private List<TreeNode> expandedNodeList;

    private BaseNodeViewFactory baseNodeViewFactory;

    private View EMPTY_PARAMETER;

    private TreeView treeView;

    private OnClickItemListener mListener;
    //private TreeNode.TreeNodeClickListener mListener;
    //private OnClickItemListener mListener;

    TreeViewAdapter(Context context, TreeNode root,
                    @NonNull BaseNodeViewFactory baseNodeViewFactory, OnClickItemListener listener) { // TreeNode.TreeNodeClickListener listener) {
        this.context = context;
        this.root = root;
        this.baseNodeViewFactory = baseNodeViewFactory;
        this.mListener = listener;
        this.EMPTY_PARAMETER = new View(context);
        this.expandedNodeList = new ArrayList<>();

        buildExpandedNodeList();
    }

    private void buildExpandedNodeList() {
        expandedNodeList.clear();

        for (TreeNode child : root.getChildren()) {
            insertNode(expandedNodeList, child);
        }
    }

    private void insertNode(List<TreeNode> nodeList, TreeNode treeNode) {
        nodeList.add(treeNode);

        if (!treeNode.hasChild()) {
            return;
        }
        if (treeNode.isExpanded()) {
            for (TreeNode child : treeNode.getChildren()) {
                insertNode(nodeList, child);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return expandedNodeList.get(position).getLevel();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int level) {
        View view=LayoutInflater.from(context).inflate(baseNodeViewFactory.getNodeViewBinder(EMPTY_PARAMETER,level).getLayoutId(),parent,false);
        BaseNodeViewBinder nodeViewBinder=baseNodeViewFactory.getNodeViewBinder(view,level);
        nodeViewBinder.setTreeView(treeView);
        return nodeViewBinder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final View nodeView = holder.itemView;
        final TreeNode treeNode = expandedNodeList.get(position);
        //final BaseNodeViewBinder viewBinder = (BaseNodeViewBinder) holder;
        final ClickIconNodeViewBinder viewBinder = (ClickIconNodeViewBinder) holder;
        if (viewBinder instanceof ClickIconNodeViewBinder) {
            final View view = nodeView.findViewById(viewBinder.getCheckableViewId());
            if (view instanceof ImageView) {
                final ImageView checkableView = (ImageView) view;
                if (viewBinder.getToggleTriggerViewId() != 0) {
                    View triggerToggleView = nodeView.findViewById(viewBinder.getToggleTriggerViewId());

                    if (triggerToggleView != null) {
                        checkableView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onNodeToggled(treeNode);
                                viewBinder.onNodeToggled(treeNode, treeNode.isExpanded());
                            }
                        });
                    }
                } else if (treeNode.isItemClickEnable()) {
                    checkableView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNodeToggled(treeNode);
                            viewBinder.onNodeToggled(treeNode, treeNode.isExpanded());
                        }
                    });
                }
            } else {
                throw new ClassCastException("The getCheckableViewId() " +
                        "must return a ImageView's id");
            }


            if (viewBinder.getToggleTriggerViewId() != 0) {
                View triggerToggleView = nodeView.findViewById(viewBinder.getToggleTriggerViewId());

                if (triggerToggleView != null) {
                    triggerToggleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (treeNode.getClickListener() != null) {
                                treeNode.getClickListener().onClick(treeNode, treeNode.getValue());
                            } else
                            if (mListener != null) {
                                mListener.onDetailClick(treeNode, treeNode.getValue());
                            }
                        }
                    });
                }
            } else if (treeNode.isItemClickEnable()) {
                nodeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (treeNode.getClickListener() != null) {
                            treeNode.getClickListener().onClick(treeNode, treeNode.getValue());
                        } else if (mListener != null) {
                            mListener.onDetailClick(treeNode, treeNode.getValue());
                        }
                    }
                });
            }

        }
        viewBinder.bindView(treeNode);
    }

    private void selectChildren(TreeNode treeNode, boolean checked) {
        List<TreeNode> impactedChildren = TreeHelper.selectNodeAndChild(treeNode, checked);
        int index = expandedNodeList.indexOf(treeNode);
        if (index != -1 && impactedChildren.size() > 0) {
            notifyItemRangeChanged(index, impactedChildren.size() + 1);
        }
    }

    private void selectParentIfNeed(TreeNode treeNode, boolean checked) {
        List<TreeNode> impactedParents = TreeHelper.selectParentIfNeedWhenNodeSelected(treeNode, checked);
        if (impactedParents.size() > 0) {
            for (TreeNode parent : impactedParents) {
                int position = expandedNodeList.indexOf(parent);
                if (position != -1) notifyItemChanged(position);
            }
        }
    }

    private void onNodeToggled(TreeNode treeNode) {
        treeNode.setExpanded(!treeNode.isExpanded());

        if (treeNode.isExpanded()) {
            expandNode(treeNode);
        } else {
            collapseNode(treeNode);
        }
    }

    @Override
    public int getItemCount() {
        return expandedNodeList == null ? 0 : expandedNodeList.size();
    }

    /**
     * Refresh all,this operation is only used for refreshing list when a large of nodes have
     * changed value or structure because it take much calculation.
     */
    void refreshView() {
        buildExpandedNodeList();
        notifyDataSetChanged();
    }

    // Insert a node list after index.
    private void insertNodesAtIndex(int index, List<TreeNode> additionNodes) {
        if (index < 0 || index > expandedNodeList.size() - 1 || additionNodes == null) {
            return;
        }
        expandedNodeList.addAll(index + 1, additionNodes);
        notifyItemRangeInserted(index + 1, additionNodes.size());
    }

    //Remove a node list after index.
    private void removeNodesAtIndex(int index, List<TreeNode> removedNodes) {
        if (index < 0 || index > expandedNodeList.size() - 1 || removedNodes == null) {
            return;
        }
        expandedNodeList.removeAll(removedNodes);
        notifyItemRangeRemoved(index + 1, removedNodes.size());
    }

    /**
     * Expand node. This operation will keep the structure of children(not expand children)
     */
    void expandNode(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }
        List<TreeNode> additionNodes = TreeHelper.expandNode(treeNode, false);
        int index = expandedNodeList.indexOf(treeNode);

        insertNodesAtIndex(index, additionNodes);
    }


    /**
     * Collapse node. This operation will keep the structure of children(not collapse children)
     */
    void collapseNode(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }
        List<TreeNode> removedNodes = TreeHelper.collapseNode(treeNode, false);
        int index = expandedNodeList.indexOf(treeNode);

        removeNodesAtIndex(index, removedNodes);
    }

    /**
     * Delete a node from list.This operation will also delete its children.
     */
    void deleteNode(TreeNode node) {
        if (node == null || node.getParent() == null) {
            return;
        }
        List<TreeNode> allNodes = TreeHelper.getAllNodes(root);
        if (allNodes.indexOf(node) != -1) {
            node.getParent().removeChild(node);
        }

        //remove children form list before delete
        collapseNode(node);

        int index = expandedNodeList.indexOf(node);
        if (index != -1) {
            expandedNodeList.remove(node);
        }
        notifyItemRemoved(index);
    }

    void setTreeView(TreeView treeView) {
        this.treeView = treeView;
    }

    /*void setOnClickCartListener(OnClickItemListener listener) {
        mListener = listener;
    }*/

    public interface OnClickItemListener {

        void onDetailClick(TreeNode treeNode, Object position);
    }
}
