package es.ants.felixgm.trmsim_wsn.trm.lftm;

import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.DefuzzifierCenterOfGravity;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.LinguisticTerm;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.MembershipFunctionTrapezoidal;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.MembershipFunctionTriangular;

import java.util.Collection;
import java.util.Vector;

final class LFTMLinguisticTermsSupport {
    private static final String[] TERM_NAMES = {"Very High", "High", "Medium", "Low", "Very Low"};
    private static final String[] TERM_LABELS = {"VH_", "H_", "M_", "L_", "VL_"};

    interface DoubleReader {
        double read(String key);
    }

    interface DoubleWriter {
        void write(String key, double value);
    }

    private LFTMLinguisticTermsSupport() {
    }

    static Collection<LinguisticTerm> createDefaults(double uMin, double uMax) {
        Vector<LinguisticTerm> terms = new Vector<LinguisticTerm>();
        terms.add(createDefaultTerm("Very Low", uMin, uMin, 0.15 * uMax, 0.2 * uMax, uMin, uMax));
        terms.add(createDefaultTerm("Low", 0.2 * uMax, 0.25 * uMax, 0.35 * uMax, 0.4 * uMax, uMin, uMax));
        terms.add(createDefaultTerm("Medium", 0.4 * uMax, 0.45 * uMax, 0.55 * uMax, 0.6 * uMax, uMin, uMax));
        terms.add(createDefaultTerm("High", 0.6 * uMax, 0.65 * uMax, 0.75 * uMax, 0.8 * uMax, uMin, uMax));
        terms.add(createDefaultTerm("Very High", 0.8 * uMax, 0.85 * uMax, uMax, uMax, uMin, uMax));
        return terms;
    }

    static Collection<LinguisticTerm> load(DoubleReader reader, double uMin, double uMax) {
        Vector<LinguisticTerm> terms = new Vector<LinguisticTerm>();
        for (int i = 0; i < TERM_NAMES.length; i++) {
            terms.add(createTerm(TERM_NAMES[i], TERM_LABELS[i], reader, uMin, uMax));
        }
        return terms;
    }

    static void write(Collection<LinguisticTerm> terms, DoubleWriter writer) {
        for (LinguisticTerm term : terms) {
            String label = labelFor(term.getTermName());
            if (label == null) {
                continue;
            }
            if (term.get_membershipFunction() instanceof MembershipFunctionTriangular) {
                MembershipFunctionTriangular triangular = (MembershipFunctionTriangular) term.get_membershipFunction();
                writer.write(label + "A", triangular.get_min());
                writer.write(label + "B", triangular.get_med());
                writer.write(label + "C", triangular.get_max());
            } else if (term.get_membershipFunction() instanceof MembershipFunctionTrapezoidal) {
                MembershipFunctionTrapezoidal trapezoidal = (MembershipFunctionTrapezoidal) term.get_membershipFunction();
                writer.write(label + "A", trapezoidal.get_min());
                writer.write(label + "B", trapezoidal.get_medLow());
                writer.write(label + "C", trapezoidal.get_medHigh());
                writer.write(label + "D", trapezoidal.get_max());
            }
        }
    }

    static void appendTo(StringBuilder builder, Collection<LinguisticTerm> terms) {
        for (LinguisticTerm term : terms) {
            String label = labelFor(term.getTermName());
            if (label == null) {
                continue;
            }
            if (term.get_membershipFunction() instanceof MembershipFunctionTriangular) {
                MembershipFunctionTriangular triangular = (MembershipFunctionTriangular) term.get_membershipFunction();
                builder.append(label).append("A=").append(triangular.get_min()).append('\n');
                builder.append(label).append("B=").append(triangular.get_med()).append('\n');
                builder.append(label).append("C=").append(triangular.get_max()).append('\n');
            } else if (term.get_membershipFunction() instanceof MembershipFunctionTrapezoidal) {
                MembershipFunctionTrapezoidal trapezoidal = (MembershipFunctionTrapezoidal) term.get_membershipFunction();
                builder.append(label).append("A=").append(trapezoidal.get_min()).append('\n');
                builder.append(label).append("B=").append(trapezoidal.get_medLow()).append('\n');
                builder.append(label).append("C=").append(trapezoidal.get_medHigh()).append('\n');
                builder.append(label).append("D=").append(trapezoidal.get_max()).append('\n');
            }
        }
    }

    private static LinguisticTerm createTerm(String name, String label, DoubleReader reader, double uMin, double uMax) {
        double a = reader.read(label + "A");
        double b = reader.read(label + "B");
        double c = reader.read(label + "C");
        LinguisticTerm term;
        try {
            double d = reader.read(label + "D");
            term = new LinguisticTerm(name, new MembershipFunctionTrapezoidal(a * uMax, b * uMax, c * uMax, d * uMax));
        } catch (Exception ex) {
            term = new LinguisticTerm(name, new MembershipFunctionTriangular(a * uMax, b * uMax, c * uMax));
        }
        term.setDefuzzifier(new DefuzzifierCenterOfGravity(uMin, uMax));
        return term;
    }

    private static LinguisticTerm createDefaultTerm(String name, double a, double b, double c, double d, double uMin, double uMax) {
        LinguisticTerm term = new LinguisticTerm(name, new MembershipFunctionTrapezoidal(a, b, c, d));
        term.setDefuzzifier(new DefuzzifierCenterOfGravity(uMin, uMax));
        return term;
    }

    private static String labelFor(String termName) {
        for (int i = 0; i < TERM_NAMES.length; i++) {
            if (TERM_NAMES[i].equals(termName)) {
                return TERM_LABELS[i];
            }
        }
        return null;
    }
}
