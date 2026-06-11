package fun.stealsolo.util;

import org.bukkit.Particle;

public record ConfigParticle(Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
}
