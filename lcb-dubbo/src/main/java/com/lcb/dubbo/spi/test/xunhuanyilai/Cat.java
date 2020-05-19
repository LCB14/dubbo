package com.lcb.dubbo.spi.test.xunhuanyilai;

/**
 * @author changbao.l Date: 2020-05-19 Time: 5:22 下午
 * @version $
 */
public class Cat implements Animal{
    Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        System.out.println("猫-跑-有个主人");
    }
}
