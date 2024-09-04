package wyattduber.cashapp.discordTickets;

import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.RegularServerChannelUpdater;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.connectors.Javacord;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class TicketHelper {

    public final CashApp ca;
    public final Database db;
    public final Javacord js;
    public final Server discordServer;

    public TicketHelper() {
        ca = CashApp.getPlugin();
        db = ca.db;
        js = ca.js;
        discordServer = js.discordServer;
    }

    public boolean createTicket(String name, String description, String ticketOpener, List<String> allowedOtherUsers, Player mcPlayerOpeningTicket, boolean isAdminOnly) {
        try {
            var ticketOpenerUser = js.checkUserExists(ticketOpener);
            if (ticketOpenerUser == null) throw new Exception("User " + ticketOpener + " cannot be found in this server!");

            ServerTextChannelBuilder channelBuilder = discordServer.createTextChannelBuilder()
                    .setName(name)
                    .setTopic(description);

            if (!isAdminOnly) {
                for (long roleId : ca.modLevelTicketRoles) {
                    var roleOptional = discordServer.getRoleById(roleId);
                    if (roleOptional.isEmpty())
                        throw new Exception("Role ID " + roleId + " does not match a role in this server!");

                    var role = roleOptional.get();

                    // Create permission builder for role and assign permissions
                    Permissions permissionsBuilderRole = (Permissions) new PermissionsBuilder()
                            .setAllowed(PermissionType.VIEW_CHANNEL)
                            .setAllowed(PermissionType.SEND_MESSAGES)
                            .setAllowed(PermissionType.EMBED_LINKS)
                            .setAllowed(PermissionType.ATTACH_FILE)
                            .setAllowed(PermissionType.READ_MESSAGE_HISTORY);

                    channelBuilder.addPermissionOverwrite(role, permissionsBuilderRole);
                }
            }

            // Set Permissions for ticket opener
            Permissions ticketOpenerPermissionBuilder = (Permissions) new PermissionsBuilder()
                    .setAllowed(PermissionType.VIEW_CHANNEL)
                    .setAllowed(PermissionType.SEND_MESSAGES)
                    .setAllowed(PermissionType.EMBED_LINKS)
                    .setAllowed(PermissionType.ATTACH_FILE)
                    .setAllowed(PermissionType.READ_MESSAGE_HISTORY);
            channelBuilder.addPermissionOverwrite(ticketOpenerUser, ticketOpenerPermissionBuilder);

            // Add Permission Overrides for other allowed users
            for (String allowedOtherUserName : allowedOtherUsers) {
                var allowedOtherUser = js.checkUserExists(allowedOtherUserName);
                if (allowedOtherUser == null) throw new Exception("User " + allowedOtherUserName + " cannot be found in this server!");

                Permissions allowedOtherUserPermissionBuilder = (Permissions) new PermissionsBuilder()
                        .setAllowed(PermissionType.VIEW_CHANNEL)
                        .setAllowed(PermissionType.SEND_MESSAGES)
                        .setAllowed(PermissionType.EMBED_LINKS)
                        .setAllowed(PermissionType.ATTACH_FILE)
                        .setAllowed(PermissionType.READ_MESSAGE_HISTORY);
                channelBuilder.addPermissionOverwrite(allowedOtherUser, allowedOtherUserPermissionBuilder);
            }

            // Set permissions for everyone
            var role = discordServer.getEveryoneRole();

            // Create permission builder for role and assign permissions
            Permissions everyoneRolePermissionBuilder = (Permissions) new PermissionsBuilder().setAllDenied();
            channelBuilder.addPermissionOverwrite(role, everyoneRolePermissionBuilder);

            // Finally, create the channel
            var createTextChannelResponse = channelBuilder.create().get();

            // Update the database assuming everything worked out
            db.openTicket(createTextChannelResponse.getId(), mcPlayerOpeningTicket.getUniqueId(), isAdminOnly, description, String.valueOf(Date.from(Instant.now())));

            return true;
        } catch (Exception e) {
            ca.error("Error Creating Ticket! Contact the developer.");
            ca.error(e.getMessage());
            return false;
        }
    }

    public boolean setTicketAdminOnly(long channelID, boolean adminOnly) {
        try {
            var channelOptional = discordServer.getTextChannelById(channelID);
            if (channelOptional.isPresent()) {
                var channel = channelOptional.get();
                var channelUpdater = new RegularServerChannelUpdater<>(channel);

                for (long roleId : ca.modLevelTicketRoles) {
                    var roleOptional = discordServer.getRoleById(roleId);
                    if (roleOptional.isEmpty())
                        throw new Exception("Role ID " + roleId + " does not match a role in this server!");

                    var role = roleOptional.get();

                    Permissions permissionsBuilderRole;
                    if (!adminOnly) {
                        permissionsBuilderRole = (Permissions) new PermissionsBuilder()
                                .setAllowed(PermissionType.VIEW_CHANNEL)
                                .setAllowed(PermissionType.SEND_MESSAGES)
                                .setAllowed(PermissionType.EMBED_LINKS)
                                .setAllowed(PermissionType.ATTACH_FILE)
                                .setAllowed(PermissionType.READ_MESSAGE_HISTORY);
                    } else {
                        permissionsBuilderRole = (Permissions) new PermissionsBuilder()
                                .setDenied(PermissionType.VIEW_CHANNEL)
                                .setDenied(PermissionType.SEND_MESSAGES)
                                .setDenied(PermissionType.EMBED_LINKS)
                                .setDenied(PermissionType.ATTACH_FILE)
                                .setDenied(PermissionType.READ_MESSAGE_HISTORY);
                    }

                    channelUpdater.addPermissionOverwrite(role, permissionsBuilderRole);
                }

                // Save Changes to Channel
                channelUpdater.update().get();

                // Update Channel status in Database
                db.setAdminOnly(channelID, adminOnly);

            } else throw new Exception();

            return true;
        } catch (Exception e) {
            ca.error("Error Setting Ticket Admin Only! Contact the developer.");
            ca.error(e.getMessage());
            return false;
        }
    }

    public boolean closeTicket(long channelID) {
        try {
            var channelOptional = discordServer.getTextChannelById(channelID);
            if (channelOptional.isPresent()) {
                channelOptional.get().delete().get();

                // TODO: Add logic to handle logging and saving to closed tickets channel

                db.closeTicket(channelID);
            } else throw new Exception();

            return true;
        } catch (Exception e) {
            ca.error("Error Deleting Ticket! Contact the developer.");
            ca.error(e.getMessage());
            return false;
        }
    }

    public boolean closeTicket(long channelID, String closeReason) {
        try {
            var channelOptional = discordServer.getTextChannelById(channelID);
            if (channelOptional.isPresent()) {
                channelOptional.get().delete().get();

                // TODO: Add logic to handle logging and saving to closed tickets channel

                db.closeTicket(channelID, closeReason);
            } else throw new Exception();

            return true;
        } catch (Exception e) {
            ca.error("Error Deleting Ticket! Contact the developer.");
            ca.error(e.getMessage());
            return false;
        }
    }

}
