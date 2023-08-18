package doubleyoucash.cashapp.commands;

import doubleyoucash.cashapp.CashApp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CA implements CommandExecutor {

    private final CashApp ca;

    public CA() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) { // reload command
                ca.reload();
                sender.sendMessage("§f[§aCash§bApp§f] Configuration Reloaded!");
                return true;
            }
        }
        return false;
    }

}
