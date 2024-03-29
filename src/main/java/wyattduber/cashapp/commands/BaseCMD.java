package wyattduber.cashapp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

public class BaseCMD implements CommandExecutor {

    private final CashApp ca;

    public BaseCMD() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) { // reload command
                ca.reload();
                ca.sendMessage(sender, "Configuration Reloaded!");
                return true;
            }
        }
        return false;
    }
}
