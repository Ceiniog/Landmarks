package me.meibion.Landmarks;

import me.meibion.Landmarks.commands.LmkDelete;
import me.meibion.Landmarks.commands.LmkList;
import me.meibion.Landmarks.commands.LmkRegisterCommand;
import me.meibion.Landmarks.commands.LmkTPCommand;
import me.meibion.Landmarks.events.CancelTeleport;
import me.meibion.MeibionsStorageUtils.StorageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main plugin;
    public static Map<String, Map<String, String>> landmarksMap = new HashMap<>();
    public static File landmarksFile;
    public static Map<UUID, int[]> tpPending = new HashMap<>();

    @Override
    public void onDisable() {
        Server server = getServer();
        server.getLogger().info("[Landmarks] - Plugin disabled");
    }

    @Override
    public void onEnable() {
        Server server = getServer();
        plugin = this;

        // LANDMARKS DATA

        // Create landmarks file if it does not already exist
        landmarksFile = new File(getDataFolder().getAbsolutePath() + "/landmarks.yml");
        if(!(landmarksFile.exists())) {

            // Write create the file and write warningFileData to the new file
            try { StorageUtils.createDefaultFile(landmarksFile, new HashMap<>()); }
            catch(IOException error) {
                server.getLogger().warning("[Landmarks] - Could not create landmarks.yml");
                server.getLogger().warning(error.getMessage()); // Output the error
                Bukkit.getPluginManager().disablePlugin(plugin); // Disable self
            }
        }

        // Get data from file
        try { getData(landmarksFile); }
        catch (IOException error) {
            server.getLogger().warning("[Landmarks] - Could not load landmarks.yml");
            server.getLogger().warning(error.getMessage()); // Output the error
            Bukkit.getPluginManager().disablePlugin(plugin); // Disable self
        }

        // COMMANDS
        getCommand("lmkregister").setExecutor(new LmkRegisterCommand()); // Create a new landmark
        getCommand("lmktp").setExecutor(new LmkTPCommand()); // Teleports a player to a landmark
        getCommand("lmkdelete").setExecutor(new LmkDelete()); // Deletes a landmark
        getCommand("lmks").setExecutor(new LmkList()); // Lists all landmarks

        // EVENT LISTENERS
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, new CancelTeleport().pl, Event.Priority.Normal, this); // Cancels the tp if the player moves



        server.getLogger().info("[Landmarks] - Plugin enabled");
    }

    public static void getData(File file) throws IOException {
        Map<String, Object> fileData;

        // Load data from file
        fileData = StorageUtils.loadFile(file);

        // Convert data to a usable type
        fileData.forEach((key, value) -> {
            Main.landmarksMap.put(key, (Map<String, String>) value);
        });
    }

    public static void writeData(File file, Map<String, Map<String, String>> data) throws IOException {
        // Cash hashmap to writable type
        Map<String, Object> temp = new HashMap<>(data);

        // Write to file
        StorageUtils.writeToFile(file, temp);

    }
}
