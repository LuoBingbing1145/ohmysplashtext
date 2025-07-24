package luobingbing1145.ohmysplashtext;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
            Custom Splashing Animations
            n for current timestamp
            sin() for sine function
            cos() for cosine function
            abs() for absolute value
            % for mod operator
            pi for π
            e.g. 0.5+abs(cos(n%(2000/3)/1000*pi*3))*2 for 0.5+2|cos(3π(n%(2000/3)/1000))|
            The "Rotation Animation" option needs to be on
            """
    )
    private String functionOfSplashingAnim = "1.8-abs(sin(n%(2000/2)/1000*2*pi)*0.1)";

    @SerialEntry(comment =
            """
            Custom Rotation Animations
            n for current timestamp
            sin() for sine function
            cos() for cosine function
            abs() for absolute value
            % for mod operator
            pi for π
            e.g. sin(n/1000*2*pi)*1.5 for 1.5sin(2π(n/1000))
            The "Splashing Animation" option needs to be on
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
                                                                () -> config.isSplashTextEnable,
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
                                                                () -> config.isButtonEnable,
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
                                                                () -> config.scale,
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
                                                                () -> config.text,
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
                                                                () -> config.color,
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
                                                                () -> config.isSplashingAnimEnable,
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
                                                                () -> config.splashingSpeed,
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
                                                                () -> config.rotation,
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
                                                                () -> config.isRotationAnimEnable,
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
                                                                () -> config.rotationSpeed,
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
                                                                () -> config.isAdvancedModeEnable,
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
                                                                () -> config.scaleX,
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
                                                                () -> config.scaleY,
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
                                                                () -> config.functionOfSplashingAnim,
                                                                value -> {
                                                                    try {
                                                                        MathExpressionParser.parse(value);  // 尝试解析表达式
                                                                        config.functionOfSplashingAnim = value;
                                                                    } catch (Exception e) {
                                                                        // 提示用户输入的表达式无效
                                                                        config.functionOfSplashingAnim = defaults.functionOfSplashingAnim;

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
                                                Option
                                                        .<String>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.rotationFunction"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.rotationFunction.desc")))
                                                        .binding(
                                                                defaults.functionOfRotationAnim,
                                                                () -> config.functionOfRotationAnim,
                                                                value -> {
                                                                    try {
                                                                        MathExpressionParser.parse(value);  // 尝试解析表达式
                                                                        config.functionOfRotationAnim = value;
                                                                    } catch (Exception e) {
                                                                        // 提示用户输入的表达式无效
                                                                        config.functionOfRotationAnim = defaults.functionOfRotationAnim;

                                                                        // 记录日志
                                                                        System.err.println("配置表达式无效，已恢复为默认值！");
                                                                    }
                                                                }
                                                        )
                                                        .available(config.isAdvancedModeEnable() && config.isRotationAnimEnable() && config.isSplashTextEnable())
                                                        .controller(StringControllerBuilder::create)
                                                        .build()
                                        )
                                        /*.option(
                                                Option
                                                        .<Float>createBuilder()
                                                        .name(Text.translatable("config.ohmysplashtext.option.scaleZ"))
                                                        .description(OptionDescription.of(Text.translatable("config.ohmysplashtext.option.scaleZ.desc")))
                                                        .binding(
                                                                defaults.scaleZ,
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
                                        )*/
                                        //源代码中闪烁标语入参有z轴大小，经测试无意义后删除配置功能
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
