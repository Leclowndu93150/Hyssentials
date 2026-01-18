package com.leclowndu93150.hyssentials.commands.teleport;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.data.LocationData;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.BackManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import javax.annotation.Nonnull;

public class TpCommand extends AbstractPlayerCommand {
    private final BackManager backManager;
    private final RequiredArg<String> targetArg = this.withRequiredArg("player", "Player to teleport to", ArgTypes.STRING);

    public TpCommand(@Nonnull BackManager backManager) {
        super("htp", "Teleport to a player (admin)");
        this.backManager = backManager;
        this.requirePermission(HytalePermissions.fromCommand("hyssentials.htp"));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String targetName = targetArg.get(context);
        PlayerRef targetPlayer = Universe.get().getPlayerByUsername(targetName, NameMatching.STARTS_WITH_IGNORE_CASE);
        if (targetPlayer == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_PLAYER_NOT_FOUND, targetName));
            return;
        }
        if (targetPlayer.getUuid().equals(playerRef.getUuid())) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_CANNOT_TELEPORT_SELF));
            return;
        }
        Ref<EntityStore> targetRef = targetPlayer.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_TARGET_NOT_AVAILABLE));
            return;
        }
        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = targetStore.getExternalData().getWorld();
        TransformComponent myTransform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation myHeadRot = store.getComponent(ref, HeadRotation.getComponentType());
        if (myTransform != null) {
            Vector3d myPos = myTransform.getPosition().clone();
            Vector3f myRot = myHeadRot != null ? myHeadRot.getRotation().clone() : new Vector3f(0, 0, 0);
            backManager.saveLocation(playerRef.getUuid(), LocationData.from(world.getName(), myPos, myRot));
        }
        targetWorld.execute(() -> {
            TransformComponent targetTransform = targetStore.getComponent(targetRef, TransformComponent.getComponentType());
            HeadRotation targetHeadRot = targetStore.getComponent(targetRef, HeadRotation.getComponentType());
            if (targetTransform == null) {
                context.sendMessage(ChatUtil.parse(Messages.ERROR_CANNOT_GET_TARGET_POSITION));
                return;
            }
            Vector3d targetPos = targetTransform.getPosition().clone();
            Vector3f targetBodyRot = targetTransform.getRotation().clone();
            Vector3f targetHeadRotation = targetHeadRot != null ? targetHeadRot.getRotation().clone() : new Vector3f(0, 0, 0);
            world.execute(() -> {
                // Use proper body and head rotation like vanilla Hytale
                Teleport teleport = new Teleport(targetWorld, targetPos, targetBodyRot)
                    .setHeadRotation(targetHeadRotation);
                store.addComponent(ref, Teleport.getComponentType(), teleport);
                context.sendMessage(ChatUtil.parse(Messages.SUCCESS_TELEPORTED_TO_PLAYER, targetPlayer.getUsername()));
            });
        });
    }
}
