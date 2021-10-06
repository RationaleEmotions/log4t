package com.rationaleemotions;

import java.nio.file.Path;
import org.apache.log4j.Layout;

public interface IAppenderInfo {

  Layout getLayout();

  Path getLogsDirectory();

}
