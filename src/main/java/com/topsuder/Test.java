package com.topsuder;

import com.spring.AppConfig;
import com.spring.TopsuderAnnotationConfigApplicationContext;
import com.topsuder.service.UserService;
import com.topsuder.service.UserServiceInterface;

/**
 * <span>Form File</span>
 * <p>Description</p>
 * <p>Copyright: Copyright (c) 2022 版权</p>
 * <p>Company:QQ 752340543</p>
 *
 * @author topsuder
 * @version v1.0.0
 * @DATE 2022/8/12-09:48
 * @Description
 * @see com.topsuder mock-spring
 */
public class Test {

    public static void main(String[] args) {
        //扫描  创建非懒加载的bean
        TopsuderAnnotationConfigApplicationContext topsuderAnnotationConfigApplicationContext = new TopsuderAnnotationConfigApplicationContext(AppConfig.class);


        final UserServiceInterface userService = (UserServiceInterface) topsuderAnnotationConfigApplicationContext.getBean("userService");

        userService.test();

    }

}
