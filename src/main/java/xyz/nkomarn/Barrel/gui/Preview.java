package xyz.nkomarn.Barrel.gui;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.nkomarn.Barrel.objects.Crate;
import xyz.nkomarn.Kerosene.gui.Gui;
import xyz.nkomarn.Kerosene.gui.GuiButton;

import java.util.concurrent.atomic.AtomicInteger;

public class Preview extends Gui {
    public Preview(Crate crate, Player player) {
        super(player, WordUtils.capitalize(crate.getName()) + " Crate", 27);
        AtomicInteger slot = new AtomicInteger(0);
        crate.getRewards().forEach(reward -> addButton(new GuiButton(
                this,
                reward.getItem(),
                slot.getAndIncrement(),
                null
        )));
        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.0f);
        open();
    }
}
