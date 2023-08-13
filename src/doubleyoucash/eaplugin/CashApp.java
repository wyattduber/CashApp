package doubleyoucash.eaplugin;

import doubleyoucash.eaplugin.commands.*;
import doubleyoucash.eaplugin.database.Database;
import doubleyoucash.eaplugin.listeners.LoginListener;
import doubleyoucash.eaplugin.listeners.VoteListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class CashApp extends JavaPlugin {

    public FileConfiguration config;
    public File customConfigFile;
    public HashMap<UUID, File> voteFiles;
    public static String[] versions = new String[2];
    public String botToken;
    public String serverID;
    public String mallMsg;
    public boolean enableUsernameSync;
    public boolean enableVoteStreak;
    public boolean enableBuycraftMessages;
    public HashMap<String, Integer> usersCurrentlySyncing;
    public JavacordStart js;
    public Database db;

    public static CashApp getPlugin() { return getPlugin(CashApp.class); }

    @Override
    public void onEnable() {

        /* Load and Initiate Configs */
        try {
            reloadCustomConfig();
            config = getCustomConfig();
            saveCustomConfig();
        } catch (Exception e) {
            error("Error setting up the config! Contact the developer if you cannot fix this issue.");
        }

        /* Load Database */
         try {
             db = new Database("cashapp.sqlite.db");
             log("Database Found! Path is " + db.getDbPath());
         } catch (SQLException e) {
             error("Error setting up database! Is there permissions issue preventing the database file creation?");
             e.printStackTrace();
             error(e.getSQLState());
         }

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

            if (enableVoteStreak) {
                STREAK s = new STREAK();
                Objects.requireNonNull(this.getCommand("streak")).setExecutor(s);
                Objects.requireNonNull(this.getCommand("streak")).setTabCompleter(s);
            }
            
            if (enableUsernameSync) {
                SDU u = new SDU();
                Objects.requireNonNull(this.getCommand("sdu")).setExecutor(u);
                Objects.requireNonNull(this.getCommand("sdu")).setTabCompleter(u);
                usersCurrentlySyncing = new HashMap<String, Integer>();
            }
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

        /* Reload database if it's gone */
        try {
            if (!db.testConnection()) db = new Database("cashapp.sqlite.db");
        } catch (SQLException e) {
            error("Error setting up database! Is there permissions issue preventing the database file creation?");
            e.printStackTrace();
            error(e.getSQLState());
        }

        if (parseConfig() || js == null) {
            js = new JavacordStart();
        } else {
            js.reload();
        }
    }

    public void initListeners() {
        try {
            new UpdateChecker(this, 88409).getVersion(version -> {
                // Initializes Login Listener when no Updates
                if (!getDescription().getVersion().equalsIgnoreCase(version)) {
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
            mallMsg = getConfigString("mall-remind-msg");
            log("Mall Reminder Message Loaded!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Mall Reminder Message! Please set the mall-remind-msg in the config.yml!");
        }

        try {
            enableUsernameSync = getConfigBoolean("enable-username-sync");
            if (enableUsernameSync) log("Username Sync Enabled!");
            else log("Username Sync Disabled!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Username Setting! Please set the enable-username-sync in the config.yml!");
        }

        try {
            enableVoteStreak = getConfigBoolean("enable-vote-streak");
            if (enableVoteStreak) log("Vote Streak Enabled!");
            else log("Vote Streak Disabled!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Vote Streak Setting! Please set the enable-vote-streak in the config.yml!");
        }

        try {
            enableBuycraftMessages = getConfigBoolean("enable-buycraft-messages");
            if (enableBuycraftMessages) log("Buycraft Messages Enabled!");
            else log("Buycraft Messages Disabled!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Buycraft Messages Setting! Please set the enable-buycraft-messages in the config.yml!");
        }

        log("Config loaded!");
        return true;
    }

    public String getConfigString(String entryName) {
        return config.getString(entryName);
    }

    public boolean getConfigBoolean(String entryName) {
        return config.getBoolean(entryName);
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
