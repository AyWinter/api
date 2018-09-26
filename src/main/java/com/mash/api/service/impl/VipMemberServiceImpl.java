package com.mash.api.service.impl;

import com.mash.api.entity.VipMember;
import com.mash.api.repository.VipMemberRepository;
import com.mash.api.service.VipMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VipMemberServiceImpl implements VipMemberService{

    @Autowired
    private VipMemberRepository vipMemberRepository;

    @Override
    public VipMember save(VipMember vipMember) {
        return vipMemberRepository.save(vipMember);
    }
}
