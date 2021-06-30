package com.firestartermc.crates.animation.entity;

import com.firestartermc.crates.crate.Reward;
import com.firestartermc.crates.util.NmsUtils;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ClientsideItem {

    private final Player player;
    private final EntityItem entity;
    private Vec3D entityPos;

    public ClientsideItem(@NotNull Player player, @NotNull Location location) {
        this.player = player;

        var world = ((CraftWorld) location.getWorld()).getHandle();
        this.entity = new EntityItem(EntityTypes.Q, world);
        entityPos = new Vec3D(location.getX(), location.getY(), location.getZ());

        entity.setPosition(location.getX(), location.getY(), location.getZ());
        entity.setCustomNameVisible(true);
        entity.setNoGravity(true);
        entity.setGlowingTag(true);
    }

    @NotNull
    public Vec3D getPos() {
        return entityPos;
    }

    public void move(@NotNull Vec3D newPos) {
        var moveVec = newPos.d(getPos());
        entityPos = newPos;

        short packetX = (short) PacketPlayOutEntity.a(moveVec.getX());
        short packetY = (short) PacketPlayOutEntity.a(moveVec.getY());
        short packetZ = (short) PacketPlayOutEntity.a(moveVec.getZ());

        var packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entity.getId(), packetX, packetY, packetZ, false);
        NmsUtils.sendPackets(player, packet);
    }

    public void spawn() {
        var spawnPacket = new PacketPlayOutSpawnEntity(entity);
        NmsUtils.sendPackets(player, spawnPacket);
    }

    public void destroy() {
        var packet = new PacketPlayOutEntityDestroy(entity.getId());
        NmsUtils.sendPackets(player, packet);
    }

    public void setItem(@NotNull ItemStack item) {
        entity.setItemStack(CraftItemStack.asNMSCopy(item));
        entity.setCustomName(new ChatComponentText(item.getItemMeta().getDisplayName()));

        var metadataPacket = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false);
        NmsUtils.sendPackets(player, metadataPacket);
    }
}
