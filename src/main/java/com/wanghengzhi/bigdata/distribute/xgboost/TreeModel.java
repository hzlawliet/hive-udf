package com.wanghengzhi.bigdata.distribute.xgboost;

import java.util.List;

/**
 * Created by wanghengzhi on 2019/3/28.
 */
public class TreeModel {
    private List<TreeNode> tree;

    public List<TreeNode> getTree() {
        return tree;
    }

    public void setTree(List<TreeNode> tree) {
        this.tree = tree;
    }
}
