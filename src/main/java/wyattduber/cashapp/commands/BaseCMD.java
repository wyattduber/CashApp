package wyattduber.cashapp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.helpers.TabCompleterHelper;

public class BaseCMD implements TabExecutor {

    private final CashApp ca;

    public BaseCMD() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            switch (args[0].toLowerCase()) { // reload command
                case "reload" -> {
                    ca.reload();
                    ca.sendMessage(sender, "Configuration Reloaded!");
                    return true;
                }
                case "commands","help" -> {
                    StringBuilder msg = new StringBuilder();
                    if (sender.hasPermission("ca.botm"))
                        msg.append("/botm <username> <world> <x> <y> <z> <message...>");
                    if (sender.hasPermission("ca.dnd")) {
                        msg.append("/dnd <on/off>");
                        if (sender.hasPermission("ca.dnd.others")) msg.append(" [player]");
                    }
                    if (sender.hasPermission("ca.rmd"))
                        msg.append("/rmd <username-1> <username-2> ... <username-n>");
                    if (sender.hasPermission("ca.getanarchyitem")) {
                        msg.append("/getAnarchyItem <item/trophy> [trophyType]");
                        if (sender.hasPermission("ca.getanarchyitem.others"))
                            msg.append(" [player]");
                    }

                    ca.sendMessage(sender, msg.toString());
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("reload");
                tabs.add("commands");
                tabs.add("help");
                tabs = TabCompleterHelper.narrowDownTabCompleteResults(args[0], tabs);
                yield tabs;
            }
            default -> tabs;
        };
    }
}
