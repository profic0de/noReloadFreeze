package com.proficode;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;
// import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class noReloadFreeze implements ModInitializer {
    public static final String MOD_ID = "proficode-noreloadfreeze";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Map to track the freeze state of players
    private final HashMap<UUID, Boolean> frozenPlayers = new HashMap<>();

    @Override
    public void onInitialize() {
        // When reload starts: allow movement
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Unfreeze all players
                frozenPlayers.put(player.getUuid(), false); // Movement allowed
                stopPlayerMovement(player, false); // Allow movement
                // player.sendMessage(Text.literal("§eDatapack reload started, movement allowed!"), false);
            }
        });

        // When reload ends: restore movement state
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Restore previous freeze state
                boolean wasFrozen = frozenPlayers.getOrDefault(player.getUuid(), false);
                stopPlayerMovement(player, wasFrozen); // Restore movement state
                // player.sendMessage(Text.literal(success ? "§aDatapack reload completed!" : "§cDatapack reload failed!"), false);
            }
            frozenPlayers.clear();
        });
    }

    private void stopPlayerMovement(ServerPlayerEntity player, boolean freeze) {
        if (freeze) {
            // Freeze player movement by setting velocity to zero
            player.setVelocity(0, 0, 0); // Manually set velocity to 0 (freeze player)
        } else {
            // Allow movement (restore velocity)
            // You can save the player's original velocity and restore it here if needed
            player.setVelocity(0, 0, 0); // Restore to normal (in case you had a way to store the velocity)
        }
    }
}
