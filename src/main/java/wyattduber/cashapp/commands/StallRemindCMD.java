package wyattduber.cashapp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

public class StallRemindCMD implements TabExecutor {

    private final CashApp ca;
    private final ConsoleCommandSender console;

    public StallRemindCMD() {
        ca = CashApp.getPlugin();
        console = Bukkit.getServer().getConsoleSender();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length < 1) {
            return false;
        }

        boolean flag = true;
        for (String arg : strings) {
            String cmd = "cmi mail send " + arg + " " + ca.mallMsg;
            flag = Bukkit.dispatchCommand(console, cmd);
        }

        if (flag) {
            ca.sendMessage(commandSender, "§aMessage sent to all players!");
        } else {
            ca.sendMessage(commandSender, "§cMessage failed to send to all players! One or more of the usernames were incorrect or couldn't be found.");
        }

        Bukkit.dispatchCommand(console, "bce -1 " + commandSender.getName());

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        tabs.add("Username-" + args.length);

        return tabs;
    }
}
