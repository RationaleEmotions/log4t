package com.rationaleemotions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * This logger utility is meant to be invoked ONLY from within a TestNG <code>@Test</code> method or
 * from within a TestNG <code>@BeforeXXX</code>/<code>@AfterXXX</code> configuration method. This
 * utility helps capture all the log messages generated by a method into its only log file.
 * <p>
 * Typically, useful when attempting to generate per test logs.
 * <p>
 * The log file location is available as an attribute in the <code>ITestResult</code> object with
 * its key set as <code>log4t.logs</code>
 */
public final class Log4T {

  private static final IAppenderInfo appenderInfo = getAppenderInfo();
  private static final Set<String> appenderInitialisedLoggers = ConcurrentHashMap.newKeySet();

  private Log4T() {
    //Utility class. Hide constructor and defeat instantiation.
  }

  /**
   * @return - A {@link Logger} for the current TestNG method (Either a test or a configuration
   * method).
   */
  public static Logger getLogger() {
    ITestResult current = Reporter.getCurrentTestResult();
    return getLogger(current);
  }

  private static Logger getLogger(ITestResult current) {
    Objects.requireNonNull(current,
        "A valid logger is available ONLY from within a TestNG Test or Config method");
    String methodName = current.getMethod().getQualifiedName();
    if (!appenderInitialisedLoggers.contains(methodName)) {
      String fileName =
          appenderInfo.getLogsDirectory().toFile().getAbsolutePath() + "/" + methodName;
      try {
        FileAppender appender = new FileAppender(appenderInfo.getLayout(), fileName, false);
        LogManager.getLogger(methodName).addAppender(appender);
        appenderInitialisedLoggers.add(methodName);
        current.setAttribute("log4t.logs", fileName);
      } catch (IOException e) {
        Logger.getRootLogger().error("Unable to add an appender to " + methodName, e);
      }
    }
    return LogManager.getLogger(methodName);
  }

  private static IAppenderInfo getAppenderInfo() {
    Iterator<IAppenderInfo> iterator = ServiceLoader.load(IAppenderInfo.class).iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return new IAppenderInfo() {
      @Override
      public Layout getLayout() {
        return new PatternLayout("%d %p [%t] %c - %m%n");
      }

      @Override
      public Path getLogsDirectory() {
        Path path = Paths.get("logs");
        path.toFile().mkdirs();
        return path;
      }
    };
  }

}
