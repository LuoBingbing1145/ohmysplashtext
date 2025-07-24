package luobingbing1145.ohmysplashtext.mixin;

import luobingbing1145.ohmysplashtext.MathExpressionParser;
import luobingbing1145.ohmysplashtext.ModClientConfig;
import luobingbing1145.ohmysplashtext.OhMySplashTextClient;
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
    ModClientConfig config = OhMySplashTextClient.getConfig();

    @Unique
    SplashTextRenderer instance = (SplashTextRenderer) (Object) this;

    @Unique
    SplashTextRendererAccessor accessor = (SplashTextRendererAccessor) instance;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void modifyScale(Args args, DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha) {
        if (config.isSplashingAnimEnable()) {
            if (config.isAdvancedModeEnable()) {
                DoubleUnaryOperator parser = MathExpressionParser.parse(config.getFunctionOfSplashingAnim());
                float originalScale = (float) ((parser.applyAsDouble(Util.getMeasuringTimeMs())) * 100f / (float) (textRenderer.getWidth(accessor.getText()) + 32));
                float x = config.getScaleX() * originalScale;
                float y = config.getScaleY() * originalScale;
                /*float z = config.getScaleZ() * originalScale;*/
                //源代码中闪烁标语入参有z轴大小，经测试无意义后删除配置功能
                args.set(0, x);
                args.set(1, y);
                args.set(2, 1f);
                //z轴入参设为1
            } else {
                float originalScale = (1.8f - MathHelper.abs(MathHelper.sin((Util.getMeasuringTimeMs() % (2000f / config.getSplashingSpeed())) / 1000f * (float) Math.PI * config.getSplashingSpeed()) * 0.1f)) * 100f / (float) (textRenderer.getWidth(accessor.getText()) + 32);
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
            if (config.isAdvancedModeEnable()) {
                DoubleUnaryOperator parser = MathExpressionParser.parse(config.getFunctionOfRotationAnim());
                return (float) parser.applyAsDouble(Util.getMeasuringTimeMs());
            } else {
                if (config.getRotationSpeed() != 0) {
                    return Util.getMeasuringTimeMs() / 1000f * config.getRotationSpeed();
                } else {
                    return config.getRotation();
                }
            }
        } else {
            return config.getRotation();
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index = 4)
    private int modifyColor(int centerX) {
        return config.getColor().getRGB();
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index = 1)
    private String modifyText(String text) {
        if (config.getText().isEmpty()) {
            return text;
        } else {
            return config.getText();
        }
    }
}
