package com.lcb.dubbo.spi.test.auto.adapt;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface WheelMaker {

    @Adaptive("raceCarMakerLoader")
    void makeWheel(URL url);
}