package dev.huskuraft.effortless.vanilla.adapters;

import dev.huskuraft.effortless.gui.Screen;
import dev.huskuraft.effortless.gui.Typeface;
import dev.huskuraft.effortless.input.KeyBinding;
import dev.huskuraft.effortless.renderer.RenderType;
import dev.huskuraft.effortless.renderer.Renderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class MinecraftClientAdapter extends MinecraftAdapter {

    public static Screen adapt(net.minecraft.client.gui.screens.Screen screen) {
        if (screen == null) {
            return null;
        }
        if (screen instanceof MinecraftProxyScreen proxyScreen) {
            return proxyScreen.getProxy();
        }
        return new MinecraftScreen(screen);
    }

    public static net.minecraft.client.gui.screens.Screen adapt(Screen screen) {
        if (screen == null) {
            return null;
        }
        if (screen instanceof MinecraftScreen screen1) {
            return screen1.getRef();
        }
        return new MinecraftProxyScreen(screen);
    }

    public static Renderer adapt(GuiGraphics guiGraphics) {
        if (guiGraphics == null) {
            return null;
        }
        return new MinecraftRenderer(guiGraphics);
    }

    public static RenderType adapt(net.minecraft.client.renderer.RenderType renderType) {
        if (renderType == null) {
            return null;
        }
        return new MinecraftRenderType(renderType);
    }

    public static net.minecraft.client.renderer.RenderType adapt(RenderType renderType) {
        if (renderType == null) {
            return null;
        }
        return ((MinecraftRenderType) renderType).getRef();
    }

    public static Typeface adapt(Font font) {
        if (font == null) {
            return null;
        }
        return new MinecraftTypeface(font);
    }

    public static Font adapt(Typeface typeface) {
        if (typeface == null) {
            return null;
        }
        return ((MinecraftTypeface) typeface).getRef();
    }

    public static KeyBinding adapt(KeyMapping keyMapping) {
        if (keyMapping == null) {
            return null;
        }
        return new MinecraftKeyBinding(keyMapping);
    }

    public static KeyMapping adapt(KeyBinding keyBinding) {
        if (keyBinding == null) {
            return null;
        }
        return ((MinecraftKeyBinding) keyBinding).getRef();
    }


}
