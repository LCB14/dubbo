package com.lcb.dubbo.spi.test.auto.adapt;


import org.apache.dubbo.common.URL;

/**
 * @author changbao.li
 * @Description TODO
 * @Date 2019-07-09 23:04
 */
public class WheelMakeImplFirst implements WheelMaker {
    @Override
    public void makeWheel(URL url) {
        System.out.println("wheeler first");
    }
}
