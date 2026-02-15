package org.mobmouser.easybazaarflipping.client.gui;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.mobmouser.easybazaarflipping.client.bazaar.api.GetBuyPrice;
import org.mobmouser.easybazaarflipping.client.bazaar.api.GetPurse;
import org.mobmouser.easybazaarflipping.client.bazaar.data.BazaarEvaluationResult;
import org.mobmouser.easybazaarflipping.client.bazaar.data.BazaarOffer;
import org.mobmouser.easybazaarflipping.client.bazaar.data.ItemMerger;
import org.mobmouser.easybazaarflipping.client.util.BazaarScorer;
import org.mobmouser.easybazaarflipping.client.util.BuyOrderScorer;
import org.mobmouser.easybazaarflipping.client.util.NpcSellCounter;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ScreenRenderer {
    private static List<BazaarOffer> cachedItems = new ArrayList<>();
    private static List<BazaarEvaluationResult> cachedEvalResults = new ArrayList<>();
    private static List<BazaarEvaluationResult> cachedBuyOrderResults = new ArrayList<>();
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 5000;
    private static boolean wasClicking = false;
    private static boolean sortByTotal = true;
    private static TextFieldWidget purseField = null;
    private static long inputPurse = 0;

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            String title = screen.getTitle().getString();
            if (title.startsWith("Bazaar")) {
                int labelWidth = client.textRenderer.getWidth("Purse: ");
                purseField = new TextFieldWidget(
                        client.textRenderer, 10 + labelWidth, 26, 100, 14, Text.literal("Purse"));
                purseField.setMaxLength(15);
                purseField.setVisible(!sortByTotal);
                purseField.setChangedListener(text -> {
                    String cleaned = text.replaceAll("[^0-9]", "");
                    try {
                        inputPurse = cleaned.isEmpty() ? 0 : Long.parseLong(cleaned);
                    } catch (NumberFormatException e) {
                        inputPurse = 0;
                    }
                });

                long cachedPurse = GetPurse.getCachedPurse();
                if (cachedPurse >= 0) {
                    purseField.setText(String.valueOf(cachedPurse));
                } else {
                    purseField.setText("0");
                }

                Screens.getButtons(screen).add(purseField);
            }

            ScreenEvents.afterRender(screen).register((scr, drawContext, mouseX, mouseY, delta) -> {
                String scrTitle = scr.getTitle().getString();


                // 既存のBazaar判定を変更
                if (scrTitle.startsWith("Bazaar") || scrTitle.startsWith("Trades")) {
                    // 日付チェック
                    NpcSellCounter.checkDateReset();

                    // 画面右半分にNPC売却情報を表示
                    int rightX = scaledWidth / 2 + 20;
                    int rightY = 10;
                    final int lineHeight = 10;

                    String sellTotal = String.format("%,d / %,d coin",
                            NpcSellCounter.getDailyTotal(), 500_000_000L);
                    String remaining = String.format("Remaining: %,d coin", NpcSellCounter.getRemaining());
                    String percent = String.format("%.1f%% used", NpcSellCounter.getUsagePercent());

                    drawContext.drawText(client.textRenderer, "NPC Sell Today:", rightX, rightY, 0xFFAA00, true);
                    rightY += lineHeight + 2;
                    drawContext.drawText(client.textRenderer, sellTotal, rightX, rightY, 0xFFFFFF, true);
                    rightY += lineHeight + 2;
                    drawContext.drawText(client.textRenderer, remaining, rightX, rightY, 0x55FF55, true);
                    rightY += lineHeight + 2;
                    drawContext.drawText(client.textRenderer, percent, rightX, rightY, 0xAAAAAA, true);
                }

                if (scrTitle.startsWith("Bazaar")) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
                        cachedItems = ItemMerger.mergeAll();
                        cachedEvalResults = BazaarScorer.evaluate(cachedItems);
                        cachedBuyOrderResults = BuyOrderScorer.evaluate(cachedItems);
                        lastUpdateTime = currentTime;
                    }

                    // クリック検出
                    boolean isClicking = GLFW.glfwGetMouseButton(
                            MinecraftClient.getInstance().getWindow().getHandle(),
                            GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                    boolean clicked = isClicking && !wasClicking;
                    wasClicking = isClicking;

                    int x = 10;
                    int y = 10;
                    final int lineHeight = 10;

                    // ソート切り替えボタン
                    String btnText = sortByTotal ? "[InstaBuyMode]" : "[BuyOrderMode]";
                    int btnWidth = client.textRenderer.getWidth(btnText);
                    drawContext.drawText(client.textRenderer, btnText, x, y, 0xFFFF00, true);

                    if (clicked && mouseX >= x && mouseX <= x + btnWidth
                            && mouseY >= y && mouseY <= y + lineHeight) {
                        sortByTotal = !sortByTotal;
                        if (purseField != null) {
                            purseField.setVisible(!sortByTotal);
                        }
                    }

                    y += lineHeight + 6;

                    // BuyOrderModeならPurseラベルとFetchボタン
                    if (!sortByTotal) {
                        drawContext.drawText(client.textRenderer, "Purse:", 10, 29, 0xFFFF00, true);

                        int labelWidth = client.textRenderer.getWidth("Purse: ");
                        String btnFetch = "[Fetch]";
                        int btnFetchWidth = client.textRenderer.getWidth(btnFetch);
                        int btnFetchX = 10 + labelWidth + 105;
                        drawContext.drawText(client.textRenderer, btnFetch, btnFetchX, 29, 0x55FFFF, true);

                        if (clicked && mouseX >= btnFetchX && mouseX <= btnFetchX + btnFetchWidth
                                && mouseY >= 26 && mouseY <= 40) {
                            GetPurse.fetchPurseAsync();

                            // 価格データも非同期で更新
                            new Thread(() -> {
                                GetBuyPrice.clearCache();
                                List<BazaarOffer> newItems = ItemMerger.mergeAll();
                                // メインスレッドで反映
                                MinecraftClient.getInstance().execute(() -> {
                                    cachedItems = newItems;
                                    cachedEvalResults = BazaarScorer.evaluate(cachedItems);
                                    cachedBuyOrderResults = BuyOrderScorer.evaluate(cachedItems);
                                    lastUpdateTime = System.currentTimeMillis();
                                });
                            }).start();
                        }

                        // ステータス表示
                        String status = GetPurse.getStatus();
                        if (!status.isEmpty()) {
                            int statusX = btnFetchX + btnFetchWidth + 5;
                            int statusColor = status.equals("Fetching...") ? 0xFFFF55
                                    : status.equals("Done!") ? 0x55FF55 : 0xFF5555;
                            drawContext.drawText(client.textRenderer, status, statusX, 29, statusColor, true);
                        }

                        // 非同期取得完了した値を反映
                        if (GetPurse.isPurseUpdated()) {
                            long purse = GetPurse.getCachedPurse();
                            if (purse >= 0) {
                                inputPurse = purse;
                                if (purseField != null) {
                                    purseField.setText(String.valueOf(purse));
                                }
                            }
                            GetPurse.consumePurseUpdate();
                        }

                        y += 20;
                    }

                    // アイテム描画
                    int limit = 10;
                    if (sortByTotal) {
                        // InstaBuyMode
                        for (BazaarEvaluationResult eval : cachedEvalResults) {
                            if (limit-- <= 0) break;
                            BazaarOffer item = eval.getOffer();

                            int itemStartY = y;

                            // 1行目
                            String nameText = item.getName() + " ";
                            String priceText = String.format("%.1f : ", item.getNpcSellPrice());
                            String profitText = String.format("%.1f coin/1item",
                                    item.getProfit());
                            int line1Width = client.textRenderer.getWidth(nameText + priceText + profitText);

                            int drawX = x;
                            drawContext.drawText(client.textRenderer, nameText, drawX, y, 0x00FFFF, true);
                            drawX += client.textRenderer.getWidth(nameText);
                            drawContext.drawText(client.textRenderer, priceText, drawX, y, 0xFFFFFF, true);
                            drawX += client.textRenderer.getWidth(priceText);
                            drawContext.drawText(client.textRenderer, profitText, drawX, y, 0x55FF55, true);

                            // 2行目
                            y += lineHeight;
                            String line2 = String.format("  %.1f coin x %d = %.1f | Trips:%.1f -> TotalProfit:%.0f",
                                    item.getInstaBuy(), item.getAmount(),
                                    item.getInstaBuy() * item.getAmount(),
                                    eval.getTrips(), item.getAmount() * item.getProfit());
                            drawContext.drawText(client.textRenderer, line2, x, y, 0xAAAAAA, true);

                            int itemEndY = y + lineHeight;

                            if (clicked && mouseX >= x && mouseX <= x + line1Width
                                    && mouseY >= itemStartY && mouseY <= itemEndY) {
                                client.player.networkHandler.sendChatCommand("bz " + item.getName());
                                NotificationRenderer.show("Copied: " + item.getAmount(), mouseX, mouseY);
                                client.keyboard.setClipboard(String.valueOf(item.getAmount()));
                            }

                            y += lineHeight + 4;
                        }
                    } else {
                        // BuyOrderMode
                        for (BazaarEvaluationResult eval : cachedBuyOrderResults) {
                            if (limit-- <= 0) break;
                            BazaarOffer item = eval.getOffer();

                            int itemStartY = y;

                            // 1行目
                            String nameText = item.getName() + " ";
                            String priceText = String.format("%.1f : ", item.getNpcSellPrice());
                            double unitProfit = item.getNpcSellPrice() - item.getBuyOrder();
                            int canBuy = item.getBuyOrder() > 0 ? (int) (inputPurse / item.getBuyOrder()) : 0;
                            String profitText = String.format("%.1f x %d = %.1f",
                                    unitProfit, canBuy, unitProfit * canBuy);
                            int line1Width = client.textRenderer.getWidth(nameText + priceText + profitText);

                            int drawX = x;
                            drawContext.drawText(client.textRenderer, nameText, drawX, y, 0x00FFFF, true);
                            drawX += client.textRenderer.getWidth(nameText);
                            drawContext.drawText(client.textRenderer, priceText, drawX, y, 0xFFFFFF, true);
                            drawX += client.textRenderer.getWidth(priceText);
                            drawContext.drawText(client.textRenderer, profitText, drawX, y, 0x55FF55, true);

                            // 2行目
                            y += lineHeight;
                            int canBuy2 = item.getBuyOrder() > 0 ? (int) (inputPurse / item.getBuyOrder()) : 0;
                            String line2 = String.format("  BuyOrder: %.1f | Can buy: %d | Trips:%.1f | Vol/day:%.0f",
                                    item.getBuyOrder(), canBuy2, eval.getTrips(), item.getDailyVolume());
                            drawContext.drawText(client.textRenderer, line2, x, y, 0xAAAAAA, true);

                            int itemEndY = y + lineHeight;

                            if (clicked && mouseX >= x && mouseX <= x + line1Width
                                    && mouseY >= itemStartY && mouseY <= itemEndY) {
                                client.keyboard.setClipboard(String.valueOf(canBuy));
                                NotificationRenderer.show("Copied: " + canBuy, mouseX, mouseY);
                                client.player.networkHandler.sendChatCommand("bz " + item.getName());
                            }

                            y += lineHeight + 4;
                        }
                    }

                }
            });
        });
    }
}