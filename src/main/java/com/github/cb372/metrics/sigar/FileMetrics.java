package com.github.cb372.metrics.sigar;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.hyperic.sigar.DirUsage;
import org.hyperic.sigar.FileInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.io.File;

/**
 * Created by zhenchuan on 6/27/14.
 */
public class FileMetrics implements CanRegisterGauges {
    private final Sigar sigar;
    private final String file;
    private final boolean isDir;
    private String prefix;

    protected FileMetrics(Sigar sigar, String file) {
        this.sigar = sigar;
        this.file = file;
        File f = new File(file);
        if (!f.exists()) {
            throw new IllegalArgumentException(file + " does not exist!");
        }
        isDir = f.isDirectory();
    }

    @Override
    public void registerGauges(MetricRegistry registry) {
        registerFileSizeGauge(registry, MetricRegistry.name(getPrefix(), "size"));
    }

    public FileMetrics setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        if (this.prefix == null) return getClass().getName();
        return this.prefix;
    }

    public void registerFileSizeGauge(MetricRegistry metricRegistry, String name) {
        metricRegistry.register(name, new Gauge<Long>() {
            @Override
            public Long getValue() {
                if (isDir) {
                    return dirSize();
                } else {
                    return fileSize();
                }

            }
        });
    }

    public long dirSize(){
        return dirSize(file);
    }

    public long fileSize(){
        return fileSize(file);
    }

    private long dirSize(String dirName) {
        try {
            DirUsage dirUsage = sigar.getDirUsage(dirName);
            if (dirUsage == null) return -1;
            return dirUsage.getDiskUsage();
        } catch (SigarException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private long fileSize(String fileName) {
        try {
            FileInfo fileInfo = sigar.getFileInfo(fileName);
            if (fileInfo == null) return -1;
            return fileInfo.getSize();
        } catch (SigarException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
