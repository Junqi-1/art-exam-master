package com.youkeda.application.art.role.control;

import com.google.common.collect.Lists;
import com.youkeda.application.art.role.model.Permission;
import com.youkeda.application.art.role.param.PermissionQueryParam;
import com.youkeda.application.art.role.service.PermissionService;
import com.youkeda.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/permission")
public class PermissionTestControl {

    @Autowired
    private PermissionService permissionService;

    @GetMapping(path = "/save")
    public Permission testSave() {
        Permission permission = new Permission();
        permission.setName("TestPermission");
        permission.setTitle("测试权限");
        permission.setRemark("这是一个测试权限");

        permission = permissionService.save(permission);

        return permission;
    }

    @GetMapping(path = "/query")
    public List<Permission> testQuery() {
        PermissionQueryParam param = new PermissionQueryParam();
        param.setCompanyId(Company.DEFAULT.getId());
        List<Permission> pDatas = permissionService.query(param);
        if (CollectionUtils.isEmpty(pDatas)) {
            System.out.println("query() 失败");
        } else {
            System.out.println("query() 成功");
        }

        param.setName("TestPermission");
        pDatas = permissionService.query(param);
        if (CollectionUtils.isEmpty(pDatas)) {
            System.out.println("query() 失败");
        } else {
            System.out.println("query() 成功");
        }

        return pDatas;
    }

    @GetMapping(path = "/del")
    public List<Permission> testDelete() {
        Permission permission = new Permission();
        permission.setName("TestPermission2");
        permission.setTitle("测试权限2");
        permission.setRemark("这是一个测试权限2");

        permission = permissionService.save(permission);

        permissionService.delete(permission.getId());

        PermissionQueryParam param = new PermissionQueryParam();
        param.setCompanyId(Company.DEFAULT.getId());
        param.setPermissionIds(Lists.newArrayList(permission.getId()));
        List<Permission> pDatas = permissionService.query(param);

        if (CollectionUtils.isEmpty(pDatas)) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }

        return pDatas;
    }


}
