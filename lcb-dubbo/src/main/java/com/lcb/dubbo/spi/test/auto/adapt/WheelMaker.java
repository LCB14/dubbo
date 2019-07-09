package com.lcb.dubbo.spi.test.auto.adapt;

import org.apache.dubbo.common.extension.SPI;

@SPI
public interface WheelMaker {
    void makeWheel();
}