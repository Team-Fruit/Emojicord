package net.teamfruit.emojicord;

import java.util.concurrent.TimeUnit;

import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

public class SimpleBase64UtilsTimeTest {
	static class MyJUnitStopWatch extends Stopwatch {
		private static void logInfo(final Description description, final String status, final long nanos) {
			final String testName = description.getMethodName();
			Log.log.info(String.format("Test %s %s, spent %d microseconds (= %d nanoseconds per times)",
					testName, status, TimeUnit.NANOSECONDS.toMicros(nanos),
					nanos / 10_000_000l));
		}

		@Override
		protected void succeeded(final long nanos, final Description description) {
			logInfo(description, "succeeded", nanos);
		}

		@Override
		protected void failed(final long nanos, final Throwable e, final Description description) {
			logInfo(description, "failed", nanos);
		}

		@Override
		protected void skipped(final long nanos, final AssumptionViolatedException e, final Description description) {
			logInfo(description, "skipped", nanos);
		}

		@Override
		protected void finished(final long nanos, final Description description) {
			logInfo(description, "finished", nanos);
		}
	}

	@Rule
	public MyJUnitStopWatch stopwatch = new MyJUnitStopWatch();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTime1() {
		for (long i = 0; i < 10_000_000l; i++)
			SimpleBase64Utils.encode1(i);
	}

	@Test
	public void testTime2() {
		for (long i = 0; i < 10_000_000l; i++)
			SimpleBase64Utils.encode2(i);
	}

	@Test
	public void testTime3() {
		for (long i = 0; i < 10_000_000l; i++)
			SimpleBase64Utils.encode3(i);
	}
}
