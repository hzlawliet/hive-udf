package com.wanghengzhi.bigdata.distribute.xgboost;

/**
 * Created by wanghengzhi on 2019/3/28.
 */
public class TreeNode {
    private Integer leftChild;
    private Integer rightChild;
    private Integer missChild;
    private Integer featureId;
    private Double threshold;
    private Double value;

    public Integer getLeftChild() {
        return leftChild;
    }
    public void setLeftChild(Integer leftChild) {
        this.leftChild = leftChild;
    }
    public Integer getFeatureId() {
        return featureId;
    }
    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }
    public Double getThreshold() {
        return threshold;
    }
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    public Integer getRightChild() {
        return rightChild;
    }
    public void setRightChild(Integer rightChild) {
        this.rightChild = rightChild;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getMissChild() {
        return missChild;
    }

    public void setMissChild(Integer missChild) {
        this.missChild = missChild;
    }
}
