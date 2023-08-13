package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.JavacordStart;
import doubleyoucash.eaplugin.database.Database;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SDU implements TabExecutor {

    private final CashApp ca;
    private final JavacordStart js;

    public SDU() {
        ca = CashApp.getPlugin();
        js = ca.js;
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
                """;

        if (args.length > 2 || args.length == 0) {
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

            if (player.hasPermission("ca.sdu")) startSync(player);
            else sender.sendMessage("§cYou must be a player to use this command!");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "unsync" -> {
                if (player.hasPermission("ca.sdu")) {
                    if (ca.usersCurrentlySyncing.containsKey(sender.getName())) {
                        sender.sendMessage("§cYou are already syncing your username! Please check your DM's for a code from the bot.");
                        return true;
                    }
                    String username = player.getName();
                    User user = js.checkUserExists(username);
                    if (user == null) {
                        player.sendMessage("§cYou are not linked to a Discord account! Please contact an admin.");
                        return true;
                    }
                    js.unsyncUsername(user);
                    player.sendMessage("Your username has been unsynced!");
                } else player.sendMessage("§cYou don't have permission to use this command!");
            }
            default -> {
                if (player.hasPermission("ca.sdu")) {
                    String username = player.getName();
                    User user = js.checkUserExists(username);
                    if (user == null) {
                        player.sendMessage("§cYou are not linked to a Discord account! Please contact an admin.");
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
                        js.syncUsername(user, player.getName());
                        player.sendMessage("Your username has been synced!");
                        ca.usersCurrentlySyncing.remove(username);
                    } else player.sendMessage("§cThe code you entered is incorrect! Please check your DM's for a code from the bot.");
                } else player.sendMessage("§cYou don't have permission to use this command!");
            }
        }
        return true;
    }

    public void startSync(Player player) {
        String username = player.getName();
        User user = js.checkUserExists(username);
        if (user == null) {
            player.sendMessage("§cYou are not linked to a Discord account! Please contact an admin.");
            return;
        }
        player.sendMessage("Please check your DM's for a code from the bot.");
        player.sendMessage("If you did not receive a DM, please contact an admin.");
        player.sendMessage("If you did receive a DM, please run /sdu confirm <code> in game.");
        ca.usersCurrentlySyncing.put(username, js.sendCode(user));
    } 

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> commands = new ArrayList<>();

        commands.add("discord_username");
        commands.add("#/unsync");

        return commands;
    }

}