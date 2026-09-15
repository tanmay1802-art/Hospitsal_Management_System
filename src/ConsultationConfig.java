package hms;

/**
 * One row of billing configuration: a department's base consultation rate
 * plus whether a given insurance network is accepted for it.
 */
public class ConsultationConfig implements Persistable {

    private String department;
    private double baseRate;
    private String acceptedInsuranceNetworks; // comma-separated, e.g. "AIA,Allianz,Prudential"

    public ConsultationConfig(String department, double baseRate, String acceptedInsuranceNetworks) {
        this.department = department;
        this.baseRate = baseRate;
        this.acceptedInsuranceNetworks = acceptedInsuranceNetworks;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getBaseRate() { return baseRate; }
    public void setBaseRate(double baseRate) { this.baseRate = baseRate; }
    public String getAcceptedInsuranceNetworks() { return acceptedInsuranceNetworks; }
    public void setAcceptedInsuranceNetworks(String acceptedInsuranceNetworks) {
        this.acceptedInsuranceNetworks = acceptedInsuranceNetworks;
    }

    public String toFileLine() {
        return department + "|" + baseRate + "|" + acceptedInsuranceNetworks;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row in the data file doesn't crash the whole load. */
    public static ConsultationConfig fromFileLine(String line) {
        if (line == null) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 3) return null;
        try {
            return new ConsultationConfig(p[0], Double.parseDouble(p[1]), p[2]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
