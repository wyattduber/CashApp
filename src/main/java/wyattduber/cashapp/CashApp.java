package wyattduber.cashapp;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wyattduber.cashapp.anarchyItems.AnarchyItemsCMD;
import wyattduber.cashapp.botm.BuildOfTheMonthCMD;
import wyattduber.cashapp.chats.AdminChatCMD;
import wyattduber.cashapp.chats.GuideChatCMD;
import wyattduber.cashapp.commands.*;
import wyattduber.cashapp.anarchyItems.customitems.ItemListener;
import wyattduber.cashapp.anarchyItems.customitems.ItemManager;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.doNotDisturb.DoNotDisturbCMD;
import wyattduber.cashapp.doNotDisturb.DoNotDisturbListener;
import wyattduber.cashapp.helpers.lib.LibrarySetup;
import wyattduber.cashapp.connectors.Javacord;
import wyattduber.cashapp.listeners.LoginListener;
import wyattduber.cashapp.listeners.item.SnifferBurgerListener;
import wyattduber.cashapp.listeners.item.SnowGolemChristmasItemListener;
import wyattduber.cashapp.mallFeatures.SetStallDescCMD;
import wyattduber.cashapp.placeholders.PlaceholderHandler;
import wyattduber.cashapp.mallFeatures.StallRemindCMD;

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
    public LoginListener ll;
    public ItemListener il;
    public DoNotDisturbListener cl;
    public SnifferBurgerListener sbl;
    public SnowGolemChristmasItemListener sgcil;
    public Javacord js;
    public Database db;
    public ProtocolManager protocolManager;
    public boolean discordConnected;

    // Config Settings
    public boolean debugMode;
    public List<String> modList;
    public List<String> modPlusList;
    public String botToken;
    public long serverID;
    public String mallMsg;
    public boolean useFirstTimeMessage;
    public int messageDelayTicks;
    public String firstTimeMessage;
    public List<String> messageNames;
    public HashMap<String, String> messages = new HashMap<>();
    public long botmChannelID;
    public long closedTicketChannelID;
    public List<Long> modLevelTicketRoles;
    public List<String> botmBannedWords;
    public char DND_SHOUT_MESSAGE_CHAR;

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
         protocolManager = ProtocolLibrary.getProtocolManager();
         initListeners();

        /* Config Parsing */
        if (parseConfig()) {
            js = new Javacord();
            js.connectAPI();
        }

        /* Commands */
        try {
            registerCommands();
        } catch (Exception e) {
            error("Error setting up commands! Contact the developer if you cannot fix this issue.");
        }

        /* Register Placeholders */
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHandler().register();
        }
    }

    @Override
    public void onDisable() {
        if (js != null) {
            js.disconnectAPI();
        }
    }

    public void reload() {
        // Un-Register Listeners
        PlayerJoinEvent.getHandlerList().unregister(ll);

        ThrownEggHatchEvent.getHandlerList().unregister(il);
        EntityShootBowEvent.getHandlerList().unregister(il);
        EntityLoadCrossbowEvent.getHandlerList().unregister(il);
        EntityDamageEvent.getHandlerList().unregister(il);
        AsyncChatEvent.getHandlerList().unregister(cl);
        EntityDeathEvent.getHandlerList().unregister(sbl);
        EntityDeathEvent.getHandlerList().unregister(sgcil);
        ProjectileLaunchEvent.getHandlerList().unregister(sgcil);

        // Unregister ProtocolLib Packet Listener
        protocolManager.removePacketListeners(this);

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
            js = new Javacord();
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

        cl = new DoNotDisturbListener();
        getServer().getPluginManager().registerEvents(cl, this);

        sbl = new SnifferBurgerListener();
        getServer().getPluginManager().registerEvents(sbl, this);

        sgcil = new SnowGolemChristmasItemListener();
        getServer().getPluginManager().registerEvents(sgcil, this);

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
            serverID = getConfigLong("server-id");
            if (Long.toString(getConfigLong("server-id")).equals("000000000000000000")) {
                throw new Exception();
            } else {
                getConfigLong("server-id");
            }
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
            mallMsg = String.valueOf(getConfigString("mall-remind-msg"));
            log("Mall Reminder Message Loaded!");
        } catch (Exception e) {
            warn("Invalid Mall Reminder Message! Please set the mall-remind-msg in the config.yml!");
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
            botmChannelID = getConfigLong("botm-channel-id");
            if (Long.toString(getConfigLong("botm-channel-id")).equalsIgnoreCase("000000000000000000")) throw new Exception();
        } catch (Exception e) {
            warn("Invalid BOTM Channel ID! Please enter a valid Bot Token in config.yml and reload the plugin.");
            discordConnected = false;
        }

        try {
            closedTicketChannelID = getConfigLong("closed-ticket-channel-id");
            if (Long.toString(getConfigLong("closed-ticket-channel-id")).equalsIgnoreCase("000000000000000000")) throw new Exception();
        } catch (Exception e) {
            warn("Invalid Closed Ticket Channel ID! Please enter a valid Bot Token in config.yml and reload the plugin.");
            discordConnected = false;
        }

        try {
            modLevelTicketRoles = getConfig().getLongList("mod-level-ticket-roles");
            if (modLevelTicketRoles.isEmpty()) throw new Exception();

            log("Ticket Roles Loaded!");
        } catch (Exception e) {
            warn("Invalid Ticket Roles! Please make sure the roles are valid in the config.yml and don't contain syntax errors.");
            flag = false;
        }

        try {
            botmBannedWords = getConfig().getStringList("botm-banned-words");
            log("BOTM Message Banned Words list loaded! Not listing here for obvious reasons.");
        } catch (Exception e) {
            warn("Invalid banned words list! Please make sure the list is valid in the config.yml and doesn't contain syntax errors.");
            flag = false;
        }

        try {
            DND_SHOUT_MESSAGE_CHAR = Objects.requireNonNull(getConfig().getString("dnd-shout-message-char")).charAt(0);
            log("DND Shout Message Character loaded!");
        } catch (Exception ex) {
            warn("Invalid DnD Shout Message Character! Please make sure the entry is valid in the config.yml and doesn't contain syntax errors.");
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
            Objects.requireNonNull(this.getCommand("am")).setExecutor(new AdminChatCMD());
            Objects.requireNonNull(this.getCommand("ca")).setExecutor(new BaseCMD());
            Objects.requireNonNull(this.getCommand("botm")).setExecutor(new BuildOfTheMonthCMD());
            Objects.requireNonNull(this.getCommand("bce")).setExecutor(new BuycraftMailCMD());
            Objects.requireNonNull(this.getCommand("dnd")).setExecutor(new DoNotDisturbCMD());
            Objects.requireNonNull(this.getCommand("gc")).setExecutor(new GuideChatCMD());
            Objects.requireNonNull(this.getCommand("rmd")).setExecutor(new StallRemindCMD());
            Objects.requireNonNull(this.getCommand("getanarchyitem")).setExecutor(new AnarchyItemsCMD());
            Objects.requireNonNull(this.getCommand("setstalldesc")).setExecutor(new SetStallDescCMD());
            Objects.requireNonNull(this.getCommand("christmasGolem")).setExecutor(new SnowGolemChristmasItemCommand());

            log("Commands Registered Successfully!");
        } catch (NullPointerException e) {
            error(e.getMessage());
        }
    }

    public final List<String> stalls = Arrays.asList
    (
        "north-1",
        "north-2",
        "north-3",
        "north-Big",
        "north-4",
        "north-5",
        "north-6",
        "east-1",
        "east-2",
        "east-3",
        "east-4",
        "south-1",
        "south-2",
        "south-3",
        "south-Big",
        "south-4",
        "south-5",
        "south-6",
        "west-1",
        "west-2",
        "west-3",
        "west-4",
        "small-1",
        "small-2",
        "small-3",
        "small-4",
        "small-5",
        "small-6",
        "small-7",
        "small-8",
        "small-9",
        "small-10",
        "small-11",
        "small-12",
        "small-13",
        "small-14",
        "small-15",
        "small-16",
        "small-17",
        "small-18",
        "small-19",
        "small-20",
        "small-21",
        "small-22",
        "small-23",
        "small-24"
    );

    private String getConfigString(String entryName) {
        return config.getString(entryName);
    }

    private int getConfigInt(String entryName) {
        return config.getInt(entryName);
    }

    private boolean getConfigBool(String entryName) {
        return config.getBoolean(entryName);
    }

    private long getConfigLong(String entryName) {
        return config.getLong(entryName);
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
}
