package org.mobmouser.easybazaarflipping.client;

import net.fabricmc.api.ClientModInitializer;
import org.mobmouser.easybazaarflipping.client.bazaar.api.GetPurse;
import org.mobmouser.easybazaarflipping.client.gui.ScreenRenderer;
import org.mobmouser.easybazaarflipping.client.util.ChatListener;
import org.mobmouser.easybazaarflipping.client.util.NpcSellCounter;

public class EasybazaarflippingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRenderer.register();
        GetPurse.initUuid();
        ChatListener.register();
        NpcSellCounter.init();
        CommandLoader.register();
    }
}
