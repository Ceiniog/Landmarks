package me.meibion.Landmarks.commands;

import me.meibion.Landmarks.Main;
import me.meibion.Landmarks.scheduled.TeleportPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class LmkTPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        // Check if the command sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command!");
            return true;
        }

        // Get the player
        Player player = (Player) commandSender;

        // Check if the player has permission to teleport
        if(!player.hasPermission("Landmarks.teleport")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Check if the correct number of arguments have been given
        if(args.length != 1) {
            player.sendMessage(ChatColor.RED + "Incorrect usage! /lmktp [landmark_name]");
            return true;
        }

        // Check if the player is already pending a teleport
        if(Main.tpPending.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already have a teleport pending!");
            return true;
        }

        // Get the selected landmark
        Map<String, String> landmark = Main.landmarksMap.get(args[0].toLowerCase());

        // Check if the landmark exists
        if(landmark == null) {
            player.sendMessage(ChatColor.RED + "That landmark does not exist!");
            return true;
        }

        // Pre-load chunk
        World world = Bukkit.getWorld(landmark.get("world"));
        Chunk chunk = world.getChunkAt((Integer.parseInt(landmark.get("x"))), Integer.parseInt(landmark.get("z")));
        if(!chunk.isLoaded()) { chunk.load(); }

        // Get player coords (will be used to check if the player has moved since starting the tp request)
        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();

        // Teleport player
        BukkitScheduler scheduler = getServer().getScheduler();
        int task = scheduler.scheduleSyncDelayedTask(new Main(), new TeleportPlayer(player, landmark), 5 * 20); // Teleport the player in 5 seconds
        Main.tpPending.put(player.getUniqueId(), new int[] {task, playerX, playerZ}); // Put the task in the tp tasks list + player coords
        player.sendMessage(ChatColor.GRAY + "You will be teleported in 5 seconds. Do not move.");

        return true;
    }
}
