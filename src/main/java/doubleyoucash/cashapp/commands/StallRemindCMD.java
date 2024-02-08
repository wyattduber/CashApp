package doubleyoucash.cashapp.commands;

import doubleyoucash.cashapp.CashApp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class StallRemindCMD implements CommandExecutor {

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

        for (String arg : strings) {
            String cmd = "cmi mail send " + arg + " " + ca.mallMsg;
            Bukkit.dispatchCommand(console, cmd);
        }

        Bukkit.dispatchCommand(console, "bce -1 " + commandSender.getName());

        return true;
    }
}