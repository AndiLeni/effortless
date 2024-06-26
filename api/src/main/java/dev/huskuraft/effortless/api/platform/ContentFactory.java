package dev.huskuraft.effortless.api.platform;

import java.util.Locale;
import java.util.Optional;

import dev.huskuraft.effortless.api.core.Item;
import dev.huskuraft.effortless.api.core.ItemStack;
import dev.huskuraft.effortless.api.core.Items;
import dev.huskuraft.effortless.api.core.ResourceLocation;
import dev.huskuraft.effortless.api.networking.Buffer;
import dev.huskuraft.effortless.api.sound.Sound;
import dev.huskuraft.effortless.api.sound.Sounds;
import dev.huskuraft.effortless.api.tag.InputStreamTagReader;
import dev.huskuraft.effortless.api.tag.OutputStreamTagWriter;
import dev.huskuraft.effortless.api.tag.TagRecord;
import dev.huskuraft.effortless.api.text.Text;

public interface ContentFactory {

    static ContentFactory getInstance() {
        return PlatformLoader.getSingleton();
    }

    ResourceLocation newResourceLocation(String namespace, String path);

    Buffer newBuffer();

    TagRecord newTagRecord();

    Optional<Item> newOptionalItem(ResourceLocation location);

    default Item newItem(ResourceLocation location) {
        return newOptionalItem(location).orElseThrow();
    }

    ItemStack newItemStack();

    ItemStack newItemStack(Item item, int count);

    ItemStack newItemStack(Item item, int count, TagRecord tag);

    Text newText();

    Text newText(String text);

    Text newText(String text, Text... args);

    Text newTranslatableText(String text);

    Text newTranslatableText(String text, Text... args);

    Text newTranslatableText(String text, Object... args);

    InputStreamTagReader getInputStreamTagReader();

    OutputStreamTagWriter getOutputStreamTagWriter();

    OperatingSystem getOperatingSystem();

    Sound getSound(Sounds sounds);

    default Optional<Item> getOptionalItem(Items items) {
        return newOptionalItem(ResourceLocation.of("minecraft", items.name().toLowerCase(Locale.ROOT)));
    }

    default Item getItem(Items items) {
        return getOptionalItem(items).orElseThrow();
    }

}
