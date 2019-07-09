package com.lcb.dubbo.spi.test.auto.packet;

import org.apache.dubbo.common.extension.SPI;

/**
 * @author changbao.li
 * @Description TODO
 * @Date 2019-07-09 22:03
 */
@SPI
public interface Protocol {

    public void sayHello(String content);
}
