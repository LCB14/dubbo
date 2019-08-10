package com.lcb.dubbo.spi.test.beginner;

import org.apache.dubbo.common.extension.SPI;

@SPI
public interface Robot {
    void sayHello();
}