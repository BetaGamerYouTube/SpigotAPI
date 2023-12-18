package de.ybeta.spigotapi.listener;

import de.ybeta.spigotapi.SpigotAPI;
import de.ybeta.spigotapi.def.DefaultPermissions;
import de.ybeta.spigotapi.util.ChatUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

public class SpigotListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String message = SpigotAPI.getJoinMessage();
        if (message == null || message.isEmpty()) {
            event.setJoinMessage(null);
            return;
        }
        String string = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
        event.setJoinMessage(ChatUtil.color(string));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String message = SpigotAPI.getQuitMessage();
        if (message == null || message.isEmpty()) {
            event.setQuitMessage(null);
            return;
        }
        String string = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
        event.setQuitMessage(ChatUtil.color(string));
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        String format = SpigotAPI.getChatFormat();
        String message = event.getMessage().replace("%", "%%");
        if (event.getPlayer().hasPermission(DefaultPermissions.CHAT_COLOR)) {
            message = ChatUtil.color(message);
        }
        if (ChatColor.stripColor(ChatUtil.color(new String(message))).isEmpty()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou can not send empty messages!");
            return;
        }
        format = ChatUtil.color(PlaceholderAPI.setPlaceholders(event.getPlayer(), format)).replace("%message%", message);
        event.setFormat(format);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTab(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        for (String cmd : SpigotAPI.getHiddenCommands()) {
            if (!event.getCommands().contains(cmd)) continue;
            if (player.hasPermission(DefaultPermissions.HIDDEN_COMMANDS)) continue;
            event.getCommands().remove(cmd);
        }

        if (!SpigotAPI.isHideSemicolonCommands()) return;
        List<String> removal = new ArrayList<>();
        for (String cmd : event.getCommands()) {
            if (cmd.split(" ")[0].contains(":"))
                removal.add(cmd);
        }
        for (String rem : removal) {
            event.getCommands().remove(rem);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = asCommand(event.getMessage());
        if (player.hasPermission(DefaultPermissions.HIDDEN_COMMANDS)) return;

        for (String cmd : SpigotAPI.getHiddenCommands()) {
            if (!command.equalsIgnoreCase(cmd)) continue;
            String message = SpigotAPI.getNoPermissionMessage();

            event.setCancelled(true);
            player.sendMessage(message);
        }

        if (!SpigotAPI.isHideSemicolonCommands()) return;
        if (command.split(" ")[0].contains(":")) {
            String message = SpigotAPI.getNoPermissionMessage();
            event.setCancelled(true);
            player.sendMessage(ChatUtil.color(message));
        }
    }

    private String asCommand(String message) {
        boolean replaceSlash = false;
        boolean splitToFirstArg = false;
        boolean isWorldEditCMD = false;

        if (message.startsWith(CommandUtil.COMMAND_IDENTIFIER.toString()))
            replaceSlash = true;
        if (message.startsWith(CommandUtil.WORLDEDIT_IDENTIFIER.toString()))
            isWorldEditCMD = true;
        if (message.split(CommandUtil.ARGS_SPLIT.toString()).length > 1)
            splitToFirstArg = true;

        String command = message;
        if (replaceSlash)
            if (isWorldEditCMD)
                command = command.replaceFirst("//", "");
            else
                command = command.replaceFirst("/", "");
        if (splitToFirstArg)
            command = command.split(CommandUtil.ARGS_SPLIT.toString())[0];
        command = command.toLowerCase();

        return command;
    }

    private enum CommandUtil {
        COMMAND_IDENTIFIER("/"),
        ARGS_SPLIT(" "),
        WORLDEDIT_IDENTIFIER("//");

        private final String value;
        CommandUtil(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
