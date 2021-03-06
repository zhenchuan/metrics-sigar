# Metrics Sigar Integration #

A system monitoring library that combines the all-round awesomeness of [Metrics](https://github.com/codahale/metrics) with the handy native-ness of [Hyperic Sigar](http://support.hyperic.com/display/SIGAR/Home). Provides various metrics about a JVM process and the system on which it is running.

## Versioning ##

Version *0.1.x* is compatible with *Metrics 2.x*.

Version *0.2.x* is compatible with *Metrics 3.x*.

## Features ##

Currently the following data can be collected:

* CPU (equivalent to `/proc/cpuinfo`)
    * Number of physical CPUs, total number of cores
    * Per-core load ratios:
        * Ratio of time spent in userland
        * Ratio of time spent in kernel
        * Ratio of time spent running 'nice' tasks
        * Ratio of time spent waiting for I/O
        * Ratio of time spent idle
        * Ratio of time spent servicing interrupts 
* Memory (equivalent to `free` command)
    * RAM size in MB
    * Main memory total, used, free (KB), used %, free %
    * Main memory 'actual' used, free (the "+/- buffers/cache" row in the Linux free command)
    * Swap memory total, used, free
    * Pages swapped in, out
* Filesystems/disk usage (equivalent to `df` command)
    * Device name, mount point, FS type, free space and total size of all mounted filesystems
* Resource limits (equivalent to `ulimit` command)
    * Core file size
    * Data segment size
    * File size
    * Pipe size
    * Memory size
    * Open files
    * Stack size
    * CPU time
    * User processes
    * Virtual memory
* JVM process
    * pid

## Usage ##

 1. Add a dependency on metrics-sigar to your project:

        <dependency>
            <groupId>com.github.cb372</groupId>
            <artifactId>metrics-sigar</artifactId>
            <version>0.2.2</version>
        </dependency>

 2. Download the Sigar native libraries. 
    * If downloading manually, download the Sigar binary package from SourceForge [here](http://sourceforge.net/projects/sigar/files/sigar/1.6/). This package includes libs for most OSes and architectures.
    * If you want to automate it in your build script, add the following dependency to your project and add a task to unzip the jar. (Note: This jar only contains libs for Linux x86/x64, Solaris x64 and OS X x64)
    
            <dependency>
                <groupId>org.fusesource</groupId>
                <artifactId>sigar</artifactId>
                <version>1.6.4</version>
                <classifier>native</classifier>
            </dependency>

 3. Make sure Sigar can find its native libs at runtime. Either:
    * Ensure the libs are in the same directory as `sigar.jar`, or
    * Set the system property `-Dorg.hyperic.sigar.path=<directory containing native libs>`

 4. You're ready to use metrics-sigar! 
    * Use the `SigarMetrics` singleton object directly for detailed data:
    
            SigarMetrics sm = SigarMetrics.getInstance();
    * Optionally expose the most useful data as Metrics `Gauge`s:
    
            SigarMetrics.getInstance().registerGauges(myMetricRegistry);

See the app in the `/sample` directory for an example.

## Acknowledgements ##

This module makes use of [Hyperic Sigar](http://support.hyperic.com/display/SIGAR/Home) ([Github link](https://github.com/hyperic/sigar)) under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## TODO ##

* Port more of the Sigar API
* Sigar seems to consistently give an incorrect value for the number of CPUs. Try to find a workaround.

## Notes ##

Sigar has a few wrinkles and may return crazy values for some metrics, depending on your platform. Still, hopefully better than nothing! Issues and pull requests welcome.
