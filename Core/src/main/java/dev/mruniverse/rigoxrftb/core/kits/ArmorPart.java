package dev.mruniverse.rigoxrftb.core.kits;

public enum ArmorPart {
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS;

    public String getPartName() {
        switch (this) {
            case HELMET:
                return "Helmet";
            case BOOTS:
                return "Boots";
            case LEGGINGS:
                return "Leggings";
            case CHESTPLATE:
            default:
                return "Chestplate";
        }
    }
}
