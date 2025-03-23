import java.util.Optional;

public class TrashCan {
    private int volumeCurrent;
    private int volumeMax;
    private int weightCurrent;
    private int weightMax;
    private int aqi_inside;
    private int aqi_outside;
    private final int id;
    private int outpostId;
    private String description;

    public TrashCan(int volumeCurrent, int volumeMax, int weightCurrent, int weightMax, int aqi_inside, int aqi_outside, int id, String description) {
        this.volumeCurrent = volumeCurrent;
        this.volumeMax = volumeMax;
        this.weightCurrent = weightCurrent;
        this.weightMax = weightMax;
        this.aqi_inside = aqi_inside;
        this.aqi_outside = aqi_outside;
        this.id = id;
        this.description = description;
    }

    public int getVolumeCurrent() {
        return this.volumeCurrent;
    }

    public String getDescription() {
        return this.description;
    }

    public void addTrash(int weight, int volume) {
        this.volumeCurrent += volume;
        this.weightCurrent += weight;
    }

    public void setVolumeCurrent(int volumeCurrent) {
        this.volumeCurrent = volumeCurrent;
    }

    public int getVolumeMax() {
        return volumeMax;
    }

    public void setVolumeMax(int volumeMax) {
        this.volumeMax = volumeMax;
    }

    public int getWeightCurrent() {
        return weightCurrent;
    }

    public void setWeightCurrent(int weightCurrent) {
        this.weightCurrent = weightCurrent;
    }

    public int getWeightMax() {
        return weightMax;
    }

    public void setWeightMax(int weightMax) {
        this.weightMax = weightMax;
    }

    public int getAqi_inside() {
        return aqi_inside;
    }

    public void setAqi_inside(int aqi_inside) {
        this.aqi_inside = aqi_inside;
    }

    public int getAqi_outside() {
        return aqi_outside;
    }

    public void setAqi_outside(int aqi_outside) {
        this.aqi_outside = aqi_outside;
    }

    public int getId() {
        return id;
    }

    public int getOutpostId() {
        return outpostId;
    }

    public void setOutpostId(int outpostId) {
        this.outpostId = outpostId;
    }
}
