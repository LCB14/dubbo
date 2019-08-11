package com.lcb.dubbo.spi.test.beginner;

import org.apache.dubbo.common.extension.SPI;

/**
 * 注解@SPI里面只有一个String类型，通过使用@SPI("dubbo")标识2接口类的具体实现，注解@SPI里面的值对应了SPI配置文件中的前缀名称。
 */
@SPI
public interface Robot {
    void sayHello();
}