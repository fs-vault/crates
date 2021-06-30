package com.firestartermc.crates.animation;

public interface Animation {

    void play();

    void stop();

    void tick();

    boolean isPlaying();
}
