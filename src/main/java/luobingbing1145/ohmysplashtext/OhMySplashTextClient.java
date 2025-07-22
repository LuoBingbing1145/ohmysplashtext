package luobingbing1145.ohmysplashtext;

import net.fabricmc.api.ClientModInitializer;

public class OhMySplashTextClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientConfig.INSTANCE.load();
    }

    public static ModClientConfig getConfig() {
        return ModClientConfig.INSTANCE.instance();
    }
}
