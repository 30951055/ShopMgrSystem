package com.fitsoft.shop.action;

import com.fitsoft.shop.bean.Article;
import com.fitsoft.shop.bean.ArticleType;
import com.fitsoft.shop.sevice.ShopService;
import com.fitsoft.shop.utils.Pager;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * @author Joker
 * @since 2019/8/14 0014 20:52
 */
@WebServlet("/list")
@MultipartConfig // 申明这个Servlet是要接收大文件对象的
public class ListServlet extends HttpServlet {

    //定义业务层对象
    private ShopService shopService;

    private HttpServletRequest request;
    private HttpServletResponse response;

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
        this.request = req;
        this.response = resp;
        request.setCharacterEncoding("UTF-8");
        String method = request.getParameter("method");
        if(method.equals("getAll")){
            getAll();
        }else if(method.equals("deleteById")){
            deleteById();
        }else if(method.equals("preArticle")){
            preArticle();
        }else if(method.equals("showUpdate")){
            showUpdate();
        }else if(method.equals("updateArticle")){
            try {
                updateArticle();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(method.equals("addArticle")){
            try {
                addArticle();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void addArticle() throws ServletException, IOException, ParseException {
        //接收图片
        String imageUrl = receiveImage();
        //添加商品的相关信息
        Article article = encapsulationArticle(request, imageUrl, "addArticle");

        shopService.saveArticle(article);
        shopService.updateArticle(article);
        request.setAttribute("tip","添加商品成功");
        getAll();
    }

    private String receiveImage() {
        try {
            //接收用户可能上传的封面
            //如果用户上传了，这里是不会出现编译异常的
            //如果没有上传，这里出现异常
            Part part = request.getPart("image");
            //保存到项目的路径中去
            String sysPath = request.getSession().getServletContext().getRealPath("/resources/images/article");
            //定义一个新的图片名称
            String fileName = UUID.randomUUID().toString();
            //提取图片的类型
            //上传文件的内容性质
            String contentDispostion = part.getHeader("content-disposition");
            //获取上传文件的后缀名
            String suffix = contentDispostion.substring(contentDispostion.lastIndexOf("."), contentDispostion.length() - 1);
            fileName+=suffix;
            //把图片保存到路径中去
            part.write(sysPath+"/"+fileName);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ServletException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Article encapsulationArticle(HttpServletRequest request,
                                      String picUrl, String operationType) throws ParseException {
        Article article = new Article();
        ArticleType type = new ArticleType();
        type.setCode(request.getParameter("code"));
        article.setArticleType(type);
        article.setTitle(request.getParameter("titleStr"));
        article.setSupplier(request.getParameter("supplier"));
        article.setLocality(request.getParameter("locality"));
        article.setPrice(Double.parseDouble(request.getParameter("price")));
        article.setStorage(Integer.parseInt(request.getParameter("storage")));
        article.setDescription(request.getParameter("description"));
        article.setImage(picUrl);
        if(operationType.equals("addArticle")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            article.setPutawayDate(sdf.parse(request.getParameter("putawayDate")));
        }else if(operationType.equals("updateArticle")){
            article.setId(Integer.valueOf(request.getParameter("id")));
        }
        return article;
    }

    private void updateArticle() throws ParseException {
        //接收界面提交的参数
        String picUrl = request.getParameter("picUrl");  //物品旧封面

        String newUrl = receiveImage();
        picUrl = newUrl != null? newUrl:picUrl;

        Article article = encapsulationArticle(request,picUrl,"updateArticle");
        shopService.updateArticle(article);
        request.setAttribute("tip","修改商品成功");
        showUpdate();
    }

    private void showUpdate() {
        try {
            String id = request.getParameter("id");
            Article article = shopService.getArticleById(id);
            request.setAttribute("article", article);
            //查询出所有的商品类型
            List<ArticleType> types = shopService.getArticleTypes();
            request.setAttribute("types", types);
            request.getRequestDispatcher("WEB-INF/jsp/updateArticle.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void preArticle() {
        try {
            String id = request.getParameter("id");
            Article article = shopService.getArticleById(id);
            request.setAttribute("article", article);
            request.getRequestDispatcher("WEB-INF/jsp/preArticle.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteById() {
        try {
            String id = request.getParameter("id");
            shopService.deleteById(id);
            request.setAttribute("tip", "删除成功");
        } catch (Exception e) {
            request.setAttribute("tip", "删除失败");
            e.printStackTrace();
        }finally {
            try {
                request.getRequestDispatcher("/list?method=getAll").forward(request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAll() throws ServletException, IOException {

        //考虑分页查询
        Pager pager = new Pager();
        //看书否传入了分页参数的页码
        String pageIndex = request.getParameter("pageIndex");
        if(!StringUtils.isEmpty(pageIndex)){
            int pSize = Integer.valueOf(pageIndex);
            pager.setPageIndex(pSize);
        }

        //二级类型
        String secondType = request.getParameter("secondType");
        request.setAttribute("secondType", secondType);
        //搜索框中的商品标题
        String title = request.getParameter("title");
        request.setAttribute("title", title);
        //接收一级类型编号查询
        String typeCode = request.getParameter("typeCode");
        //根据一级类型查询对应的二级类型
        if(!StringUtils.isEmpty(typeCode)){
            List<ArticleType> secondTypes = shopService.loadSecondTypes(typeCode);
            request.setAttribute("typeCode", typeCode);
            request.setAttribute("secondTypes", secondTypes);
        }

        //1.查询所有的一级类型数据
        List<ArticleType> firstArticleTypes = shopService.loadArticleTypes();
        //2.查询所有的商品信息
        List<Article> articles = shopService.searchArticles(typeCode, secondType, title, pager);
        request.setAttribute("articleTypes", shopService.getArticleTypes());
        request.setAttribute("firstArticleTypes", firstArticleTypes);
        request.setAttribute("pager", pager);
        request.setAttribute("articles", articles);
        request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
    }
}
