package dev.mruniverse.rigoxrftb.core.games;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.Files;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Game {
    private final String gameName;

    public ArrayList<Player> players;
    public ArrayList<Location> signs;
    public ArrayList<Player> runners;

    int times;
    public int gameTimer;
    public int timer;
    public int time;
    public int min;
    public int worldTime;
    public int max;
    public int starting;
    public int ending;
    public int inventoryNumber;

    public Location waiting;
    public Location selectedBeast;
    public Location beastLocation;
    public Location runnersLocation;

    public Player beast;
    public FileConfiguration gameFile = RigoxRFTB.getInstance().getFiles().getControl(Files.GAMES);

    public boolean invincible = true;
    public boolean preparingStage;
    public boolean startingStage;
    public boolean inGameStage;
    public boolean playingStage;
    public boolean endingStage;

    public Game(String name) {
        this.gameTimer = 0;
        this.players = new ArrayList<>();
        this.signs = new ArrayList<>();
        this.timer = 500;
        this.min = 2;
        this.time = 0;
        this.max = 10;
        this.beastLocation = null;
        this.runnersLocation = null;
        this.runners = new ArrayList<>();
        this.beast = null;
        this.preparingStage = true;
        this.startingStage = false;
        this.inGameStage = false;
        this.playingStage = false;
        this.endingStage = false;
        this.starting = 30;
        this.ending = 150;
        this.times = 0;
        this.inventoryNumber = -1;
        this.gameName = name;
        try {
            loadGame();
        } catch (Throwable throwable) {
            RigoxRFTB.getInstance().getLogs().error("Can't load arena: " + gameName);
            RigoxRFTB.getInstance().getLogs().error(throwable);
            this.preparingStage = false;
        }
    }

    private void loadGame() {
        this.time = gameFile.getInt("games." + gameName + ".time");
        this.worldTime = gameFile.getInt("games." + gameName + ".worldTime");
        this.max = gameFile.getInt("games." + gameName + ".max");
        this.min = gameFile.getInt("games." + gameName + ".min");
        String bL = gameFile.getString("games." + gameName + ".locations.beast");
        if(bL == null) bL = "notSet";
        this.beastLocation = RigoxRFTB.getInstance().getUtils().getLocationFromString(bL);
        String sbL = gameFile.getString("games." + gameName + ".locations.selected-beast");
        if(sbL == null) sbL = "notSet";
        this.selectedBeast = RigoxRFTB.getInstance().getUtils().getLocationFromString(sbL);
        String rL = gameFile.getString("games." + gameName + ".locations.runners");
        if(rL == null) rL = "notSet";
        this.runnersLocation = RigoxRFTB.getInstance().getUtils().getLocationFromString(rL);
        String wL = gameFile.getString("games." + gameName + ".locations.waiting");
        if(wL == null) wL = "notSet";
        this.waiting = RigoxRFTB.getInstance().getUtils().getLocationFromString(wL);
        if (this.beastLocation == null || this.runnersLocation == null || this.selectedBeast == null || this.waiting == null)
            this.preparingStage = false;
        //loadSigns();
    }



    public String getName() {
        return gameName;
    }

    public void join(Player player) {

    }
}
