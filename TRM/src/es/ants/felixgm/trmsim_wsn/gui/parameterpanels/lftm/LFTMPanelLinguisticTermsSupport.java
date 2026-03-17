package es.ants.felixgm.trmsim_wsn.gui.parameterpanels.lftm;

import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.DefuzzifierCenterOfGravity;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.FuzzySetPanel;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.LinguisticTerm;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.MembershipFunctionTrapezoidal;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.MembershipFunctionTriangular;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Collection;
import java.util.Vector;

final class LFTMPanelLinguisticTermsSupport {
    private static final String[] TERM_NAMES = {"Very High", "High", "Medium", "Low", "Very Low"};

    private LFTMPanelLinguisticTermsSupport() {
    }

    static Collection<LinguisticTerm> readTerms(double uMin, double uMax, JComboBox[] combos, JTextField[][] fields) {
        Vector<LinguisticTerm> terms = new Vector<LinguisticTerm>();
        for (int i = 0; i < TERM_NAMES.length; i++) {
            terms.add(createTerm(TERM_NAMES[i], uMin, uMax, combos[i], fields[i]));
        }
        return terms;
    }

    static FuzzySetPanel applyTerms(Collection<LinguisticTerm> terms, JComboBox[] combos, JTextField[][] fields, JPanel termsPanel, JPanel currentContainer) {
        for (LinguisticTerm term : terms) {
            int index = indexOf(term.getTermName());
            if (index >= 0) {
                applyTerm(term, combos[index], fields[index]);
            }
        }

        FuzzySetPanel panel = new FuzzySetPanel(terms);
        panel.setPreferredSize(currentContainer.getPreferredSize());
        ((javax.swing.GroupLayout) termsPanel.getLayout()).replace(currentContainer, panel);
        return panel;
    }

    private static LinguisticTerm createTerm(String name, double uMin, double uMax, JComboBox combo, JTextField[] fields) {
        double a = Double.parseDouble(fields[0].getText());
        double b = Double.parseDouble(fields[1].getText());
        double c = Double.parseDouble(fields[2].getText());
        LinguisticTerm term;
        if ("Trapezoidal".equals(combo.getSelectedItem())) {
            double d = Double.parseDouble(fields[3].getText());
            term = new LinguisticTerm(name, new MembershipFunctionTrapezoidal(a * uMax, b * uMax, c * uMax, d * uMax));
        } else {
            term = new LinguisticTerm(name, new MembershipFunctionTriangular(a * uMax, b * uMax, c * uMax));
        }
        term.setDefuzzifier(new DefuzzifierCenterOfGravity(uMin, uMax));
        return term;
    }

    private static void applyTerm(LinguisticTerm term, JComboBox combo, JTextField[] fields) {
        if (term.get_membershipFunction() instanceof MembershipFunctionTriangular) {
            MembershipFunctionTriangular triangular = (MembershipFunctionTriangular) term.get_membershipFunction();
            combo.setSelectedItem("Triangular");
            fields[0].setText(String.valueOf(triangular.get_min()));
            fields[1].setText(String.valueOf(triangular.get_med()));
            fields[2].setText(String.valueOf(triangular.get_max()));
        } else if (term.get_membershipFunction() instanceof MembershipFunctionTrapezoidal) {
            MembershipFunctionTrapezoidal trapezoidal = (MembershipFunctionTrapezoidal) term.get_membershipFunction();
            combo.setSelectedItem("Trapezoidal");
            fields[0].setText(String.valueOf(trapezoidal.get_min()));
            fields[1].setText(String.valueOf(trapezoidal.get_medLow()));
            fields[2].setText(String.valueOf(trapezoidal.get_medHigh()));
            fields[3].setText(String.valueOf(trapezoidal.get_max()));
        }
    }

    private static int indexOf(String termName) {
        for (int i = 0; i < TERM_NAMES.length; i++) {
            if (TERM_NAMES[i].equals(termName)) {
                return i;
            }
        }
        return -1;
    }
}
