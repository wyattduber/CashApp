package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class RMD implements CommandExecutor {

    private final CashApp ca;
    private final ConsoleCommandSender console;

    public RMD() {
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

        return true;
    }
}
