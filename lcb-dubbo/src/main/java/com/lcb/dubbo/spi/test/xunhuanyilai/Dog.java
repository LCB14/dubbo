package com.lcb.dubbo.spi.test.xunhuanyilai;

import org.apache.dubbo.common.extension.Adaptive;

/**
 * @author changbao.l Date: 2020-05-19 Time: 4:56 下午
 * @version $
 */
@Adaptive
public class Dog implements Animal {
    Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        System.out.println("狗-跑-还有个主人");
    }
}
