package dev.huskuraft.effortless.api.core;

import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.tag.TagRecord;
import dev.huskuraft.effortless.api.tag.TagSerializable;
import dev.huskuraft.effortless.api.text.Text;

import java.util.List;

public abstract class ItemStack implements TagSerializable {

    public static ItemStack empty() {
        return Entrance.getInstance().getPlatform().newItemStack();
    }

    public static ItemStack of(Item item, int count) {
        return Entrance.getInstance().getPlatform().newItemStack(item, count);
    }

    public static ItemStack of(Item item, int count, TagRecord tag) {
        return Entrance.getInstance().getPlatform().newItemStack(item, count, tag);
    }

    public abstract List<Text> getTooltips(Player player, TooltipType flag);

    public abstract boolean isEmpty();

    public abstract boolean isAir();

    public abstract boolean isBlock();

    public abstract Item getItem();

    public abstract int getStackSize();

    public abstract void setStackSize(int count);

    public abstract int getMaxStackSize();

    public abstract Text getHoverName();

    public abstract void increase(int count);

    public abstract void decrease(int count);

    public abstract ItemStack copy();

    public abstract TagRecord getTag();

    public abstract void setTag(TagRecord tagRecord);

    public abstract boolean isItem(Item item);

    public abstract boolean itemEquals(ItemStack itemStack);

    public abstract boolean tagEquals(ItemStack itemStack);

    public abstract BlockState getBlockState(Player player, BlockInteraction interaction);

    public enum TooltipType {
        NORMAL,
        NORMAL_CREATIVE,
        ADVANCED,
        ADVANCED_CREATIVE
    }

}