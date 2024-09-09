package wyattduber.cashapp.doNotDisturb;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.helpers.ChatMessageHelper;

public class DoNotDisturbListener implements Listener {

    private final CashApp ca;
    private final Database db;

    public DoNotDisturbListener() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event) {
        // Check if user has do not disturb status
        // If so, they should not be able to send messages. Should alert user of their status
        if (db.getDoNotDisturbStatus(event.getPlayer())) {
            event.setCancelled(true);
            ca.log("Player " + event.getPlayer().getName() + " attempted to send a message while having Do Not Disturb enabled. Message: " + PlainTextComponentSerializer.plainText().serialize(event.message()));
            ChatMessageHelper.sendMessage(event.getPlayer(), "&cYou have Do Not Disturb enabled. You cannot send messages. Do &a/dnd off &cto disable.");
            return;
        }

        // Special case if a staff member were to specifically shout a message out to all players
        var message = event.message();
        if (message.toString().charAt(0) == ca.DND_SHOUT_MESSAGE_CHAR) {
            var sender = event.getPlayer();

            if (sender.hasPermission("ca.donotremove.premodperm") ||
                sender.hasPermission("ca.donotremove.moderatorperm") ||
                sender.hasPermission("ca.donotremove.moderatorplusperm") ||
                sender.hasPermission("ca.donotremove.councilperm"))
                return;
        }
        event.viewers().removeIf(viewer -> {
            if (viewer instanceof Player player) {
                return db.getDoNotDisturbStatus(player);
            }
            return false;
        });
    }

    @EventHandler
    public void onBroadcastMessage(BroadcastMessageEvent event) {
        event.getRecipients().removeIf(recipient -> {
            if (recipient instanceof Player player) {
                return db.getDoNotDisturbStatus(player);
            }
            return false;
        });
    }
}
