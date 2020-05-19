package com.lcb.dubbo.spi.test.xunhuanyilai;

import org.apache.dubbo.common.extension.Adaptive;

/**
 * @author changbao.l Date: 2020-05-19 Time: 4:54 下午
 * @version $
 */
//@Adaptive
public class Student implements Person {
    Animal animal;

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void sayHello() {
        System.out.println("小学生-有条狗");
    }
}
