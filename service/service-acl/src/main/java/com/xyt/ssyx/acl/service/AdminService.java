package com.xyt.ssyx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.ssyx.model.acl.Admin;
import com.xyt.ssyx.vo.acl.AdminQueryVo;


public interface AdminService  extends IService<Admin> {


    IPage<Admin> selectPageUser(Page<Admin> page, AdminQueryVo adminQueryVo);
}
