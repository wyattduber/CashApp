package wyattduber.cashapp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wyattduber.cashapp.commands.*;
import wyattduber.cashapp.commands.tabcomplete.BaseTC;
import wyattduber.cashapp.commands.tabcomplete.SyncDiscordUsernameTC;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.javacord.JavacordHelper;
import wyattduber.cashapp.lib.LibrarySetup;
import wyattduber.cashapp.listeners.LoginListener;
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
    public List<String> modList;
    public List<String> modPlusList;
    public String botToken;
    public String serverID;
    public String mallMsg;
    public String syncReminderMsg;
    public boolean enableUsernameSync;
    public boolean enableBuycraftMessages;
    public String botmChannelID;
    public List<String> botmBannedWords;
    public HashMap<String, Integer> usersCurrentlySyncing;
    public JavacordHelper js;
    public Database db;

    public static CashApp getPlugin() { return getPlugin(CashApp.class); }

    @Override
    public void onEnable() {

        /* Load Dependencies */
        LibrarySetup librarySetup = new LibrarySetup();
        librarySetup.loadLibraries();

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
             error("Exception Message:" + e.getMessage());
             error("SQL State: " + e.getSQLState());
         }

        /* Config Parsing */
        if (parseConfig()) {
            initListeners();

            if (enableUsernameSync) {
                js = new JavacordHelper();
            }

            if (enableBuycraftMessages) {
                js = new JavacordHelper();
            }
        }

        /* Commands */
        try {
            initCommands();
        } catch (Exception e) {
            error("Error setting up commands! Contact the developer if you cannot fix this issue.");
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
            if (!db.testConnection())
                if (db.getDbPath().isEmpty() || db.getDbPath().isBlank() || db.getDbPath() == null) new Database("cashapp.sqlite.db");
        } catch (SQLException e) {
            error("Error setting up database! Is there permissions issue preventing the database file creation? View the following error message:");
            error("Error Message: " + e.getMessage());
            error("SQL State: " + e.getSQLState());
        }

        if (parseConfig() || js == null) {
            js = new JavacordHelper();
        } else {
            js.reload();
        }
    }

    public void initListeners() {
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        log("Listeners Loaded!");
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
            modList = getConfig().getStringList("mod-list");
            modPlusList = getConfig().getStringList("mod-plus-list");
            log("Mods loaded: " + Arrays.toString(modList.toArray()));
            log("Mod+'s loaded: " + Arrays.toString(modPlusList.toArray()));
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Staff List! Please enter a valid Staff List in config.yml and reload the plugin.");
            return false;
        }

        try {
            mallMsg = replaceColors(getConfigString("mall-remind-msg"));
            log("Mall Reminder Message Loaded!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Mall Reminder Message! Please set the mall-remind-msg in the config.yml!");
        }

        try {
            syncReminderMsg = replaceColors(getConfigString("sync-reminder-msg"));
            log("Sync Reminder Message Loaded!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Sync Reminder Message! Please set the sync-reminder-msg in the config.yml!");
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
            enableBuycraftMessages = getConfigBoolean("enable-buycraft-messages");
            if (enableBuycraftMessages) log("Buycraft Messages Enabled!");
            else log("Buycraft Messages Disabled!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Buycraft Messages Setting! Please set the enable-buycraft-messages in the config.yml!");
        }

        try {
            botmChannelID = getConfigString("botm-channel-id");
            if (getConfigString("botm-channel-id").equalsIgnoreCase("000000000000000000") || getConfigString("botm-channel-id").equalsIgnoreCase("")) throw new Exception();
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid BOTM Channel ID! Please enter a valid Bot Token in config.yml and reload the plugin.");
            return false;
        }

        try {
            botmBannedWords = getConfig().getStringList("botm-banned-words");
            log("BOTM Message Banned Words list loaded! Not listing here for obvious reasons.");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid banned words list! Please make sure the list is valid in the config.yml and doesn't contain syntax errors.");
            return false;
        }

        log("Config loaded!");
        return true;
    }

    public void initCommands() {
        try {
            Objects.requireNonNull(this.getCommand("ca")).setExecutor(new BaseCMD());
            Objects.requireNonNull(this.getCommand("ca")).setTabCompleter(new BaseTC());
            Objects.requireNonNull(this.getCommand("botm")).setExecutor(new BuildOfTheMonthCMD());

            if (enableBuycraftMessages) {
                Objects.requireNonNull(this.getCommand("bce")).setExecutor(new BuycraftMailCMD());
                Objects.requireNonNull(this.getCommand("rmd")).setExecutor(new StallRemindCMD());
            }
            
            if (enableUsernameSync) {
                Objects.requireNonNull(this.getCommand("sdu")).setExecutor(new SyncDiscordUsernameCMD());
                Objects.requireNonNull(this.getCommand("sdu")).setTabCompleter(new SyncDiscordUsernameTC());
                usersCurrentlySyncing = new HashMap<>();
            }
        } catch (NullPointerException e) {
            error(e.getMessage());
        }
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
            error(e.getMessage());
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

    public void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage("§f[§aCash§bApp§f] " + replaceColors(message));
        } else {
            log(message);
        }
    }

    /**
     * The escape sequence for minecraft special chat codes
     */
    public static final char ESCAPE = '§';

    /**
     * Replace all of the color codes (prepended with &) with the corresponding color code.
     * This uses raw char arrays so it can be considered to be extremely fast.
     *
     * @param text the text to replace the color codes in
     * @return string with color codes replaced
     */
    public String replaceColors(String text) {
        char[] chrarray = text.toCharArray();

        for (int index = 0; index < chrarray.length; index ++) {
            char chr = chrarray[index];

            // Ignore anything that we don't want
            if (chr != '&') {
                continue;
            }

            if ((index + 1) == chrarray.length) {
                // we are at the end of the array
                break;
            }

            // get the forward char
            char forward = chrarray[index + 1];

            // is it in range?
            if ((forward >= '0' && forward <= '9') || (forward >= 'a' && forward <= 'f') || (forward >= 'k' && forward <= 'r')) {
                // It is! Replace the char we are at now with the escape sequence
                chrarray[index] = ESCAPE;
            }
        }

        // Rebuild the string and return it
        return new String(chrarray);
    }

}
