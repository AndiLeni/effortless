package dev.huskuraft.effortless;

import dev.huskuraft.effortless.api.networking.Channel;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.building.StructureBuilder;
import dev.huskuraft.effortless.config.ConfigManager;
import dev.huskuraft.effortless.networking.packets.AllPacketListener;

public abstract class Effortless implements Entrance {

    public static final String MOD_ID = "effortless";
    public static final int VERSION_NUMBER = 2;

    private final EffortlessEventsRegistry registry;
    private final EffortlessServerChannel channel;
    private final EffortlessServerStructureBuilder structureBuilder;

    protected Effortless() {
        Instance.set(this);

        this.registry = new EffortlessEventsRegistry();
        this.channel = new EffortlessServerChannel(this);
        this.structureBuilder = new EffortlessServerStructureBuilder(this);
    }

    public Channel<AllPacketListener> getChannel() {
        return channel;
    }

    public EffortlessEventsRegistry getEventRegistry() {
        return registry;
    }

    public StructureBuilder getStructureBuilder() {
        return structureBuilder;
    }

    public ConfigManager getConfigManager() {
        return null;
    }

    @Override
    public String getId() {
        return MOD_ID;
    }

}
