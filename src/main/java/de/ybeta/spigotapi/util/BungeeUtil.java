package de.ybeta.spigotapi.util;

import de.ybeta.spigotapi.SpigotPlugin;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeUtil {

    public static void sendToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException eee) {
            eee.printStackTrace();
        }
        player.sendPluginMessage(SpigotPlugin.getInstance(), "BungeeCord", b.toByteArray());
    }

}
