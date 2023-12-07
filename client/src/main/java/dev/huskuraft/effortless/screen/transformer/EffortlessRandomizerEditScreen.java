package dev.huskuraft.effortless.screen.transformer;

import dev.huskuraft.effortless.building.pattern.randomize.Chance;
import dev.huskuraft.effortless.building.pattern.randomize.ItemRandomizer;
import dev.huskuraft.effortless.building.pattern.randomize.Randomizer;
import dev.huskuraft.effortless.core.Entrance;
import dev.huskuraft.effortless.core.Item;
import dev.huskuraft.effortless.core.Player;
import dev.huskuraft.effortless.core.ItemStack;
import dev.huskuraft.effortless.gui.AbstractScreen;
import dev.huskuraft.effortless.gui.Dimens;
import dev.huskuraft.effortless.gui.button.Button;
import dev.huskuraft.effortless.gui.container.EditableEntry;
import dev.huskuraft.effortless.gui.container.EditableEntryList;
import dev.huskuraft.effortless.gui.input.EditBox;
import dev.huskuraft.effortless.gui.input.NumberField;
import dev.huskuraft.effortless.gui.slot.ItemSlot;
import dev.huskuraft.effortless.gui.slot.TextSlot;
import dev.huskuraft.effortless.gui.text.TextWidget;
import dev.huskuraft.effortless.math.MathUtils;
import dev.huskuraft.effortless.screen.item.EffortlessItemPickerScreen;
import dev.huskuraft.effortless.screen.transformer.tooltip.TransformerTooltipEntry;
import dev.huskuraft.effortless.text.Text;
import dev.huskuraft.effortless.text.TextStyle;

import java.util.List;
import java.util.function.Consumer;

public class EffortlessRandomizerEditScreen extends AbstractScreen {

    private static final int MAX_RANDOMIZER_SIZE = 36; // Inventory.INVENTORY_SIZE // FIXME: 23/10/23 move
    private static final int MAX_RANDOMIZER_NAME_LENGTH = 255;
    private static final int MIN_ITEM_COUNT = 0;
    private static final int MAX_ITEM_COUNT = 99;

    private final Consumer<ItemRandomizer> applySettings;
    private final ItemRandomizer defaultSettings;
    private ItemRandomizer lastSettings;
    private TextWidget titleTextWidget;
    private ItemChanceList entries;
    private EditBox nameEditBox;
    private Button orderButton;
    private Button supplierButton;
    private Button upButton;
    private Button downButton;
    private Button deleteButton;
    private Button addButton;
    private Button saveButton;
    private Button cancelButton;
    private TextSlot textSlot;

    private Randomizer.Order lastOrder;
    private Randomizer.Target lastTarget;

    public EffortlessRandomizerEditScreen(Entrance entrance, Consumer<ItemRandomizer> consumer, ItemRandomizer randomizer) {
        super(entrance, Text.translate("effortless.randomizer.edit.title"));
        this.applySettings = consumer;
        this.defaultSettings = randomizer;
        this.lastSettings = randomizer;
        this.lastOrder = randomizer.getOrder();
        this.lastTarget = randomizer.getTarget();
    }

    @Override
    public void onCreate() {

        this.titleTextWidget = addWidget(new TextWidget(getEntrance(), getWidth() / 2, 24 - 16, getScreenTitle(), TextWidget.Gravity.CENTER));

        this.textSlot = addWidget(new TextSlot(getEntrance(), getWidth() / 2 - (Dimens.RegularEntry.ROW_WIDTH) / 2, 16 + 2, Dimens.ICON_WIDTH, Dimens.ICON_HEIGHT, Text.empty()));

        this.nameEditBox = addWidget(
                new EditBox(getEntrance(), getWidth() / 2 - (Dimens.RegularEntry.ROW_WIDTH) / 2 + 40, 24, Dimens.RegularEntry.ROW_WIDTH - 40, 20, null)
        );
        this.nameEditBox.setMaxLength(MAX_RANDOMIZER_NAME_LENGTH);
        this.nameEditBox.setHint(Text.translate("effortless.randomizer.edit.name_hint"));
        this.nameEditBox.setValue(lastSettings.getName().getString());

        this.orderButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.order", Text.translate(lastSettings.getOrder().getNameKey())), button -> {
            lastOrder = Randomizer.Order.values()[(lastOrder.ordinal() + 1) % Randomizer.Order.values().length];
            orderButton.setMessage(Text.translate("effortless.randomizer.edit.order", Text.translate(lastOrder.getNameKey())));
        }).bounds(getWidth() / 2 - 154, 48 + 6, 150, 20).build());
        this.supplierButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.target", Text.translate(lastTarget.getNameKey())), button -> {
            lastTarget = Randomizer.Target.values()[(lastTarget.ordinal() + 1) % Randomizer.Target.values().length];
            supplierButton.setMessage(Text.translate("effortless.randomizer.edit.target", Text.translate(lastTarget.getNameKey())));
        }).bounds(getWidth() / 2 + 4, 48 + 6, 150, 20).build());

        this.entries = addWidget(new ItemChanceList(getEntrance(), 0, 82, getWidth(), getHeight() - 82 - 60));
        this.entries.reset(lastSettings.getChances());

        this.upButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.up"), button -> {
            if (entries.hasSelected()) {
                entries.moveUpSelected();
            }
        }).bounds(getWidth() / 2 - 154, getHeight() - 52, 72, 20).build());
        this.downButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.down"), button -> {
            if (entries.hasSelected()) {
                entries.moveDownSelected();
            }
        }).bounds(getWidth() / 2 - 76, getHeight() - 52, 72, 20).build());

        this.deleteButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.delete"), button -> {
            entries.deleteSelected();
        }).bounds(getWidth() / 2 + 4, getHeight() - 52, 72, 20).build());
        this.addButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.add"), button -> {
            new EffortlessItemPickerScreen(
                    getEntrance(),
                    item -> {
                        entries.insertSelected(Chance.of(item, (byte) 1));
                        onReload();
                    }
            ).attach();
        }).bounds(getWidth() / 2 + 82, getHeight() - 52, 72, 20).build());

        this.saveButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.save"), button -> {
            applySettings.accept(lastSettings);
            detach();
        }).bounds(getWidth() / 2 - 154, getHeight() - 28, 150, 20).build());
        this.cancelButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.edit.cancel"), button -> {
            detach();
        }).bounds(getWidth() / 2 + 4, getHeight() - 28, 150, 20).build());
    }

    @Override
    public void onReload() {
        textSlot.setMessage(TransformerTooltipEntry.getSymbol(lastSettings));

        upButton.setActive(entries.hasSelected() && entries.indexOfSelected() > 0);
        downButton.setActive(entries.hasSelected() && entries.indexOfSelected() < entries.children().size() - 1);
        deleteButton.setActive(entries.hasSelected());
        addButton.setActive(entries.children().size() <= MAX_RANDOMIZER_SIZE);

        lastSettings = ItemRandomizer.create(
                Text.text(nameEditBox.getValue()), // TODO: 6/12/23 use default if no changes
                lastOrder,
                lastTarget,
                Randomizer.Category.ITEM,
                entries.items());
    }

    public static final class ItemChanceList extends EditableEntryList<Chance<Item>> {

        public ItemChanceList(Entrance entrance, int x, int y, int width, int height) {
            super(entrance, x, y, width, height);
        }

        public static List<Text> getRandomizerEntryTooltip(Player player, Chance<Item> chance, int totalCount) {
            var components = chance.content().getDefaultStack().getDescription(player, ItemStack.TooltipType.ADVANCED_CREATIVE);
            var percentage = String.format("%.2f%%", 100.0 * chance.chance() / totalCount);
            components.add(
                    Text.empty()
            );
            components.add(
                    Text.text("Total Probability:").withStyle(TextStyle.GRAY).append(Text.text(" ")).append(Text.text(percentage).withStyle(TextStyle.GOLD).append(Text.text(" (" + chance.chance() + "/" + totalCount + ")").withStyle(TextStyle.DARK_GRAY)))
            );
            return components;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getWidth() / 2 + 160;
        }

        @Override
        protected EditableEntry<Chance<Item>> createHolder(Chance<Item> item) {
            return new ItemChanceEntry(getEntrance(), item);
        }

        // TODO: 15/9/23 remove
        public int totalCount() {
            return items().stream().mapToInt(Chance::chance).sum();
        }

        public class ItemChanceEntry extends EditableEntry<Chance<Item>> {

            private ItemSlot itemSlot;
            private TextWidget nameTextWidget;
            private TextWidget chanceTextWidget;
            private NumberField numberField;

            public ItemChanceEntry(Entrance entrance, Chance<Item> chance) {
                super(entrance, chance);
            }

            @Override
            public void onCreate() {
                this.numberField = addWidget(new NumberField(getEntrance(), getX() + getWidth() - 42, getY() + 1, 42, 18));
                this.numberField.getTextField().setFilter(string -> {
                    if (string.isEmpty()) {
                        return true;
                    }
                    try {
                        var result = Integer.parseInt(string);
                        if (result < MIN_ITEM_COUNT || result > MAX_ITEM_COUNT) {
                            numberField.getTextField().setValue(String.valueOf(MathUtils.clamp(result, MIN_ITEM_COUNT, MAX_ITEM_COUNT)));
                            return false;
                        }
                        if (!String.valueOf(result).equals(string)) {
                            numberField.getTextField().setValue(String.valueOf(result));
                            return false;
                        }
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
                this.numberField.getTextField().setValue(String.valueOf(getItem().chance()));
                this.numberField.getTextField().setResponder(string -> {
                    var count = (byte) 0;
                    try {
                        count = Byte.parseByte(string);
                    } catch (NumberFormatException ignored) {
                    }
                    this.setItem(Chance.of(getItem().content(), count));
                });
                this.itemSlot = addWidget(new ItemSlot(getEntrance(), getX() + 1, getY() + 1, Dimens.SLOT_WIDTH, Dimens.SLOT_HEIGHT, getItem().content(), Text.text(String.valueOf(getItem().chance()))));
                this.nameTextWidget = addWidget(new TextWidget(getEntrance(), getX() + 24, getY() + 6, getDisplayName(getItem())));
                this.chanceTextWidget = addWidget(new TextWidget(getEntrance(), getX() + getWidth() - 50, getY() + 6, Text.empty()));
            }

            @Override
            public void onReload() {
                var percentage = String.format("%.2f%%", 100.0 * getItem().chance() / totalCount());
                chanceTextWidget.setX(((getX() + getWidth()) - 50 - getTypeface().measureWidth(percentage)));
                chanceTextWidget.setMessage(Text.text(percentage));
            }

            @Override
            public void onBindItem() {
                itemSlot.setItemStack(getItem().content().getDefaultStack());
                itemSlot.setDescription(Text.text(String.valueOf(getItem().chance())));
                nameTextWidget.setMessage(getDisplayName(getItem()));
            }

            // TODO: 8/2/23
            @Override
            public Text getNarration() {
                return Text.translate("narrator.select", getDisplayName(getItem()));
            }

            private Text getDisplayName(Chance<Item> chance) {
                return chance.content().getDefaultStack().getHoverName();
            }

            @Override
            public int getWidth() {
                return Dimens.RegularEntry.ROW_WIDTH;
            }

            @Override
            public int getHeight() {
                return 24;
            }
        }
    }
}
