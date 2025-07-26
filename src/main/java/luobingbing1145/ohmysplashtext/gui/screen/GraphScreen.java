package luobingbing1145.ohmysplashtext.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.DoubleUnaryOperator;

public class GraphScreen extends Screen {
    private final int PADDING;
    private final double SCALE_X;
    private final double SCALE_Y;
    private final double PRECISION;
    private final DoubleUnaryOperator function;  // 要绘制的函数
    private final Screen parent;

    public GraphScreen(Screen parent, DoubleUnaryOperator function, double scaleX, double scaleY, double precision, int padding) {
        super(Text.translatable("graghScreen.title"));
        this.function = function;
        this.parent = parent;
        this.SCALE_X = scaleX;
        this.SCALE_Y = scaleY;
        this.PRECISION = precision;
        this.PADDING = padding;
    }

    @Override
    protected void init() {
        // 添加返回按钮
        addDrawableChild(
                ButtonWidget
                        .builder(
                                Text.translatable("gui.back"),
                                (button) -> {
                                    if (client != null) {
                                        client.setScreen(parent);
                                    }
                                })
                        .position(width - 100, height - 30)
                        .size(100, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawAxes(context, new Color(0xffffffff, true));
        drawFunction(context, function, new Color(0xffffff00, true));
        context.drawTextWithShadow(textRenderer, Text.translatable("graghScreen.ratio", SCALE_X, SCALE_Y), 0, 0, 0xffffffff);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
    }

    private void drawAxes(@NotNull DrawContext context, @NotNull Color color) {
        int centerX = width / 2;
        int centerY = height / 2;

        context.drawVerticalLine(centerX, PADDING, height - PADDING, color.getRGB());
        context.drawHorizontalLine(PADDING, width - PADDING, centerY, color.getRGB());

        for (int i = 0; i < 5; i++) {
            context.fill(width - PADDING - i, centerY + i, width - PADDING - i - 1, centerY + i + 1, color.getRGB());
            context.fill(width - PADDING - i, centerY - i, width - PADDING - i - 1, centerY - i - 1, color.getRGB());
            context.fill(centerX + i, PADDING + i, centerX + i + 1, PADDING + i + 1, color.getRGB());
            context.fill(centerX - i, PADDING + i, centerX - i - 1, PADDING + i + 1, color.getRGB());
        }
    }

    private void drawFunction(DrawContext context, DoubleUnaryOperator func, Color fColor) {
        int centerX = width / 2;
        int centerY = height / 2;

        for (double px = PADDING; px <= width - PADDING; px += PRECISION) {
            double nx = (-centerX + px) * SCALE_X;
            double ny = func.applyAsDouble(nx);
            double py = (int) (centerY - ny / SCALE_Y);
            context.fill((int) px, (int) py, (int) (px + 1), (int) (py + 1), fColor.getRGB());
        }
    }
}
