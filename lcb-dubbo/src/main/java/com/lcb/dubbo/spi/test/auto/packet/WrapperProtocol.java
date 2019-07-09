package com.lcb.dubbo.spi.test.auto.packet;

/**
 * @author changbao.li
 * @Description ExtensionLoader 在加载扩展点时，如果加载到的扩展点有拷贝构造函数，则判定为扩展点 Wrapper 类。
 *  该包装类，会被应用到所有实现Protocol接口的类实例调用中，类似AOP中的切面。
 *  @Date 2019-07-09 22:04
 */
public class WrapperProtocol implements Protocol {
    Protocol protocol;

    public WrapperProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void sayHello(String content) {

        System.out.println("前置操作");
        protocol.sayHello(content);
        System.out.println("后置操作");
    }
}
