package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {

    private final boolean updateRequired;
    private final String[] versions;
    private final CashApp ca;

    public LoginListener(boolean updateRequired, String[] versions) {
        this.updateRequired = updateRequired;
        this.versions = versions;
        this.ca = CashApp.getPlugin();
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        /* Check for Updates and send message to player with permission to see updates */
        if (updateRequired && (event.getPlayer().hasPermission("cashapp.update") || event.getPlayer().isOp())) {
            event.getPlayer().sendMessage("[§aCash§bApp§f] Version §c" + versions[0] + " §favailable! You have §c" + versions[1] + ".");
            ca.log("Version " + versions[0] + " available! You have " + versions[1] + ".");
        }
    }

}
