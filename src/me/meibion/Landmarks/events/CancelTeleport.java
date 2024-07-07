package me.meibion.Landmarks.events;

import me.meibion.Landmarks.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;

import static org.bukkit.Bukkit.getServer;

public class CancelTeleport implements Listener {

    public PlayerListener pl = new PlayerListener() {

        // Cancels any pending teleports if the player moves
        public void onPlayerMove(PlayerMoveEvent event) {

            // Get the player
            Player player = event.getPlayer();

            // Check if the player is pending a teleport
            if(!Main.tpPending.containsKey(player.getUniqueId())) { return; }

            // Get data from the pending map
            int[] pendingData = Main.tpPending.get(player.getUniqueId());
            int task = pendingData[0]; // Task ID of the teleport event
            int oldX = pendingData[1]; // The players X coord when the tp request was made
            int oldZ = pendingData[2]; // The players Z coord when the tp request was made

            // Get the players current coords
            int newX = player.getLocation().getBlockX();
            int newZ = player.getLocation().getBlockZ();

            // Check if the player has moved too much
            if((newX > oldX + 2 || newX < oldX -2) || (newZ > oldZ + 2 || newZ < oldZ -2)) {

                // Cancel the scheduled teleport
                BukkitScheduler scheduler = getServer().getScheduler();
                scheduler.cancelTask(task);

                // Remove the player from the pending TP map
                Main.tpPending.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Teleport cancelled.");
            }
        }
    };
}
