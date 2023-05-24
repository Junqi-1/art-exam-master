package com.youkeda.application.art.interceptor;

import com.youkeda.application.art.config.NeedLogin;
import com.youkeda.application.art.member.model.LoginUser;
import com.youkeda.application.art.member.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class MemberHandlerInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MemberHandlerInterceptor.class);

    // Controller方法执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        // 拦截目标必须是一个方法
        boolean isHandler = handler instanceof HandlerMethod;

        if (!isHandler) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod)handler;

        // 如果是系统 bean 则不处理
        if (handlerMethod.getBean().getClass().getName().startsWith("org.springframework")) {
            return true;
        }

        // 先判断拦截目标方法对于的 bean 的类型，有没有 NeedLogin 注解
        NeedLogin needLogin = handlerMethod.getBeanType().getAnnotation(NeedLogin.class);
        if (needLogin == null) {
            // 再判断方法本身有没有 NeedLogin 注解
            needLogin = handlerMethod.getMethodAnnotation(NeedLogin.class);
        }

        HttpSession session = request.getSession();
        // 取得 session 中存储的登录用户
        LoginUser lu = (LoginUser)session.getAttribute(MemberUtil.LOGIN_KEY);

        // 存在 needLogin 注解但 session 里没有登录用户，则跳转
        if (needLogin != null && lu == null) {

            // 如果方法拿到 ResponseBody 注解，说明是一个 api 请求，那么久不用登录了，直接返回 401 状态。
            if (handlerMethod.getMethodAnnotation(ResponseBody.class) != null) {
                response.setStatus(401);
                response.getWriter().print(MemberUtil.LOGIN_PAGE_URL);
                response.getWriter().flush();
                return false;
            }

            // 跳转登录
            response.sendRedirect(MemberUtil.LOGIN_PAGE_URL);
            return false;
        }

        return true;
    }
}
