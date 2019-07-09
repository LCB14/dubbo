package com.lcb.dubbo.spi.test.auto.loader;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author changbao.li
 * @Description dubbo自动装配测试，直接这样搞发现行不通！！
 * @Date 2019-07-09 23:06
 */
public class Test {
    public static void main(String[] args) {
        ExtensionLoader<CarMaker> extensionLoader =
                ExtensionLoader.getExtensionLoader(CarMaker.class);

        CarMaker carMaker = extensionLoader.getExtension("raceCarMaker");
        carMaker.makeCar();
    }
}
