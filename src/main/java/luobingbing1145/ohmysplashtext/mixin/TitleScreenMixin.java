package luobingbing1145.ohmysplashtext.mixin;

import luobingbing1145.ohmysplashtext.ModClientConfig;
import luobingbing1145.ohmysplashtext.OhMySplashTextClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Unique
    private final ModClientConfig CONFIG = OhMySplashTextClient.getConfig();

    @Unique
    private final TitleScreen INSTANCE = (TitleScreen) (Object) this;

    @Unique
    private final ScreenInvoker INVOKER = (ScreenInvoker) INSTANCE;

    @Unique
    private final ScreenAccessor ACCESSOR = (ScreenAccessor) INSTANCE;

    @Inject(method = "initWidgetsNormal", at = @At(value = "TAIL"))
    private void addButton(int y, int spacingY, CallbackInfo ci) {
        if (CONFIG.isButtonEnable()) {
            INVOKER.invokeAddDrawableChild(
                    ButtonWidget
                            .builder(
                                    Text.translatable("menu.ohmysplashtext.configScreen"),
                                    (button) -> ACCESSOR.getClient().setScreen(ModClientConfig.makeScreen(INSTANCE))
                            )
                            .dimensions(INSTANCE.width - 100, -10, 100, 20)
                            .build()
            );
        }
    }
}
