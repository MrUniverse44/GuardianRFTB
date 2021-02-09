package dev.mruniverse.rigoxrftb.core.games;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;

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
                return RigoxRFTB.getInstance().getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.gameStatus.waiting");
            case IN_GAME:
                return RigoxRFTB.getInstance().getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.gameStatus.InGame");
            case STARTING:
                return RigoxRFTB.getInstance().getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.gameStatus.starting");
            case PREPARING:
                return RigoxRFTB.getInstance().getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.gameStatus.preparing");
            case PLAYING:
                return RigoxRFTB.getInstance().getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.gameStatus.playing");
            case RESTARTING:
            default:
                return RigoxRFTB.getInstance().getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.gameStatus.ending");
        }
    }
}


