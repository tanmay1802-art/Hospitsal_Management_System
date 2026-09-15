package hms;

/**
 * Represents a physical hospital asset: consultation room, inpatient ward,
 * lab or X-ray/imaging room. Admin can create/read/update/delete these
 * and mark them as Available / Occupied / Under Maintenance.
 */
public class HospitalAsset implements Persistable {

    private String assetId;
    private String assetType;   // "Consultation Room", "Ward", "Lab", "Imaging Room"
    private String assetName;
    private String location;
    private String status;      // "Available", "Occupied", "Maintenance"

    public HospitalAsset(String assetId, String assetType, String assetName, String location, String status) {
        this.assetId = assetId;
        this.assetType = assetType;
        this.assetName = assetName;
        this.location = location;
        this.status = status;
    }

    public String getAssetId() { return assetId; }
    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toFileLine() {
        return assetId + "|" + assetType + "|" + assetName + "|" + location + "|" + status;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static HospitalAsset fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 5) return null;
            return new HospitalAsset(p[0], p[1], p[2], p[3], p[4]);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
