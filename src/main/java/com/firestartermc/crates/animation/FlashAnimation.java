package com.firestartermc.crates.animation;

import com.firestartermc.crates.animation.entity.ClientsideItem;
import com.firestartermc.crates.crate.Crate;
import com.firestartermc.crates.crate.Reward;
import com.firestartermc.crates.util.NmsUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FlashAnimation implements Animation {

    private final Crate crate;
    private final Reward reward;
    private final Player player;
    private final Random random;
    private ClientsideItem item;
    private int tickCounter;

    public FlashAnimation(@NotNull Crate crate, @NotNull Reward reward, @NotNull Player player) {
        this.crate = crate;
        this.reward = reward;
        this.player = player;
        this.random = new Random();
    }

    @Override
    public void play() {
        item = new ClientsideItem(player, crate.block().getLocation().clone().add(0.5, 1.75, 0.5));
        NmsUtils.sendChestAction(player, crate.block(), true, true);
    }

    @Override
    public void stop() {
        reward.actions().forEach(action -> action.perform(reward, player));
        item.destroy();
    }

    @Override
    public void tick() {
        var location = crate.block().getLocation().add(0.5, 2, 0.5);
        tickCounter++;

        if (tickCounter == 5) {
            item.spawn();
        }

        if (tickCounter >= 5 && tickCounter <= 40) {
            var rewardsList = crate.rewards();
            var randomReward = rewardsList.get(random.nextInt(rewardsList.size()));
            item.setItem(randomReward.item());
            player.playSound(location, Sound.ITEM_SPYGLASS_USE, 0.5f, 1.0f);
            return;
        }

        if (tickCounter == 41) {
            item.setItem(reward.item());

            player.spawnParticle(Particle.FLASH, location, 1);
            player.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1.0f, 1.0f);
            player.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2.0f, 1.0f);
            return;
        }

        if (tickCounter > 41 && tickCounter < 85) {
            player.spawnParticle(Particle.ELECTRIC_SPARK, location, 5);
        }

        if (tickCounter == 85) {
            NmsUtils.sendChestAction(player, crate.block(), false, true);
        }
    }

    @Override
    public boolean isPlaying() {
        return tickCounter <= 90;
    }
}
