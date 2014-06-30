package com.github.cb372.metrics.sigar;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.hyperic.sigar.Sigar;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SigarMetricsTest extends CheckSigarLoadsOk {

    @Test
    public void pidIsGreaterThanZero() throws Exception {
        assertThat(SigarMetrics.getInstance().pid(), is(greaterThan(0L)));
    }

    @Test
    public void canFindThePidOfJavaProcess(){
        assertTrue(SigarMetrics.getInstance().pid("java")>0);
    }

    @Test
    public void testConsoleOut() throws InterruptedException {
        final MetricRegistry registry = new MetricRegistry();
        FileMetrics fileMetrics = SigarMetrics.getInstance().fileMetrics("/tmp") ;
        fileMetrics.setPrefix("leveldb.data")  ;
        ProcessMetrics metrics = SigarMetrics.getInstance().processMetrics("java");
        metrics.setPrefix("tomcat.7200");
        metrics.registerGauges(registry);
        fileMetrics.registerGauges(registry);
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(10,TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(60);

    }


}
