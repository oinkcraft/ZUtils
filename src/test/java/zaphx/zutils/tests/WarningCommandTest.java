package zaphx.zutils.tests;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
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
import zaphx.zutils.tests.helpers.TestInstanceCreator;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.bukkit.ChatColor.RED;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZUtils.class, PluginDescriptionFile.class, JavaPluginLoader.class, SQLHandler.class, DriverManager.class, WarningFactory.class})
@PowerMockIgnore({"javax.script.*"})
public class WarningCommandTest {

    private WarningCommand mockWarningCommand = new WarningCommand();

    @Test
    public void onCommand_testTooFewArgumentsConsole() {
        CommandSender mockSender = mock(CommandSender.class);
        when(mockSender.hasPermission(anyString())).thenReturn(true);
        Command mockCommand = mock(Command.class);


        mockWarningCommand.onCommand(mockSender, mockCommand, "warn", new String[]{});

        String expectedResult = RED + "You did not provide enough arguments to perform that command!";

        verify(mockSender, times(1)).sendMessage(expectedResult);


    }


}
