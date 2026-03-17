package es.ants.felixgm.trmsim_wsn.trm.lftm;

import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.FuzzyRule;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.FuzzyRuleSet;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.LinguisticTerm;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.Variable;

import java.util.Vector;

final class LFTMFuzzyRuleSupport {
    interface ConsequentResolver {
        String resolve(int i, int j);
    }

    private LFTMFuzzyRuleSupport() {
    }

    static FuzzyRuleSet buildRuleSet(
            Variable first,
            Variable second,
            Variable result,
            Vector<LinguisticTerm> sortedTerms,
            ConsequentResolver resolver) {
        FuzzyRuleSet ruleSet = new FuzzyRuleSet();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                FuzzyRule rule = new FuzzyRule();
                rule.addAntecedent(false, first, sortedTerms.get(i).getTermName());
                rule.addAntecedent(false, second, sortedTerms.get(j).getTermName());
                rule.addConsequent(false, result, resolver.resolve(i, j));
                ruleSet.add(rule);
            }
        }
        return ruleSet;
    }
}
