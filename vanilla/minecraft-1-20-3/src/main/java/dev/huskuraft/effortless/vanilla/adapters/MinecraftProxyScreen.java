package dev.huskuraft.effortless.vanilla.adapters;

import dev.huskuraft.effortless.gui.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class MinecraftProxyScreen extends net.minecraft.client.gui.screens.Screen {

    private final Screen proxy;

    MinecraftProxyScreen(Screen screen) {
        super(Component.empty());
        this.proxy = screen;
    }

    public Screen getProxy() {
        return proxy;
    }

    @Override
    public void tick() {
        proxy.tick();
    }

    @Override
    protected void init() {
        proxy.setWidth(width);
        proxy.setHeight(height);
        proxy.onDestroy();
        proxy.onCreate();
        proxy.onLoad();
    }

    @Override
    protected void rebuildWidgets() {
        proxy.setWidth(width);
        proxy.setHeight(height);
        proxy.onDestroy();
        proxy.onCreate();
        proxy.onLoad();
    }

    @Override
    public void removed() {
        proxy.onDestroy();
    }

    @Override
    public void onClose() {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        proxy.onReload();
        proxy.render(new MinecraftRenderer(guiGraphics), i, j, f);
        proxy.renderOverlay(new MinecraftRenderer(guiGraphics), i, j, f);
    }

    @Override
    public boolean isPauseScreen() {
        return proxy.isPauseGame();
    }

    @Override
    public void mouseMoved(double d, double e) {
        proxy.onMouseMoved(d, e);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return proxy.onMouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        return proxy.onMouseReleased(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        return proxy.onMouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        return proxy.onMouseScrolled(d, e, f, g);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return proxy.onKeyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        return proxy.onKeyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return proxy.onCharTyped(c, i);
    }

    //    @Override
    @Deprecated
    public boolean changeFocus(boolean bl) {
        return proxy.onFocusMove(bl);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return proxy.isMouseOver(d, e);
    }

    @Override
    public Component getTitle() {
        return MinecraftText.toMinecraftText(proxy.getScreenTitle());
    }
}
