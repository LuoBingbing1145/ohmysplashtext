package luobingbing1145.ohmysplashtext;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import luobingbing1145.ohmysplashtext.gui.screen.GraphScreen;
import luobingbing1145.ohmysplashtext.util.MathExpressionParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.function.DoubleUnaryOperator;

public class ModClientConfig {
    public static ConfigClassHandler<ModClientConfig> INSTANCE =
            ConfigClassHandler
                    .createBuilder(ModClientConfig.class)
                    .id(Identifier.of(OhMySplashText.MOD_ID, "client_config"))
                    .serializer(config ->
                            GsonConfigSerializerBuilder
                                    .create(config)
                                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("ohmysplashtext-client.json5"))
                                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                                    .setJson5(true)
                                    .build()
                    )
                    .build();

    @SerialEntry
    private boolean isSplashTextEnable = true;

    @SerialEntry
    private boolean isButtonEnable = true;

    @SerialEntry
    private float scale = 1f;

    @SerialEntry
    private String text = "";

    @SerialEntry
    private Color color = new Color(0xffffff00, true);

    @SerialEntry
    private boolean isSplashingAnimEnable = true;

    @SerialEntry(comment = "The \"Splashing Animation\" option needs to be on")
    private float splashingSpeed = 2f;

    @SerialEntry
    private float rotation = -20f;

    @SerialEntry
    private boolean isRotationAnimEnable = false;

    @SerialEntry(comment = "The \"Rotation Animation\" option needs to be on")
    private float rotationSpeed = 0f;

    @SerialEntry
    private boolean isAdvancedModeEnable = false;

    @SerialEntry
    private float scaleX = 1;

    @SerialEntry
    private float scaleY = 1;

    @SerialEntry(comment =
            """
            Customize Splashing Animation
            n Represents Current Timestamp(Seconds)
            sin(), cos(), abs(), %, pi Supported
            Example: 0.5+abs(cos(n*pi*3))*2 = 0.5+2|cos(3πn)|
            Requires "Rotation Animation" Enable
            """
    )
    private String functionOfSplashingAnim = "1.8-abs(sin(n*2*pi)*0.1)";

    @SerialEntry(comment =
            """
            Customize Rotation Animation
            n Represents Current Timestamp(Seconds)
            sin(), cos(), abs(), %, pi Supported
            Example: sin(n*2*pi)*1.5 = 1.5sin(2πn))
            Requires "Splashing Animation" Enable
            """
    )
    private String functionOfRotationAnim = "-20";

    /*@SerialEntry
    private float scaleZ = 1;*/
    //源代码中闪烁标语入参有z轴大小，经测试无意义后删除配置功能

    public static Screen makeScreen(Screen parent) {
        return YetAnotherConfigLib.create(INSTANCE, (defaults, config, builder) ->
                builder
                        .title(Text.translatable("config.ohmysplashtext.title"))
                        .category(
                                ConfigCategory
                                        .createBuilder()
                                        .name(Text.translatable("config.ohmysplashtext.title"))
                                        .option(
                                                LabelOption
                                                        .createBuilder()
                                                        .line(Text.translatable("config.ohmysplashtext.label.general"))
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashText"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashText.desc")))
                                                        .binding(
                                                                defaults.isSplashTextEnable,
                                                                config::isSplashTextEnable,
                                                                value -> {
                                                                    config.isSplashTextEnable = value;
                                                                    refreshScreen(makeScreen(parent));
                                                                }
                                                        )
                                                        .controller(BooleanControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.button"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.button.desc")))
                                                        .binding(
                                                                defaults.isButtonEnable,
                                                                config::isButtonEnable,
                                                                value -> config.isButtonEnable = value
                                                        )
                                                        .controller(BooleanControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                LabelOption
                                                        .createBuilder()
                                                        .line(Text.translatable("config.ohmysplashtext.label.text"))
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scale"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scale.desc")))
                                                        .binding(
                                                                defaults.scale,
                                                                config::getScale,
                                                                value -> config.scale = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(!config.isAdvancedModeEnable() && config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<String>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.text"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.text.desc")))
                                                        .binding(
                                                                defaults.text,
                                                                config::getText,
                                                                value -> config.text = value
                                                        )
                                                        .controller(StringControllerBuilder::create)
                                                        .available(config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Color>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.color"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.color.desc")))
                                                        .binding(
                                                                defaults.color,
                                                                config::getColor,
                                                                value -> config.color = value
                                                        )
                                                        .controller(colorOption ->
                                                                ColorControllerBuilder
                                                                        .create(colorOption)
                                                                        .allowAlpha(true)
                                                        )
                                                        .available(config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                LabelOption
                                                        .createBuilder()
                                                        .line(Text.translatable("config.ohmysplashtext.label.splashing"))
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashingAnim"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashingAnim.desc")))
                                                        .binding(
                                                                defaults.isSplashingAnimEnable,
                                                                config::isSplashingAnimEnable,
                                                                value -> {
                                                                    config.isSplashingAnimEnable = value;
                                                                    refreshScreen(makeScreen(parent));
                                                                }
                                                        )
                                                        .controller(BooleanControllerBuilder::create)
                                                        .available(config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashingSpeed"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashingSpeed.desc")))
                                                        .binding(
                                                                defaults.splashingSpeed,
                                                                config::getSplashingSpeed,
                                                                value -> config.splashingSpeed = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isSplashingAnimEnable() && !config.isAdvancedModeEnable() && config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                LabelOption
                                                        .createBuilder()
                                                        .line(Text.translatable("config.ohmysplashtext.label.rotation"))
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotation"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotation.desc")))
                                                        .binding(
                                                                defaults.rotation,
                                                                config::getRotation,
                                                                value -> config.rotation = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(-180f, 180f)
                                                                        .step(1f)
                                                        )
                                                        .available(!config.isRotationAnimEnable() && config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Boolean>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotationAnim"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotationAnim.desc")))
                                                        .binding(
                                                                defaults.isRotationAnimEnable,
                                                                config::isRotationAnimEnable,
                                                                value -> {
                                                                    config.isRotationAnimEnable = value;
                                                                    refreshScreen(makeScreen(parent));
                                                                }
                                                        )
                                                        .controller(BooleanControllerBuilder::create)
                                                        .available(config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotationSpeed"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotationSpeed.desc")))
                                                        .binding(
                                                                defaults.rotationSpeed,
                                                                config::getRotationSpeed,
                                                                value -> config.rotationSpeed = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(-360f, 360f)
                                                                        .step(10f)
                                                        )
                                                        .available(config.isRotationAnimEnable() && !config.isAdvancedModeEnable() && config.isSplashTextEnable())
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
                                                                defaults.isAdvancedModeEnable,
                                                                config::isAdvancedModeEnable,
                                                                value -> {
                                                                    config.isAdvancedModeEnable = value;
                                                                    refreshScreen(makeScreen(parent));
                                                                }
                                                        )
                                                        .controller(TickBoxControllerBuilder::create)
                                                        .available(config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scaleX"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scaleX.desc")))
                                                        .binding(
                                                                defaults.scaleX,
                                                                config::getScaleX,
                                                                value -> config.scaleX = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isAdvancedModeEnable() && config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scaleY"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scaleY.desc")))
                                                        .binding(
                                                                defaults.scaleY,
                                                                config::getScaleY,
                                                                value -> config.scaleY = value
                                                        )
                                                        .controller(floatOption ->
                                                                FloatSliderControllerBuilder
                                                                        .create(floatOption)
                                                                        .range(0.1f, 5f)
                                                                        .step(0.1f)
                                                        )
                                                        .available(config.isAdvancedModeEnable() && config.isSplashTextEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<String>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.splashingFunction"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.splashingFunction.desc")))
                                                        .binding(
                                                                defaults.functionOfSplashingAnim,
                                                                config::getFunctionOfSplashingAnim,
                                                                value -> {
                                                                    try {
                                                                        MathExpressionParser.parse(value);  // 尝试解析表达式
                                                                        config.functionOfSplashingAnim = value;
                                                                    } catch (Exception e) {
                                                                        // 提示用户输入的表达式无效
                                                                        config.functionOfSplashingAnim = defaults.getFunctionOfSplashingAnim();

                                                                        // 记录日志
                                                                        System.err.println("配置表达式无效，已恢复为默认值！");
                                                                    }
                                                                }
                                                        )
                                                        .available(config.isAdvancedModeEnable() && config.isSplashingAnimEnable() && config.isSplashTextEnable())
                                                        .controller(StringControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                ButtonOption
                                                        .createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.gragh"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.gragh.desc")))
                                                        .action((yaclScreen, buttonOption) -> {
                                                            DoubleUnaryOperator parser = MathExpressionParser.parse(config.getFunctionOfSplashingAnim());
                                                            MinecraftClient.getInstance().setScreen(new GraphScreen(makeScreen(parent), parser, 0.01, 0.02, 0.3, 20));
                                                        })
                                                        .available(config.isSplashTextEnable() && config.isAdvancedModeEnable() && config.isSplashingAnimEnable())
                                                        .build()
                                        )
                                        .option(
                                                Option
                                                        .<String>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotationFunction"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotationFunction.desc")))
                                                        .binding(
                                                                defaults.getFunctionOfRotationAnim(),
                                                                config::getFunctionOfRotationAnim,
                                                                value -> {
                                                                    try {
                                                                        MathExpressionParser.parse(value);  // 尝试解析表达式
                                                                        config.functionOfRotationAnim = value;
                                                                    } catch (Exception e) {
                                                                        // 提示用户输入的表达式无效
                                                                        config.functionOfRotationAnim = defaults.getFunctionOfRotationAnim();

                                                                        // 记录日志
                                                                        System.err.println("配置表达式无效，已恢复为默认值！");
                                                                    }
                                                                }
                                                        )
                                                        .available(config.isAdvancedModeEnable() && config.isRotationAnimEnable() && config.isSplashTextEnable())
                                                        .controller(StringControllerBuilder::create)
                                                        .build()
                                        )
                                        .option(
                                                ButtonOption
                                                        .createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.gragh2"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.gragh.desc")))
                                                        .action((yaclScreen, buttonOption) -> {
                                                            DoubleUnaryOperator parser = MathExpressionParser.parse(config.getFunctionOfRotationAnim());
                                                            MinecraftClient.getInstance().setScreen(new GraphScreen(makeScreen(parent), parser, 0.01, 0.5, 0.3, 20));
                                                        })
                                                        .available(config.isSplashTextEnable() && config.isAdvancedModeEnable() && config.isRotationAnimEnable())
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

    private static void refreshScreen(Screen screen) {
        MinecraftClient.getInstance().setScreen(screen);
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

    /*public float getScaleZ() {
        return scaleZ;
    }*/
    //源代码中闪烁标语入参有z轴大小，经测试无意义后删除配置功能

    public float getScale() {
        return scale;
    }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public String getFunctionOfRotationAnim() {
        return functionOfRotationAnim;
    }

    public boolean isSplashTextEnable() {
        return isSplashTextEnable;
    }

    public boolean isButtonEnable() {
        return isButtonEnable;
    }
}
