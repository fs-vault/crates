package xyz.nkomarn.Barrel.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.nkomarn.Barrel.objects.Crate;
import xyz.nkomarn.Barrel.objects.Reward;

/**
 * Fired when a player obtains a reward from a crate.
 */
public class CrateRewardEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Crate crate;
    private final Reward reward;

    /**
     * Define a new CrateRewardEvent.
     * @param player The player which won the reward.
     * @param crate The crate which the reward came from.
     * @param reward The reward that was given.
     */
    public CrateRewardEvent(Player player, Crate crate, Reward reward) {
        this.player = player;
        this.crate = crate;
        this.reward = reward;
    }

    /**
     * Returns the player that won the reward.
     * @return Player object.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the crate the reward came from.
     * @return Crate object.
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * Returns the reward that was given.
     * @return Reward object.
     */
    public Reward getReward() {
        return this.reward;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
