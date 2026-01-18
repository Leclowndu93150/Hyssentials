package com.leclowndu93150.hyssentials.commands.admin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.lang.Messages;
import com.leclowndu93150.hyssentials.manager.VanishManager;
import com.leclowndu93150.hyssentials.util.ChatUtil;
import com.leclowndu93150.hyssentials.util.Permissions;
import javax.annotation.Nonnull;

public class VanishCommand extends AbstractPlayerCommand {
    private final VanishManager vanishManager;

    public VanishCommand(@Nonnull VanishManager vanishManager) {
        super("vanish", "Toggle vanish mode");
        this.vanishManager = vanishManager;
        this.addAliases("v");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        if (!Permissions.hasPermission(playerRef, Permissions.VANISH)) {
            context.sendMessage(ChatUtil.parse(Messages.NO_PERMISSION_VANISH));
            return;
        }

        boolean nowVanished = vanishManager.toggleVanish(playerRef.getUuid());

        if (nowVanished) {
            context.sendMessage(ChatUtil.parse(Messages.SUCCESS_VANISH_ENABLED));
        } else {
            context.sendMessage(ChatUtil.parse(Messages.SUCCESS_VANISH_DISABLED));
        }
    }
}
