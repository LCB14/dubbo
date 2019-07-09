package com.lcb.dubbo.spi.test.auto.adapt;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author changbao.li
 * @Description dubbo扩展点自适应测试
 * @Date 2019-07-09 23:06
 */
public class Test {
    public static void main(String[] args) {
        ExtensionLoader<CarMaker> extensionLoader =
                ExtensionLoader.getExtensionLoader(CarMaker.class);

        Map<String, String> map = new HashMap();
        map.put("raceCarMakerLoader","wheelMakeImplFirst");
        URL url = new URL("", "", 1, map);

        CarMaker carMaker = extensionLoader.getExtension("raceCarMakerLoader");
        carMaker.makeCar(url);
    }
}
