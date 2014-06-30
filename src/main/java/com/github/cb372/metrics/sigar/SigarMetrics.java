package com.github.cb372.metrics.sigar;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Ps;

import java.util.ArrayList;
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

    private final List<FileMetrics> fileMetricsList = new ArrayList<FileMetrics>(5);
    private final List<ProcessMetrics> processMetricsList = new ArrayList<ProcessMetrics>(5);

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

        for (FileMetrics fileMetrics : fileMetricsList) {
            fileMetrics.registerGauges(registry);
        }

        for (ProcessMetrics processMetrics : processMetricsList) {
            processMetrics.registerGauges(registry);
        }
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

    public ProcessMetrics processMetrics(long pid) {
        ProcessMetrics processMetrics = new ProcessMetrics(sigar, pid);
        processMetricsList.add(processMetrics);
        return processMetrics;
    }

    public ProcessMetrics processMetrics(String processNameFlag) {
        long pid = pid(processNameFlag);
        if (pid == -1) {
            throw new IllegalArgumentException("can't find the process with your process name's flag : [" + processNameFlag + "]");
        }
        return processMetrics(pid);
    }

    public FileMetrics fileMetrics(String absoluteFilePath) {
        FileMetrics fileMetrics = new FileMetrics(sigar, absoluteFilePath);
        fileMetricsList.add(fileMetrics);
        return fileMetrics;
    }

    public long pid(String processNameFlag) {
        long foundPid = -1;
        List<String> foundProcessNames = new ArrayList<String>();
        try {
            Ps ps = new Ps();
            long[] pids = sigar.getProcList();
            for (long pid : pids) {
                List<String> info = ps.getInfo(sigar, pid);
                if (info.size() > 8) {
                    String name = info.get(8);
                    if (name.contains(processNameFlag)) {
                        foundPid = pid;
                        foundProcessNames.add(name);
                    }
                }
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
        if (foundProcessNames.size() > 1) {
            throw new IllegalArgumentException("find more than one process with your provided process name's flag : [" + processNameFlag + "].\tfound processes :\n" +
                    foundProcessNames);
        }
        return foundPid;
    }

}
