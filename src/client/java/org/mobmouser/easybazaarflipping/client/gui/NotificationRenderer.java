package org.mobmouser.easybazaarflipping.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class NotificationRenderer {
    private static String message = "";
    private static long startTime = 0;
    private static int posX = 0;
    private static int posY = 0;
    private static final long DURATION = 2000;
    private static final long FADE_START = 1500;

    public static void show(String text, int x, int y) {
        message = text;
        startTime = System.currentTimeMillis();
        posX = x + 10;
        posY = y - 5;
    }

    public static void render(DrawContext drawContext) {
        if (message.isEmpty()) return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= DURATION) {
            message = "";
            return;
        }

        int alpha = 255;
        if (elapsed > FADE_START) {
            alpha = (int) (255 * (1.0 - (elapsed - FADE_START) / (double)(DURATION - FADE_START)));
        }

        if (alpha > 0) {
            int color = (alpha << 24) | 0x55FF55;
            MinecraftClient client = MinecraftClient.getInstance();
            drawContext.drawText(client.textRenderer, message, posX, posY, color, true);
        }
    }
}