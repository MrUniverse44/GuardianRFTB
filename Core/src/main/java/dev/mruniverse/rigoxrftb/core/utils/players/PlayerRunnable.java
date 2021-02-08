package dev.mruniverse.rigoxrftb.core.utils.players;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.games.GameBossFormat;
import dev.mruniverse.rigoxrftb.core.utils.FloatConverter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerRunnable extends BukkitRunnable {
    private final RigoxRFTB plugin;
    private boolean bossLb,bossGm,actionLb;
    private String bossLobby,actionLobby,bossGameBeast,bossGameRunners;
    private GameBossFormat gameBossFormat;
    private final FloatConverter floatConverter;
    public PlayerRunnable(RigoxRFTB main) {
        plugin = main;
        floatConverter = new FloatConverter();
        bossLb = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.lobby-bossBar");
        bossLobby = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.lobby.bossBar");
        actionLb = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.lobby-actionBar");
        actionLobby = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.lobby.actionBar");
        bossGm = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.ShowBeastDistance.toggle");
        bossGameRunners = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.others.bossBar.toRunners");
        bossGameBeast = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.others.bossBar.toBeasts");
        String format = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.ShowBeastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    public void update() {
        bossLb = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.lobby-bossBar");
        bossLobby = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.lobby.bossBar");
        actionLb = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.lobby-actionBar");
        actionLobby = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.lobby.actionBar");
        bossGm = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.ShowBeastDistance.toggle");
        bossGameRunners = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.others.bossBar.toRunners");
        bossGameBeast = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.others.bossBar.toBeasts");
        String format = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.ShowBeastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    @Override
    public void run() {
        for (UUID uuid : plugin.getRigoxPlayers().keySet()) {
            PlayerManager playerManager = plugin.getPlayerData(uuid);
            PlayerStatus playerStatus = playerManager.getStatus();
            Player player = playerManager.getPlayer();
            plugin.getScoreboards().setScoreboard(playerManager.getBoard(),playerManager.getPlayer());
            if (playerStatus.equals(PlayerStatus.IN_LOBBY)) {
                if(bossLb) {
                    plugin.getUtils().sendBossBar(player, bossLobby);
                }
                if(actionLb) {
                    plugin.getUtils().sendActionbar(player, actionLobby);
                }
            } else {
                if(playerManager.getBoard().equals(RigoxBoard.WAITING) || playerManager.getBoard().equals(RigoxBoard.STARTING) || playerManager.getBoard().equals(RigoxBoard.SELECTING) || playerManager.getBoard().equals(RigoxBoard.BEAST_SPAWN) || playerManager.getBoard().equals(RigoxBoard.WIN_RUNNERS_FOR_RUNNERS) || playerManager.getBoard().equals(RigoxBoard.WIN_RUNNERS_FOR_BEAST) || playerManager.getBoard().equals(RigoxBoard.WIN_BEAST_FOR_RUNNERS) || playerManager.getBoard().equals(RigoxBoard.WIN_BEAST_FOR_BEAST)    ) {
                    if(bossLb) {
                        plugin.getUtils().sendBossBar(player, bossLobby);
                    }
                } else {
                    if(bossGm) {
                        String message;
                        if(plugin.getUtils().isBeast(player)) {
                            message = bossGameBeast;
                        } else {
                            message = bossGameRunners;
                        }
                        if(gameBossFormat.equals(GameBossFormat.BOSSBAR)) {
                            boolean changeLife = false;
                            if(!plugin.getUtils().isBeast(player)) {
                                changeLife = true;
                            }
                            Player beast = plugin.getUtils().getRandomBeast(player);
                            double mainDistance = player.getLocation().distance(beast.getLocation());
                            float distance = floatConverter.converter(mainDistance);
                            message = message.replace("%runners%",playerManager.getGame().getRunners().size() + "")
                            .replace("%beastName%",beast.getName())
                            .replace("%beastDistance%",floatConverter.meters(mainDistance) + "m");
                            if(!changeLife) {
                                plugin.getUtils().sendBossBar(player, message);
                            } else {
                                plugin.getUtils().sendBossBar(player, message, distance);
                            }
                        } else {
                            Player beast = plugin.getUtils().getRandomBeast(player);
                            double mainDistance = player.getLocation().distance(beast.getLocation());
                            float distance = floatConverter.meters(mainDistance);
                            message = message.replace("%runners%",playerManager.getGame().getRunners().size() + "")
                                    .replace("%beastName%",beast.getName())
                                    .replace("%beastDistance%",distance + "m");
                            plugin.getUtils().sendActionbar(player,message);
                        }
                    }
                }
            }

        }
    }
}
