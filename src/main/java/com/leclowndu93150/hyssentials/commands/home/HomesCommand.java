package com.leclowndu93150.hyssentials.commands.home;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.leclowndu93150.hyssentials.manager.HomeManager;
import com.leclowndu93150.hyssentials.util.Permissions;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public class HomesCommand extends AbstractPlayerCommand {
    private final HomeManager homeManager;

    public HomesCommand(@Nonnull HomeManager homeManager) {
        super("homes", "List all your homes");
        this.homeManager = homeManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        UUID playerUuid = playerRef.getUuid();
        Set<String> homes = homeManager.getHomeNames(playerUuid);
        if (homes.isEmpty()) {
            context.sendMessage(Message.raw("You have no homes set. Use /sethome to create one."));
            return;
        }
        int count = homes.size();
        boolean hasVipHomes = Permissions.hasVipHomes(playerRef);
        int max = hasVipHomes ? homeManager.getVipMaxHomes() : homeManager.getMaxHomes();
        context.sendMessage(Message.raw(String.format("Your homes (%d/%d):", count, max)));
        for (String home : homes) {
            context.sendMessage(Message.raw("  - " + home));
        }
    }
}
