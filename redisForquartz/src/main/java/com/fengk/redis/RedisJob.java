package com.fengk.redis;

import com.fengk.utils.QiniuUtils;
import com.fengk.utils.RedisConstant;
import com.fengk.utils.RedisMessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 *
 */
@Component
public class RedisJob {
    @Autowired
    JedisPool jedisPool;

    public void redisOnWeb(String filename){
jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,filename);
    }
    public void redisInDatabase(String filename){
jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,filename);
    }
    public void findDiffAndDel(){
        Set<String> sdiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        for (String filename : sdiff) {
            System.out.println(filename);
            QiniuUtils.deleteFileFromQiniu(filename);
            System.out.println(filename+"在web端删除成功");
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,filename);
            System.out.println(filename+"在redis端删除成功");
        }

    }
    public void addCode(int seconds,String telphone,Integer code){
        jedisPool.getResource().set(RedisMessageConstant.SENDTYPE_ORDER+telphone,code.toString());
        jedisPool.getResource().expire(RedisMessageConstant.SENDTYPE_ORDER+telphone,seconds);
    }

    public String getCodeFromRedis(String telphone,String type) {
        String code = jedisPool.getResource().get(type + telphone);
return  code;
    }

    public void addLoginCode(int seconds, String telphone, Integer code) {
        jedisPool.getResource().set(RedisMessageConstant.SENDTYPE_LOGIN+telphone,code.toString());
        jedisPool.getResource().expire(RedisMessageConstant.SENDTYPE_LOGIN+telphone,seconds);
    }
}
