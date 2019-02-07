package com.linghong.fkdp.controller;

import com.linghong.fkdp.pojo.*;
import com.linghong.fkdp.service.AdminService;
import com.linghong.fkdp.utils.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/27 20:44
 * @Version 1.0
 * @Description:
 */
@Controller
public class AdminController {
    @Resource
    private AdminService adminService;

    @GetMapping("/admin/login.html")
    public String login() {
        return "login";
    }

    @PostMapping("/admin/doLogin")
    public String doLogin(String mobilePhone, String password, HttpServletRequest request) {
        Admin admin = adminService.login(mobilePhone, password);
        if (admin != null) {
            request.getSession().setAttribute("admin", admin);
            return "redirect:/admin/index";
        }
        return "login";
    }


    @RequestMapping("/admin/index")
    public String index(Model model) {

        Map<String, Object> result = adminService.index();
        List<User> userList = (List<User>) result.get("userList");
        model.addAttribute("userList", userList);
        List<Goods> goodsList = (List<Goods>) result.get("goodsList");
        model.addAttribute("goodsList", goodsList);
        List<GoodsOrder> goodsOrderList = (List<GoodsOrder>) result.get("goodsOrderList");
        model.addAttribute("goodsOrderList", goodsOrderList);
        List<Merchant> merchantList = (List<Merchant>) result.get("merchantList");
        model.addAttribute("merchantList", merchantList);
        List<MessageBack> messageBackList = (List<MessageBack>) result.get("messageBackList");
        model.addAttribute("messageBackList", messageBackList);
        List<BackGoods> backGoodsList = (List<BackGoods>) result.get("backGoodsList");
        model.addAttribute("backGoodsList", backGoodsList);

        return "index";
    }

    @PostMapping("/admin/updatePassword")
    public String updatePassword(String newPassword, HttpServletRequest request) {
        Admin admin = (Admin) request.getSession().getAttribute("admin");
        admin.setPassword(MD5Util.md5(newPassword));
        adminService.updatePassword(admin);
        return "redirect:/admin/index";
    }

}
