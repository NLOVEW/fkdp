package com.linghong.fkdp.filter;

import com.alibaba.fastjson.JSON;
import com.linghong.fkdp.dto.Response;
import com.linghong.fkdp.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 前后端分离时（仅仅是请求数据，不返回任何页面的情况下）
 */
public class ShiroLoginFilter extends FormAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(ShiroLoginFilter.class);

    /**
     * 当权限没有，被拒绝时进入此方法
     *
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) {
        logger.info("jwt认证---------------");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "*");
//        response.setHeader("Access-Control-Allow-Credentials", "true"); //是否支持cookie跨域
        Claims parameters = JwtUtil.getParameterByHttpServletRequest(request);
        if (parameters != null) {
            //TODO 判断token过期 -----算了吧  还需要更新操作
            logger.info("token验证通过:{}",JwtUtil.getUserId(request));
            return true;
        } else {
            Response result = new Response();
            result.set(false, 707, null, "请先登录");
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                logger.error("认证未通过");
                response.getWriter().write(JSON.toJSONString(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
