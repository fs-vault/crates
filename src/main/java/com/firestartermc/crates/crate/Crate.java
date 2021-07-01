package com.firestartermc.crates.crate;

import com.firestartermc.kerosene.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a loot crate, which can be opened with a
 * specific key item and gives out a number of randomized
 * rewards.
 *
 * @since 1.0
 */
public record Crate(@NotNull String name, @NotNull String displayName, @NotNull Block block,
                    @NotNull List<Reward> rewards, int prizesPerRoll, @NotNull Material keyMaterial) {

    public static final NamespacedKey KEY_CRATE_NBT = new NamespacedKey("barrel", "crate");

    /**
     * Returns a key item that is compatible with this crate.
     *
     * @param amount The amount of keys.
     * @return The key item for this crate.
     * @since 1.0
     */
    @NotNull
    public ItemStack key(int amount) {
        return ItemBuilder.of(keyMaterial())
                .amount(amount)
                .name(displayName + " Crate Key")
                .lore(
                        "&7Redeemable voucher",
                        "&f ",
                        "&fRedeem this key by right-",
                        "&fclicking the respective crate",
                        "&fat /warp crates and receive",
                        "&fa random reward!"
                )
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .persistData(KEY_CRATE_NBT, PersistentDataType.STRING, name())
                .build();
    }

    /**
     * Returns whether a given {@link ItemStack} is a valid
     * key to open this crate.
     *
     * @return Whether the key is valid.
     * @since 1.0
     */
    public boolean isKey(@NotNull ItemStack key) {
        if (keyMaterial() != key.getType()) {
            return false;
        }

        var data = key.getItemMeta().getPersistentDataContainer();
        var storedName = data.get(KEY_CRATE_NBT, PersistentDataType.STRING);

        return storedName != null && storedName.equals(name());
    }

    /**
     * Selects a random {@link Reward} from this crate's pool
     * of rewards.
     *
     * @return The randomly chosen reward.
     * @since 1.0
     */
    @NotNull
    public Reward selectRandomReward() {
        double randomWeight = Math.random() * rewards.stream().mapToDouble(Reward::chance).sum();
        double countWeight = 0.0;

        for (Reward reward : rewards) {
            countWeight += reward.chance();

            if (countWeight < randomWeight) {
                continue;
            }

            return reward;
        }

        throw new IllegalStateException();
    }
}
