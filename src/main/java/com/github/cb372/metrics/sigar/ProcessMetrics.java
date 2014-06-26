package com.github.cb372.metrics.sigar;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by zhenchuan on 6/26/14.
 */
public class ProcessMetrics implements CanRegisterGauges{

    private final Sigar sigar;
    private final long pid;

    protected ProcessMetrics(Sigar sigar, long pid) {
        this.sigar = sigar;
        this.pid = pid;
    }

    public void registerGauges(MetricRegistry registry) {
        registerResidentMem(registry,MetricRegistry.name(getClass(), "mem"));
        //registerShareMem(registry,MetricRegistry.name(getClass(), "share-mem"));
        //registerSysCpu(registry,MetricRegistry.name(getClass(), "sys-cpu"));
        //registerTotalCpu(registry,MetricRegistry.name(getClass(), "total-cpu"));
        //registerUserCpu(registry,MetricRegistry.name(getClass(), "usr-cpu"));
        registerPercentCpu(registry, MetricRegistry.name(getClass(), "cpu"));
    }

    public void registerPercentCpu(MetricRegistry registry,String name){
        registry.register(name,new Gauge<Double>() {
            @Override
            public Double getValue() {
                return percentCpu();
            }
        }) ;
    }



    public void registerUserCpu(MetricRegistry registry,String name){
        registry.register(name,new Gauge<Long>() {
            @Override
            public Long getValue() {
                return userCpu();
            }
        }) ;
    }

    public void registerSysCpu(MetricRegistry registry,String name){
        registry.register(name,new Gauge<Long>() {
            @Override
            public Long getValue() {
                return sysCpu();
            }
        }) ;
    }



    public void registerTotalCpu(MetricRegistry registry,String name){
        registry.register(name,new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalCpu();
            }
        }) ;
    }



    public void registerResidentMem(MetricRegistry registry,String name){
        registry.register(name,new Gauge<Long>() {
            @Override
            public Long getValue() {
                return residentMem();
            }
        }) ;
    }



    public void registerShareMem(MetricRegistry registry,String name){
        registry.register(name,new Gauge<Long>() {
            @Override
            public Long getValue() {
                return shareMem();
            }
        }) ;
    }

    private Long shareMem() {
        ProcMem mem = mem();
        if(mem!=null)return mem.getShare();
        return -1l;
    }

    private Long residentMem() {
        ProcMem mem = mem();
        if(mem!=null)return mem.getResident();
        return -1l;
    }

    private double percentCpu() {
        ProcCpu cpu = cpu();
        if(cpu!=null){
            BigDecimal b = new BigDecimal(cpu.getPercent());
            return b.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return -1l;
    }

    private Long userCpu() {
        ProcCpu cpu = cpu();
        if(cpu!=null)return cpu.getUser();
        return -1l;
    }

    private Long sysCpu() {
        ProcCpu cpu = cpu();
        if(cpu!=null)return cpu.getSys();
        return -1l;
    }

    private Long totalCpu() {
        ProcCpu cpu = cpu();
        if(cpu!=null)return cpu.getTotal();
        return -1l;
    }

    private ProcMem mem(){
        try {
            return sigar.getProcMem(pid);
        } catch (SigarException e) {
            return null;
        }
    }

    private ProcCpu cpu(){
        try {
            return sigar.getProcCpu(pid);
        } catch (SigarException e) {
            return null;
        }
    }

}
