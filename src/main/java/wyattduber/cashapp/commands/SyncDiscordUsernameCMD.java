package wyattduber.cashapp.commands;

import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.javacord.JavacordHelper;
import wyattduber.cashapp.database.Database;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public class SyncDiscordUsernameCMD implements CommandExecutor {

    private final CashApp ca;
    private final JavacordHelper js;
    private final Database db;

    public SyncDiscordUsernameCMD() {
        ca = CashApp.getPlugin();
        js = ca.js;
        db = ca.db;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String text, @NotNull String[] args) {
        String BASIC_HELP_MESSAGE = """
                Sync Discord Username (SDU) Help:
                Command Format: /sdu <username> [#/unsync]
                - <username> is your Discord username, not your nickname.
                - [#] is used for the code that is sent to you in your DM's by the Bot.
                - [unsync] is used to unsync your username.

                How to use:
                - So for example, first you will run "/sdu wcash" in game.
                - Then you will receive a DM from the bot with a code.
                - Then you will run "/sdu wcash <code>" in game, and your username will be synced.
                - If you want to unsync your username, run "/sdu wcash unsync" in game.
                
                To toggle the login reminder, use &a/sdu reminder [on/off]&7.
                """;

        // Check if the server is connected to discord
        if (!ca.discordConnected) {
            ca.sendMessage(sender, "&cThe server is not connected to Discord!");
            return true;
        }

        // Check args length
        if (args.length > 3 || args.length == 0) {
            ca.sendMessage(sender, BASIC_HELP_MESSAGE);
            return true;
        }

        // Check if the command is being executed by the console
        if (sender instanceof ConsoleCommandSender) {
            ca.sendMessage(sender, "&cYou must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (ca.usersCurrentlySyncing.containsKey(sender.getName())) {
                ca.sendMessage(sender, "&cYou are already syncing your username! Please check your DM's for a code from the bot.");
                return true;
            }

            if (player.hasPermission("ca.sdu")) startSync(player, args[0]);
            else ca.sendMessage(sender, "&cYou must be a player to use this command!");
            return true;
        }

        if (args[1].equalsIgnoreCase("unsync")) {
            if (player.hasPermission("ca.sdu")) {
                if (ca.usersCurrentlySyncing.containsKey(sender.getName())) {
                    ca.sendMessage(sender, "&cYou are already syncing your username! Please check your DM's for a code from the bot.");
                    return true;
                }
                String username = player.getName();
                User user = js.checkUserExists(args[0]);
                if (user == null) {
                    ca.sendMessage(player, "&cYou are not linked to a Discord account! Please contact an admin.");
                    return true;
                }
                if (!db.isSynced(player.getUniqueId())) {
                    ca.sendMessage(player, "&cYou are not synced to a Discord account!");
                    return true;
                }
                if (!Long.toString(db.getSyncedDiscordID(player.getUniqueId())).equals(user.getIdAsString())) {
                    ca.sendMessage(player, "&cYou are not synced to that account!");
                    return true;
                }
                js.unsyncUsername(user);
                db.updateSyncRecord(player.getUniqueId(), username, user.getId(), false);
                ca.sendMessage(player, "Your username has been unsynced!");
                return true;
            } else {
                ca.sendMessage(player, "&cYou don't have permission to use this command!");
                return true;
            }
        } else if (args[1].equalsIgnoreCase("reminder")) {
            if (player.hasPermission("ca.sdu")) {
                if (args.length < 3) {
                    String status = db.getSyncReminderStatus(player.getUniqueId()) ? "&aON" : "&cOFF";
                    ca.sendMessage(player, "&aReminder Status: " + status);
                    return true;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    db.setSyncReminderStatus(player.getUniqueId(), true);
                    ca.sendMessage(player, "&aUsername Sync Reminder turned on!");
                    return true;
                } else if (args[2].equalsIgnoreCase("off")) {
                    db.setSyncReminderStatus(player.getUniqueId(), false);
                    ca.sendMessage(player, "&aUsername Sync Reminder turned off!");
                    return true;
                } else {
                    ca.sendMessage(player, "&cInvalid argument! Please use &a/sdu reminder [on/off]&c.");
                    return true;
                }
            } else {
                ca.sendMessage(player, "&cYou don't have permission to use this command!");
                return true;
            }
        }

        if (player.hasPermission("ca.sdu")) {
            String username = player.getName();
            User user = js.checkUserExists(args[0]);
            if (user == null) {
                ca.sendMessage(player, "&cThat Discord account does not exist!");
                return true;
            }
            if (db.isSynced(player.getUniqueId())) {
                ca.sendMessage(player, "&cThat Discord account is already synced to a Minecraft account!");
                return true;
            }
            if (!ca.usersCurrentlySyncing.containsKey(username)) {
                ca.sendMessage(player, "&cYou are not syncing your username!");
                return true;
            }
            int code;
            try {
                code = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ca.sendMessage(player, "&cThe code you entered is not a number! Please check your DM's for a code from the bot.");
                return true;
            }
            int savedCode = ca.usersCurrentlySyncing.get(username);
            if (code == savedCode) {
                try {
                    js.syncUsername(user, player.getName());
                } catch (Exception e) {
                    ca.sendMessage(player, "&cAn error occurred while syncing your username! Please contact an admin.");
                    return true;
                }
                ca.usersCurrentlySyncing.remove(username);
                if (!db.userExistsInSync(player.getUniqueId())) {
                    ca.log("Returned false! " + username + " " + user.getId() + " " + player.getUniqueId() + " " + player.getName());
                    db.addSyncRecord(player.getUniqueId(), player.getName(), user.getId(), true);
                } else {
                    ca.log("Returned false! " + username + " " + user.getId() + " " + player.getUniqueId() + " " + player.getName());
                    db.updateSyncRecord(player.getUniqueId(), player.getName(), user.getId(), true);
                }
                ca.sendMessage(player, "Your username has been synced!");
                return true;
            } else {
                ca.sendMessage(player, "&cThe code you entered is incorrect! Please check your DM's for a code from the bot.");
                return true;
            }
        } else {
            ca.sendMessage(player, "&cYou don't have permission to use this command!");
            return true;
        }
    }

    public void startSync(Player player, String username) {
        User user = js.checkUserExists(username);
        if (user == null) {
            ca.sendMessage(player, "&cYou are not linked to a Discord account! Please contact an admin.");
            return;
        }

        String message =
                """
                Please check your DM's for a code from the bot.
                If you did not receive a DM, please contact an admin.
                If you did receive a DM, please run /sdu <username> <code> in game.
                """;

        ca.sendMessage(player, message);
        ca.usersCurrentlySyncing.put(player.getName(), js.sendCode(user));
    }
}