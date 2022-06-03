package io.github.sjouwer.blockenforcer.packets;

import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutUnloadChunk;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_14_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketWorldChunk {
    private PacketWorldChunk() {
    }

    //reload the chunk for the player
    public static void send(final Chunk chunk, final Player player) {
        PacketPlayOutUnloadChunk unload = new PacketPlayOutUnloadChunk(chunk.getX(), chunk.getZ());
        PacketPlayOutMapChunk load = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535);
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
        conn.sendPacket(unload);
        conn.sendPacket(load);
    }
}
