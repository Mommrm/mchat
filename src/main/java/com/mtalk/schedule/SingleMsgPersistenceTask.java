package com.mtalk.schedule;

import cn.hutool.json.JSONUtil;
import com.mtalk.entity.GroupMessage;
import com.mtalk.entity.SingleMessage;
import com.mtalk.mapper.MessageMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.mtalk.utils.constant.RedisConstant.CHAT_GROUP_KEY;
import static com.mtalk.utils.constant.RedisConstant.CHAT_SINGLE_KEY;

@Component
public class SingleMsgPersistenceTask {

    private static final Logger logger = LoggerFactory.getLogger(SingleMsgPersistenceTask.class);
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MessageMapper messageMapper;
    // 每个DataPersistenceTask对应一个group
    private String groupId = "";
    private boolean flag = false;

    // 每2秒运行一次 只有执行发送消息时才把groupId设置为有值 才去保存数据
    @Scheduled(fixedRate = 2000)
    public synchronized void persistDataToDB() {
        if(!groupId.isEmpty()){
            saveSingleChatHistory();
            closeSaveSingleHistory();
        }
    }

    private void saveSingleChatHistory(){
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CHAT_SINGLE_KEY + groupId);
        logger.info("保存groupId的聊天历史数据: {}",entries);
        entries.forEach((sendTime,messageJson) ->{
            SingleMessage singleMessage = JSONUtil.toBean((String) messageJson, SingleMessage.class);
            logger.info("sendTime: {} singleMessage: {}", sendTime,singleMessage);
            messageMapper.InsertSingleHistory(singleMessage);
            // 持久化之后删除Redis缓存的值
            stringRedisTemplate.opsForHash().delete(CHAT_SINGLE_KEY + groupId,sendTime);
        });

    }

    public void startSaveSingleHistory(String groupId){
        this.groupId = groupId;
    }

    public void closeSaveSingleHistory(){
        this.groupId = "";
    }
}