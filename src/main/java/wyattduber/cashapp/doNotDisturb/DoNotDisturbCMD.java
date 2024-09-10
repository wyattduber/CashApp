package wyattduber.cashapp.doNotDisturb;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.helpers.ChatMessageHelper;
import wyattduber.cashapp.helpers.TabCompleterHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoNotDisturbCMD implements TabExecutor {

    private final CashApp ca;
    private final Database db;

    public DoNotDisturbCMD() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    /**
     * Usage: /stats (statType) (statSubType) [player]
     * @param sender The sender of the command
     * @param command The command that was executed
     * @param label The alias of the command
     * @param args The arguments provided with the command
     * @return true if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("ca.dnd")) {
            ChatMessageHelper.sendMessage(sender, "&cYou don't have permission to use this command!", true);
            return true;
        }

        if (args.length == 0 || args.length > 2) return false;

        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off") && !args[0].equalsIgnoreCase("status")) return false;

            if (!(sender instanceof Player player)) {
                ChatMessageHelper.sendMessage(sender, "&cThis command must be run as a player, or you must target a player!", true);
            }
            else {
                boolean statusRequested = args[0].equalsIgnoreCase("status");

                if (statusRequested) {
                    var currentStatus = db.getDoNotDisturbStatus(player);
                    ChatMessageHelper.sendMessage(sender, "Do Not Disturb Status currently " + (currentStatus ? "&aEnabled" : "&cDisabled" + "&f!"), true);
                } else {
                    boolean status = args[0].equalsIgnoreCase("on");

                    db.setDoNotDisturbStatus(player, status);
                    ChatMessageHelper.sendMessage(player, "Do Not Disturb " + (status ? "Enabled!" : "Disabled!"), true);
                }
            }
        }
        else {
            if (!sender.hasPermission("ca.dnd.others") && !(sender instanceof ConsoleCommandSender)) {
                ChatMessageHelper.sendMessage(sender, "&cYou don't have permission to run this command on other players!", true);
            }
            if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off") && !args[0].equalsIgnoreCase("status")) return false;

            boolean statusRequested = args[0].equalsIgnoreCase("status");
            String playerName = args[1];

            Player player = getPlayerIfExists(playerName);
            if (player == null) {
                ChatMessageHelper.sendMessage(sender, "&cPlayer " + playerName + "cannot be found!", true);
                return true;
            }

            if (statusRequested) {
                var currentStatus = db.getDoNotDisturbStatus(player);
                ChatMessageHelper.sendMessage(sender, "Do Not Disturb Status currently " + (currentStatus ? "&aEnabled" : "&cDisabled" + "&f!"), true);
            }
            else {
                boolean status = args[0].equalsIgnoreCase("on");

                db.setDoNotDisturbStatus(player, status);
                ChatMessageHelper.sendMessage(sender, "Do Not Disturb " + (status ? "Enabled!" : "Disabled!") + "for " + playerName, true);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("on");
                tabs.add("off");
                tabs.add("status");
                tabs = TabCompleterHelper.narrowDownTabCompleteResults(args[0], tabs);
                yield tabs;
            }
            case 2 -> {
                if (sender.hasPermission("ca.dnd.others")) {
                    tabs = TabCompleterHelper.narrowDownTabCompleteResultsOnlinePlayerList(args[1]);
                }
                yield tabs;
            }
            default -> tabs;
        };
    }

    private @Nullable Player getPlayerIfExists(String playerName) {
        Player player = null;
        var onlinePlayer = ca.getServer().getPlayerExact(playerName);
        if (onlinePlayer != null) {
            player = ca.getServer().getPlayer(playerName);
        }
        else {
            var offlinePlayerList = Arrays.stream(Bukkit.getOfflinePlayers()).toList();
            var offlinePlayerNameList = new ArrayList<String>();
            offlinePlayerList.forEach(offlinePlayer -> offlinePlayerNameList.add(offlinePlayer.getName()));

            if (offlinePlayerNameList.contains(playerName)) {
                player = Bukkit.getOfflinePlayer(playerName).getPlayer();
            }
        }

        return player;
    }

}
