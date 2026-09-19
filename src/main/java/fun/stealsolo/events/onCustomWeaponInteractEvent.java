package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.commands.AddCustomEnchantCommand;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class onCustomWeaponInteractEvent implements Listener {

    private static final Map<String, Long> COOLDOWNS = new ConcurrentHashMap<>();

    @EventHandler
    public void CustomWeaponInteractEvent(PlayerInteractEvent e) {
        if (!Stealsolo.isCurrencyEnabled()) return;

        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        ItemStack itemStack = e.getItem();

        NamespacedKey customKey = new NamespacedKey(Stealsolo.getPlugin(), DuckAPI.getPersistentDataKey() + ".CustomEnchant");
        NamespacedKey setbonusKey = new NamespacedKey(Stealsolo.getPlugin(), DuckAPI.getPersistentDataKey() + ".SetbonusEnchant");

        if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getItemMeta() != null) {
            PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
            String raw = pdc.get(customKey, PersistentDataType.STRING);
            boolean isSetbonus = false;

            if (raw == null) {
                raw = pdc.get(setbonusKey, PersistentDataType.STRING);
                isSetbonus = raw != null;
            }

            if (raw != null && !raw.isEmpty()) {
                if (!isSetbonus || allArmorPiecesHaveCustomEnchant(player, customKey, setbonusKey)) {
                    applyEnchantEffect(player, raw, "hand");
                }
            }
        }

        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = new ItemStack[] {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        String[] armorSlots = new String[] {"helmet", "chestplate", "leggings", "boots"};
        for (int i = 0; i < armor.length; i++) {
            ItemStack armorPiece = armor[i];
            String slotName = armorSlots[i];

            if (armorPiece == null || armorPiece.getType() == Material.AIR || !armorPiece.hasItemMeta()) {
                continue;
            }

            PersistentDataContainer pdc = armorPiece.getItemMeta().getPersistentDataContainer();
            String raw = pdc.get(customKey, PersistentDataType.STRING);
            boolean isSetbonus = false;

            if (raw == null) {
                raw = pdc.get(setbonusKey, PersistentDataType.STRING);
                isSetbonus = raw != null;
            }

            if (raw != null && !raw.isEmpty()) {
                if (!isSetbonus || allArmorPiecesHaveCustomEnchant(player, customKey, setbonusKey)) {
                    applyEnchantEffect(player, raw, slotName);
                }
            }
        }
    }

    private void applyEnchantEffect(Player player, String raw, String sourceId) {
        String[] split = raw.split(";", 2);
        if (split.length < 2) return;

        AddCustomEnchantCommand.CustomEnchantType type;
        try {
            type = AddCustomEnchantCommand.CustomEnchantType.valueOf(split[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return;
        }

        String value = split[1].trim();
        if (value.isEmpty()) return;

        switch (type) {
            case KNOCKBACK -> handleKnockback(player, value, sourceId);
            case DASH -> handleDash(player, value, sourceId);
            case EFFECT -> handleEffect(player, value, sourceId);
        }
    }

    private boolean allArmorPiecesHaveCustomEnchant(Player player, NamespacedKey customKey, NamespacedKey setbonusKey) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = new ItemStack[] {
                inv.getHelmet(),
                inv.getChestplate(),
                inv.getLeggings(),
                inv.getBoots()
        };

        for (ItemStack piece : armor) {
            if (piece == null || piece.getType() == Material.AIR || !piece.hasItemMeta()) {
                return false;
            }

            PersistentDataContainer pdc = piece.getItemMeta().getPersistentDataContainer();
            String custom = pdc.get(customKey, PersistentDataType.STRING);
            String setbonus = pdc.get(setbonusKey, PersistentDataType.STRING);

            if ((custom == null || custom.isBlank()) && (setbonus == null || setbonus.isBlank())) {
                return false;
            }
        }

        return true;
    }

    private void handleKnockback(Player player, String value, String sourceId) {
        double radius;
        try {
            radius = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return;
        }
        if (radius <= 0) return;

        FileConfiguration cfg = Stealsolo.getPlugin().getConfig();
        double power = cfg.getDouble("customenchant.knockback.power", 1.2D);
        long cooldownMs = (long) (cfg.getDouble("customenchant.knockback.cooldown-seconds", 3.0D) * 1000L);

        if (isOnCooldown(player, "KNOCKBACK", sourceId, cooldownMs)) return;
        setCooldown(player, "KNOCKBACK", sourceId);

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.isInvulnerable()) continue;
            if (target.getUniqueId().equals(player.getUniqueId())) continue;
            if (!target.getWorld().equals(player.getWorld())) continue;
            if (target.getLocation().distanceSquared(player.getLocation()) > (radius * radius)) continue;

            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            Location loc = BukkitAdapter.adapt(player.getLocation());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(loc);

            if (!set.testState(localPlayer, Stealsolo.getKnockbackFlag())) {
                return;
            }

            Vector dir = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            dir.setY(0.35);
            target.setVelocity(dir.multiply(power));
        }

        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 18, 0.6, 0.4, 0.6, 0.02);
    }

    private void handleDash(Player player, String value, String sourceId) {
        double strength;
        try {
            strength = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return;
        }
        if (strength <= 0) return;

        FileConfiguration cfg = Stealsolo.getPlugin().getConfig();
        long cooldownMs = (long) (cfg.getDouble("customenchant.dash.cooldown-seconds", 2.5D) * 1000L);

        if (isOnCooldown(player, "DASH", sourceId, cooldownMs)) return;
        setCooldown(player, "DASH", sourceId);

        Vector velocity = player.getLocation().getDirection().normalize().multiply(strength);

        if (player.isOnGround()) {
            velocity.setY(Math.max(velocity.getY(), 0.15D));
        }

        player.setVelocity(velocity);
        player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1.0F, 1.0F);
    }

    private void handleEffect(Player player, String value, String sourceId) {
        PotionEffectType effectType = PotionEffectType.getByName(value.toUpperCase(Locale.ROOT));
        if (effectType == null) return;

        FileConfiguration cfg = Stealsolo.getPlugin().getConfig();
        int durationSeconds = cfg.getInt("customenchant.effect.duration-seconds", 5);
        int amplifier = cfg.getInt("customenchant.effect.amplifier", 0);
        long cooldownMs = (long) (cfg.getDouble("customenchant.effect.cooldown-seconds", 8.0D) * 1000L);

        if (durationSeconds <= 0) return;
        if (amplifier < 0) amplifier = 0;

        if (isOnCooldown(player, "EFFECT", sourceId, cooldownMs)) return;
        setCooldown(player, "EFFECT", sourceId);

        player.addPotionEffect(new PotionEffect(effectType, durationSeconds * 20, amplifier, true, true, true));
    }

    private boolean isOnCooldown(Player player, String enchant, String sourceId, long cooldownMs) {
        String key = player.getUniqueId() + ":" + enchant + ":" + sourceId;
        long now = System.currentTimeMillis();
        Long last = COOLDOWNS.get(key);
        return last != null && (now - last) < cooldownMs;
    }

    private void setCooldown(Player player, String enchant, String sourceId) {
        COOLDOWNS.put(player.getUniqueId() + ":" + enchant + ":" + sourceId, System.currentTimeMillis());
    }
}