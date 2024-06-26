package dev.huskuraft.effortless.screen.transformer;

import java.util.function.Consumer;

import dev.huskuraft.effortless.api.gui.AbstractScreen;
import dev.huskuraft.effortless.api.gui.Dimens;
import dev.huskuraft.effortless.api.gui.button.Button;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.pattern.randomize.ItemRandomizer;
import dev.huskuraft.effortless.building.settings.RandomizerSettings;

public class EffortlessRandomizerSettingsScreen extends AbstractScreen {

    private final Consumer<RandomizerSettings> applySettings;
    private RandomizerSettings lastSettings;
    private TextWidget titleTextWidget;
    private TransformerList entries;
    private Button editButton;
    private Button deleteButton;
    private Button duplicateButton;
    private Button newButton;
    private Button resetButton;
    private Button doneButton;
    private Button cancelButton;

    public EffortlessRandomizerSettingsScreen(Entrance entrance, Consumer<RandomizerSettings> consumer, RandomizerSettings randomizerSettings) {
        super(entrance, Text.translate("effortless.randomizer.settings.title"));
        this.applySettings = consumer;
        this.lastSettings = randomizerSettings;
    }

    @Override
    public void onCreate() {
        this.titleTextWidget = addWidget(new TextWidget(getEntrance(), getWidth() / 2, Dimens.Screen.TITLE_36 - 12, getScreenTitle(), TextWidget.Gravity.CENTER));

        this.editButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.edit"), button -> {
            if (entries.hasSelected()) {
                new EffortlessRandomizerEditScreen(
                        getEntrance(),
                        randomizer -> {
                            entries.replaceSelect(randomizer);
                            onReload();
                        },
                        (ItemRandomizer) entries.getSelected().getItem()
                ).attach();
            }
        }).setBoundsGrid(getWidth(), getHeight(), 1f, 0f, 0.25f).build());

        this.deleteButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.delete"), button -> {
            if (entries.hasSelected()) {
                entries.deleteSelected();
            }
        }).setBoundsGrid(getWidth(), getHeight(), 1f, 0.25f, 0.25f).build());

        this.duplicateButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.duplicate"), button -> {
            if (entries.hasSelected()) {
                entries.insertSelected(entries.getSelected().getItem().withRandomId());
            }
        }).setBoundsGrid(getWidth(), getHeight(), 1f, 0.5f, 0.25f).build());

        this.newButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.new"), button -> {
            new EffortlessRandomizerEditScreen(
                    getEntrance(),
                    randomizer -> {
                        entries.insertSelected(randomizer);
                        onReload();
                    },
                    ItemRandomizer.EMPTY
            ).attach();
        }).setBoundsGrid(getWidth(), getHeight(), 1f, 0.75f, 0.25f).build());

        this.resetButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.reset"), button -> {
            entries.reset(ItemRandomizer.getDefaultItemRandomizers());
        }).setBoundsGrid(getWidth(), getHeight(), 1f, 0f, 1f).build());

        this.cancelButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.cancel"), button -> {
            detach();
        }).setBoundsGrid(getWidth(), getHeight(), 0f, 0f, 0.5f).build());
        this.doneButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.randomizer.settings.done"), button -> {
            applySettings.accept(lastSettings);
            detach();
        }).setBoundsGrid(getWidth(), getHeight(), 0f, 0.5f, 0.5f).build());
        this.entries = addWidget(new TransformerList(getEntrance(), 0, Dimens.Screen.TITLE_36, getWidth(), getHeight() - Dimens.Screen.TITLE_36 - Dimens.Screen.BUTTON_ROW_2));
        this.entries.reset(lastSettings.randomizers());
    }

    @Override
    public void onReload() {
        editButton.setActive(entries.hasSelected());
        deleteButton.setActive(entries.hasSelected());
        duplicateButton.setActive(entries.hasSelected());

        editButton.setVisible(!getEntrance().getClient().getWindow().isAltDown());
        deleteButton.setVisible(!getEntrance().getClient().getWindow().isAltDown());
        duplicateButton.setVisible(!getEntrance().getClient().getWindow().isAltDown());
        newButton.setVisible(!getEntrance().getClient().getWindow().isAltDown());
        resetButton.setVisible(getEntrance().getClient().getWindow().isAltDown());

        lastSettings = new RandomizerSettings(
                entries.items().stream().map(ItemRandomizer.class::cast).toList()
        );
    }

}
