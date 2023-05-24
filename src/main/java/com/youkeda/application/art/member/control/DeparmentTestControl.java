package com.youkeda.application.art.member.control;

import com.google.common.collect.Lists;
import com.youkeda.application.art.member.exception.ErrorException;
import com.youkeda.application.art.member.model.*;
import com.youkeda.application.art.member.service.DepartmentAccountService;
import com.youkeda.application.art.member.service.DepartmentService;
import com.youkeda.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: <strong>DeparmentTestControl</strong>
 * @Author: XiaoKui
 * @Date: 2022/8/4 14:33
 */
@RestController
@RequestMapping("/test/dep")
public class DeparmentTestControl {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentAccountService departmentAccountService;

    @GetMapping(path = "/save")
    public Department testSave() {
        Department dep1 = new Department();
        dep1.setName("dep1");
        dep1.setMemberNum(0);
        dep1.setSort(1);

        Department dep2 = new Department();
        dep2.setName("dep2");
        dep2.setMemberNum(0);
        dep2.setSort(1);

        dep1.addChildren(dep2);
        departmentService.save(dep1);

        dep2.setParentId(dep1.getId());
        departmentService.save(dep2);

        User user1 = buildUser(1);
        Account acc1 = buildAccount(user1, 1);
        try {
            departmentAccountService.addAccount(dep1.getId(), acc1);
        } catch (ErrorException e) {
            e.printStackTrace();
        }

        User user2 = buildUser(2);
        Account acc2 = buildAccount(user2, 2);
        try {
            departmentAccountService.addAccount(dep2.getId(), acc2);
        } catch (ErrorException e) {
            e.printStackTrace();
        }

        return dep1;
    }

    @GetMapping(path = "/get")
    public Department testGet() {
        Department dp = departmentService.getAllWithTree(Company.DEFAULT.getId());

        return dp;
    }

    @GetMapping(path = "/getByIds")
    public List<Department> testGetByIds() {
        Department dp = departmentService.getAllWithTree(Company.DEFAULT.getId());
        List<String> dpIds = new ArrayList<>();
        dpIds.add(dp.getId());
        if (!CollectionUtils.isEmpty(dp.getChildren())) {
            for (Department child : dp.getChildren()) {
                dpIds.add(child.getId());
                if (!CollectionUtils.isEmpty(child.getChildren())) {
                    for (Department cc : child.getChildren()) {
                        dpIds.add(cc.getId());
                    }
                }
            }
        }

        List<Department> dpDatas = departmentService.getByIds(dpIds);

        return dpDatas;
    }

    @GetMapping(path = "/keyword")
    public List<Department> testByKeyword() {
        List<Department> deps = departmentService.queryByUserInfo(Company.DEFAULT.getId(), "dep");

        return deps;
    }

    @GetMapping(path = "/del")
    public List<Department> testDelete() {
        Department dep3 = new Department();
        dep3.setName("dep3");
        dep3.setMemberNum(0);
        dep3.setSort(2);

        departmentService.save(dep3);

        try {
            departmentService.delete(dep3.getId());
        } catch (ErrorException e) {
            e.printStackTrace();
        }

        List<Department> dpDatas = departmentService.getByIds(Lists.newArrayList(dep3.getId()));

        return dpDatas;
    }

    private User buildUser(int index) {
        User user = new User();
        user.setName("test" + index);
        user.setUserName("testUser" + index);
        user.setNickName("test" + index);
        user.setStatus(UserStatus.enabled);
        user.setGender(Gender.male);
        user.setEmail("abc" + index + "@126.com");
        user.setMobile("1364567890" + index);
        user.setAgreementVersion("1.0");
        user.setBirthday(new Date());

        return user;
    }

    private Account buildAccount(User user, int index) {
        Account account = new Account();
        account.setAccountName("testAccount" + index);
        account.setStatus(AccountStatus.enabled);
        account.setAccountMobile("1364567890" + index);
        account.setEmail(user.getEmail());
        account.setJoinTime(LocalDateTime.now());
        account.setJobNumber("00" + index);
        account.setUser(user);

        AccountProfile ap = new AccountProfile();
        ap.setFaculty("计算机学院");
        ap.setMajor("软件工程");
        account.setProfile(ap);

        return account;
    }
}
