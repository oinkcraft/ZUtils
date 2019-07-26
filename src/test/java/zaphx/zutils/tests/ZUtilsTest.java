package zaphx.zutils.tests;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import zaphx.zutils.ZUtils;
import zaphx.zutils.managers.SQLHandler;
import zaphx.zutils.managers.WarningFactory;
import zaphx.zutils.tests.helpers.TestInstanceCreator;

import java.sql.Connection;
import java.sql.DriverManager;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZUtils.class, PluginDescriptionFile.class, JavaPluginLoader.class, SQLHandler.class, DriverManager.class, WarningFactory.class})
@PowerMockIgnore({"javax.script.*"})
public class ZUtilsTest {

    private TestInstanceCreator testIntance;
    private ZUtils zUtils;
    private SQLHandler sqlHandler;

    @Before
    public void setUp() throws Exception {
        testIntance = new TestInstanceCreator();
        Assert.assertTrue(testIntance.setUp());
        Server mockServer = testIntance.getServer();
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
    public void instanceCreation_testInstanceNotNull() {

        assertNotNull(zUtils);
    }
}