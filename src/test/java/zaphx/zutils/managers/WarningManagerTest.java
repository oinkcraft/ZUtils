package zaphx.zutils.managers;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import zaphx.zutils.objects.ActionType;
import zaphx.zutils.ZUtils;
import zaphx.zutils.managers.helpers.TestInstanceCreator;

import java.sql.*;

import static org.bukkit.ChatColor.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.easymock.PowerMock.expectPrivate;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ZUtils.class, PluginDescriptionFile.class, JavaPluginLoader.class, SQLHandler.class, DriverManager.class, WarningFactory.class})
@PowerMockIgnore({"javax.script.*","java.sql.*"})
public class WarningManagerTest {

    private TestInstanceCreator testIntance;
    private ZUtils zUtils;
    private SQLHandler sqlHandler;

    @Before
    public void setUp() throws Exception {
        testIntance = new TestInstanceCreator();
        assertTrue(testIntance.setUp());
        Server mockServer = testIntance.getServer();
        CommandSender mockServerCommandSender = testIntance.getCommandSender();
        Plugin plugin = mockServer.getPluginManager().getPlugin("ZUtils");
        zUtils = (ZUtils) plugin;
        Connection mockConnection = mock(Connection.class);

        PowerMockito.mockStatic(DriverManager.class);
        PowerMockito.when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);


        sqlHandler = spy(new SQLHandler(mockConnection));
        zUtils.sqlHandler = sqlHandler;
    }

    @After
    public void tearDown() {
        testIntance.tearDown();
        sqlHandler.closeConnection();
    }

    @Test
    public void sendWarning_userGetsWarning() {
        System.out.println("Checking if user gets warning and sender gets result");
        // SETUP

        Connection mockConnection = mock(Connection.class);
        CommandSender mockCommandSender = mock(CommandSender.class);
        Player mockWarned = mock(Player.class);
        when(mockCommandSender.getName()).thenReturn("Zaphoo");
        when(mockWarned.getName()).thenReturn("Mobkinz78");

        // EXPECTATIONS
        String expectedMessage = GRAY + "You have been warned by Zaphoo for: " + RED + "Hacking too hard";
        String expectedResult = GRAY + "You warned Mobkinz78 for: " + RED + "Hacking too hard";

        // ACTUAL TEST
        WarningFactory warningFactory = new WarningFactory(zUtils, sqlHandler);
        warningFactory.sendWarning(mockWarned, mockCommandSender, "Hacking too hard");

        verify(mockCommandSender).sendMessage(expectedResult);
        verify(mockWarned).sendMessage(expectedMessage);
    }

    @Test
    public void autoKick_userGetsKicked() throws Exception {
        System.out.println("Checking if user gets kicked when autokick is enabled");
        // SETUP
        FileConfiguration config = mock(FileConfiguration.class);
        WarningFactory warningFactory = new WarningFactory(zUtils, sqlHandler);
        Player mockPlayer = mock(Player.class);

        when(zUtils.getConfig()).thenReturn(config);
        when(config.getBoolean("warning.autokick.enabled")).thenReturn(true);
        when(config.getInt("warning.autokick.warning-limit")).thenReturn(5);
        when(config.getString("warning.autokick.message")).thenReturn("You were automatically kicked by ZUtils for not abiding by the rules!");
        doReturn(5L).when(sqlHandler).countTickets(mockPlayer);
        // EXPECTATIONS
        String expectedKickMessage = RED + "You were automatically kicked by ZUtils for not abiding by the rules!";

        // ACTUAL TEST
        warningFactory.autoKick(mockPlayer);

        verify(mockPlayer, times(1)).kickPlayer(expectedKickMessage);
    }

    @Test
    public void hasWarningLimit_userHasMoreThanLimit() throws Exception {
        System.out.println("Testing if the user has more kicks than is needed for autokick.");
        // SETUP
        FileConfiguration config = mock(FileConfiguration.class);
        Player mockPlayer = mock(Player.class);

        WarningFactory warningFactory = spy(new WarningFactory(zUtils, sqlHandler));
        when(zUtils.getConfig()).thenReturn(config);
        when(config.getBoolean("warning.autokick.enabled")).thenReturn(true);
        //when(config.getInt("warning.autokick.warning-limit")).thenReturn(5);
        doReturn(5).when(config).getInt("warning.autokick.warning-limit");
        doReturn(9L).when(sqlHandler).countTickets(mockPlayer);
        doReturn(9L).when(warningFactory).getAmountOfWarnings(mockPlayer);

        FieldSetter.setField(warningFactory, warningFactory.getClass().getField("KICK_LIMIT"), 5);

        System.out.println("Player warnings: " + sqlHandler.countTickets(mockPlayer));
        System.out.println("Warning limit:   " + warningFactory.KICK_LIMIT);
        System.out.println("Return value:    " + warningFactory.hasWarningLimit(ActionType.KICK, mockPlayer));

        boolean result = warningFactory.hasWarningLimit(ActionType.KICK, mockPlayer);

        assertTrue(result);

        // ACTUAL TEST
    }

    @Test
    public void hasWarningLimit_userHasLessThanLimit() throws Exception {
        System.out.println("Testing if the user has less kicks than is needed for autokick.");

        // SETUP
        FileConfiguration config = mock(FileConfiguration.class);
        Player mockPlayer = mock(Player.class);
        WarningFactory warningFactory = spy(new WarningFactory(zUtils, sqlHandler));

        when(zUtils.getConfig()).thenReturn(config);
        when(config.getBoolean("warning.autokick.enabled")).thenReturn(true);
        //when(config.getInt("warning.autokick.warning-limit")).thenReturn(5);
        doReturn(5).when(config).getInt("warning.autokick.warning-limit");
        doReturn(2L).when(sqlHandler).countTickets(mockPlayer);
        doReturn(2L).when(warningFactory).getAmountOfWarnings(mockPlayer);

        // ACTUAL TEST
        System.out.println("Player warnings: " + sqlHandler.countTickets(mockPlayer));
        System.out.println("Warning limit:   " + warningFactory.KICK_LIMIT);
        System.out.println("Return value:    " + warningFactory.hasWarningLimit(ActionType.KICK, mockPlayer));
        assertFalse(warningFactory.hasWarningLimit(ActionType.KICK, mockPlayer));

    }


    @Test
    public void getInstance_setsNotNull() {
        System.out.println("Testing if instance is set");
        WarningFactory warningFactory = new WarningFactory(zUtils, sqlHandler);

        assertNotNull(warningFactory);
    }
}