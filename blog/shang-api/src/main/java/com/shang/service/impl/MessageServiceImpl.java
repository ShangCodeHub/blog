package com.shang.service.impl;

import com.shang.service.MessageService;
import com.shang.entity.SysMessage;
import com.shang.mapper.SysMessageMapper;
import com.shang.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final SysMessageMapper messageMapper;

    @Override
    public List<SysMessage> getMessageList() {
        return messageMapper.selectList(null);
    }

    @Override
    public Boolean add(SysMessage sysMessage) {
        String ip = IpUtil.getIp();
        sysMessage.setIp(ip);
        sysMessage.setSource(IpUtil.getIp2region(ip));
        messageMapper.insert(sysMessage);
        return true;
    }
}
