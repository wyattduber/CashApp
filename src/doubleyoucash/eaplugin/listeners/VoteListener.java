package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.bencodez.votingplugin.events.PlayerVoteEvent;

public class VoteListener implements Listener {

    private final CashApp ca;

    public VoteListener() {
        this.ca = CashApp.getPlugin();
    }

    @EventHandler
    public void onVote(PlayerVoteEvent event) {
        Player player = event.getVotingPluginUser().getPlayer();



    }

}
