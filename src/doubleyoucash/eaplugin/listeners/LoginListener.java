package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class LoginListener implements Listener {

    private final boolean updateRequired;
    private final String[] versions;
    private final CashApp ca;
    private final Database db;

    public LoginListener(boolean updateRequired, String[] versions) {
        this.updateRequired = updateRequired;
        this.versions = versions;
        ca = CashApp.getPlugin();
        db = ca.db;

    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        /* Check for Updates and send message to player with permission to see updates */
        if (updateRequired && (event.getPlayer().hasPermission("cashapp.update") || event.getPlayer().isOp())) {
            event.getPlayer().sendMessage("[§aCash§bApp§f] Version §c" + versions[0] + " §favailable! You have §c" + versions[1] + ".");
            ca.log("Version " + versions[0] + " available! You have " + versions[1] + ".");
        }

        /* Check for voting streak status and send message if enabled for that user */
        if (db.userExistsInStreaks(id) && db.getStreakStatus(id)) {
            int streak = db.getStreak(id);
            if (streak == 1) event.getPlayer().sendMessage("[§aCash§bApp§f] Current vote streak: " + streak + " day");
                else event.getPlayer().sendMessage("[§aCash§bApp§f] Current vote streak: " + streak + " days");
        }

    }

}
