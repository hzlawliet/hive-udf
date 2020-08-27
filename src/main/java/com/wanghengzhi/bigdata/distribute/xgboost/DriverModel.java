package com.wanghengzhi.bigdata.distribute.xgboost;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Created by wanghengzhi on 2019/4/18.
 */
public class DriverModel extends UDF{

    static EnsembleModel driverModel;

        private static synchronized void init(String modelName) throws Exception {
            InputStream modelinput = new FileInputStream((new File(modelName)));
            loadTreeModels(modelinput);
            modelinput.close();

        }


        public static double evaluate(String modelName, double missValue, String feastr) throws Exception {

            String[] features = feastr.trim().split(",");

            double[] feaVec = new double[features.length];

            int count = 0;
            for (String kk : features
                    ) {
                if (NumberUtils.isNumber(kk)) {
                    feaVec[count++] = Double.valueOf(kk);
                } else {
                    feaVec[count++] = missValue;
                }
            }

//
            if (driverModel == null) {
                init(modelName);
            }

            return predictProba(feaVec, driverModel.getTrees(), missValue);

        }

    public static void loadTreeModels(InputStream modelinput) {
        EnsembleModel tmpModel = new EnsembleModel();
        BufferedReader infile = new BufferedReader(new InputStreamReader(modelinput));

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
            driverModel = tmpModel;
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
