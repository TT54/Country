package fr.tt54.country.utils.materials;

public class InterVersionMaterial {

    private final int startVersion;
    private final int endVersion;
    private final String name;
    private final MaterialCategory materialCategory;
    private final String materialName;

    public InterVersionMaterial(String name, MaterialCategory materialCategory, String materialName, int startVersion, int endVersion) {
        this.startVersion = startVersion;
        this.endVersion = endVersion;
        this.name = name;
        this.materialCategory = materialCategory;
        this.materialName = materialName;
    }

    public InterVersionMaterial(String name, MaterialCategory materialCategory, String materialName) {
        this.name = name;
        this.materialCategory = materialCategory;
        this.startVersion = -1;
        this.endVersion = -1;
        this.materialName = materialName;
    }

    public InterVersionMaterial(String name, MaterialCategory materialCategory, String materialName, int startVersion) {
        this.startVersion = startVersion;
        this.name = name;
        this.materialCategory = materialCategory;
        this.materialName = materialName;
        this.endVersion = -1;
    }

    public int getStartVersion() {
        return startVersion;
    }

    public int getEndVersion() {
        return endVersion;
    }

    public String getName() {
        return name;
    }

    public MaterialCategory getMaterialCategory() {
        return materialCategory;
    }

    public String getMaterialName() {
        return materialName;
    }
}
