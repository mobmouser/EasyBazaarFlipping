package org.mobmouser.easybazaarflipping.client.util;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class ChatListener {
    public static void register() {
        ClientReceiveMessageEvents.GAME.register((t, b) -> {
            String message = t.getString();

            if (message.startsWith("You sold") || message.startsWith("You bought back")) {
                NpcSellCounter.register(message);
            }
        });
    }
}
