package com.github.cb372.metrics.sigar;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.hyperic.sigar.DirUsage;
import org.hyperic.sigar.FileInfo;
import org.hyperic.sigar.Sigar;

import java.io.File;

/**
 * Created by zhenchuan on 6/27/14.
 */
public class FileMetrics implements CanRegisterGauges{
    private final Sigar sigar;
    private final String file;
    private final boolean isDir ;

    public FileMetrics(Sigar sigar,String file){
        this.sigar = sigar;
        this.file = file;
        File f = new File(file);
        if(!f.exists()){
            throw new IllegalArgumentException(file + " does not exist!");
        }
        isDir = f.isDirectory();
    }

    @Override
    public void registerGauges(MetricRegistry registry) {
        registerFileSizeGauge(registry,MetricRegistry.name(getClass(),"size"));
    }

    public void registerFileSizeGauge(MetricRegistry metricRegistry,String name){
        metricRegistry.register(name,new Gauge<Long>() {
            @Override
            public Long getValue() {
                if(isDir){
                    return dirSize(file);
                }else {
                    return fileSize(file);
                }

            }
        }) ;
    }

    private long dirSize(String dirName){
        try {
            DirUsage dirUsage = sigar.getDirUsage(dirName);
            return dirUsage.getDiskUsage();
        } catch (Exception e) {
            return -1;
        }
    }

    private long fileSize(String fileName){
        try {
            FileInfo fileInfo = sigar.getFileInfo(fileName);
            return fileInfo.getSize();
        } catch (Exception e) {
            return -1;
        }
    }

}
