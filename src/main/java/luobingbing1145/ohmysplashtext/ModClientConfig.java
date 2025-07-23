package luobingbing1145.ohmysplashtext;

import com.google.gson.GsonBuilder;
import com.terraformersmc.modmenu.gui.ModsScreen;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ModClientConfig {
    public static ConfigClassHandler<ModClientConfig> INSTANCE =
            ConfigClassHandler
                    .createBuilder(ModClientConfig.class)
                    .id(new Identifier(OhMySplashText.MOD_ID, "client_config"))
                    .serializer(config ->
                            GsonConfigSerializerBuilder
                                    .create(config)
                                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("ohmysplashtext-client.json"))
                                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                                    .setJson5(true)
                                    .build()
                    )
                    .build();

    @SerialEntry
    private float scale = 1f;

    @SerialEntry
    private Color color = new Color(0xffffff00, true);

    @SerialEntry
    private float rotation = -20f;

    @SerialEntry
    private boolean isSplashingAnimEnable = true;

    @SerialEntry
    private float splashingSpeed = 2f;

    @SerialEntry
    private boolean isRotationAnimEnable = false;

    @SerialEntry
    private float rotationSpeed = 0f;

    @SerialEntry
    private boolean isAdvancedModeEnable = false;

    @SerialEntry(comment = "Custom Splashing Animation\nn for current timestamp\nsin() for sine function\ncos() for cosine function\nabs() for absolute value\n% for mod operator\npi for π\ne.g. 0.5+abs(cos(n%(2000/3)/1000*pi*3))*2 for 0.5+2|cos 3π(n%(2000/3)/1000)|")
    private String functionOfSplashingAnim = "1.8-abs(sin(n%(2000/2)/1000*2*pi)*0.1)";

    @SerialEntry
    private float scaleX = 1;

    @SerialEntry
    private float scaleY = 1;

    @SerialEntry
    private float scaleZ = 1;

    public static Screen makeScreen(Screen parent) {
        return YetAnotherConfigLib.create(INSTANCE, (defaults, config, builder) ->
                builder
                        .title(Text.translatable("config.ohmysplashtext.title"))
                        .category(
                                ConfigCategory
                                        .createBuilder()
                                        .name(Text.translatable("config.ohmysplashtext.title"))
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scale"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scale.desc")))
                                                        .binding(
                                                                1f,
                                                                () -> config.scale,
                                                                value -> config.scale = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(!config.isAdvancedModeEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Color>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.color"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.color.desc")))
                                                        .binding(
                                                                new Color(0xffffff00, true),
                                                                () -> config.color,
                                                                value -> config.color = value
                                                        )
                                                        .controller(colorOption ->
                                                                ColorControllerBuilder
                                                                        .create(colorOption)
                                                                        .allowAlpha(true)
                                                        )
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotation"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotation.desc")))
                                                        .binding(
                                                                -20f,
                                                                () -> config.rotation,
                                                                value -> config.rotation = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(-180f, 180f)
                                                                        .step(1f)
                                                        )
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashingAnim"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashingAnim.desc")))
                                                        .binding(
                                                                true,
                                                                () -> config.isSplashingAnimEnable,
                                                                value -> {
                                                                    config.isSplashingAnimEnable = value;
                                                                    MinecraftClient.getInstance().setScreen(makeScreen(new ModsScreen(new TitleScreen())));
                                                                }
                                                        )
                                                        .controller(BooleanControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashingSpeed"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashingSpeed.desc")))
                                                        .binding(
                                                                2f,
                                                                () -> config.splashingSpeed,
                                                                value -> config.splashingSpeed = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isSplashingAnimEnable() && !config.isAdvancedModeEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotationAnim"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotationAnim.desc")))
                                                        .binding(
                                                                false,
                                                                () -> config.isRotationAnimEnable,
                                                                value -> {
                                                                    config.isRotationAnimEnable = value;
                                                                    MinecraftClient.getInstance().setScreen(makeScreen(new ModsScreen(new TitleScreen())));
                                                                }
                                                        )
                                                        .controller(BooleanControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotationSpeed"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotationSpeed.desc")))
                                                        .binding(
                                                                0f,
                                                                () -> config.rotationSpeed,
                                                                value -> config.rotationSpeed = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(-360f, 360f)
                                                                        .step(10f)
                                                        )
                                                        .available(config.isRotationAnimEnable)
                                                        .build()
                                        )
                                        .option(
                                                LabelOption
                                                        .createBuilder()
                                                        .line(Text.translatable("config.ohmysplashtext.label.advancedOptions"))
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.advancedMode"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.advancedMode.desc")))
                                                        .binding(
                                                                false,
                                                                () -> config.isAdvancedModeEnable,
                                                                value -> {
                                                                    config.isAdvancedModeEnable = value;
                                                                    MinecraftClient.getInstance().setScreen(makeScreen(new ModsScreen(new TitleScreen())));
                                                                }
                                                        )
                                                        .controller(TickBoxControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<String>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashingFunction"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashingFunction.desc")))
                                                        .binding(
                                                                "1.8-abs(sin(n%(2000/2)/1000*2*pi)*0.1)",
                                                                () -> config.functionOfSplashingAnim,
                                                                value -> {
                                                                    try {
                                                                        MathExpressionParser.parse(value);  // 尝试解析表达式
                                                                        config.functionOfSplashingAnim = value;
                                                                    } catch (Exception e) {
                                                                        // 提示用户输入的表达式无效
                                                                        config.functionOfSplashingAnim = "1.8-abs(sin(n%(2000/2))/1000*2*pi)*0.1)";

                                                                        // 记录日志
                                                                        System.err.println("配置表达式无效，已恢复为默认值！");
                                                                    }
                                                                }
                                                        )
                                                        .available(config.isAdvancedModeEnable() && config.isSplashingAnimEnable)
                                                        .controller(StringControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scaleX"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scaleX.desc")))
                                                        .binding(
                                                                1f,
                                                                () -> config.scaleX,
                                                                value -> config.scaleX = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isAdvancedModeEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scaleY"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scaleY.desc")))
                                                        .binding(
                                                                1f,
                                                                () -> config.scaleY,
                                                                value -> config.scaleY = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isAdvancedModeEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scaleZ"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scaleZ.desc")))
                                                        .binding(
                                                                1f,
                                                                () -> config.scaleZ,
                                                                value -> config.scaleZ = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isAdvancedModeEnable())
                                                        .build()
                                        )
                                        .option(
                                                LabelOption
                                                        .createBuilder()
                                                        .line(Text.translatable("config.ohmysplashtext.label.tips"))
                                                        .build()
                                        )
                                        .build()
                        )
        ).generateScreen(parent);
    }

    public float getSplashingSpeed() {
        return splashingSpeed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public float getScaleX() {
        return scaleX;
    }

    public boolean isSplashingAnimEnable() {
        return isSplashingAnimEnable;
    }

    public float getRotation() {
        return rotation;
    }

    public String getFunctionOfSplashingAnim() {
        return functionOfSplashingAnim;
    }

    public boolean isAdvancedModeEnable() {
        return isAdvancedModeEnable;
    }

    public boolean isRotationAnimEnable() {
        return isRotationAnimEnable;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public float getScale() {
        return scale;
    }

    public Color getColor() {
        return color;
    }
}
