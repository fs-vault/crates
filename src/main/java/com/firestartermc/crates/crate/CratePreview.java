package com.firestartermc.crates.crate;

import com.firestartermc.kerosene.gui.GuiPosition;
import com.firestartermc.kerosene.gui.PlayerGui;
import com.firestartermc.kerosene.gui.components.item.ItemComponent;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CratePreview extends PlayerGui {

    private final Crate crate;
    private final Random random;

    public CratePreview(@NotNull Crate crate, @NotNull Player player) {
        super(player, ChatColor.RESET + WordUtils.capitalize(crate.name()) + " Crate", (int) Math.ceil(crate.rewards().size() / 9F) + 1);
        this.crate = crate;
        this.random = new Random();

        int slot = 0;
        for (var reward : crate.rewards()) {
            addElement(new ItemComponent(GuiPosition.fromSlot(slot), reward.item()));
        }
    }

    @Override
    public void onOpen(Player player) {
        sendChestAction(true);
    }

    @Override
    public void onClose(Player player) {
        sendChestAction(false);
    }

    private void sendChestAction(boolean open) {
        var block = crate.block();
        var pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        var packet = new PacketPlayOutBlockAction(pos, Blocks.bX /* Blocks.CHEST */, 1, open ? 1 : 0);
        ((CraftPlayer) getPlayer()).getHandle().b.sendPacket(packet);

        var enderChest = block.getType() == Material.ENDER_CHEST;
        var sound = open ?
                enderChest ? Sound.BLOCK_ENDER_CHEST_OPEN : Sound.BLOCK_CHEST_OPEN :
                enderChest ? Sound.BLOCK_ENDER_CHEST_CLOSE : Sound.BLOCK_CHEST_CLOSE;
        var pitch = random.nextFloat() * 0.1F + 0.9F;
        getPlayer().playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 0.5F, pitch);
    }
}
