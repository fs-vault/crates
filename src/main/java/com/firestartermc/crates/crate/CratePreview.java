package com.firestartermc.crates.crate;

import com.firestartermc.crates.util.NmsUtils;
import com.firestartermc.kerosene.gui.GuiPosition;
import com.firestartermc.kerosene.gui.PlayerGui;
import com.firestartermc.kerosene.gui.components.item.ItemComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CratePreview extends PlayerGui {

    private final Crate crate;

    public CratePreview(@NotNull Crate crate, @NotNull Player player) {
        super(player, ChatColor.RESET + WordUtils.capitalize(crate.name()) + " Crate", (int) Math.ceil(crate.rewards().size() / 9F));
        this.crate = crate;

        int slot = 0;
        for (var reward : crate.rewards()) {
            addElement(new ItemComponent(GuiPosition.fromSlot(slot++), reward.item()));
        }
    }

    @Override
    public void onOpen(Player player) {
        NmsUtils.sendChestAction(getPlayer(), crate.block(), true, true);
    }

    @Override
    public void onClose(Player player) {
        NmsUtils.sendChestAction(getPlayer(), crate.block(), false, true);
    }
}
