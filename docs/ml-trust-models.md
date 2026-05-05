# ML Trust Models

This project extends the simulator with two machine-learning-based trust models:

- `BayesTrust`
- `SVMTrust`

The goal is not to reproduce heavyweight offline ML pipelines inside the simulator. The goal is to provide online trust models that can update from transaction evidence during simulation runs and can therefore be compared with the classical trust models already present in TRMSIM.

## BayesTrust

`BayesTrust` is implemented as a Bayesian trust estimator using a Beta-Bernoulli update rule.

Core idea:

- each client stores direct success/failure evidence about servers
- each server stores witness evidence from transactions observed with other clients
- direct and witness evidence are combined using configurable weights
- the posterior trust estimate is computed from `priorAlpha`, `priorBeta`, weighted successes, and weighted failures
- the final ranking score uses a conservative posterior estimate reduced by posterior variance
- longer paths are penalized

Interpretation:

- posterior mean captures expected trustworthiness
- posterior variance captures uncertainty when evidence is scarce
- the uncertainty penalty prevents cold-start nodes from looking too strong after only one or two successful interactions

This is an appropriate Bayesian baseline for online trust estimation in a WSN-style simulator.

## SVMTrust

`SVMTrust` is implemented as a LIBSVM-backed linear Support Vector Machine classifier with online evidence collection.

Core idea:

- the model uses a feature vector derived from trust evidence
- current features are:
  - signed direct success rate
  - signed witness success rate
  - evidence confidence
  - path-length score
  - direct/witness agreement
- transactions produce labels `+1` for successful service and `-1` for failed service
- examples are collected incrementally during the simulation
- once enough positive and negative examples are available, LIBSVM trains a linear C-SVC model
- the model is retrained periodically as new transaction evidence arrives
- before enough training data exists, the implementation cycles through candidate paths to collect labeled examples

Important note:

- the classifier is a real SVM implementation through LIBSVM
- training data is still generated online by the simulator rather than loaded from a separate offline dataset
- the current integration uses a linear kernel to keep the model explainable and efficient inside simulation runs

That distinction matters in the thesis text. The implementation is valid as a machine-learning-based trust approach because it performs feature-based classification and periodically retrains a LIBSVM decision boundary from observed transaction data.

## Why this design fits the simulator

The simulator generates evidence incrementally during execution. Because of that, online models are more appropriate than offline models requiring a large labeled dataset prepared in advance.

These models therefore support the statement that the simulator investigates the feasibility of selected ML-based trust approaches:

- a Bayesian classifier-like trust estimator
- a LIBSVM-backed linear SVM trust classifier

## Recommended wording for the thesis

Preferred wording:

> The simulator was extended with two machine-learning-based trust models: a Bayesian trust estimator based on Beta-Bernoulli updating and a LIBSVM-backed linear Support Vector Machine classifier trained from transaction evidence collected during simulation.

Safer wording if you want to avoid overclaiming:

> The implementation investigates the feasibility of Bayesian and SVM-based trust modeling in the simulator using incremental evidence collection and periodic SVM retraining.

## Limitations

- no offline dataset split or cross-validation is currently built into the simulator
- `SVMTrust` currently uses a linear LIBSVM kernel only
- witness evidence is aggregated in a bounded recent-history window
- evaluation quality still depends on scenario design and comparative experiments against the classical models
