package dev.huskuraft.effortless;

import com.google.auto.service.AutoService;

import dev.huskuraft.effortless.api.events.CommonEventRegistry;
import dev.huskuraft.effortless.api.events.EventRegister;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.api.text.TextStyle;

@AutoService(Entrance.class)
public class Effortless implements Entrance {

    public static final String MOD_ID = "effortless";
    public static final int PROTOCOL_VERSION = 4;

    private final CommonEventRegistry commonEventRegistry = (CommonEventRegistry) EventRegister.getCommon();
    private final EffortlessNetworkChannel networkChannel = new EffortlessNetworkChannel(this);
    private final EffortlessStructureBuilder structureBuilder = new EffortlessStructureBuilder(this);
    private final EffortlessSessionConfigStorage sessionConfigStorage = new EffortlessSessionConfigStorage(this);
    private final EffortlessSessionManager sessionManager = new EffortlessSessionManager(this);
    private final EffortlessServerManager serverManager = new EffortlessServerManager(this);

    public static Effortless getInstance() {
        return (Effortless) Entrance.getInstance();
    }

    public CommonEventRegistry getEventRegistry() {
        return commonEventRegistry;
    }

    public EffortlessNetworkChannel getChannel() {
        return networkChannel;
    }

    public EffortlessStructureBuilder getStructureBuilder() {
        return structureBuilder;
    }

    public EffortlessSessionConfigStorage getSessionConfigStorage() {
        return sessionConfigStorage;
    }

    public EffortlessSessionManager getSessionManager() {
        return sessionManager;
    }

    public EffortlessServerManager getServerManager() {
        return serverManager;
    }

    @Override
    public String getId() {
        return MOD_ID;
    }

    public static Text getSystemMessage(Text msg) {
        var id = TextStyle.GRAY + "[" + Text.translate("effortless.name") + "]" + TextStyle.RESET + " ";
        return Text.text(id + msg.getString());
    }

}
