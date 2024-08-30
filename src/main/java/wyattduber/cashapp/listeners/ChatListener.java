package wyattduber.cashapp.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        event.viewers().removeIf(player -> db.getDoNotDisturbStatus((Player) player));
    }

}
