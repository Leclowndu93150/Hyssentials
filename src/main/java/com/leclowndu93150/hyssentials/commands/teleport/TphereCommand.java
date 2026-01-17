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

public class TphereCommand extends AbstractPlayerCommand {
    private final BackManager backManager;
    private final RequiredArg<String> targetArg = this.withRequiredArg("player", "Player to teleport to you", ArgTypes.STRING);

    public TphereCommand(@Nonnull BackManager backManager) {
        super("htphere", "Teleport a player to you (admin)");
        this.backManager = backManager;
        this.requirePermission(HytalePermissions.fromCommand("hyssentials.htphere"));
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
        Ref<EntityStore> targetRef = targetPlayer.getReference();
        if (targetRef == null || !targetRef.isValid()) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_TARGET_NOT_AVAILABLE));
            return;
        }
        Store<EntityStore> targetStore = targetRef.getStore();
        World targetWorld = targetStore.getExternalData().getWorld();
        TransformComponent myTransform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation myHeadRot = store.getComponent(ref, HeadRotation.getComponentType());
        if (myTransform == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_CANNOT_GET_POSITION));
            return;
        }
        Vector3d myPos = myTransform.getPosition().clone();
        Vector3f myRot = myHeadRot != null ? myHeadRot.getRotation().clone() : new Vector3f(0, 0, 0);
        // Get body rotation for the teleport
        Vector3f myBodyRot = myTransform.getRotation().clone();
        targetWorld.execute(() -> {
            TransformComponent targetTransform = targetStore.getComponent(targetRef, TransformComponent.getComponentType());
            HeadRotation targetHeadRot = targetStore.getComponent(targetRef, HeadRotation.getComponentType());
            if (targetTransform != null) {
                Vector3d targetPos = targetTransform.getPosition().clone();
                Vector3f targetRot = targetHeadRot != null ? targetHeadRot.getRotation().clone() : new Vector3f(0, 0, 0);
                backManager.saveLocation(targetPlayer.getUuid(), LocationData.from(targetWorld.getName(), targetPos, targetRot));
            }
            // Use proper body and head rotation like vanilla Hytale
            Teleport teleport = new Teleport(world, myPos, myBodyRot)
                .setHeadRotation(myRot);
            targetStore.addComponent(targetRef, Teleport.getComponentType(), teleport);
            targetPlayer.sendMessage(ChatUtil.parse(Messages.SUCCESS_PLAYER_TELEPORTED_TO_YOU, playerRef.getUsername()));
        });
        context.sendMessage(ChatUtil.parse(Messages.SUCCESS_TELEPORTED_PLAYER_TO_YOU, targetPlayer.getUsername()));
    }
}
