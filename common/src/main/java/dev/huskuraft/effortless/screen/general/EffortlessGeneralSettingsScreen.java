package dev.huskuraft.effortless.screen.general;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.gui.AbstractScreen;
import dev.huskuraft.effortless.api.gui.Dimens;
import dev.huskuraft.effortless.api.gui.button.Button;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.networking.packets.player.PlayerOperatorCheckPacket;
import dev.huskuraft.effortless.screen.settings.EffortlessNotAnOperatorScreen;
import dev.huskuraft.effortless.screen.settings.SettingButtonsList;

public class EffortlessGeneralSettingsScreen extends AbstractScreen {

    public EffortlessGeneralSettingsScreen(Entrance entrance) {
        super(entrance, Text.translate("effortless.general_settings.title"));
    }

    @Override
    public void onCreate() {
        addWidget(new TextWidget(getEntrance(), getWidth() / 2, Dimens.Screen.TITLE_36 - 12, getScreenTitle(), TextWidget.Gravity.CENTER));

        var entries = addWidget(new SettingButtonsList(getEntrance(), 0, Dimens.Screen.TITLE_36, getWidth(), getHeight() - Dimens.Screen.TITLE_36 - Dimens.Screen.BUTTON_ROW_1));
        entries.addTab(Text.translate("effortless.global_general_settings.title"), (button) -> {
            getEntrance().getChannel().sendPacket(new PlayerOperatorCheckPacket(getEntrance().getClient().getPlayer().getId()), (packet) -> {
                if (packet.isOperator()) {
                    getEntrance().getClient().execute(() -> {
                        new EffortlessGlobalGeneralSettingsScreen(getEntrance(), getEntrance().getSessionManager().getServerSessionConfigOrEmpty().getGlobalConfig(), config -> {
                            getEntrance().getSessionManager().updateGlobalConfig(config);
                        }).attach();
                    });
                } else {
                    getEntrance().getClient().execute(() -> {
                        new EffortlessNotAnOperatorScreen(getEntrance()).attach();
                    });
                }
            });
        });
        entries.addTab(Text.translate("effortless.per_player_general_settings.title"), (button) -> {
            getEntrance().getChannel().sendPacket(new PlayerOperatorCheckPacket(getEntrance().getClient().getPlayer().getId()), (packet) -> {
                if (packet.isOperator()) {
                    getEntrance().getClient().execute(() -> {
                        new EffortlessPerPlayerGeneralSettingsListScreen(getEntrance(), getEntrance().getSessionManager().getServerSessionConfigOrEmpty().playerConfigs(), playerConfigs -> {
                            getEntrance().getSessionManager().updatePlayerConfig(playerConfigs);
                        }).attach();
                    });
                } else {
                    getEntrance().getClient().execute(() -> {
                        new EffortlessNotAnOperatorScreen(getEntrance()).attach();
                    });
                }
            });
        });

        addWidget(Button.builder(getEntrance(), Text.translate("effortless.button.done"), button -> {
            detach();
        }).setBounds(getWidth() / 2 - Button.TAB_WIDTH / 2, getHeight() - Button.DEFAULT_HEIGHT - Button.MARGIN, Button.TAB_WIDTH, Button.DEFAULT_HEIGHT).build());

    }

    @Override
    protected EffortlessClient getEntrance() {
        return (EffortlessClient) super.getEntrance();
    }
}
