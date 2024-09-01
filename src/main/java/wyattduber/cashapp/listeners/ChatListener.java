package wyattduber.cashapp.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;

public class ChatListener implements Listener {

    private final CashApp ca;
    private final Database db;

    public ChatListener() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event) {
        // Special case if a staff member were to specifically shout a message out to all players
        var message = event.message();
        if (message.toString().charAt(0) == ca.DND_SHOUT_MESSAGE_CHAR) {
            var sender = event.getPlayer();
            PermissionUser permUser = PermissionsEx.getUser(sender);
            var rank = permUser.getRankLadderGroup("default");
            String rankName = rank.getName();
            if (rankName.equalsIgnoreCase("premod") ||
                    rankName.equalsIgnoreCase("moderator") ||
                    rankName.equalsIgnoreCase("moderator+") ||
                    rankName.equalsIgnoreCase("council") ||
                    rankName.equalsIgnoreCase("element") ||
                    rankName.equalsIgnoreCase("daemon"))
                return;

        }
        event.viewers().removeIf(player -> db.getDoNotDisturbStatus((Player) player));
    }

}
