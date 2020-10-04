package com.fitsoft.shop.action;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fitsoft.shop.bean.User;
import com.fitsoft.shop.sevice.ShopService;
import com.fitsoft.shop.utils.Constants;

/**
 * @author Joker
 * @since 2019/8/13 0013 20:08
 */
@SuppressWarnings("serial")
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private HttpServletRequest request;
    private HttpServletResponse response;

    //定义业务层对象
    private ShopService shopService;

    @Override
    public void init() throws ServletException {
        super.init();
        //获取Spring的容器，然后从容器中获取业务层对象
        ServletContext servletContext = this.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        shopService = (ShopService) context.getBean("shopService");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //跳转到登录界面
        this.request = req;
        this.response = resp;
        String method = req.getParameter("method");
        if(method.equals("getjsp")){
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }else if(method.equals("login")){
            login();
        }
    }

    private void login(){
        try {
            String loginName = request.getParameter("loginName");
            String password = request.getParameter("passWord");
            Map<String, Object> result = shopService.login(loginName, password);
            if(result.get("code").equals(0)){
                //登录成功
                //把登录成功的用户注入到Session会话中去
                //跳转到主界面
                User user = (User) result.get("msg");
                request.setAttribute(Constants.USER_SESSION, user);
                //请求跳转到主界面的Servlet
                response.sendRedirect(request.getContextPath()+"/list?method=getAll");
                //request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            }else{
                //登录失败
                String msg = (String) result.get("msg");
                request.setAttribute("msg", msg);
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            }
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
