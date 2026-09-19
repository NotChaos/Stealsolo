package fun.stealsolo.util;

import org.bukkit.entity.Player;

public record BattleRequest(Player sender, Player target) {
}
