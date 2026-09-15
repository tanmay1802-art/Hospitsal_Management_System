package hms;

/**
 * Takes the BP/temperature/pulse a Doctor types into the consultation form
 * and turns them into a Normal / Elevated / Critical grade. This is what the
 * "classify health metrics based on pre-defined criteria" requirement asks
 * for - the thresholds below are the standard adult clinical ranges we were
 * told to use as a reference.
 *
 * Everything here is static because there's no state to keep - it's just
 * three small classifiers plus one that combines them.
 */
public class VitalsGrader {

    public static final String NORMAL = "Normal";
    public static final String ELEVATED = "Elevated";
    public static final String CRITICAL = "Critical";
    public static final String UNKNOWN = "Unknown"; // field was blank or couldn't be parsed

    private VitalsGrader() { } // no instances, just static helpers

    /** Expects "systolic/diastolic", e.g. "120/80". */
    public static String gradeBloodPressure(String bp) {
        if (bp == null || bp.trim().isEmpty()) return UNKNOWN;
        String[] parts = bp.trim().split("/");
        if (parts.length != 2) return UNKNOWN;
        try {
            int systolic = Integer.parseInt(parts[0].trim());
            int diastolic = Integer.parseInt(parts[1].trim());

            if (systolic >= 180 || diastolic >= 120) return CRITICAL;
            if (systolic >= 140 || diastolic >= 90) return ELEVATED;
            if (systolic < 90 || diastolic < 60) return ELEVATED; // low BP also needs attention
            return NORMAL;
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }

    /** Expects a plain number in Celsius, e.g. "37.5". */
    public static String gradeTemperature(String tempC) {
        if (tempC == null || tempC.trim().isEmpty()) return UNKNOWN;
        try {
            double t = Double.parseDouble(tempC.trim());
            if (t >= 39.0 || t < 35.0) return CRITICAL;
            if (t >= 37.8) return ELEVATED;
            return NORMAL;
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }

    /** Expects a resting pulse in bpm, e.g. "78". */
    public static String gradePulse(String pulseBpm) {
        if (pulseBpm == null || pulseBpm.trim().isEmpty()) return UNKNOWN;
        try {
            int p = Integer.parseInt(pulseBpm.trim());
            if (p >= 130 || p < 40) return CRITICAL;
            if (p >= 100 || p < 60) return ELEVATED;
            return NORMAL;
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }

    /**
     * Overall grade for the visit = the worst of the three individual grades.
     * A blank/unparseable reading (UNKNOWN) doesn't drag the visit down to
     * Critical - it's just ignored when picking the worst, unless all three
     * are unknown.
     */
    public static String overallGrade(String bp, String tempC, String pulseBpm) {
        String bpGrade = gradeBloodPressure(bp);
        String tempGrade = gradeTemperature(tempC);
        String pulseGrade = gradePulse(pulseBpm);

        if (bpGrade.equals(CRITICAL) || tempGrade.equals(CRITICAL) || pulseGrade.equals(CRITICAL)) return CRITICAL;
        if (bpGrade.equals(ELEVATED) || tempGrade.equals(ELEVATED) || pulseGrade.equals(ELEVATED)) return ELEVATED;
        if (bpGrade.equals(NORMAL) || tempGrade.equals(NORMAL) || pulseGrade.equals(NORMAL)) return NORMAL;
        return UNKNOWN; // nothing was filled in / parseable
    }

    /**
     * Billing surcharge on top of the department's base consultation rate,
     * for visits that needed more attention. Critical visits usually mean
     * more of the doctor's time (extra checks, possible referral), so we
     * charge a bit more; Elevated is a smaller bump; Normal/Unknown pay the
     * plain base rate.
     */
    public static double surchargeMultiplier(String grade) {
        if (CRITICAL.equals(grade)) return 1.25; // +25%
        if (ELEVATED.equals(grade)) return 1.10; // +10%
        return 1.0;
    }
}
