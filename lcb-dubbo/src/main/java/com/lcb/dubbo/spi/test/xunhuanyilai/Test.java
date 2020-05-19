package com.lcb.dubbo.spi.test.xunhuanyilai;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author changbao.l Date: 2020-05-19 Time: 4:58 下午
 * @version $
 */
public class Test {
    public static void main(String[] args) {
        ExtensionLoader<Person> extensionLoader =
                ExtensionLoader.getExtensionLoader(Person.class);

        Person person = extensionLoader.getExtension("student");
        person.sayHello();
    }
}
