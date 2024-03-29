package wyattduber.cashapp.javacord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;
import wyattduber.cashapp.CashApp;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

public class JavacordHelper {

    public DiscordApi api;
    public Server discordServer;
    public TextChannel botmChannel;

    private final CashApp ca = CashApp.getPlugin();

    public JavacordHelper() {
        parseConfig();
    }

    public void disableAPI() {
        try {
            if (api != null) {
                api.disconnect();
            }
            api = null;
        } catch (Exception e) {
            ca.error("Error Disconnecting from API! Contact the developer.");
        }
    }

    public void reload() {
        disableAPI();
        parseConfig();
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
            if (api.getServerById(ca.serverID).isPresent())
                discordServer = api.getServerById(ca.serverID).get();
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

    public void sendBOTMMessage(String username, String world, String x, String y, String z, String message) {
        String messageToSend = "Name: " + username + "\n" +
                               "World: " + world + "\n" +
                               "Co-Ords: " + x + " " + y + " " + z + "\n" +
                               "Other: " + message;
        botmChannel.sendMessage(messageToSend);
    }

    public int sendCode(User user) {
        int code = (int) (Math.random() * 1000000);
        user.openPrivateChannel().thenAccept(channel -> channel.sendMessage("Your code is: " + code)).join();
        return code;
    }

    public void syncUsername(User user, String username) {
        discordServer.updateNickname(user, username, "Username Sync").exceptionally(ExceptionLogger.get());
    }

    public void unsyncUsername(User user) {
        discordServer.updateNickname(user, "", "Username Unsync").exceptionally(ExceptionLogger.get());
    }

    public String getUserName(long discordId) {
        try {
            if (api.getCachedUserById(discordId).isPresent())
                return api.getCachedUserById(discordId).get().getDisplayName(discordServer);
            else throw new Exception();
        } catch (Exception e) {
            return null;
        }
    }
}
