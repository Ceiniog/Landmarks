package me.meibion.Landmarks.commands;

import me.meibion.Landmarks.Main;
import me.meibion.MeibionsStorageUtils.StorageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class LmkDelete implements CommandExecutor {
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
        if(!player.hasPermission("Landmarks.delete.self") && !player.hasPermission("Landmarks.delete.others")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Check if more than 1 arg has been given - invalid usage
        if(args.length != 1) {
            player.sendMessage(ChatColor.RED + "Incorrect usage!  /lmkdelete [landmark_name]");
            return true;
        }

        // Get the landmark
        Map<String, String> landmark = Main.landmarksMap.get(args[0].toLowerCase());

        // Check if the landmark exists
        if(landmark == null) {
            player.sendMessage(ChatColor.RED + "That landmark does not exist!");
            return true;
        }

        // Check if the player created the landmark
        if(!player.hasPermission("landmarks.delete.self") && !Objects.equals(landmark.get("creator"), playerUUID)) {
            player.sendMessage(ChatColor.RED + "You cannot delete a landmark you did not create!");
            return true;
        }

        // Delete landmark from map
        Main.landmarksMap.remove(args[0].toLowerCase());

        // Update changes to file
        try { Main.writeData(Main.landmarksFile, Main.landmarksMap); }
        catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Something went wrong while deleting the landmark!");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Landmark successfully deleted!");

        return true;
    }
}
