package com.fitsoft.shop.action;

import com.fitsoft.shop.bean.ArticleType;
import com.fitsoft.shop.sevice.ShopService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Joker
 * @since 2019/8/13 0013 18:22
 */
@WebServlet("/getArticleTypes")
public class ArticleTypeServlet extends HttpServlet {

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
        //super.service(req, resp);
        //查询所有商品类型的信息
        List<ArticleType> articleTypes = shopService.getArticleTypes();
        System.out.println(articleTypes);
    }
}
