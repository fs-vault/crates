package com.firestartermc.crates.animation;

import com.firestartermc.crates.crate.Crate;
import com.firestartermc.crates.crate.Reward;
import com.firestartermc.crates.util.NmsUtils;
import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ScrollingAnimation implements Animation {

    private final Crate crate;
    private final Player player;
    private final Reward reward;
    private final Random random;
    private final boolean lightning;
    private ArmorStand armorStand;
    private Item item;
    private int tickCounter;

    public ScrollingAnimation(@NotNull Crate crate, @NotNull Player player, @NotNull Reward reward, boolean lightning) {
        this.crate = crate;
        this.player = player;
        this.reward = reward;
        this.random = new Random();
        this.lightning = lightning;
    }

    @Override
    public void play() {
        var standLocation = crate.block().getLocation().add(0.5, 0.25, 0.5);
        var itemLocation = crate.block().getLocation().add(0.5, 0, 0.5);

        item = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.DROPPED_ITEM);
        item.setItemStack(ItemBuilder.of(Material.CHEST).build());
        item.setCanPlayerPickup(false);
        item.setCanMobPickup(false);
        item.setGlowing(true);
        item.setGravity(false);
        item.setCustomNameVisible(true);

        armorStand = (ArmorStand) standLocation.getWorld().spawnEntity(standLocation, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.addPassenger(item);

        adjustCrateLid(true);
    }

    @Override
    public void stop() {
        reward.actions().forEach(action -> action.perform(reward, player));
        item.remove();
        armorStand.remove();
    }

    @Override
    public void tick() {
        tickCounter++;

        var location = item.getLocation();
        var world = location.getWorld();

        if (tickCounter <= 40) {
            var rewardsList = crate.rewards();
            var randomReward = rewardsList.get(random.nextInt(rewardsList.size()));

            displayItem(randomReward.item());
            world.playSound(location, Sound.ITEM_SPYGLASS_USE, 0.5f, 1.0f);
            return;
        }

        if (tickCounter == 41) {
            displayItem(reward.item());
            world.spawnParticle(Particle.FLASH, location.add(0, 0.25, 0), 1);
            world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1.0f, 1.0f);
            world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2.0f, 1.0f);

            if (lightning) {
                world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 1.7f);
                world.strikeLightningEffect(crate.block().getLocation().add(0.5, 0, 0.5));
            }

            return;
        }

        if (tickCounter > 41 && tickCounter < 85) {
            world.spawnParticle(Particle.ELECTRIC_SPARK, location.add(0, 0.25, 0), 5);
        }

        if (tickCounter == 85) {
            adjustCrateLid(false);
        }
    }

    @Override
    public boolean isPlaying() {
        return tickCounter <= 90;
    }

    private void displayItem(@NotNull ItemStack itemStack) {
        item.setItemStack(itemStack);
        item.customName(itemStack.getItemMeta().displayName());
    }

    private void adjustCrateLid(boolean state) {
        var block = crate.block();

        block.getLocation().getNearbyPlayers(25).forEach(player -> {
            NmsUtils.sendChestAction(player, block, state, true);
        });
    }
}
