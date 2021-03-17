package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import org.bukkit.configuration.file.FileConfiguration;

public enum GameType {
    DOUBLE_BEAST,
    INFECTED,
    KILLER,
    ISLAND_OF_THE_BEAST,
    ISLAND_OF_THE_BEAST_DOUBLE_BEAST,
    ISLAND_OF_THE_BEAST_KILLER,
    CLASSIC;

    public String getType() {
        FileConfiguration file = GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS);
        switch (this) {
            case KILLER:
                return file.getString("gameType.KILLER");
            case ISLAND_OF_THE_BEAST:
                return file.getString("gameType.ISLAND_OF_THE_BEAST");
            case ISLAND_OF_THE_BEAST_KILLER:
                return file.getString("gameType.ISLAND_OF_THE_BEAST_KILLER");
            case ISLAND_OF_THE_BEAST_DOUBLE_BEAST:
                return file.getString("gameType.ISLAND_OF_THE_BEAST_DOUBLE");
            case DOUBLE_BEAST:
                return file.getString("gameType.DOUBLE_BEAST");
            case INFECTED:
                return file.getString("gameType.INFECTED");
            case CLASSIC:
            default:
                return file.getString("gameType.CLASSIC");
        }
    }
}
