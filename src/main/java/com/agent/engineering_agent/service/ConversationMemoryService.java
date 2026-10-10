package com.agent.engineering_agent.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationMemoryService {
 private final StringRedisTemplate redisTemplate;
 private static final String KEY_PREFIX="chat:history:";
private static final long TTL_HOUR=24;
	public void saveMessage(String conversationId,String role,String message) {
		String key=KEY_PREFIX+conversationId;
		String formattedMessage=role+" "+message;
	 redisTemplate.opsForList().rightPush(conversationId, formattedMessage);
	 redisTemplate.expire(key,TTL_HOUR,TimeUnit.HOURS);
 }
 public List<String> getHistory(String conversationId){
		String key=KEY_PREFIX+conversationId;
	 List<String> history=redisTemplate.opsForList().range(key, 0, -1);
	 if(history.isEmpty())return Collections.EMPTY_LIST;
	 return history;
 }
 public void clearHistory(String conversationId) {
		String key=KEY_PREFIX+conversationId;
         redisTemplate.delete(key);
 }
}
