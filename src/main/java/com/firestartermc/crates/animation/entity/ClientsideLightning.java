package com.firestartermc.crates.animation.entity;

import com.firestartermc.crates.util.NmsUtils;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClientsideLightning {

    private final Player player;
    private final EntityLightning entity;

    public ClientsideLightning(@NotNull Player player, @NotNull Location location) {
        this.player = player;

        var world = ((CraftWorld) location.getWorld()).getHandle();
        this.entity = new EntityLightning(EntityTypes.U, world);
        entity.setLocation(location.getX(), location.getY(), location.getZ(), 0F, 0F);
        entity.setEffect(true);
    }

    public void spawn() {
        var spawnPacket = new PacketPlayOutSpawnEntity(entity);
        NmsUtils.sendPackets(player, spawnPacket);
    }
}
