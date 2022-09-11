package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.service.PermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@RequestMapping("permission")
@Controller
public class PermissionController extends BaseController {
    @Resource
    private PermissionService permissionService;
}
