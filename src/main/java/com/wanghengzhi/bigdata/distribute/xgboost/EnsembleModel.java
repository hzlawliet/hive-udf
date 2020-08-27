package com.wanghengzhi.bigdata.distribute.xgboost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghengzhi on 2019/3/28.
 */
public class EnsembleModel {
    private List<TreeModel> trees;
    private Double prior;
    private Double learningRate;

    public EnsembleModel() {
        List<TreeModel> trees = new ArrayList<TreeModel>();
        this.trees = trees;
    }
    public List<TreeModel> getTrees() {
        return trees;
    }

    public void setTrees(List<TreeModel> trees) {
        this.trees = trees;
    }

    public Double getPrior() {
        return prior;
    }

    public void setPrior(Double prior) {
        this.prior = prior;
    }

    public Double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(Double learningRate) {
        this.learningRate = learningRate;
    }
}
