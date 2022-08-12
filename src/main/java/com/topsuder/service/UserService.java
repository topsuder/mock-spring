package com.topsuder.service;

import com.spring.Autowired;
import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.InitializingBean;
import com.spring.Scope;

/**
 * <span>Form File</span>
 * <p>Description</p>
 * <p>Copyright: Copyright (c) 2022 版权</p>
 * <p>Company:QQ 752340543</p>
 *
 * @author topsuder
 * @version v1.0.0
 * @DATE 2022/8/12-10:07
 * @Description
 * @see com.topsuder mock-spring
 */
@Component("userService")
@Scope("singleton")
public class UserService implements InitializingBean,UserServiceInterface, BeanNameAware {

    @Autowired
    private OrderService orderService;

    @TopsuderValue("awdaw")
    private String value;

    private String beanName;

    public void test() {
        System.out.println(value);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(123);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName=name;
    }
}
