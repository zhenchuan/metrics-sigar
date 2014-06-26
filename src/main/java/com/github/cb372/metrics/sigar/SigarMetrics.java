package com.github.cb372.metrics.sigar;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Ps;

import java.util.List;

public class SigarMetrics implements CanRegisterGauges {
    private static final SigarMetrics instance = new SigarMetrics();

    public static SigarMetrics getInstance() {
        return instance;
    }

    private final Sigar sigar = new Sigar();
    private final CpuMetrics cpu = new CpuMetrics(sigar); 
    private final MemoryMetrics memory = new MemoryMetrics(sigar); 
    private final FilesystemMetrics fs = new FilesystemMetrics(sigar); 
    private final UlimitMetrics ulimit = new UlimitMetrics(sigar);

    private SigarMetrics() {
        // singleton
    }

    public void registerGauges(MetricRegistry registry) {
        registry.register(MetricRegistry.name(getClass(), "pid"), new Gauge<Long>() {
          public Long getValue() {
            return pid();
          }
        });     

        cpu.registerGauges(registry);
        memory.registerGauges(registry);
        fs.registerGauges(registry);
        ulimit.registerGauges(registry);
    }

    public long pid() {
        return sigar.getPid();
    }

    public CpuMetrics cpu() {
        return cpu;
    }

    public MemoryMetrics memory() {
        return memory;
    }

    public FilesystemMetrics filesystems() {
        return fs;
    }

    public UlimitMetrics ulimit() {
        return ulimit;
    }

    public ProcessMetrics processMetrics(long pid){
         return new ProcessMetrics(sigar,pid);
    }

    public ProcessMetrics processMetrics(String flag){
        long pid = pid(flag);
        if(pid == -1){
            throw new IllegalArgumentException("can't find the process with your process flag : " + flag);
        }
        return new ProcessMetrics(sigar,pid);
    }

    public FileMetrics fileMetrics(String absoluteFilePath){
        return new FileMetrics(sigar,absoluteFilePath);
    }

    public long pid(String flag){
        try {
            Ps ps = new Ps();
            long[] pids = sigar.getProcList();
            for (long pid : pids) {
                List<String> info = ps.getInfo(sigar,pid);
                if(info.size()>8){
                    String name = info.get(8);
                    if(name.contains(flag)){
                        return pid;
                    }
                }
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
