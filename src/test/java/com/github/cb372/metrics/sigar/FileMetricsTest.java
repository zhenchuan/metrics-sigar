package com.github.cb372.metrics.sigar;

import com.codahale.metrics.MetricRegistry;
import org.junit.Before;
import org.junit.Test;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * Created by zhenchuan on 6/30/14.
 */
public class FileMetricsTest extends CheckSigarLoadsOk{
    private static final int FILE_SIZE = 26 * 1000;

    private FileMetrics fileMetrics;
    private FileMetrics dirMetrics;


    @Before
    public void setUp(){
        File file = createTmpFile();
        File dir = file.getParentFile();
        fileMetrics = SigarMetrics.getInstance().fileMetrics(file.getAbsolutePath());
        dirMetrics = SigarMetrics.getInstance().fileMetrics(dir.getAbsolutePath());
    }

    @Test
    public void testFileSize(){
        assertEquals(FILE_SIZE,fileMetrics.fileSize());
    }

    @Test
    public void testDirSize(){
        assertTrue(dirMetrics.dirSize() > FILE_SIZE);
    }

    private File createTmpFile(){
        File file = null;
        try {
            file = File.createTempFile("prefix",".tmp");
            System.out.println(file.getAbsolutePath());
            FileWriter fw = new FileWriter(file,false);
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            for(int i = 0 ; i < 1000 ;i++){
                fw.write(chars);
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }



}
