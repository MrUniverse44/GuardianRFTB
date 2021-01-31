package dev.mruniverse.rigoxrftb.core.utils;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Logger {
    private final RigoxRFTB plugin;
    public Logger(RigoxRFTB main) {
        plugin = main;
    }
    /**
     * Colorize a string provided to method
     *
     * @param message Message to transform.
     * @return transformed message with colors.
     */
    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Send a error message to console.
     * @param message message to send.
     */
    public void error(String message) {
        sendMessage("&f[&cERROR &7| &fRigox RFTB] " + message);
    }
    /**
     * Send a error message to console.
     * @param throwable throwable to send.
     */
    public void error(Throwable throwable) {
        String location = throwable.getClass().getName();
        String error = throwable.getClass().getSimpleName();
        sendMessage("&f[&cERROR &7| &fRigox RFTB] -------------------------");
        sendMessage("&f[&cERROR &7| &fRigox RFTB] Location: " + location.replace("." + error,""));
        sendMessage("&f[&cERROR &7| &fRigox RFTB] Error: " + error);
        if(throwable.getStackTrace() != null) {
            sendMessage("&f[&cERROR &7| &fRigox RFTB] Internal - StackTrace: ");
            List<StackTraceElement> other = new ArrayList<>();
            for(StackTraceElement line : throwable.getStackTrace()) {
                if(line.toString().contains("mruniverse")) {
                    sendMessage("&f[&cERROR &7| &fRigox RFTB] (Line: " + line.getLineNumber() + ") " + line.toString().replace("(" + line.getClassName() + ".java:" + line.getLineNumber() + ")",""));
                } else {
                    other.add(line);
                }
            }
            sendMessage("&f[&cERROR &7| &fRigox RFTB]  -------------------------");
            sendMessage("&f[&cERROR &7| &fRigox RFTB] External - StackTrace: ");
            for(StackTraceElement line : other) {
                sendMessage("&f[&cERROR &7| &fRigox RFTB] (Line: " + line.getLineNumber() + ") " + line.toString().replace("(" + line.getClassName() + ".java:" + line.getLineNumber() + ")",""));
            }

        }
        sendMessage("&f[&cERROR &7| &fRigox RFTB]  -------------------------");
    }

    /**
     * Send a warn message to console.
     * @param message message to send.
     */
    public void warn(String message) {
        sendMessage("&f[&eWARN &7| &fRigox RFTB] " + message);
    }

    /**
     * Send a debug message to console.
     * @param message message to send.
     */
    public void debug(String message) {
        sendMessage("&f[&9DEBUG &7| &fRigox RFTB] " + message);
    }

    /**
     * Send a info message to console.
     * @param message message to send.
     */
    public void info(String message) {
        sendMessage("&f[&bINFO &7| &fRigox RFTB] " + message);
    }

    /**
     * Sends a message to a Bukkit command sender.
     *
     * @param sender Bukkit CommandSender
     * @param message Message to send.
     */
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }


    /**
     * Used to other methods and prevent this copy pasta
     * to those methods.
     *
     * @param message Provided message
     */
    private void sendMessage(String message) {
        plugin.getServer().getConsoleSender().sendMessage(color(message));
    }
}
