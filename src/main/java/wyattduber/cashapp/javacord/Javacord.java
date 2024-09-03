package wyattduber.cashapp.javacord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.MessageCreateListener;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.javacord.tickets.TicketMessageListener;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

public class Javacord {

    public DiscordApi api;
    public Server discordServer;
    public TextChannel botmChannel;
    public List<Long> openTickets;
    public TextChannel closedTicketChannel;

    private MessageCreateListener ticketMessageListener;
    private final CashApp ca = CashApp.getPlugin();
    private final Database db = ca.db;

    public void connectAPI() {
        parseConfig();
    }

    public void disconnectAPI() {
        try {
            // Unregister Listeners
            api.removeListener(ticketMessageListener);

            if (api != null) {
                api.disconnect();
            }
            api = null;
        } catch (Exception e) {
            ca.error("Error Disconnecting from API! Contact the developer.");
        }
    }

    public void reload() {
        disconnectAPI();
        connectAPI();
    }

    private void parseConfig() {
        if (ca.botToken == null) {
            return;
        }

        try {
            api = new DiscordApiBuilder().setToken(ca.botToken).setAllIntents().login().join();
            ca.log("Connected to " + api.getYourself().getName() + " Bot!");
        } catch (Exception e) {
            ca.warn("Could not connect to API! Please enter a valid Bot Token in config.yml and reload the plugin.");
            ca.warn("If the bot-token is valid, please file an issue on our GitHub.");
        }

        try {
            assert api != null;
            if (api.getServerById(ca.serverID).isPresent())
                discordServer = api.getServerById(ca.serverID).get();
            assert discordServer != null;
            ca.log("Connected to " + discordServer.getName() + " Discord Server!");
        } catch (Exception e) {
            ca.warn("Server not Found! Please enter a valid Server ID in config.yml and reload the plugin.");
        }

        try {
            if (api.getTextChannelById(ca.botmChannelID).isPresent()) {
                botmChannel = api.getTextChannelById(ca.botmChannelID).get();
                ca.log("Connected to BOTM Channel!");
            }
        } catch (Exception e) {
            ca.warn("BOTM Channel not Found! Please enter a valid Channel ID in config.yml and reload the plugin.");
        }

        try {
            if (api.getTextChannelById(ca.closedTicketChannelID).isPresent()) {
                closedTicketChannel = api.getTextChannelById(ca.closedTicketChannelID).get();
                ca.log("Connected to Closed Ticket Channel!");
            }
        } catch (Exception e) {
            ca.warn("Closed Ticket Channel not Found! Please enter a valid Channel ID in config.yml and reload the plugin.");

        }

        verifyOpenTickets();
        initListeners();
    }

    private void verifyOpenTickets() {
        openTickets = db.getOpenTickets();

        for (long ticket : openTickets) {
            if (api.getTextChannelById(ticket).isEmpty()) {
                ca.warn("Ticket Channel " + ticket + " not Found! Please enter a valid Channel ID in the database.");
                db.closeTicket(ticket);
                openTickets.remove(ticket);
            }
        }
    }

    private void initListeners() {
        ticketMessageListener = new TicketMessageListener();
        api.addMessageCreateListener(ticketMessageListener);
    }

    public void sendBOTMMessage(String username, String world, String x, String y, String z, String message) {
        String messageToSend = "Name: " + username + "\n" +
                               "World: " + world + "\n" +
                               "Co-Ords: " + x + " " + y + " " + z + "\n" +
                               "Other: " + message;
        botmChannel.sendMessage(messageToSend);
    }

    public User checkUserExists(String username) {
        try {
            User user = api.getCachedUsersByNameIgnoreCase(username).iterator().next();
            discordServer.requestMember(user).get().getId();
            return user;
        } catch (NullPointerException | InterruptedException | ExecutionException | NoSuchElementException e) {
            return null;
        }
    }
}
