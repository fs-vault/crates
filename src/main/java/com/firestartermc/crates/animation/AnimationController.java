package com.firestartermc.crates.animation;

import com.firestartermc.crates.Crates;
import com.firestartermc.crates.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AnimationController implements Runnable {

    private final Map<Player, Animation> playingAnimations;

    public AnimationController(@NotNull Crates crates) {
        this.playingAnimations = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(crates, this, 0L, 1L);
    }

    public boolean isViewingAnimation(@NotNull Player player) {
        return playingAnimations.containsKey(player);
    }

    public void playAnimation(@NotNull Player player, @NotNull Animation animation) {
        playingAnimations.put(player, animation);
        animation.play();
    }

    @Override
    public void run() {
        playingAnimations.entrySet().removeIf(entry -> {
            var animation = entry.getValue();

            if (animation.isPlaying()) {
                animation.tick();
                return false;
            }

            animation.stop();
            return true;
        });
    }
}
