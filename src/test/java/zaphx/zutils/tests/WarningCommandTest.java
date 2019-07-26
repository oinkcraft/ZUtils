package zaphx.zutils.tests;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import zaphx.zutils.ZUtils;
import zaphx.zutils.commands.WarningCommand;
import zaphx.zutils.managers.SQLHandler;
import zaphx.zutils.managers.WarningFactory;
import zaphx.zutils.objects.UUIDFetcher;
import zaphx.zutils.tests.helpers.TestInstanceCreator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZUtils.class, PluginDescriptionFile.class, JavaPluginLoader.class, SQLHandler.class, DriverManager.class, WarningFactory.class})
@PowerMockIgnore({"javax.script.*"})
public class WarningCommandTest {

    private TestInstanceCreator testIntance;
    private ZUtils zUtils;
    private Server mockServer;
    private SQLHandler sqlHandler;
    private WarningCommand mockWarningCommand;

    @Before
    public void setUp() throws Exception {
        testIntance = new TestInstanceCreator();
        assertTrue(testIntance.setUp());
        mockServer = testIntance.getServer();
        Plugin plugin = mockServer.getPluginManager().getPlugin("ZUtils");
        zUtils = (ZUtils) plugin;
        Connection mockConnection = mock(Connection.class);

        PowerMockito.mockStatic(DriverManager.class);
        PowerMockito.when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);


        sqlHandler = spy(new SQLHandler(mockConnection));
        zUtils.sqlHandler = sqlHandler;
        mockWarningCommand = new WarningCommand(zUtils);
    }

    @After
    public void tearDown() {
        testIntance.tearDown();
        sqlHandler.closeConnection();
    }


    @Test
    public void onCommand_testTooFewArguments() {
        System.out.println("Checking if warning command rejects if there are no arguments");
        // Set up
        CommandSender mockSender = mock(CommandSender.class);
        when(mockSender.hasPermission(anyString())).thenReturn(true);
        Command mockCommand = mock(Command.class);
        String expectedResult = RED + "You did not provide enough arguments to perform that command!";

        // Act
        mockWarningCommand.onCommand(mockSender, mockCommand, "warn", new String[]{});

        // Verify
        verify(mockSender, times(1)).sendMessage(expectedResult);
    }

    @Test
    public void onCommand_testNoPermission() {
        System.out.println("Checking if warning command rejects if sender does not have permission to perform the command");
        // Set up
        CommandSender mockSender = mock(CommandSender.class);
        when(mockSender.hasPermission(anyString())).thenReturn(false);
        Command mockCommand = mock(Command.class);
        String expectedResult = RED + "You do not have permission to perform that command!";

        // Act
        mockWarningCommand.onCommand(mockSender, mockCommand, "warn", new String[]{"Lorem", "Ipsum", "Dolor", "Sit", "Amet"});

        // Verify
        verify(mockSender, times(1)).sendMessage(expectedResult);
    }

    @Test
    public void onCommand_testIfWarningIsSuccessfullForOnlinePlayer() {
        System.out.println("Checking if warning command succeeds when every criteria is met");
        // Set up
        CommandSender mockSender = mock(CommandSender.class);
        Command mockCommand = mock(Command.class);
        OfflinePlayer mockTarget = mock(OfflinePlayer.class);
        Player mockOnlineTarget = mock(Player.class);

        UUID targetUUID = UUIDFetcher.getUUID("Mobkinz78");

        when(mockSender.hasPermission(anyString())).thenReturn(true);
        when(mockSender.getName()).thenReturn("Zaphoo");

        when(mockTarget.getName()).thenReturn("Mobkinz78");
        when(mockTarget.hasPlayedBefore()).thenReturn(true);
        when(mockTarget.isOnline()).thenReturn(true);
        when(mockTarget.getUniqueId()).thenReturn(targetUUID);

        when(mockTarget.getPlayer()).thenReturn(mockOnlineTarget);

        when(mockServer.getPlayer("Mobkinz78")).thenReturn(mockOnlineTarget);
        when(mockServer.getPlayer(mockTarget.getUniqueId())).thenReturn(mockOnlineTarget);
        when(zUtils.getServer().getOfflinePlayer(mockTarget.getUniqueId())).thenReturn(mockTarget);

        String expectedTargetResult = GRAY + "You have been warned by Zaphoo for: " + RED + "Lorem Ipsum Dolor Sit Amet";
        String expectedSenderResult = GRAY + "You have warned Mobkinz78 for: " + RED + "Lorem Ipsum Dolor Sit Amet";

        mockWarningCommand.onCommand(mockSender, mockCommand, "warn", new String[]{mockTarget.getName(), "Lorem", "Ipsum", "Dolor", "Sit", "Amet"});

        verify(mockOnlineTarget).sendMessage(expectedTargetResult);
    }
    @Test
    public void onCommand_testIfWarningIsSuccessfullForOfflinePlayer() {
        System.out.println("Checking if warning command succeeds when every criteria is met but player is offlinegit ");
        // Set up
        CommandSender mockSender = mock(CommandSender.class);
        Command mockCommand = mock(Command.class);
        OfflinePlayer mockTarget = mock(OfflinePlayer.class);

        UUID targetUUID = UUIDFetcher.getUUID("Mobkinz78");

        when(mockSender.hasPermission(anyString())).thenReturn(true);
        when(mockSender.getName()).thenReturn("Zaphoo");

        when(mockTarget.getName()).thenReturn("Mobkinz78");
        when(mockTarget.hasPlayedBefore()).thenReturn(true);
        when(mockTarget.isOnline()).thenReturn(false);
        when(mockTarget.getUniqueId()).thenReturn(targetUUID);

        when(zUtils.getServer().getOfflinePlayer(mockTarget.getUniqueId())).thenReturn(mockTarget);

        String expectedSenderResult = GRAY + "You warned Mobkinz78 for: " + RED + "Lorem Ipsum Dolor Sit Amet";

        mockWarningCommand.onCommand(mockSender, mockCommand, "warn", new String[]{mockTarget.getName(), "Lorem", "Ipsum", "Dolor", "Sit", "Amet"});

        verify(mockSender).sendMessage(expectedSenderResult);
    }


}
