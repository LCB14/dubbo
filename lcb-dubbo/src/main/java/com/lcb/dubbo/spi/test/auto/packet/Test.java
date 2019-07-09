package com.lcb.dubbo.spi.test.auto.packet;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author changbao.li
 * @Description 扩展点自动包装测试
 * @Date 2019-07-09 22:05
 */
public class Test {
    public static void main(String[] args) {
        ExtensionLoader<Protocol> extensionLoader =
                ExtensionLoader.getExtensionLoader(Protocol.class);

        Protocol protocol = extensionLoader.getExtension("protocol");
        protocol.sayHello("hello dubbo");
    }
}
