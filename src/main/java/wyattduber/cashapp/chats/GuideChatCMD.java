package wyattduber.cashapp.chats;

import com.Zrips.CMI.Containers.CMIUser;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

public class GuideChatCMD implements CommandExecutor {

    private final CashApp ca;

    public GuideChatCMD() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }

        String message = String.join(" ", args);
        if (sender instanceof ConsoleCommandSender CCSender) {
            String messageFromServer = "§5§lGuideChat §6Server§f: " + message;
            CCSender.sendMessage(messageFromServer);
            for (Player player : ca.getServer().getOnlinePlayers()) {
                if (player.hasPermission("ca.guidechat")) {
                    player.sendMessage(Component.text(messageFromServer));
                }
            }
        } else if (sender instanceof Player player) {
            CMIUser user = CMIUser.getUser(player);
            String messageFromPlayer = "§5§lGuideChat §r" + getPrefix(user) + user.getDisplayName() + "§f: " + message;
            ca.getServer().getConsoleSender().sendMessage(messageFromPlayer);
            for (Player recipient : ca.getServer().getOnlinePlayers()) {
                if (recipient.hasPermission("ca.guidechat")) {
                    recipient.sendMessage(Component.text(messageFromPlayer));
                }
            }
        }
        return true;
    }

    private String getPrefix(CMIUser user) {
        var prefix = user.getPrefix();
        return prefix.isEmpty() ? "" : prefix + " ";
    }
}
