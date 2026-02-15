package org.mobmouser.easybazaarflipping.client.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.mobmouser.easybazaarflipping.client.util.ConfigLoader;

public class BazaarApiReload {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            // DebugCommand.java に追加
            dispatcher.register(ClientCommandManager.literal("bzreload")
                    .executes(context -> {
                        ConfigLoader.reload();
                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal("§aConfig reloaded!"), false);
                        }
                        return 1;
                    }));
        });
    }
}
