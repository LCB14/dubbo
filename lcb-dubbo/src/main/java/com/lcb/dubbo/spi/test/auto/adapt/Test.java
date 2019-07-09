package com.lcb.dubbo.spi.test.auto.adapt;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author changbao.li
 * @Description dubbo扩展点自适应测试
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
