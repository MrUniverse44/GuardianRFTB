package dev.mruniverse.rigoxrftb.rigoxrftb.utils;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Logger {
    private RigoxRFTB plugin;
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
        sendMessage("&f[&cERROR &7| &fRigox RFTB] -------------------------");
        sendMessage("&f[&cERROR &7| &fRigox RFTB] Class: " + throwable.getClass().getName() +".class");
        if(throwable.getStackTrace() != null) {
            sendMessage("&f[&cERROR &7| &fRigox RFTB] StackTrace: ");
            for(StackTraceElement line : throwable.getStackTrace()) {
                sendMessage("&f[&cERROR &7| &fRigox RFTB] (" + line.getLineNumber() + ") " + line.toString());
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
