package dev.huskuraft.effortless;

import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.core.ResourceLocation;
import dev.huskuraft.effortless.api.networking.NetworkChannel;
import dev.huskuraft.effortless.api.networking.NetworkRegistry;
import dev.huskuraft.effortless.api.networking.Packet;
import dev.huskuraft.effortless.api.networking.Side;
import dev.huskuraft.effortless.networking.packets.AllPacketListener;
import dev.huskuraft.effortless.networking.packets.player.PlayerBuildPacket;
import dev.huskuraft.effortless.networking.packets.player.PlayerBuildPreviewPacket;
import dev.huskuraft.effortless.networking.packets.player.PlayerCommandPacket;
import dev.huskuraft.effortless.networking.packets.player.PlayerOperatorCheckPacket;
import dev.huskuraft.effortless.networking.packets.player.PlayerSettingsPacket;
import dev.huskuraft.effortless.networking.packets.session.SessionConfigPacket;
import dev.huskuraft.effortless.networking.packets.session.SessionPacket;

public final class EffortlessClientNetworkChannel extends NetworkChannel<AllPacketListener> {

    private static final ResourceLocation DEFAULT_CHANNEL = ResourceLocation.of(Effortless.MOD_ID, "default_channel");
    private static final int COMPATIBILITY_VERSION = Effortless.PROTOCOL_VERSION;
    private final EffortlessClient entrance;
    private final AllPacketListener listener;

    public EffortlessClientNetworkChannel(EffortlessClient entrance) {
        this(entrance, DEFAULT_CHANNEL);
    }

    public EffortlessClientNetworkChannel(EffortlessClient entrance, ResourceLocation channelId) {
        super(channelId, Side.CLIENT);
        this.entrance = entrance;
        this.listener = new ClientPacketListener();

        registerPacket(PlayerCommandPacket.class, new PlayerCommandPacket.Serializer());
        registerPacket(PlayerSettingsPacket.class, new PlayerSettingsPacket.Serializer());
        registerPacket(PlayerBuildPacket.class, new PlayerBuildPacket.Serializer());
        registerPacket(PlayerBuildPreviewPacket.class, new PlayerBuildPreviewPacket.Serializer());
        registerPacket(PlayerOperatorCheckPacket.class, new PlayerOperatorCheckPacket.Serializer());

        registerPacket(SessionPacket.class, new SessionPacket.Serializer());
        registerPacket(SessionConfigPacket.class, new SessionConfigPacket.Serializer());

        getEntrance().getEventRegistry().getRegisterNetworkEvent().register(this::onRegisterNetwork);
    }

    private void onRegisterNetwork(NetworkRegistry registry) {
        getPlatformChannel().registerClientReceiver(this);

    }

    private EffortlessClient getEntrance() {
        return entrance;
    }

    @Override
    public void receivePacket(Packet packet, Player player) {
        packet.handle(listener, player);
    }

    @Override
    public int getCompatibilityVersion() {
        return COMPATIBILITY_VERSION;
    }

    private class ClientPacketListener implements AllPacketListener {

        @Override
        public void handle(PlayerCommandPacket packet, Player player) {
            switch (packet.action()) {
                case REDO, UNDO -> {
                    // noop
                }
            }
        }

        @Override
        public void handle(PlayerBuildPacket packet, Player player) {
        }

        @Override
        public void handle(PlayerSettingsPacket packet, Player player) {
        }

        @Override
        public void handle(PlayerBuildPreviewPacket packet, Player player) {

            if (getEntrance().getClient() == null) {
                return;
            }

            if (getEntrance().getClient().getWorld() == null) {
                return;
            }

            var owner = getEntrance().getClient().getWorld().getPlayer(packet.playerId());
            if (owner == null) {
                return;
            }
            getEntrance().getStructureBuilder().onContextReceived(owner, packet.context());
        }

        @Override
        public void handle(PlayerOperatorCheckPacket packet, Player player) {

        }

        @Override
        public void handle(SessionPacket packet, Player player) {
            getEntrance().getSessionManager().onSession(packet.session(), player);
        }

        @Override
        public void handle(SessionConfigPacket packet, Player player) {
            getEntrance().getSessionManager().onSessionConfig(packet.sessionConfig(), player);

        }
    }

}
