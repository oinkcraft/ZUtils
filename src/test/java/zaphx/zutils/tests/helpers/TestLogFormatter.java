/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 ******************************************************************************/

package zaphx.zutils.tests.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class is a modified version of the Multiverse-Core class 'TestInstanceCreator.java' which is licensed under the BSD.
 * For more information, go to <a href="https://github.com/Multiverse/Multiverse-Core">Multiverse-Core GitHub page</a>.
 */

public class TestLogFormatter extends Formatter {
    private static final DateFormat df = new SimpleDateFormat("HH:mm:ss");

    public  String format(LogRecord record) {
        StringBuilder ret = new StringBuilder();

        ret.append("[").append(df.format(record.getMillis())).append("] [")
                .append(record.getLoggerName()).append("] [")
                .append(record.getLevel().getLocalizedName()).append("] ");
        ret.append(record.getMessage());
        ret.append('\n');

        if (record.getThrown() != null) {
            // An Exception was thrown! Let's print the StackTrace!
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            ret.append(writer);
        }

        return ret.toString();
    }
}
