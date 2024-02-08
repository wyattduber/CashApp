package doubleyoucash.cashapp.commands;

import doubleyoucash.cashapp.CashApp;
import doubleyoucash.cashapp.javacord.JavacordHelper;
import doubleyoucash.cashapp.database.Database;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyncDiscordUsernameCMD implements TabExecutor {

    private final CashApp ca;
    private final JavacordHelper js;
    private final Database db;

    public SyncDiscordUsernameCMD() {
        ca = CashApp.getPlugin();
        js = ca.js;
        db = ca.db;
    }

    /* §c */

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
                
                To toggle the login reminder, use §a/sdu reminder [on/off]§7.
                """;

        if (args.length > 3 || args.length == 0) {
            sender.sendMessage(BASIC_HELP_MESSAGE);
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("§cYou must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (ca.usersCurrentlySyncing.containsKey(sender.getName())) {
                sender.sendMessage("§cYou are already syncing your username! Please check your DM's for a code from the bot.");
                return true;
            }

            if (player.hasPermission("ca.sdu")) startSync(player, args[0]);
            else sender.sendMessage("§cYou must be a player to use this command!");
            return true;
        }

        if (args[1].equalsIgnoreCase("unsync")) {
            if (player.hasPermission("ca.sdu")) {
                if (ca.usersCurrentlySyncing.containsKey(sender.getName())) {
                    sender.sendMessage("§cYou are already syncing your username! Please check your DM's for a code from the bot.");
                    return true;
                }
                String username = player.getName();
                User user = js.checkUserExists(args[0]);
                if (user == null) {
                    player.sendMessage("§cYou are not linked to a Discord account! Please contact an admin.");
                    return true;
                }
                if (!db.isSynced(player.getUniqueId())) {
                    player.sendMessage("§cYou are not synced to a Discord account!");
                    return true;
                }
                if (!Long.toString(db.getSyncedDiscordID(player.getUniqueId())).equals(user.getIdAsString())) {
                    player.sendMessage("§cYou are not synced to that account!");
                    return true;
                }
                js.unsyncUsername(user);
                db.updateSyncRecord(player.getUniqueId(), username, user.getId(), false);
                player.sendMessage("Your username has been unsynced!");
                return true;
            } else {
                player.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }
        } else if (args[1].equalsIgnoreCase("reminder")) {
            if (player.hasPermission("ca.sdu")) {
                if (args.length < 3) {
                    String status = db.getSyncReminderStatus(player.getUniqueId()) ? "§aON" : "§cOFF";
                    player.sendMessage("§aReminder Status: " + status);
                    return true;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    db.setSyncReminderStatus(player.getUniqueId(), true);
                    player.sendMessage("§aUsername Sync Reminder turned on!");
                    return true;
                } else if (args[2].equalsIgnoreCase("off")) {
                    db.setSyncReminderStatus(player.getUniqueId(), false);
                    player.sendMessage("§aUsername Sync Reminder turned off!");
                    return true;
                } else {
                    player.sendMessage("§cInvalid argument! Please use §a/sdu reminder [on/off]§c.");
                    return true;
                }
            } else {
                player.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }
        }

        if (player.hasPermission("ca.sdu")) {
            String username = player.getName();
            User user = js.checkUserExists(args[0]);
            if (user == null) {
                player.sendMessage("§cThat Discord account does not exist!");
                return true;
            }
            if (db.isSynced(player.getUniqueId())) {
                player.sendMessage("§cThat Discord account is already synced to a Minecraft account!");
                return true;
            }
            if (!ca.usersCurrentlySyncing.containsKey(username)) {
                player.sendMessage("§cYou are not syncing your username!");
                return true;
            }
            int code = 0;
            try {
                code = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cThe code you entered is not a number! Please check your DM's for a code from the bot.");
                return true;
            }
            int savedCode = ca.usersCurrentlySyncing.get(username);
            if (code == savedCode) {
                try {
                    js.syncUsername(user, player.getName());
                } catch (Exception e) {
                    player.sendMessage("§cAn error occurred while syncing your username! Please contact an admin.");
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
                player.sendMessage("Your username has been synced!");
                return true;
            } else {
                player.sendMessage("§cThe code you entered is incorrect! Please check your DM's for a code from the bot.");
                return true;
            }
        } else {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
    }

    public void startSync(Player player, String username) {
        User user = js.checkUserExists(username);
        if (user == null) {
            player.sendMessage("§cYou are not linked to a Discord account! Please contact an admin.");
            return;
        }
        player.sendMessage("Please check your DM's for a code from the bot.");
        player.sendMessage("If you did not receive a DM, please contact an admin.");
        player.sendMessage("If you did receive a DM, please run /sdu <username> <code> in game.");
        ca.usersCurrentlySyncing.put(player.getName(), js.sendCode(user));
    } 

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> commands = new ArrayList<>();

        commands.add("discord_username");
        commands.add("#/unsync");

        return commands;
    }

}