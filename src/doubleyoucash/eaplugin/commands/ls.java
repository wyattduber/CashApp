package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ls implements CommandExecutor {

    private final CashApp ca;
    private final ConsoleCommandSender console;

    public ls() {
        ca = CashApp.getPlugin();
        console = Bukkit.getServer().getConsoleSender();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {

        if (strings.length < 2) return false;
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("§cCommand cannot be used from console!");
            return true;
        }

        Player player = (Player) commandSender;

        int line = Integer.parseInt(strings[0]);
        line--;
        if (line > 3 || line < 0) {
            player.sendMessage("§cLine Range must be between 1 and 4!");
            return true;
        }
        StringBuilder message = new StringBuilder();

        if (strings.length > 2) {
            for (int i = 2; i < strings.length; i++) {
                message.append(strings[i]).append(" ");
            }
        } else {
            message.append(strings[1]);
        }

        String s = message.toString();
        Sign sign;
        if (player.getTargetBlock(null, 10).getState() instanceof Sign) {
            sign = (Sign) player.getTargetBlock(null, 10).getState();
        } else {
            player.sendMessage("§cYou are not looking at a sign!");
            return true;
        }

        try {
            s = ChatColor.translateAlternateColorCodes('&', s);
            sign.line(line, Component.text(s));
            sign.update();
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§cUnknown Error Occurred. Nag Wcash about this error.");
            return true;
        }

        line++;
        player.sendMessage("Line " + line + " updated!");
        return true;
    }
}
