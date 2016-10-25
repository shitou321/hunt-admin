package com.hunt.controller;

import com.hunt.model.dto.PageInfo;
import com.hunt.model.entity.SysPermission;
import com.hunt.model.entity.SysPermissionGroup;
import com.hunt.service.SysPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import system.Result;

import java.util.List;

/**
 * @Author: ouyangan
 * @Date : 2016/10/15
 */
@Controller
@RequestMapping("permission")
public class PermissionController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private SysPermissionService sysPermissionService;

    @RequestMapping(value = "toPermission", method = RequestMethod.GET)
    public String toPermission() {
        return "system/permission";
    }

    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public Result insert(@RequestParam long groupId,
                         @RequestParam String name,
                         @RequestParam String code,
                         @RequestParam String description,
                         @RequestParam(defaultValue = "1") int isFinal) {
        boolean isExistName = sysPermissionService.isExistName(groupId, name);
        boolean isExistCode = sysPermissionService.isExistCode(groupId, code);
        System.out.println(isExistName);
        System.out.println(isExistCode);
        if (isExistName) {
            return Result.error("该分组下名称已存在1");
        }
        if (isExistCode) {
            return Result.error("该权限分组下编码已存在已存在");
        }
        SysPermission sysPermission = new SysPermission();
        sysPermission.setName(name);
        sysPermission.setCode(code);
        sysPermission.setDescription(description);
        sysPermission.setIsFinal(isFinal);
        sysPermission.setSysPermissionGroupId(groupId);
        long id = sysPermissionService.insertPermission(sysPermission);
        return Result.success(id);
    }

    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public Result delete(@RequestParam long id) {
        SysPermission sysPermission = sysPermissionService.selectById(id);
        if (sysPermission == null) {
            return Result.error("该记录不存在");
        }
        if (sysPermission.getIsFinal() == 2) {
            return Result.error("该数据不可修改");
        }
        sysPermission.setStatus(2);
        sysPermissionService.update(sysPermission);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Result update(@RequestParam long id,
                         @RequestParam long groupId,
                         @RequestParam String name,
                         @RequestParam String code,
                         @RequestParam String description) {
        SysPermission sysPermission = sysPermissionService.selectById(id);
        if (sysPermission == null) {
            return Result.error("该记录不存在");
        }
        if (sysPermission.getIsFinal() == 2) {
            return Result.error("该数据不可修改");
        }
        boolean isExistName = sysPermissionService.isExistNameExcludeId(id, groupId, name);
        if (isExistName) {
            return Result.error("该分组下名称已存在");
        }
        boolean isExistCode = sysPermissionService.isExistCodeExcludeId(id, groupId, code);
        if (isExistCode) {
            return Result.error("该权限分组下编码已存在已存在");
        }
        sysPermission.setName(name);
        sysPermission.setCode(code);
        sysPermission.setDescription(description);
        sysPermission.setSysPermissionGroupId(groupId);
        sysPermissionService.update(sysPermission);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value = "select", method = RequestMethod.GET)
    public PageInfo select(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "30") int rows) {
        PageInfo pageInfo = sysPermissionService.selectPage(page, rows);
        return pageInfo;
    }

    @ResponseBody
    @RequestMapping(value = "insertGroup", method = RequestMethod.POST)
    public Result insertGroup(@RequestParam String name,
                              @RequestParam String description) {
        boolean isExistGroupName = sysPermissionService.isExistGroupName(name);
        if (isExistGroupName) {
            return Result.error("该分组名称已存在");
        }
        SysPermissionGroup sysPermissionGroup = new SysPermissionGroup();
        sysPermissionGroup.setName(name);
        sysPermissionGroup.setDescription(description);
        sysPermissionGroup.setIsFinal(2);
        long id = sysPermissionService.insertPermissionGroup(sysPermissionGroup);
        return Result.success(id);
    }

    @ResponseBody
    @RequestMapping(value = "selectGroup", method = RequestMethod.GET)
    public List<SysPermissionGroup> selectGroup() {
        List<SysPermissionGroup> list = sysPermissionService.selectGroup();
        return list;
    }

}
