package me.meibion.Landmarks.commands;

import me.meibion.Landmarks.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LmkRegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        // Check if the command sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command!");
            return true;
        }

        // Get the player
        Player player = (Player) commandSender;
        String playerUUID = player.getUniqueId().toString();

        // Check if the player has permissions
        if(!player.hasPermission("landmarks.register.single") && !player.hasPermission("Landmarks.register.multiple")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Check if more than 1 arg has been given - invalid usage
        if(args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /lmkregister [landmark_name]");
            return true;
        }

        // Check if the player already has a landmark - if the player can only 1 active
        if(!player.hasPermission("landmarks.register.multiple")) {
            boolean[] lmkExists = {false}; // Has to be an array because of some lambda reasons idk

            Main.landmarksMap.forEach((key, value) -> {
                if(value.get("creator").equals(playerUUID)) {
                    lmkExists[0] = true; // Set hasMultiple to true
                    return; // Ends the for loop early
                }
            });

            // Ends function if the user already has a landmark
            if(lmkExists[0]) {
                player.sendMessage(ChatColor.RED + "You can only have 1 active landmark!");
                return true;
            }
        }

        // Check if a landmark with the given name already exists
        if(Main.landmarksMap.containsKey(args[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "This landmark already exists!");
            return true;
        }

        // Check if the landmark name is too long or short
        if(args[0].length() > 24) {
            player.sendMessage(ChatColor.RED + "Landmark name cannot exceed 24 characters!");
            return true;
        }
        else if(args[0].length() < 2) {
            player.sendMessage(ChatColor.RED + "Landmark name cannot be less than 2 characters!");
            return true;
        }

        // Create the landmark
        try { createLandmark(player, args[0]); }
        catch(IOException error) {
            player.sendMessage(ChatColor.RED + "An error occurred while creating a landmark!");
            return true;
        }

        return true;
    }

    private void createLandmark(Player player, String landmarkName) throws IOException {
        Map<String, String> lmkMap = new HashMap<>();

        // Set creator uuid
        lmkMap.put("creator", player.getUniqueId().toString());

        // Set landmark name (Only used for outputting the landmark name, not for lookups)
        lmkMap.put("name", landmarkName.replace("_", " "));

        // Get player coords
        lmkMap.put("x", String.valueOf(player.getLocation().getBlockX())); // x
        lmkMap.put("y", String.valueOf(player.getLocation().getBlockY())); // y
        lmkMap.put("z", String.valueOf(player.getLocation().getBlockZ())); // z
        lmkMap.put("yaw", String.valueOf(player.getLocation().getYaw())); // yaw

        // Get player world
        lmkMap.put("world", player.getLocation().getWorld().getName()); // Player world

        // Set date created
        lmkMap.put("createdTime", new Date().toString());

        // Add landmark to the landmarks map
        Main.landmarksMap.put(landmarkName.toLowerCase(), lmkMap);

        // Write to file
        Main.writeData(Main.landmarksFile, Main.landmarksMap);

        // Success message
        player.sendMessage(ChatColor.GREEN + "Successfully registered " + lmkMap.get("name") + "!");
    }
}
