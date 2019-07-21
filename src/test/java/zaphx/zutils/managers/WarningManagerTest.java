package zaphx.zutils.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import zaphx.zutils.ZUtils;

import javax.jnlp.SingleInstanceListener;

import static org.bukkit.ChatColor.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class WarningManagerTest {

    @Test
    public void sendWarning_userGetsWarning() {
        WarningManager warningManager = WarningManager.getInstance();
        Player player = mock(Player.class);
        CommandSender sender = mock(CommandSender.class);
        String warning = "You were hacking";

        String expectedWarning = GRAY + "You have been warned by Zaphoo for: "+ RED +"You were hacking";
        String expectedResult = GRAY + "You warned Hackerman for: "+ RED +"You were hacking";

        when(sender.getName()).thenReturn("Zaphoo");
        when(player.getName()).thenReturn("Hackerman");

        warningManager.sendWarning(player, sender, warning);

        verify(player, times(1)).sendMessage(expectedWarning);
        verify(sender, times(1)).sendMessage(expectedResult);

    }

    @Test
    public void checkAutoKick_userGetsKicked() {
        ZUtils zUtils = mock()

    }

    @Test
    public void getInstance_setsNotNull() {
        WarningManager warningManager = WarningManager.getInstance();

        assertNotNull(warningManager);
    }
}