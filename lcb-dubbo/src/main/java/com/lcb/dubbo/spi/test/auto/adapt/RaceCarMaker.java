package com.lcb.dubbo.spi.test.auto.adapt;


import org.apache.dubbo.common.URL;

public class RaceCarMaker implements CarMaker {

    private WheelMaker wheelMaker;

    /**
     *  当CarMake扩展点被加载时，会自动装配WheelMaker
     *
     *  思考：当WheelMaker有多个实现时，dubbo是通过什么机制选择来选择相关的实例呢？
     */
    public void setWheelMaker(WheelMaker wheelMaker) {
        this.wheelMaker = wheelMaker;
    }

    @Override
    public void makeCar(URL url) {
        wheelMaker.makeWheel(url);
    }
}