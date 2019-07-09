package com.lcb.dubbo.spi.test.auto.loader;


/**
 * @author changbao.li
 * @Description TODO
 * @Date 2019-07-09 23:04
 */
public class WheelMakeImpl implements WheelMaker {
    @Override
    public void makeWheel() {
        System.out.println("wheeler");
    }
}
