package de.ybeta.spigotapi;

import de.ybeta.spigotapi.util.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SpigotAPI {

    @Getter @Setter private static String joinMessage = null;
    @Getter @Setter private static String quitMessage = null;
    @Getter @Setter private static String chatFormat = "%player_name%: %message%";
    @Getter private static final List<String> hiddenCommands = Collections.emptyList();
    @Getter @Setter private static boolean hideSemicolonCommands = false;
    @Getter private static final String noPermissionMessage = getPaperNoPermissionMessage();

    public static void hideCommand(String command) {
        if (command.startsWith("/")) command = command.replace("/", "");
        if (hiddenCommands.contains(command)) return;
        hiddenCommands.add(command);
    }

    public static void removeHiddenCommand(String command) {
        hiddenCommands.remove(command);
    }

    public static boolean isCommandHidden(String command) {
        return hiddenCommands.contains(command);
    }

    private static String getPaperNoPermissionMessage() {
        File file = new File("config/paper-global.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
        return ChatUtil.color(conf.getString("messages.no-permission"));
    }

}
