package wyattduber.cashapp;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wyattduber.cashapp.commands.*;
import wyattduber.cashapp.commands.tabcomplete.*;
import wyattduber.cashapp.customitems.ItemListener;
import wyattduber.cashapp.customitems.ItemManager;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.javacord.JavacordHelper;
import wyattduber.cashapp.lib.LibrarySetup;
import wyattduber.cashapp.listeners.LeashListener;
import wyattduber.cashapp.listeners.LoginListener;
import wyattduber.cashapp.placeholders.PlaceholderHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class CashApp extends JavaPlugin {

    public FileConfiguration config;
    public File customConfigFile;
    public boolean debugMode;
    public List<String> modList;
    public List<String> modPlusList;
    public String botToken;
    public String serverID;
    public String mallMsg;
    public String syncReminderMsg;
    public boolean useFirstTimeMessage;
    public int messageDelayTicks;
    public String firstTimeMessage;
    public List<String> messageNames;
    public HashMap<String, String> messages = new HashMap<>();
    public LoginListener ll;
    public ItemListener il;
    public LeashListener leashListener;
    public String botmChannelID;
    public List<String> botmBannedWords;
    public HashMap<String, Integer> usersCurrentlySyncing;
    public JavacordHelper js;
    public Database db;
    public boolean discordConnected;

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

        /* Register Custom Items */
        ItemManager.registerCustomItems();

        /* Load Database */
         try {
             db = new Database("cashapp.sqlite.db");
             log("Database Found! Path is " + db.getDbPath());
         } catch (SQLException e) {
             error("Error setting up database! Is there permissions issue preventing the database file creation?");
             error("Exception Message:" + e.getMessage());
             error("SQL State: " + e.getSQLState());
         }

         /* Load Listeners */
         initListeners();

        /* Config Parsing */
        if (parseConfig()) {
            js = new JavacordHelper();
        }

        /* Commands */
        try {
            registerCommands();
        } catch (Exception e) {
            error("Error setting up commands! Contact the developer if you cannot fix this issue.");
        }

        /* Register Placeholders */
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHandler(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (js != null) {
            js.disableAPI();
        }
    }

    public void reload() {
        // Un-Register Listeners
        PlayerJoinEvent.getHandlerList().unregister(ll);

        ThrownEggHatchEvent.getHandlerList().unregister(il);
        EntityShootBowEvent.getHandlerList().unregister(il);
        EntityLoadCrossbowEvent.getHandlerList().unregister(il);
        EntityDamageEvent.getHandlerList().unregister(il);

        PlayerLeashEntityEvent.getHandlerList().unregister(leashListener);

        /* Load and Initiate Configs */
        try {
            reloadCustomConfig();
            config = getCustomConfig();
            saveCustomConfig();
        } catch (Exception e) {
            error("Error setting up the config! Contact the developer if you cannot fix this issue.");
        }

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

        initListeners();
    }

    private void initListeners() {
        ll = new LoginListener();
        getServer().getPluginManager().registerEvents(ll, this);

        il = new ItemListener();
        getServer().getPluginManager().registerEvents(il, this);

        leashListener = new LeashListener();
        getServer().getPluginManager().registerEvents(leashListener, this);

        log("Listeners Loaded!");
    }

    private boolean parseConfig() {
        boolean flag = true;
        discordConnected = true;

        try {
            debugMode = getConfigBool("debug");
        } catch (Exception e) {
            warn("Invalid Debug Mode setting! Please set a valid boolean entry for the debug mode and reload the plugin.");
            flag = false;
        }

        try {
            botToken = getConfigString("bot-token");
            if (getConfigString("bot-token").equalsIgnoreCase("BOTTOKEN") || getConfigString("bot-token").equalsIgnoreCase("")) throw new Exception();
        } catch (Exception e) {
            warn("Invalid Bot Token! Please enter a valid Bot Token in config.yml and reload the plugin.");
            discordConnected = false;
        }

        try {
            serverID = getConfigString("server-id");
            if (getConfigString("server-id").equalsIgnoreCase("000000000000000000") || getConfigString("server-id").equalsIgnoreCase("")) throw new Exception();
            log("Discord Server Found!");
        } catch (Exception e) {
            warn("Invalid Server ID! Please enter a valid Server ID in config.yml and reload the plugin.");
            discordConnected = false;
        }

        try {
            modList = getConfig().getStringList("mod-list");
            modPlusList = getConfig().getStringList("mod-plus-list");

            if (modList.isEmpty()) throw new Exception();
            if (modPlusList.isEmpty()) throw new Exception();

            log("Mods loaded: " + Arrays.toString(modList.toArray()));
            log("Mod+'s loaded: " + Arrays.toString(modPlusList.toArray()));
        } catch (Exception e) {
            warn("Invalid Staff List! Please enter a valid Staff List in config.yml and reload the plugin.");
            flag = false;
        }

        try {
            mallMsg = replaceColors(getConfigString("mall-remind-msg"));
            log("Mall Reminder Message Loaded!");
        } catch (Exception e) {
            warn("Invalid Mall Reminder Message! Please set the mall-remind-msg in the config.yml!");
        }

        try {
            syncReminderMsg = replaceColors(getConfigString("sync-reminder-msg"));
            log("Sync Reminder Message Loaded!");
        } catch (Exception e) {
            warn("Invalid Sync Reminder Message! Please set the sync-reminder-msg in the config.yml!");
        }

        try {
            messageDelayTicks = getConfigInt("message-delay");
            log("Message Delay Loaded!");
        } catch (NullPointerException e) {
            error("Cannot Find \"message-delay\" Boolean in Config! Make sure it's there and reload the plugin.");
        }

        try {
            StringBuilder message = new StringBuilder();
            useFirstTimeMessage = getConfigBool("enable-first-time-message");
            String[] tempAdd = new String[config.getStringList("first-time-message").size()];
            tempAdd = config.getStringList("first-time-message").toArray(tempAdd);
            parseColorCodesOld(message, tempAdd);
            firstTimeMessage = message.toString();

            log("First Time Message Loaded!");
        } catch (NullPointerException e) {
            error(e.getMessage());
            error("Cannot Find \"enable-first-time-message\" Boolean in Config! Make sure it's there and reload the plugin.");
        }

        /* Parse Message Names and Messages by Permission Node */
        try {
            messageNames = config.getStringList("messages"); // Initialize the Array as a Template

            for (String messageName : messageNames) {
                StringBuilder message = new StringBuilder();
                String[] tempAdd = new String[config.getStringList(messageName).size()];
                tempAdd = config.getStringList(messageName).toArray(tempAdd);
                parseColorCodesOld(message, tempAdd);
                messages.put("ca.message." + messageName, message.toString());
            }

            log("Messages Loaded!");
        } catch (NullPointerException e) {
            error(e.getMessage());
            error("Error with the Message Section in the Config! Make sure it's set properly and reload the plugin.");
        }

        try {
            botmChannelID = getConfigString("botm-channel-id");
            if (getConfigString("botm-channel-id").equalsIgnoreCase("000000000000000000") || getConfigString("botm-channel-id").equalsIgnoreCase("")) throw new Exception();
        } catch (Exception e) {
            warn("Invalid BOTM Channel ID! Please enter a valid Bot Token in config.yml and reload the plugin.");
            discordConnected = false;
        }

        try {
            botmBannedWords = getConfig().getStringList("botm-banned-words");
            log("BOTM Message Banned Words list loaded! Not listing here for obvious reasons.");
        } catch (Exception e) {
            warn("Invalid banned words list! Please make sure the list is valid in the config.yml and doesn't contain syntax errors.");
            flag = false;
        }

        log("Config loaded!");
        return flag;
    }

    private void parseColorCodesOld(StringBuilder message, String[] tempAdd) {
        for (int i = 0; i < tempAdd.length; i++) {
            tempAdd[i] = tempAdd[i].replaceAll("&", "§");
            if (i == 0) {
                message.append(tempAdd[i]);
            } else {
                message.append("\n").append(tempAdd[i]);
            }
        }
    }

    private void registerCommands() {
        try {
            Objects.requireNonNull(this.getCommand("ca")).setExecutor(new BaseCMD());
            Objects.requireNonNull(this.getCommand("ca")).setTabCompleter(new BaseTC());

            Objects.requireNonNull(this.getCommand("botm")).setExecutor(new BuildOfTheMonthCMD());
            Objects.requireNonNull(this.getCommand("botm")).setTabCompleter(new BuildOfTheMonthTC());

            Objects.requireNonNull(this.getCommand("bce")).setExecutor(new BuycraftMailCMD());

            Objects.requireNonNull(this.getCommand("rmd")).setExecutor(new StallRemindCMD());
            Objects.requireNonNull(this.getCommand("rmd")).setTabCompleter(new StallRemindTC());

            Objects.requireNonNull(this.getCommand("sdu")).setExecutor(new SyncDiscordUsernameCMD());
            Objects.requireNonNull(this.getCommand("sdu")).setTabCompleter(new SyncDiscordUsernameTC());

            Objects.requireNonNull(this.getCommand("getanarchyitem")).setExecutor(new AnarchyItemsCMD());
            Objects.requireNonNull(this.getCommand("getanarchyitem")).setTabCompleter(new AnarchyItemsTC());

            log("Commands Registered Successfully!");
        } catch (NullPointerException e) {
            error(e.getMessage());
        }
    }

    private String getConfigString(String entryName) {
        return config.getString(entryName);
    }

    private int getConfigInt(String entryName) {
        return config.getInt(entryName);
    }

    private boolean getConfigBool(String entryName) {
        return config.getBoolean(entryName);
    }

    private void reloadCustomConfig() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(customConfigFile);
        config.options().copyDefaults(true);

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

    private FileConfiguration getCustomConfig() {
        if (config == null) {
            reloadCustomConfig();
        }
        return config;
    }

    private void saveCustomConfig() {
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

    public void debug(String message) {
        this.getLogger().log(Level.FINE, message);
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
     * Replace all the color codes (prepended with &) with the corresponding color code.
     * This uses raw char arrays, so it can be considered to be extremely fast.
     *
     * @param text the text to replace the color codes in
     * @return string with color codes replaced
     */
    public static String replaceColors(String text) {
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
