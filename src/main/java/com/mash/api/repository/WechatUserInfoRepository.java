package com.mash.api.repository;

import com.mash.api.entity.WechatUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WechatUserInfoRepository extends JpaRepository<WechatUserInfo, Integer>{

    WechatUserInfo findByOpenId(String openId);
}
