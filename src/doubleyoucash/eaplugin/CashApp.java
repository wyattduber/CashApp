package doubleyoucash.eaplugin;

import doubleyoucash.eaplugin.commands.BCE;
import doubleyoucash.eaplugin.commands.BOTM;
import doubleyoucash.eaplugin.commands.CA;
import doubleyoucash.eaplugin.commands.RMD;
import doubleyoucash.eaplugin.commands.ls;
import doubleyoucash.eaplugin.listeners.LoginListener;
import doubleyoucash.eaplugin.listeners.VoteListener;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.logging.Level;

public class CashApp extends JavaPlugin {

    public FileConfiguration config;
    public File customConfigFile;
    public File voteFolder;
    public HashMap<UUID, File> voteFiles;
    public static String[] versions = new String[2];
    public String botToken;
    public String serverID;
    public String mallMsg;
    public JavacordStart js;
    public Permission perms;

    public static CashApp getPlugin() { return getPlugin(CashApp.class); }

    @Override
    public void onEnable() {

        /* Use Libby */
        //loadDependencies();

        /* Load and Initiate Configs */
        try {
            reloadCustomConfig();
            config = getCustomConfig();
            saveCustomConfig();
        } catch (Exception e) {
            error("Error setting up the config! Contact the developer if you cannot fix this issue.");
        }

        /* Load and Initiate Voting Folder/Data */
        try {
            initVotingData();
        } catch (Exception e) {
            error("Error gathering the voting data! Contact the developer if you cannot fix this issue.");
            e.printStackTrace();
        }

        /* Permissions Loading */
        setupPermissions();

        /* Config Parsing */
        if (parseConfig()) {
            initListeners();
            js = new JavacordStart();
        }

        /* Commands */
        try {
            Objects.requireNonNull(this.getCommand("ca")).setExecutor(new CA());
            Objects.requireNonNull(this.getCommand("botm")).setExecutor(new BOTM());
            Objects.requireNonNull(this.getCommand("bce")).setExecutor(new BCE());
            Objects.requireNonNull(this.getCommand("rmd")).setExecutor(new RMD());
            Objects.requireNonNull(this.getCommand("ls")).setExecutor((new ls()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        if (js != null) {
            js.disableAPI();
        }
    }

    public void reload() {
        reloadCustomConfig();
        config = getCustomConfig();
        saveCustomConfig();

        if (parseConfig() || js == null) {
            js = new JavacordStart();
        } else {
            js.reload();
        }
    }

    /*public void loadDependencies() {
        BukkitLibraryManager manager = new BukkitLibraryManager(this); //depends on the server core you are using
        manager.addMavenCentral(); //there are also methods for other repositories
        manager.fromGeneratedResource(this.getResource("AzimDP.json")).forEach(library->{
            try {
                manager.loadLibrary(library);
            }catch(RuntimeException e) { // in case some of the libraries cant be found or dont have .jar file or etc
                getLogger().info("Skipping download of\""+library+"\", it either doesnt exist or has no .jar file");
            }
        });
    }*/

    public void initVotingData() throws IOException {
        voteFolder = new File(getDataFolder().getName() + "/voting");
        if (!voteFolder.createNewFile()) throw new FileSystemException(voteFolder.getName());

        log("Voting File Initiated!");
        try {
            for (File file : voteFolder.listFiles()) {
                UUID uuid = UUID.fromString(file.getName().substring(0, file.getName().length() - 4));
                voteFiles.put(uuid, file);
                log("Vote File for " + getServer().getPlayer(uuid) + " : " + uuid + " loaded!");
            }
        } catch (NullPointerException e) {
            log("No files found! This is normal on first startup / no votes.");
        }
    }

    public void initListeners() {
        try {
            new UpdateChecker(this, 88409).getVersion(version -> {
                // Initializes Login Listener when no Updates
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    versions[0] = version;
                    versions[1] = this.getDescription().getVersion();
                    getServer().getPluginManager().registerEvents(new LoginListener(true, versions), this);
                } else {
                    getServer().getPluginManager().registerEvents(new LoginListener(false, versions), this);
                }
                getServer().getPluginManager().registerEvents(new VoteListener(), this);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        log("Minecraft Listeners Loaded!");
    }

    public boolean parseConfig() {
        try {
            botToken = getConfigString("bot-token");
            if (getConfigString("bot-token").equalsIgnoreCase("BOTTOKEN") || getConfigString("bot-token").equalsIgnoreCase("")) throw new Exception();
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Bot Token! Please enter a valid Bot Token in config.yml and reload the plugin.");
            return false;
        }

        try {
            serverID = getConfigString("server-id");
            if (getConfigString("server-id").equalsIgnoreCase("000000000000000000") || getConfigString("server-id").equalsIgnoreCase("")) throw new Exception();
            log("Discord Server Found!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Server ID! Please enter a valid Server ID in config.yml and reload the plugin.");
            return false;
        }

        try {
            for (int i = 0; i < config.getStringList("staff-list").size(); i++) {
                System.currentTimeMillis();
                //staffUUID.add(i, getServer().getPlayerUniqueId(config.getStringList("staff-list").get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            mallMsg = getConfigString("mall-remind-msg");
            log("Mall Reminder Message Loaded!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Mall Reminder Message! Please set the mall-remind-msg in the config.yml!");
        }

        log("Config loaded!");
        return true;
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        assert rsp != null;
        perms = rsp.getProvider();
    }

    public String getConfigString(String entryName) {
        return config.getString(entryName);
    }

    public void reloadCustomConfig() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("config.yml")), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCustomConfig() {
        if (config == null) {
            reloadCustomConfig();
        }
        return config;
    }

    public void saveCustomConfig() {
        if (config == null || customConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(getDataFolder(), "config.yml");
        }
        if (!customConfigFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    public void log(String message) {
        this.getLogger().log(Level.INFO, message);
    }

    public void warn(String message) {
        this.getLogger().log(Level.WARNING, message);
    }

    public void error(String message) {
        this.getLogger().log(Level.SEVERE, message);
    }

}
