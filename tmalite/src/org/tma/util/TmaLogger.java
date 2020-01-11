package org.tma.util;

import org.tinylog.Level;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

public class TmaLogger {
	
	private static final int STACKTRACE_DEPTH = 2;

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();
	
	// @formatter:off
		private static final boolean MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(Level.TRACE);
		private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(Level.DEBUG);
		private static final boolean MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(Level.INFO);
		private static final boolean MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(Level.WARN);
		private static final boolean MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(Level.ERROR);
		// @formatter:on
	
	public static TmaLogger getLogger() {
		return new TmaLogger();
	}

	public void debug(String message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, message, (Object[]) null);
		}
	}

	public void debug(String message, Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, message, arguments);
		}
	}
	
	private static boolean isCoveredByMinimumLevel(final Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

	public void error(String message, Throwable e) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, e, message, (Object[]) null);
		}
	}

	public void error(String message, Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, message, arguments);
		}
		
	}

	public void info(String message, Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, message, arguments);
		}
	}
	
	public void trace(String message, Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, message, arguments);
		}
	}
	
	public void warn(String message, Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, message, arguments);
		}
	}

}
