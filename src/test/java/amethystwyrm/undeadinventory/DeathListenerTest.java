package amethystwyrm.undeadinventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeathListenerTest {

    private DeathListener listener;

    @BeforeEach
    public void setUp() {
        listener = new DeathListener();
    }

    @Test
    public void testOnPlayerDeath_KillerIsNull() {
        PlayerDeathEvent event = mock(PlayerDeathEvent.class);
        Player player = mock(Player.class);
        when(event.getEntity()).thenReturn(player);
        when(player.getKiller()).thenReturn(null);
        // Should do nothing, no exception
        listener.onPlayerDeath(event);
    }


    @Test
    public void testOnEntityDeath_NotKillerMob() {
        EntityDeathEvent event = mock(EntityDeathEvent.class);
        LivingEntity entity = mock(LivingEntity.class);
        when(event.getEntity()).thenReturn(entity);
    when(entity.isCustomNameVisible()).thenReturn(false);
        listener.onEntityDeath(event); // Should do nothing
    }
}
