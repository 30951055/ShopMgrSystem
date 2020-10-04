一. 项目部署
1. 配置pom.xml文件。首先创建Maven工程，在pom.xml中加入项目所需依赖：
2. 配置c3p0.properties文件。由于项目使用c3p0连接池，因此需要在resource目录下配置c3p0.properties，主要配置数据库驱动、地址、用户名、密码等属性。
3. 配置web.xml文件。项目启动首先要进入web.xml文件，因此Spring的监听器就放在这里配置
4. 配置application.xml文件。监听器用来加载application.xml的配置文件。

二. 项目分层，编写实体类
5. 编写三个实体类Article，ArticleType，User。在com.fitsoft.shop.bean包下，编写所需的三个实体类。
6. 在com.fitsoft.shop.repository包下编写其数据访问层接口：ArticleMapper，ArticleTypeMapper，UserMapper
7. 在resource目录下，新建和数据访问层一样的包，并在这里编写映射文件，文件名相对应：ArticleMapper.xml，ArticleTypeMapper.xml，UserMapper.xml
8. 在com.fitsoft.shop.sevice包下创建业务层接口：ShopService 
9. 创建其实现类：ShopServiceImpl

三. 登录功能
10.编写index.jsp界面，令其自动访问登录界面.
   如果web.xml文件件中没有配置默认访问界面，tomcat会自动寻找index.html，但发现index.html也没有，就会访问index.jsp界面了
11.在com.fitsoft.shop.action包下新建LoginServlet类，接收处理请求

四. 登录
12.登录逻辑介绍。
   1）从登录界面进入主界面要进行用户名和密码的验证，首先，前台通过login.jsp中的form表单的“login?method=login”提交。
   2）在后台LoginServlet类的service方法来获取用户名和密码以及mothod
   3）在LoginServlet类的login()方法中实现对登录用户的校验
   4）在shopService.login(loginName, password);方法进行判断，首先在接口中定义方法
   5）然后进入shopServiceImpl实现类
   6）在shopServiceImpl中得到数据访问层对象，然后调用login方法
   7）在shopServiceImpl中利用StringUtils.isEmpty()方法判断传递过来参数是否为空，如果不为空则调用userMapper中的login方法 User user = userMapper.login(loginName);
   8）在userMapper中调用数据库语句，根据用户名获取user然后返回业务层，再根据user中的密码比对用户输入的密码是否相同，将结果放入Map类型的result中，然后在LoginServlet中读取result中的内容。如果结果中的code为0，代表数据库中的用户名和密码匹配，用户登录成功，然后跳转到主界面。

五. 商品的增删改查
13. 查询
   1）进入主界面，首先要进行商品的查询
   2）上一次在登录成功之后进入ListServlet查询所有商品并进行分页，查询的信息主要包括商品的各类信息，以及分页信息，一级和二级菜单的展示，这里仅以上旬所有商品信息为例。
   3）在LoginServlet.login中判断request中的method，然后调用getAll()方法
   4）在Pager类中进行分页信息类的设置
   5）在getAll()方法中判断是否有页码的传递，如果没有，默认显示第一页，然后查询所有商品的信息
   6）在接口ShopService.searchArticles中有四个参数，第一个参数为商品的一级类型，第二个参数为商品的二级类型，第三个参数为商品的标题，第四个参数为商品的分页信息
	如：    List<Article> searchArticles(String typeCode, String secondType, String title, Pager pager);  
   7）在接口业务层ArticleMapper的实现类调用searchArticles方法
   8）在Spring的application.xml中的 bean id 中的sqlSessionFactory为持久化类做了扫描，所以别名的设置就简单了，在数据访问层这里设置相关参数，然后在映射文件ArticleMapper.xml中selectId为searchArticles中书写SQL语句，查询到所有的商品信息并返回，然后跳转到主界面进行展示

14. 增加
   前端list.jsp中的form里面要加上 enctype="multipart/form-data" 用来传递文件，后台的ListServlet类里面 也要加上 @MultipartConfig 用于接收，否则会造成request中参数无法获取，然后ListServlet类调用 encapsulationArticle()方法为Article设置属性信息
   然后执行 shopService.saveArticle(article);方法，在 ArticleMapper.xml书写SQL语句

15. 修改
ListServlet.updateArticle方法进行修改处理
首先判断用户是否上传了新的图片信息，如果没有就使用之前的图片，ListServlet.receiveImage方法接收图片 
下一步也是用ArticleMapper.xml中的<update id="update" parameterType="Article">来设置Article的一些属性信息，然后调用 shopService.updateArticle(article)方法

16. 删除
单击删除按钮，调用ListServlet中的deleteById()方法
直接看ArticleMapper类中的数据库语句：
@Delete("delete from ec_article where id = #{id}")
void deleteById(String id);

语句比较短，直接用注解解决，像这种只带一个参数的数据库语句，id就不是特别关键了，只有一个，不会起冲突，把上一个语句改成：
@Delete("delete from ec_article where id = #{xxxx}")
void deleteById(String id);

   






