package dev.huskuraft.effortless.api.platform;

import dev.huskuraft.effortless.api.core.Player;

import java.util.List;

public abstract class Server {

    public abstract List<Player> getPlayers();

    public abstract void execute(Runnable runnable);

}