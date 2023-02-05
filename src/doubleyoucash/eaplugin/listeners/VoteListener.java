package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.bencodez.votingplugin.events.PlayerVoteEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class VoteListener implements Listener {

    private final CashApp ca;

    public VoteListener() {
        this.ca = CashApp.getPlugin();
    }

    @EventHandler
    public void onVote(PlayerVoteEvent event) {
        Player player = event.getVotingPluginUser().getPlayer();

        File file = ca.voteFiles.get(player.getUniqueId());

        try (FileWriter fw = new FileWriter(file)) {
            fw.append("Hello, world!");
        } catch (IOException e) {
            ca.error("Nag the developer about this error! This should not happen!");
            e.printStackTrace();
        }

    }

}
