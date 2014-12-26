package com.github.cb372.metrics.sigar;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Ps;
import org.hyperic.sigar.ptql.ProcessFinder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhenchuan on 6/26/14.
 */
public class ProcessMetrics implements CanRegisterGauges {

    private final Sigar sigar;

    private final String flag;
    private String prefix;

    protected ProcessMetrics(Sigar sigar, String flag) {
        this.sigar = sigar;
        this.flag = flag;
    }

    public void registerGauges(MetricRegistry registry) {
        registerResidentMem(registry, MetricRegistry.name(getPrefix(), "mem"));
        registerShareMem(registry,MetricRegistry.name(getPrefix(), "share-mem"));
        registerSysCpu(registry,MetricRegistry.name(getPrefix(), "sys-cpu"));
        registerTotalCpu(registry,MetricRegistry.name(getPrefix(), "total-cpu"));
        registerUserCpu(registry,MetricRegistry.name(getPrefix(), "usr-cpu"));
        registerPercentCpu(registry, MetricRegistry.name(getPrefix(), "cpu-usage"));
    }

    public ProcessMetrics setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        if (this.prefix == null) return getClass().getName();
        return this.prefix;
    }

    public void registerPercentCpu(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Double>() {
            @Override
            public Double getValue() {
                return percentCpu();
            }
        });
    }


    public void registerUserCpu(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return userCpu();
            }
        });
    }

    public void registerSysCpu(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return sysCpu();
            }
        });
    }


    public void registerTotalCpu(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalCpu();
            }
        });
    }


    public void registerResidentMem(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return residentMem();
            }
        });
    }


    public void registerShareMem(MetricRegistry registry, String name) {
        registry.register(name, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return shareMem();
            }
        });
    }

    public Long shareMem() {
        ProcMem mem = mem();
        if (mem != null) return mem.getShare();
        return -1l;
    }

    public Long residentMem() {
        ProcMem mem = mem();
        if (mem != null) return mem.getResident();
        return -1l;
    }

    public double percentCpu() {
        ProcCpu cpu = cpu();
        if (cpu != null) {
            BigDecimal b = new BigDecimal(cpu.getPercent());
            return b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return -1l;
    }

    public Long userCpu() {
        ProcCpu cpu = cpu();
        if (cpu != null) return cpu.getUser();
        return -1l;
    }

    public Long sysCpu() {
        ProcCpu cpu = cpu();
        if (cpu != null) return cpu.getSys();
        return -1l;
    }

    public Long totalCpu() {
        ProcCpu cpu = cpu();
        if (cpu != null) return cpu.getTotal();
        return -1l;
    }

    public ProcMem mem() {
        try {
            return sigar.getProcMem(SigarMetrics.getInstance().pid(flag));
        } catch (SigarException e) {
            return null;
        }
    }

    public ProcCpu cpu() {
        try {
            return sigar.getProcCpu(SigarMetrics.getInstance().pid(flag));
        } catch (SigarException e) {
            return null;
        }
    }

}
