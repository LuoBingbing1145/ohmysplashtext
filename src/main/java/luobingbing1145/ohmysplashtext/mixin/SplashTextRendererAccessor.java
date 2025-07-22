package luobingbing1145.ohmysplashtext.mixin;

import net.minecraft.client.gui.screen.SplashTextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SplashTextRenderer.class)
public interface SplashTextRendererAccessor {
    @Accessor("text")
    String getText();
}
