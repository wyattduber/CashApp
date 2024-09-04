package wyattduber.cashapp.doNotDisturb;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;

public class DoNotDisturbProtocolListener {

    public static void initProtocolListeners(CashApp plugin, ProtocolManager protocolManager, Database db) {
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (db.getDoNotDisturbStatus(player)) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.SYSTEM_CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (db.getDoNotDisturbStatus(player)) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.DISGUISED_CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (db.getDoNotDisturbStatus(player)) {
                    event.setCancelled(true);
                }
            }
        });
    }

}
