package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.JavacordStart;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CA implements CommandExecutor {

    private final CashApp ca = CashApp.getPlugin();
    private final JavacordStart js;

    public CA() {
        js = ca.js;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            if (sender instanceof Player player) {
                if (args[0].equalsIgnoreCase("reload")) { // reload command
                    ca.reload();
                    player.sendMessage("§f[§aCash§bApp§f] Configuration Reloaded!");
                    return true;
                }
            }
        }

        return false;
    }

}
