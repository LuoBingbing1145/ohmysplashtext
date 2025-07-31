package luobingbing1145.ohmysplashtext.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

public class GraphScreen extends Screen {
    private final int PADDING;
    private final float SCALE_X;
    private final float SCALE_Y;
    private final float PRECISION;
    private final DoubleUnaryOperator FUNCTION;  // 要绘制的函数
    private final Screen PARENT;
    // 缓存绘制的函数点
    private ArrayList<Point> cachedPoints = null;
    private boolean coord;

    public GraphScreen(Screen parent, DoubleUnaryOperator function, float scaleX, float scaleY, float precision, int padding) {
        super(Text.translatable("graghScreen.title"));
        this.FUNCTION = function;
        this.PARENT = parent;
        this.SCALE_X = scaleX;
        this.SCALE_Y = scaleY;
        this.PRECISION = precision;
        this.PADDING = padding;
        coord = true;
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
                                        client.setScreen(PARENT);
                                    }
                                })
                        .position(width - 110, height - 30)
                        .size(100, 20)
                        .build()
        );
        addDrawableChild(
                ButtonWidget
                        .builder(
                                Text.translatable("graghScreen.coord"),
                                button -> coord = !coord
                        )
                        .position(width - 110, 10)
                        .size(100, 20)
                        .build()
        );

        // 只有尺寸改变时才重新计算函数点
        cachedPoints = null;
        cacheFunctionPoints();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // 如果函数点没有缓存，缓存并绘制
        if (cachedPoints == null) {
            cacheFunctionPoints();
        }

        drawAxes(context, new Color(0xffffffff, true));
        drawFunction(context, new Color(0xffffff00, true));
        drawTooltip(context, mouseX, new Color(0xff007fff, true));

        context.drawTextWithShadow(textRenderer, Text.translatable("graghScreen.ratio", SCALE_X, SCALE_Y), 0, 0, 0xffffffff);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
    }

    private void cacheFunctionPoints() {
        int centerX = width / 2;
        int centerY = height / 2;

        cachedPoints = new ArrayList<>();

        int lastX = 0;
        int lastY = 0;

        for (float px = PADDING; px <= width - PADDING; px += PRECISION) {
            float nx = (-centerX + px) * SCALE_X;
            float ny = (float) FUNCTION.applyAsDouble(nx);
            float py = centerY - ny / SCALE_Y;
            int x = (int) (px + 0.5);
            int y = (int) (py + 0.5);
            if (y != lastY || x != lastX) {
                cachedPoints.add(new Point(x, y));
            }
            lastX = x;
            lastY = y;
        }
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

    private void drawFunction(DrawContext context, Color color) {
        for (Point p : cachedPoints) {
            context.fill(p.x, p.y, p.x + 1, p.y + 1, color.getRGB());
        }
    }

    private void drawTooltip(@NotNull DrawContext context, int mouseX, Color color) {
        int centerX = width / 2;
        int centerY = height / 2;

        float nx = (-centerX + mouseX) * SCALE_X;
        float ny = (float) FUNCTION.applyAsDouble(nx);
        float py = centerY - ny / SCALE_Y;

        if (mouseX >= PADDING && mouseX <= width - PADDING && coord) {
            context.fill(mouseX - 1, (int) (py - 1), mouseX + 2, (int) (py + 2), color.getRGB());
            context.drawTooltip(textRenderer, Text.translatable("graghScreen.tooltip", String.format("%.2f", nx), String.format("%.2f", ny)), mouseX, (int) py);
        }
    }

    private record Point(int x, int y) {}
}
