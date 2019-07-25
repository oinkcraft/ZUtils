/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 ******************************************************************************/

package zaphx.zutils.tests.helpers;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * This class is a modified version of the Multiverse-Core class 'TestInstanceCreator.java' which is licensed under the BSD.
 * For more information, go to <a href="https://github.com/Multiverse/Multiverse-Core">Multiverse-Core GitHub page</a>.
 */

public class Util {
    private Util() {
    }

    public static final Logger logger = Logger.getLogger("ZUtils-Test");

    static {
        logger.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new TestLogFormatter());
        Handler[] handlers = logger.getHandlers();

        for (Handler h : handlers)
            logger.removeHandler(h);

        logger.addHandler(handler);
    }

    public static void log(Throwable t) {
        log(Level.WARNING, t.getLocalizedMessage(), t);
    }

    public static void log(Level level, Throwable t) {
        log(level, t.getLocalizedMessage(), t);
    }

    public static void log(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    public static void log(Level level, String message, Throwable t) {
        LogRecord record = new LogRecord(level, message);
        record.setThrown(t);
        logger.log(record);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static  boolean deleteFolder(File file) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(file);
            return true;
        } catch (IOException e) {
            logger.warning(e.getMessage());
            return false;
        }
    }

}

