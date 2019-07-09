package com.lcb.dubbo.spi.test.auto.adapt;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.SPI;

@SPI
public interface CarMaker {

    void makeCar(URL url);
}