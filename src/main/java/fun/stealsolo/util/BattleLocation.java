package fun.stealsolo.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public record BattleLocation(Location spawnPoint, boolean active, Player firstPlayer, Player secondPlayer) {
}
