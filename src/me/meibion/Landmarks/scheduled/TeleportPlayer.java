package me.meibion.Landmarks.scheduled;

import me.meibion.Landmarks.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;

public class TeleportPlayer implements Runnable {

    private Player player;
    private final Map<String, String> landmark;

    // Constructor
    public TeleportPlayer(Player player, Map<String, String> landmark) {
        this.player = player;
        this.landmark = landmark;
    }

    @Override
    public void run() {

        // Get landmark details
        World world = Bukkit.getWorld(landmark.get("world"));
        int x = Integer.parseInt(landmark.get("x"));
        int y = Integer.parseInt(landmark.get("y"));
        int z = Integer.parseInt(landmark.get("z"));
        float yaw = Float.parseFloat(landmark.get("yaw"));

        Location loc = new Location(world, x, y, z);

        // Teleport player
        loc.setYaw(yaw);
        player.teleport(loc);
        player.sendMessage(ChatColor.GRAY + "Welcome to " + landmark.get("name") + "!");

        // Remove player from pending map
        Main.tpPending.remove(player.getUniqueId());
    }
}
