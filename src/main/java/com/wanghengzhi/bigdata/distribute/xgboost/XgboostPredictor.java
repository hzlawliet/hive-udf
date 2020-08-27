package com.wanghengzhi.bigdata.distribute.xgboost;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghengzhi on 2019/3/28.
 */
public class XgboostPredictor extends UDF {
    private static EnsembleModel pagModel, driverModel;

    static {
        loadTreeModels(".conf", "pag");
        loadTreeModels(".conf", "driver");
    }

    public String evaluate(String modelType, Double missing, String feature) {
        List<TreeModel> treeModel;
        if (modelType.equals("pag")) {
            treeModel = pagModel.getTrees();
        } else {
            treeModel = driverModel.getTrees();
        }
        String[] features = feature.trim().split(",");
        double[] feaVec = new double[features.length];
        int count = 0;
        for (String kk : features) {
            if (NumberUtils.isCreatable(kk)) {
                feaVec[count++] = Double.valueOf(kk);
            } else {
                feaVec[count++] = missing;
            }
        }

        return predictProba(feaVec, treeModel, missing).toString();
    }

    public static void loadTreeModels(String path, String modelType) {
        EnsembleModel tmpModel = new EnsembleModel();
        InputStream inputStream = XgboostPredictor.class.getClassLoader().getResourceAsStream(path);
        BufferedReader infile = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            Integer preCity = null;
            List<TreeModel> trees = new ArrayList<TreeModel>();

            while (true) {
                line = infile.readLine();
                if (line == null || line.length() == 0) break;
                String[] l = line.trim().split("\t");
                Integer cityId = Integer.parseInt(l[0]);
                if (! cityId.equals(preCity)) {
                    if (preCity != null) {
                        tmpModel.setTrees(trees);
                    }
                    tmpModel = new EnsembleModel();
                    trees = new ArrayList<TreeModel>();
                    preCity = cityId;
                }

                // tree
                String[] treeInfo = l[1].split(";");
                TreeModel treeModel = new TreeModel();
                List<TreeNode> tree = new ArrayList<TreeNode>();
                for (String point : treeInfo) {
                    point = point.replaceAll("\\(", "");
                    point = point.replaceAll("\\)", "");
                    String[] params = point.split(",");

                    TreeNode node = new TreeNode();
                    node.setLeftChild(Integer.parseInt(params[0].trim()));
                    node.setRightChild(Integer.parseInt(params[1].trim()));
                    node.setMissChild(Integer.parseInt(params[2].trim()));
                    node.setFeatureId(Integer.parseInt(params[3].trim()));
                    node.setThreshold(Double.parseDouble(params[4].trim()) - 2e-7);
                    node.setValue(Double.parseDouble(params[5].trim()));
                    tree.add(node);
                }
                treeModel.setTree(tree);
                trees.add(treeModel);
            }
            tmpModel.setTrees(trees);
            if (modelType.equals("pag")) {
                pagModel = tmpModel;
            } else {
                driverModel = tmpModel;
            }
        } catch (IOException e) {
//            logger.error("xgboost模型读取异常：" + e.getMessage());
        } finally {
            try {
                infile.close();
            } catch (Exception e) {
            }
        }

    }

    public static Double predictProba(double[] features, List<TreeModel> trees, double missing) {
        //Double stepSize = 0.03D;
        Double result = 0.0D;
        for (TreeModel base : trees) {
            List<TreeNode> tree = base.getTree();
            Integer now = 0;
            Double subPredict = 0.0D;
            while (true) {
                TreeNode node = tree.get(now);

                if (node.getLeftChild() == -1
                        && node.getRightChild() == -1) {
                    subPredict = node.getValue();
                    break;
                }

                if (features[node.getFeatureId()] < node.getThreshold()
                        && features[node.getFeatureId()] != missing) {
                    now = node.getLeftChild();
                } else {
                    if (features[node.getFeatureId()] >= node.getThreshold()
                            && features[node.getFeatureId()] != missing) {
                        now = node.getRightChild();
                    } else {
                        now = node.getMissChild();
                    }
                }
            }
            result += subPredict;
        }
        return 1D / (1 + Math.exp(-result));
    }

}
