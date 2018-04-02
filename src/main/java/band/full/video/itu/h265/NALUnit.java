package band.full.video.itu.h265;

public class NALUnit {
    public final NALUnitType type;

    public NALUnit(NALUnitType type) {
        this.type = type;
    }

    public String getTypeString() {
        return type.fullName + " (" + type.shortName + ")";
    }
}
