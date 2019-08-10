package com.lcb.dubbo.spi.test.javaSPI;

import com.sun.tools.javac.util.ServiceLoader;
import org.junit.Test;


public class JavaSPITest {

    @Test
    public void sayHello() throws Exception {
        ServiceLoader<Robot> serviceLoader = ServiceLoader.load(Robot.class);
        System.out.println("Java SPI");
        serviceLoader.forEach(Robot::sayHello);
    }
}
