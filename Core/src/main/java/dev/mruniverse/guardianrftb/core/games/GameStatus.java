package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;

public enum GameStatus {
    PREPARING,
    WAITING,
    STARTING,
    PLAYING,
    IN_GAME,
    RESTARTING;

    public String getStatus() {
        switch (this) {
            case WAITING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.gameStatus.waiting");
            case IN_GAME:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.gameStatus.InGame");
            case STARTING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.gameStatus.starting");
            case PREPARING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.gameStatus.preparing");
            case PLAYING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.gameStatus.playing");
            case RESTARTING:
            default:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.gameStatus.ending");
        }
    }
}


