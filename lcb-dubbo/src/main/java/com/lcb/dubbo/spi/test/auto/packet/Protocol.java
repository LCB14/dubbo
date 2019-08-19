package com.lcb.dubbo.spi.test.auto.packet;

import org.apache.dubbo.common.extension.SPI;

/**
 * @author changbao.li
 * @Description 当wrapper有多个实现时可以在@SPI注解中指定默认实现如@SPI("protocol")
 * @Date 2019-07-09 22:03
 */
@SPI
public interface Protocol {

    public void sayHello(String content);
}
