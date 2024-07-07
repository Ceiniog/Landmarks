package me.meibion.Landmarks.commands;

import me.meibion.Landmarks.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class LmkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        // Check if the command sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command!");
            return true;
        }

        // Get the player
        Player player = (Player) commandSender;

        if (args.length == 1) {
            Bukkit.getServer().dispatchCommand(player, "lmktp " + args[0]);
            return true;
        }
        else if(args.length != 0) {
            player.sendMessage(ChatColor.RED + "Incorrect usage /lmk (LandmarkName)");
            return true;
        }

        // Check if the player has permissions
        if(!player.hasPermission("Landmarks.View")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Get a list of landmark names
        List<String> lmkNames = new ArrayList<>();
        Main.landmarksMap.forEach((key, value) -> lmkNames.add(key));

        // Check if there are any landmarks
        if(lmkNames.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no registered landmarks!");
            return true;
        }

        // Sort names alphabetically
        lmkNames.sort(String.CASE_INSENSITIVE_ORDER);

        // Output the names to the user
        player.sendMessage(MessageFormat.format(ChatColor.WHITE + "Player landmarks: ({0})", lmkNames.size()));
        String outputStr = "";
        for(String name : lmkNames) {

            // Ensure that each landmark is shown on a full line - rather than being split over multiple
            if(outputStr.length() + name.length() + 2 > 45) {
                player.sendMessage(ChatColor.GRAY + outputStr);
                outputStr = (name + ", "); // Add un-outputted name to the output string
            }
            // Add the landmark to the output string if there is room
            else { outputStr += (name + ", "); }
        }

        // Check if there are any remaining un-outputted names
        if(outputStr.length() > 0) { player.sendMessage(ChatColor.GRAY + outputStr); }


        return true;
    }
}
