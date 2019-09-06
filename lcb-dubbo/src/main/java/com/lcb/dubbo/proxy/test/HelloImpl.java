package com.lcb.dubbo.proxy.test;

public class HelloImpl implements IHello {
    @Override
    public void sayHello() {
        System.out.println("Hello world!");
    }
}
