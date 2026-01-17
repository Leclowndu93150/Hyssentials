package com.leclowndu93150.hyssentials.commands.spawn;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import java.text.DecimalFormat;
import javax.annotation.Nonnull;

public class SetSpawnCommand extends AbstractPlayerCommand {
    private static final DecimalFormat DECIMAL = new DecimalFormat("#.###");

    public SetSpawnCommand() {
        super("setspawn", "Set the world spawn at your current location");
        this.requirePermission(HytalePermissions.fromCommand("hyssentials.setspawn"));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) {
            context.sendMessage(ChatUtil.parse(Messages.ERROR_CANNOT_GET_POSITION));
            return;
        }

        Vector3d position = transformComponent.getPosition().clone();

        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        Vector3f rotation = headRotation != null ? headRotation.getRotation() : Vector3f.FORWARD;

        Transform transform = new Transform(position, rotation);
        WorldConfig worldConfig = world.getWorldConfig();
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(transform));
        worldConfig.markChanged();

        context.sendMessage(ChatUtil.parse(Messages.SUCCESS_SPAWN_SET,
            DECIMAL.format(position.getX()),
            DECIMAL.format(position.getY()),
            DECIMAL.format(position.getZ()),
            DECIMAL.format(rotation.getX()),
            DECIMAL.format(rotation.getY()),
            DECIMAL.format(rotation.getZ())
        ));
    }
}
