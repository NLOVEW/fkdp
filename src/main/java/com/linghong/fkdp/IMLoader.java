package com.linghong.fkdp;


import com.linghong.fkdp.websocket.ImServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/6 13:05
 * @Version 1.0
 * @Description:  spring 环境启动后加载
 */
@Component
public class IMLoader implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            if (contextRefreshedEvent.getApplicationContext().getParent() == null){
                ImServer.getInstance().start(9797);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
