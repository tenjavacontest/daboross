/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.tenjava;

import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class DragonCreator implements Listener {

    private final HashSet<Integer> dragons = new HashSet<>();
    private final Plugin plugin;

    public void createDragon(Location target) {
        Vector direction = target.getDirection().multiply(5);
        Location from = target.clone().add(target.getDirection().multiply(5));
        LivingEntity i = (LivingEntity) target.getWorld().spawnEntity(from, EntityType.ENDER_DRAGON);
        dragons.add(i.getEntityId());
        DragonRun run = new DragonRun(i, direction.clone().multiply(-0.01), target);
        run.task = plugin.getServer().getScheduler().runTaskTimer(plugin, new DragonRun(i, direction.clone().multiply(-0.01), target), 1, 1);
    }

    public void onDamage(EntityDamageByEntityEvent evt) {
        if (dragons.contains(evt.getDamager().getEntityId())) {
            evt.setCancelled(true);
        }
    }

    @RequiredArgsConstructor
    private class DragonRun implements Runnable {

        private final LivingEntity dragon;
        private final Vector direction;
        private final Location target;
        private BukkitTask task;

        @Override
        public void run() {
            dragon.teleport(dragon.getLocation().add(direction));
            if (isWithin(dragon.getLocation(), target, 1)) {
                dragons.remove(dragon.getEntityId());
                dragon.damage(dragon.getMaxHealth());
                task.cancel();
            }
        }
    }

    private boolean isWithin(Location l1, Location l2, double diff) {
        return (l1.getX() + diff > l2.getX() || l2.getX() + diff > l1.getX())
                && (l1.getY() + diff > l2.getY() || l2.getY() + diff > l1.getY())
                && (l1.getZ() + diff > l2.getZ() || l2.getZ() + diff > l1.getZ());
    }
}
