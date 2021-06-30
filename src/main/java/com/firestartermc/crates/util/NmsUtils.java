package com.firestartermc.crates.util;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class NmsUtils {

    private static final Random RANDOM = new Random();

    public static void sendChestAction(@NotNull Player player, @NotNull Block block, boolean open, boolean playSound) {
        var pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        var packet = new PacketPlayOutBlockAction(pos, Blocks.bX /* Blocks.CHEST */, 1, open ? 1 : 0);
        sendPackets(player, packet);

        if (!playSound) {
            return;
        }

        var enderChest = block.getType() == Material.ENDER_CHEST;
        var sound = open ?
                enderChest ? Sound.BLOCK_ENDER_CHEST_OPEN : Sound.BLOCK_CHEST_OPEN :
                enderChest ? Sound.BLOCK_ENDER_CHEST_CLOSE : Sound.BLOCK_CHEST_CLOSE;
        var pitch = RANDOM.nextFloat() * 0.1F + 0.9F;
        player.playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 0.5F, pitch);
    }

    public static void sendPackets(@NotNull Player player, @NotNull Packet<?>... packets) {
        for (var packet : packets) {
            ((CraftPlayer) player).getHandle().b.sendPacket(packet);
        }
    }
}
