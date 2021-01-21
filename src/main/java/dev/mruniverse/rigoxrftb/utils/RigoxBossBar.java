package dev.mruniverse.rigoxrftb.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import dev.mruniverse.rigoxrftb.RigoxRFTB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RigoxBossBar extends BukkitRunnable {
    private static String title;
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    private static final HashMap<UUID, Object> withers = new HashMap<>();
    private static Class<?> craftworldclass, entitywitherclass, packetentinitylivingout, craftplayerclass;
    private static Class<?> packetclass, packetconnection, packetplayoutdestroy, packetplayoutmeta, packetplayteleport;
    private static Constructor<?> witherconstructor, entitylivingconstructor, packetplayteleportconstructor, packetplaymetaconstructor;
    private static Method craftworldgethandle, craftplayergethandle, packetconnectsend, getentitywitherid, withersetlocationmethod;

    static {
        try {
            craftworldclass = getObcClass("CraftWorld");
            entitywitherclass = getNmsClass("EntityWither");
            packetentinitylivingout = getNmsClass("PacketPlayOutSpawnEntityLiving");
            craftplayerclass = getObcClass("entity.CraftPlayer");
            packetclass = getNmsClass("Packet");
            packetconnection = getNmsClass("PlayerConnection");
            packetplayoutdestroy = getNmsClass("PacketPlayOutEntityDestroy");
            packetplayoutmeta = getNmsClass("PacketPlayOutEntityMetadata");
            packetplayteleport = getNmsClass("PacketPlayOutEntityTeleport");
            witherconstructor = entitywitherclass.getConstructor(getNmsClass("World"));
            entitylivingconstructor = packetentinitylivingout.getConstructor(getNmsClass("EntityLiving"));
            packetplaymetaconstructor = packetplayoutmeta.getConstructor(int.class, getNmsClass("DataWatcher"), boolean.class);
            packetplayteleportconstructor = packetplayteleport.getConstructor(getNmsClass("Entity"));
            craftworldgethandle = craftworldclass.getMethod("getHandle");
            craftplayergethandle = craftplayerclass.getMethod("getHandle");
            packetconnectsend = packetconnection.getMethod("sendPacket", packetclass);
            getentitywitherid = entitywitherclass.getMethod("getId");
            withersetlocationmethod = entitywitherclass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public RigoxBossBar(String title) {
        RigoxBossBar.title = title;
        JavaPlugin fixed = RigoxRFTB.getInstance();
        runTaskTimer(fixed, 0, 0);
    }

    public static void addPlayer(Player player) {
        try {
            Object craftworld = craftworldclass.cast(player.getWorld());
            Object worldserver = craftworldgethandle.invoke(craftworld);
            Object wither = witherconstructor.newInstance(worldserver);
            Location location = getWitherLocation(player.getLocation());
            wither.getClass().getMethod("setCustomName", String.class).invoke(wither, title);
            wither.getClass().getMethod("setInvisible", boolean.class).invoke(wither, true);
            wither.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(wither, location.getX(), location.getY(), location.getZ(), 0, 0);
            Object packet = entitylivingconstructor.newInstance(wither);
            Object craftplayer = craftplayerclass.cast(player);
            Object entityplayer = craftplayergethandle.invoke(craftplayer);
            Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
            packetconnectsend.invoke(packetconnection.cast(playerconnection), packet);
            withers.put(player.getUniqueId(), wither);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void removePlayer(Player player) {
        try {
            if (withers.containsKey(player.getUniqueId())) {
                Object packet = packetplayoutdestroy.getConstructor(getNmsClass("EntityLiving")).newInstance(withers.get(player.getUniqueId()));
                withers.remove(player.getUniqueId());
                packetconnection = getNmsClass("PlayerConnection");
                packetclass = getNmsClass("Packet");
                craftplayerclass = getObcClass("entity.CraftPlayer");
                Object craftplayer = craftplayerclass.cast(player);
                Object entityplayer = craftplayergethandle.invoke(craftplayer);
                Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
                packetconnectsend.invoke(packetconnection.cast(playerconnection), packet);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
    public static void setTitleForPlayer(String title,Player player){
        try {
            if(hasPlayer(player)) {
                Object wither = withers.get(player.getUniqueId());
                Class<?> witherClass = getNmsClass("EntityWither");
                wither.getClass().getMethod("setCustomName", String.class).invoke(wither, title);
                Object packet = packetplaymetaconstructor.newInstance((getentitywitherid.invoke(wither)), witherClass.getMethod("getDataWatcher").invoke(wither), true);
                craftplayerclass = getObcClass("entity.CraftPlayer");
                Object craftplayer = craftplayerclass.cast(player);
                Object entityplayer = craftplayergethandle.invoke(craftplayer);
                packetclass = getNmsClass("Packet");
                packetconnection = getNmsClass("PlayerConnection");
                Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
                packetconnectsend.invoke(packetconnection.cast(playerconnection), packet);
            }
        }catch (Throwable ignored) {

        }
    }
    public static void setTitle(String title) {
        try {
            RigoxBossBar.title = title;
            for (Entry<UUID, Object> entry : withers.entrySet()) {
                Object wither = entry.getValue();
                entitywitherclass = getNmsClass("EntityWither");
                wither.getClass().getMethod("setCustomName", String.class).invoke(wither, title);
                Object packet = packetplaymetaconstructor.newInstance((getentitywitherid.invoke(wither)), entitywitherclass.getMethod("getDataWatcher").invoke(wither), true);
                craftplayerclass = getObcClass("entity.CraftPlayer");
                assert Bukkit.getPlayer(entry.getKey()) != null;
                Object craftplayer = craftplayerclass.cast(Bukkit.getPlayer(entry.getKey()));
                Object entityplayer = craftplayergethandle.invoke(craftplayer);
                packetclass = getNmsClass("Packet");
                packetconnection = getNmsClass("PlayerConnection");
                Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
                packetconnectsend.invoke(packetconnection.cast(playerconnection), packet);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void setProgress(float progress) {
        if (progress <= 0) {
            progress = (float) 0.001;
        }
        try {
            for (Entry<UUID, Object> entry : withers.entrySet()) {
                Object wither = entry.getValue();
                entitywitherclass = getNmsClass("EntityWither");
                wither.getClass().getMethod("setHealth", float.class).invoke(wither, progress * (float) entitywitherclass.getMethod("getMaxHealth").invoke(wither));
                Object packet = packetplayoutmeta.getConstructor(int.class, getNmsClass("DataWatcher"), boolean.class).newInstance((entitywitherclass.getMethod("getId").invoke(wither)), entitywitherclass.getMethod("getDataWatcher").invoke(wither), true);
                craftplayerclass = getObcClass("entity.CraftPlayer");
                assert Bukkit.getPlayer(entry.getKey()) != null;
                Object craftplayer = craftplayerclass.cast(Bukkit.getPlayer(entry.getKey()));
                Object entityplayer = craftplayergethandle.invoke(craftplayer);
                packetclass = getNmsClass("Packet");
                packetconnection = getNmsClass("PlayerConnection");
                Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
                packetconnectsend.invoke(packetconnection.cast(playerconnection), packet);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static boolean hasPlayer(Player player) {
        return withers.containsKey(player.getUniqueId());
    }

    public void run() {
        for (Entry<UUID, Object> entry : withers.entrySet()) {
            if (Bukkit.getPlayer(entry.getKey()) != null) {
                try {
                    Object wither = entry.getValue();
                    assert Bukkit.getPlayer(entry.getKey()) != null;
                    Location location = getWitherLocation(Bukkit.getPlayer(entry.getKey()).getEyeLocation());
                    entitywitherclass = getNmsClass("EntityWither");
                    withersetlocationmethod.invoke(wither, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                    Object packet = packetplayteleportconstructor.newInstance(wither);
                    craftplayerclass = getObcClass("entity.CraftPlayer");
                    assert Bukkit.getPlayer(entry.getKey()) != null;
                    Object craftplayer = craftplayerclass.cast(Bukkit.getPlayer(entry.getKey()));
                    Object entityplayer = craftplayergethandle.invoke(craftplayer);
                    packetclass = getNmsClass("Packet");
                    packetconnection = getNmsClass("PlayerConnection");
                    Object playerconnection = entityplayer.getClass().getField("playerConnection").get(entityplayer);
                    packetconnectsend.invoke(packetconnection.cast(playerconnection), packet);
                } catch (Exception error) {
                    error.printStackTrace();
                }
            } else {
                cancel();
            }
        }
    }

    public static Location getWitherLocation(Location location) {
        return location.add(location.getDirection().normalize().multiply(20).add(new Vector(0, 5, 0)));
    }

    public static Class<?> getNmsClass(String classname) {
        String fullname = "net.minecraft.server." + version + classname;
        Class<?> realclass = null;
        try {
            realclass = Class.forName(fullname);
        } catch (Exception error) {
            error.printStackTrace();
        }
        return realclass;
    }

    public static Class<?> getObcClass(String classname) {
        String fullname = "org.bukkit.craftbukkit." + version + classname;
        Class<?> realclass = null;
        try {
            realclass = Class.forName(fullname);
        } catch (Exception error) {
            error.printStackTrace();
        }
        return realclass;
    }

}
