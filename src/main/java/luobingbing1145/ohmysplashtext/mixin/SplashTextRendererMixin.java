package luobingbing1145.ohmysplashtext.mixin;

import luobingbing1145.ohmysplashtext.MathExpressionParser;
import luobingbing1145.ohmysplashtext.ModClientConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.DoubleUnaryOperator;

@Mixin(SplashTextRenderer.class)
public abstract class SplashTextRendererMixin {
    @Unique
    ModClientConfig config = ModClientConfig.INSTANCE.instance();

    @Unique
    SplashTextRenderer instance = (SplashTextRenderer) (Object) this;

    @Unique
    SplashTextRendererAccessor accessor = (SplashTextRendererAccessor) instance;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void modifyScale(Args args, DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha) {
        if (config.isSplashingAnimEnable()) {
            if (config.isAdvancedModeEnable()) {
                DoubleUnaryOperator parser = MathExpressionParser.parse(config.getFunctionOfSplashingAnim());
                float originalScale = (float) ((parser.applyAsDouble((Util.getMeasuringTimeMs() % 1000f) / 1000f)) * 100.0F / (float) (textRenderer.getWidth(accessor.getText()) + 32));
                float x = config.getScaleX() * originalScale;
                float y = config.getScaleY() * originalScale;
                float z = config.getScaleZ() * originalScale;
                args.set(0, x);
                args.set(1, y);
                args.set(2, z);
            } else {
                float originalScale = (1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMeasuringTimeMs() % 1000L) / 1000.0F * ((float) Math.PI * config.getSplashingSpeed())) * 0.1F)) * 100.0F / (float) (textRenderer.getWidth(accessor.getText()) + 32);
                float scale = config.getScale() * originalScale;
                args.set(0, scale);
                args.set(1, scale);
                args.set(2, scale);
            }
        } else {
            float originalScale = 1.8f * 100.0f / (float) (textRenderer.getWidth(accessor.getText()) + 32);
            float scale = config.getScale() * originalScale;
            args.set(0, scale);
            args.set(1, scale);
            args.set(2, scale);
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
    private float modifyRotation(float deg) {
        if (config.isRotationAnimEnable()) {
            if (config.getRotationSpeed() != 0) {
                return Util.getMeasuringTimeMs() / 1000f * config.getRotationSpeed();
            } else {
                return config.getRotation();
            }
        } else {
            return config.getRotation();
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index = 4)
    private int modifyColor(int centerX) {
        return config.getColor().getRGB();
    }
}
