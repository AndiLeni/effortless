package dev.huskuraft.effortless.vanilla.input;

import dev.huskuraft.effortless.api.input.KeyBinding;
import net.minecraft.client.KeyMapping;

public class MinecraftKeyBinding implements KeyBinding {

    private final KeyMapping reference;

    public MinecraftKeyBinding(KeyMapping reference) {
        this.reference = reference;
    }

    @Override
    public KeyMapping referenceValue() {
        return reference;
    }

    @Override
    public String getName() {
        return reference.getName();
    }

    @Override
    public String getCategory() {
        return reference.getCategory();
    }

    @Override
    public int getDefaultKey() {
        return reference.getDefaultKey().getValue();
    }

    @Override
    public boolean consumeClick() {
        return reference.consumeClick();
    }

    @Override
    public boolean isDown() {
        return reference.isDown();
    }

    @Override
    public int getBoundCode() {
        return reference.key.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MinecraftKeyBinding keyBinding && reference.equals(keyBinding.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }

}
