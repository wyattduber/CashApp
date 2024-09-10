package wyattduber.cashapp.chats;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.helpers.ChatMessageHelper;

public class AdminChatCMD implements CommandExecutor {

    private final CashApp ca;

    public AdminChatCMD() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (string.isEmpty()) {
            return false;
        }

        String message = String.join(" ", args);
        if (sender instanceof ConsoleCommandSender CCSender) {
            String messageFromServer = "§3§lAdminChat §6Server§f: " + message;
            ChatMessageHelper.sendMessage(sender, messageFromServer, false);
            for (Player player : ca.getServer().getOnlinePlayers()) {
                if (player.hasPermission("ca.adminchat")) {
                    ChatMessageHelper.sendMessage(player, messageFromServer, false);
                }
            }
        } else if (sender instanceof Player player) {
            CMIUser user = CMIUser.getUser(player);
            String messageFromPlayer = "§3§lAdminChat§r" + getPrefix(user) + user.getDisplayName() + "§f: " + message;
            ChatMessageHelper.sendMessage(ca.getServer().getConsoleSender(), messageFromPlayer, false);
            for (Player recipient : ca.getServer().getOnlinePlayers()) {
                if (recipient.hasPermission("ca.adminchat")) {
                    ChatMessageHelper.sendMessage(recipient, CMIChatColor.translate(messageFromPlayer), false);
                }
            }
        }
        return true;
    }

    private String getPrefix(CMIUser user) {
        var prefix = user.getPrefix();
        var manager = CMI.getInstance().getChatFormatManager();
        return prefix.isEmpty() ? "" : prefix + " ";
    }
}
