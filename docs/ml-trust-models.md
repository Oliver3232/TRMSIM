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

`SVMTrust` is implemented as an online linear large-margin classifier inspired by linear SVM training.

Core idea:

- the model uses a feature vector derived from trust evidence
- current features are:
  - signed direct success rate
  - signed witness success rate
  - evidence confidence
  - path-length score
  - direct/witness agreement
- transactions produce labels `+1` for successful service and `-1` for failed service
- weights are updated online with hinge-loss style learning and L2 regularization
- the learning rate decays with the number of updates to stabilize training during long simulations

Important note:

- this is an online linear SVM-style classifier
- it is not a batch-trained kernel SVM with a separate offline training dataset

That distinction matters in the thesis text. The implementation is still valid as a machine-learning-based trust approach because it performs feature-based classification and updates the decision boundary from observed data during simulation.

## Why this design fits the simulator

The simulator generates evidence incrementally during execution. Because of that, online models are more appropriate than offline models requiring a large labeled dataset prepared in advance.

These models therefore support the statement that the simulator investigates the feasibility of selected ML-based trust approaches:

- a Bayesian classifier-like trust estimator
- an SVM-inspired linear large-margin trust classifier

## Recommended wording for the thesis

Preferred wording:

> The simulator was extended with two online machine-learning-based trust models: a Bayesian trust estimator based on Beta-Bernoulli updating and a linear SVM-inspired large-margin classifier trained incrementally from transaction evidence.

Safer wording if you want to avoid overclaiming:

> The implementation investigates the feasibility of Bayesian and SVM-inspired trust modeling in the simulator through lightweight online variants suitable for incremental evidence collection.

## Limitations

- no offline dataset split or cross-validation is currently built into the simulator
- `SVMTrust` is linear only and does not support kernels
- witness evidence is aggregated in a bounded recent-history window
- evaluation quality still depends on scenario design and comparative experiments against the classical models
