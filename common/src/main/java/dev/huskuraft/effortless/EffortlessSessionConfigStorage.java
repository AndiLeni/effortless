package dev.huskuraft.effortless;

import dev.huskuraft.effortless.api.file.ConfigFileStorage;
import dev.huskuraft.effortless.api.file.FileType;
import dev.huskuraft.effortless.session.config.SessionConfig;
import dev.huskuraft.effortless.session.config.serializer.SessionConfigSerializer;

public final class EffortlessSessionConfigStorage extends ConfigFileStorage<SessionConfig> {

    private static final String CONFIG_NAME = "effortless.toml";

    public EffortlessSessionConfigStorage(Effortless entrance) {
        super(CONFIG_NAME, FileType.TOML, new SessionConfigSerializer());
    }

}
