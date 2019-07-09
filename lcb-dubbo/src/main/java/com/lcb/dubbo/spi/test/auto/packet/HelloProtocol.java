package com.lcb.dubbo.spi.test.auto.packet;

/**
 * @author changbao.li
 * @Description TODO
 * @Date 2019-07-09 22:31
 */
public class HelloProtocol implements Protocol{

    @Override
    public void sayHello(String content) {
        System.out.println("hello dubbo");
    }
}
