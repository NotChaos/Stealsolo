package fun.stealsolo.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fun.stealsolo.Stealsolo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onDamageEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            Location playerLocation = player.getLocation();

            com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(playerLocation);
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(loc);

            set.forEach(region -> {
               if (region.getFlag(Stealsolo.getDamageMultiplierFlag()) == null) {
                   return;
               }

               Double damageMultiplier = region.getFlag(Stealsolo.getDamageMultiplierFlag());
               if (damageMultiplier == null || damageMultiplier <= 0) {
                   return;
               }

               event.setDamage(event.getDamage() * damageMultiplier);
            });
        }
    }
}