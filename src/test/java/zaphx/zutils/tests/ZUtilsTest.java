package zaphx.zutils.tests;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zaphx.zutils.ZUtils;
import zaphx.zutils.tests.helpers.TestInstanceCreator;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class ZUtilsTest {
    TestInstanceCreator testIntance;
    Server mockServer;
    CommandSender mockCommandSender;


    @Before
    public void setUp() throws Exception {
        testIntance = new TestInstanceCreator();
        assertTrue(testIntance.setUp());
        mockServer = testIntance.getServer();
        mockCommandSender = testIntance.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        testIntance.tearDown();
    }

    @Test
    public void instanceCreation_testInstanceNotNull() {
        Plugin plugin = mockServer.getPluginManager().getPlugin("ZUtils");
        ZUtils zUtils = (ZUtils) plugin;

        assertNotNull(plugin);

        assertTrue(plugin.isEnabled());


    }
}