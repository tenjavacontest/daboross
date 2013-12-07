/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
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
import java.util.LinkedList;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class DragonCreator implements Listener {

    private final HashSet<Integer> dragons = new HashSet<>();
    private final Plugin plugin;

    public void createDragon(Location l) {
        //l = l.clone().add(l.getDirection().multiply(5))
        LivingEntity i = (LivingEntity) l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
        dragons.add(i.getEntityId());
        plugin.getServer().getScheduler().runTaskLater(plugin, new DragonRemoveClass(i), 20l);
    }

    public void onDamage(EntityDamageByEntityEvent evt) {
        if (dragons.contains(evt.getDamager().getEntityId())) {
            evt.setCancelled(true);
        }
    }

    @RequiredArgsConstructor
    private class DragonRemoveClass implements Runnable {

        private final LivingEntity i;

        @Override
        public void run() {
            dragons.remove(i.getEntityId());
            i.damage(i.getMaxHealth());
        }
    }
}
