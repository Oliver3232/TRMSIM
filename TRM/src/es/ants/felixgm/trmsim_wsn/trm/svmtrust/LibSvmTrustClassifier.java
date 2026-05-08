package es.ants.felixgm.trmsim_wsn.trm.svmtrust;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;
import libsvm.svm_problem;

import java.util.LinkedList;
import java.util.List;

/**
 * Small adapter that keeps LIBSVM details out of the simulator-facing trust model.
 */
final class LibSvmTrustClassifier {
    private static final double TRUSTED_LABEL = 1.0;
    private static final double UNTRUSTED_LABEL = -1.0;
    private static final double DEFAULT_SCORE = 0.5;

    private final LinkedList<TrainingExample> examples = new LinkedList<TrainingExample>();
    private svm_model model;
    private boolean hasTrustedExamples;
    private boolean hasUntrustedExamples;
    private int trustedExampleCount;
    private int untrustedExampleCount;
    private int examplesSinceTraining;
    private int trainingCount;

    static {
        svm.svm_set_print_string_function(new svm_print_interface() {
            public void print(String output) {
            }
        });
    }

    void reset() {
        examples.clear();
        model = null;
        hasTrustedExamples = false;
        hasUntrustedExamples = false;
        trustedExampleCount = 0;
        untrustedExampleCount = 0;
        examplesSinceTraining = 0;
        trainingCount = 0;
    }

    void addExample(double[] features, boolean trusted, SVMTrust_Parameters parameters) {
        double label = trusted ? TRUSTED_LABEL : UNTRUSTED_LABEL;
        examples.add(new TrainingExample(features.clone(), label));
        examplesSinceTraining++;
        if (trusted) {
            trustedExampleCount++;
            hasTrustedExamples = true;
        } else {
            untrustedExampleCount++;
            hasUntrustedExamples = true;
        }

        while (examples.size() > parameters.get_maxTrainingExamples()) {
            examples.removeFirst();
            refreshLabelFlags();
        }

        if (shouldTrain(parameters)) {
            train(parameters);
        }
    }

    boolean isTrained() {
        return model != null;
    }

    int getTrainingExampleCount() {
        return examples.size();
    }

    int getTrustedExampleCount() {
        return trustedExampleCount;
    }

    int getUntrustedExampleCount() {
        return untrustedExampleCount;
    }

    int getTrainingCount() {
        return trainingCount;
    }

    double score(double[] features) {
        if (model == null) {
            return DEFAULT_SCORE;
        }

        double[] probabilities = new double[2];
        double predictedLabel = svm.svm_predict_probability(model, toNodes(features), probabilities);
        int[] labels = new int[2];
        svm.svm_get_labels(model, labels);
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == (int) TRUSTED_LABEL) {
                return clampProbability(probabilities[i]);
            }
        }
        return predictedLabel == TRUSTED_LABEL ? 1.0 : 0.0;
    }

    private boolean shouldTrain(SVMTrust_Parameters parameters) {
        return hasTrustedExamples
                && hasUntrustedExamples
                && (examples.size() >= parameters.get_minTrainingExamples())
                && ((model == null) || (examplesSinceTraining >= parameters.get_retrainInterval()));
    }

    private void train(SVMTrust_Parameters parameters) {
        svm_problem problem = new svm_problem();
        problem.l = examples.size();
        problem.x = new svm_node[problem.l][];
        problem.y = new double[problem.l];

        for (int i = 0; i < examples.size(); i++) {
            TrainingExample example = examples.get(i);
            problem.x[i] = toNodes(example.features);
            problem.y[i] = example.label;
        }

        svm_parameter svmParameters = createParameters(parameters);
        String error = svm.svm_check_parameter(problem, svmParameters);
        if (error != null) {
            throw new IllegalStateException("Invalid LIBSVM parameters: " + error);
        }

        model = svm.svm_train(problem, svmParameters);
        examplesSinceTraining = 0;
        trainingCount++;
    }

    private svm_parameter createParameters(SVMTrust_Parameters parameters) {
        svm_parameter svmParameters = new svm_parameter();
        svmParameters.svm_type = svm_parameter.C_SVC;
        svmParameters.kernel_type = svm_parameter.LINEAR;
        svmParameters.degree = 3;
        svmParameters.gamma = 0.0;
        svmParameters.coef0 = 0.0;
        svmParameters.cache_size = 64.0;
        svmParameters.eps = 0.001;
        svmParameters.C = parameters.get_svmCost();
        svmParameters.nr_weight = 0;
        svmParameters.weight_label = new int[0];
        svmParameters.weight = new double[0];
        svmParameters.nu = 0.5;
        svmParameters.p = 0.1;
        svmParameters.shrinking = 1;
        svmParameters.probability = 1;
        return svmParameters;
    }

    private svm_node[] toNodes(double[] features) {
        svm_node[] nodes = new svm_node[features.length];
        for (int i = 0; i < features.length; i++) {
            svm_node node = new svm_node();
            node.index = i + 1;
            node.value = features[i];
            nodes[i] = node;
        }
        return nodes;
    }

    private double clampProbability(double probability) {
        if (Double.isNaN(probability)) {
            return DEFAULT_SCORE;
        }
        return Math.max(0.0, Math.min(1.0, probability));
    }

    private void refreshLabelFlags() {
        hasTrustedExamples = false;
        hasUntrustedExamples = false;
        trustedExampleCount = 0;
        untrustedExampleCount = 0;
        for (TrainingExample example : examples) {
            if (example.label == TRUSTED_LABEL) {
                trustedExampleCount++;
                hasTrustedExamples = true;
            } else if (example.label == UNTRUSTED_LABEL) {
                untrustedExampleCount++;
                hasUntrustedExamples = true;
            }
        }
    }

    private static final class TrainingExample {
        private final double[] features;
        private final double label;

        private TrainingExample(double[] features, double label) {
            this.features = features;
            this.label = label;
        }
    }
}
