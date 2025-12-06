package fun.stealsolo.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public record KillEntry(@NotNull UUID killerUUID, @NotNull UUID killedUUID, @NotNull Timestamp timestamp) {
}
