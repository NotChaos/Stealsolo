package fun.stealsolo.Packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import fun.stealsolo.Stealsolo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketEventsPacketListener implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        if (!Stealsolo.getAfkArea().isInArea(player.getLocation())) {
            return;
        }

        PacketType.Play.Server[] list = PacketType.Play.Server.values();
        list = Arrays.stream(list)
                .filter(packetType -> packetType.name().contains("ENTITY"))
                .toArray(PacketType.Play.Server[]::new);

        List<PacketType.Play.Server> blockedPackets = new ArrayList<>(Arrays.asList(list));
        blockedPackets.add(PacketType.Play.Server.SPAWN_PLAYER);

        if (blockedPackets.contains(event.getPacketType())) {
            //Stealsolo.getPlugin().getLogger().info("Cancelling movement packet from player " + player.getName() + " in AFK area.");
            event.setCancelled(true);
        }
    }
}
