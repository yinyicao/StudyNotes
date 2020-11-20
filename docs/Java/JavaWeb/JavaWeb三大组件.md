# JavaWeb三大组件

## 三大组件之Servlet

> Java Servlet技术简称Servlet技术，是Java开发Web应用的底层技术。由Sun公式于1996年发布，用来代替CGI——当时生成Web动态内容的主流技术。

### Servlet技术与CGI技术的比较

> Servlet是一个特殊的Java程序，一个基于Java的Web应用通常包含一个或多个Servlet类。Servlet不能够自行创建并执行，必须运行在Servlet容器中。Servlet容器将用户的请求传递给Servlet程序，并将Servlet的响应回传给用户。通常一个Servlet会关联一个或多个JSP页面。CGI技术的主要问题是每个Web请求都需要新启动一个进程来处理，创建进程会消耗不少CPU周期，导致难以编写可扩展的CGI程序。而Servlet有着比CGI程序更好的性能，因为Servlet在创建后（处理第一个请求时）就一直保持在内容中。

#### CGI的缺点

- 需要为每个请求加载和运行一个CGI程序，这将带来很大的CPU开销
- 需要重复编写处理网络协议的代码以及编码，耗时费力。

#### Servlet的优点

- 只需要启动一个操作系统进程以及加载一个JVM，大大降低了系统的开销
- 如果多个请求需要做同样处理的时候，这时候只需要加载一个类，这也大大降低了开销
- 所有动态加载的类可以实现对网络协议以及请求解码的共享，大大降低了工作量。
- Servlet能直接和Web服务器交互，而普通的CGI程序不能。Servlet还能在各个程序之间共享数据，使数据库连接池之类的功能很容易实现。

### Servlet运行原理

#### Servlet的处理步骤

<img src="_images/1174433025.jpg" />

#### Servlet的生命周期

> Servlet生命周期定义了一个Servlet如何被加载、初始化，以及它怎样接收请求、响应请求，提供服务等。

##### Servlet接口

在这里我们自己随便建的一个Servlet,他默认继承自HttpServlet类，可以发现最终实现了接口Servlet。见下图：

<img src="_images/1547967677803.png" />  

其中Servlet接口中定义了如下五个方法，其中**init,service,destory是生命周期方法**：

```java
void init(ServletConfig var1) throws ServletException;

ServletConfig getServletConfig();

void service(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;

String getServletInfo();

void destroy();
```

##### 生命周期方法

- init()方法，**只执行一次**：只有当该Servlet第一次被请求时，Servlet容器会调用一次这个方法。无论有多少客户机访问Servlet，都不会再重复执行这个方法。我们可以利用这个方法执行相应的初始化工作。

- service()方法，**客户端每次请求Servlet都会执行**：它是Servlet的核心，每当请求Servlet时，Servlet容器就会调用这个方法，而且每次请求都会传递给这个方法一个“请求”（ServletRequest）对象和一个“响应”（ServletResponse）对象作为参数。第一次请求Servlet时，Servlet容器调用init()方法和service()方法。后续的请求将只调用service()方法。

  通常service()方法会根据需要调用与请求对应的**doGet或doPost**等方法（是HttpServlet这个类处理的）。

- destroy()方法，**只执行一次**：当服务器关闭或项目被卸载时服务器会将Servlet实例销毁，此时会调用Servlet的destroy()方法。此时init()方法中的所有初始化工作将被销毁。

Servlet中有时会用到一些需要初始化与销毁的资源，因此可以把初始化资源的代码放入init方法中，销毁资源的代码放入destroy方法中，这样就不需要每次处理客户端的请求都要初始化与销毁资源。

##### 生命周期方法中的几个参数

- ServletRequest：对于每一个HTTP请求，Servlet容器都会创建一个ServletRequest实例，并将它传给Servlet的Service方法。ServletRequest封装了关于这个请求的信息。

- ServletResponse:：javax.servlet.ServletResponse接口表示一个Servlet响应。在**调用Servlet的Service方法前**，Servlet容器会首先创建一个ServletResponse，并将它作为第二个参数传给Service方法。ServletResponse隐藏了向浏览器发送响应的复杂过程。

  在ServletResponse中定义的方法之一是getWriter方法，它返回了一个可以向客户端发送文本的java.io.PrintWriter。默认情况下，PrintWriter对象使用**ISO-8859-1**编码。

- ServletConfig：Servlet的初始化方法(init方法)会传入一个ServletConfig。它里面封装的是初始参数（是key，value键值对），初始参数的设置可以通过web.xml和@WebServlet设置。该接口中包含了如下方法：

```java
    String getServletName();

    ServletContext getServletContext();

    String getInitParameter(String var1);

    Enumeration<String> getInitParameterNames();
```

通过在ServletConfig中调用**getServletContext**方法，可以获取ServletContext，它表示一个Servlet应用程序。每个Web应用程序只有一个上下文。如果将一个应用程序同时部署到多个容器的分布式环境中时，每台java虚拟机上的Web应用都会有一个ServletContext对象。有了ServletContext，就可以共享从应用程序中可以访问到的信息，并且可以动态注册Web对象。

可以通过**java.util.Enumeration<java.lang.String> getInitParameterNames()**获取所有的初始参数名称；

可以通过**java.lang.String getInitParameter(java.lang.String name)**获取到初始参数的值。

##### GenericServlet和HttpServlet类

- GenericServlet类

由于Servlet类是一个接口，如果通过实现Servlet接口来写Servlet的话，那我们就必须实现它的所有方法（5个），即便其中有一些根本就没有包含任何代码。所以我们那样做是没有意义的。还好有GenericServlet类的出现，它是一个抽象类，实现了Servlet和ServletConfig接口，并完成以下任务：

> - 将init方法中的ServletConfig赋给一个类级变量，以便可以通过调用getServletConfig获取。
> - 为Servlet接口中的所有方法提供默认的实现。
> - 提供方法，包含ServletConfig中的方法。

- HttpServlet类

HttpServlet类覆盖了GenericServlet类。使用HttpServlet时，还要借助分别代表Servlet请求和Servlet响应的HttpServletRequest和HttpServletResponse对象，HttpServletRequest和HttpServletResponse分别继承自ServletRequest和ServletResponse，并新增了一些方法（比如：Cookie相关，Session相关方法）。

在HttpServlet类中，原始的Service(覆写自GenericServlet)方法将Servlet容器request和response对象分别转换成HttpServletRequest和HttpServletResponse，并调用新的Service方法。

在新的Service方法中会检验用来发送请求的Http方法（通过调用request.getMethod），并调用相对应的方法（doGet、doPost、doHead、doPut、doTrace、doOptions和doDelete）,这七种方法中，每一种方法都表示一个Http方法，doGet和doPost是最常用的。

因此，我们继承自HttpServlet的Servlet就不用覆盖Service方法了，只需要覆盖我们需要的对应http方法即可。

## 三大组件之Filter

> Filter是拦截Request请求的对象：在用户的请求资源前处理ServletRequest以及ServletResponse,它可用于日志记录、加解密、Session检查、图像文件保护等。通过Filter可以拦截处理某个资源或者某些资源。Filter的配置可以通过Annotation或者部署描述来完成。当一个资源或者某些资源需要被多个Filter所使用到，且它的触发顺序很重要时，只能通过部署描述（web.xml）来配置。

### Filter API

filter与servlet在很多的方面极其相似，但是也有不同，例如**filter和servlet一样都有三个生命周期方法**，同时他们在web.xml中的配置文件也是差不多的、 但是servlet主要负责处理请求，而filter主要负责拦截请求，和放行。 

Filter的实现必须继承```javax.servlet.Filter```接口。这个接口中定义了**三个生命周期方法**：

```java
void init(FilterConfig var1) throws ServletException;

void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

void destroy();
```

#### 生命周期方法

- init()方法，**只执行一次**：Servlet容器初始化Filter时，会触发Filter的init方法，一般来说是在应用开始时，而不是在该Filter相关的资源使用到时才初始化。
- doFilter()方法，**Servlet容器每次处理Filter相关的资源时都会调用**：在Filter的doFilter的实现中，最后一行需要调用FilterChain中的doChain方法。doFilter方法里的第三个参数就是filterChain的实例。```filterChain.doFilter(request,response)```,一个资源可能需要被多个Filter关联到（过滤链），这时Filter.doFilter()的方法将触发Filter链的下一个Filter。只有在过滤链中最有一个Filter里调用的```FilterChain.doFilter()```,才会触发处理资源的方法。
- destroy()方法，**执行一次**：该方法在Servlet容器要销毁Filter时才触发，一般在应用停止的时候进行调用。

#### 生命周期方法中的几个参数

- ServletRequest

- ServletResponse

- FilterConfig

  这与Servlet生命周期中的方法参数用法差不多。可以参考Servlet生命周期中的参数用法。

### filter四种拦截方式

- REQUEST（默认）：直接访问目标资源时执行过滤器。包括：在地址栏中直接访问、表单提交、超链接、重定向，只要在地址栏中可以看到目标资源的路径，就是REQUEST；
- FORWARD：转发访问执行过滤器。包括RequestDispatcher#forward()方法、< jsp:forward>标签都是转发访问；
- INCLUDE：包含访问执行过滤器。包括RequestDispatcher#include()方法、< jsp:include>标签都是包含访问；
- ERROR：当目标资源在web.xml中配置为< error-page>中时，并且真的出现了异常，转发到目标资源时，会执行过滤器。

filter默认是使用request拦截方式的。如果要实现不同的方式拦截，我们需要在部署描述中(web.xml)中配置，配置规则：

- forward：在```<Filter-mapping>```中添加```<Dispacher>forward</Dispachaer>```（这个字段会把之前的request的值进行覆盖。并且会拦截需要转发的请求）

- include：include 的用法和dispacher 的用法相似

- error：error的用法是用来拦截错误信息页面，我们可以在web.xml的页面中配置错误页面的信息，具体配置如下（在Mapping之外配置）：

  ```xml
  <error-page>
   <error-code>500</error-code>//此处应该写的数错误状态码
   <location>/index.jsp</location>//此处应该写的是拦截的具体路径
  </error-page>
  ```

  错误页面的信息配置完成之后，我们就可以像之前的request拦截一样，在```<Filter-mapping>```中进行配置```<Dispacher>error</Dispachaer>```这时候，当我们要访问的页面出错误时，过滤器便会自动进行拦截，同时我们可以使用```request.sendError(500,"错误信息")；```来让页面产生错误。

### url-mapping匹配规则的写法 

匹配规则有三种：

- 精确匹配 —— 如/foo.htm，只会匹配foo.htm这个完整的URL
- 路径匹配 —— 如/foo/\*，会匹配以foo为前缀的URL
- 后缀匹配 —— 如\*.htm，会匹配所有以.htm为后缀的URL

< url-pattern>的其他写法，不能是属于路径映射，也属于扩展映射，否则会导致容器无法判断，如```/admin/*.html，/foo/ ，/.htm ，/foo ```都是不对的。 

### 执行filter的顺序 

如果有多个过滤器都匹配该请求，顺序决定于web.xml中filter-mapping的顺序，在前面的先执行，后面的后执行 。

## 三大组件之Listener

> Servlet API提供多个监听器类型。这些监听器可以分为三类：application范围、session范围、request范围。它可以监听Application、Session、Request对象，当这些对象发生变化就会调用对应的监听方法。 

### ServletContext监听

> ServletContext（监听Application）。此范围的监听有两个监听接口：```ServletContextListener```和```ServletContextAttributeListener```。

#### ServletContextListener

```ServletContextListener```能对ServletContext的创建和销毁做出响应，该接口有两个方法。

当```ServletContext```初始化时，容器会调用所有注册的```ServletContextListener```的`contextInitialized`方法。

当`ServletContext`将要销毁时，容器会调用所有注册的```ServletContextListener```的`contextDestroyed`方法。

这两个方法的定义如下：

```java
void contextInitialized(ServletContextEvent event);
```

```java
void contextDestroyed(ServletContextEvent event);
```

这两个方法都有一个参数```ServletContextEvent```,它是```java.util.EventObject```的子类，它定义了一个访问ServletContext的方法，如下：

```java
public class ServletContextEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public ServletContextEvent(ServletContext source) {
        super(source);
    }

    public ServletContext getServletContext() {
        return (ServletContext)super.getSource();
    }
}
```

#### ServletContextAttributeListener

`ServletContextAttributeListener`能对ServletContext范围属性的添加、删除或替换进行监听，该接口有三个方法。

当在`ServletContext`范围内的属性被添加、删除、替换时，会分别调用`attributeAdded`、`attributeRemoved`、`attributeReplaced`方法。

这三个方法的定义如下：

```java
void attributeAdded(ServletContextAttributeEvent var1);
```

```java
void attributeRemoved(ServletContextAttributeEvent var1);
```
```java
void attributeReplaced(ServletContextAttributeEvent var1);
```
这三个方法都有一个参数```ServletContextAttributeEvent```,它继承自```ServletContextEvent```，并且增加了如下两个方法分别用于获取该属性的名称和值。

```java
    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
```

### HttpSession 监听

> HttpSession （监听Session），该范围内有四个相关的监听接口：`HttpSessionListener`,`HttpSessionActivationListener`,`HttpSessionAttributeListener`,`HttpSessionBindingListener`。

#### HttpSessionListener

它所实现的功能和`ServletContextListener`实现的功能类似，只是`ServletContextListener`的作用范围是在ServletContext，而`HttpSessionListener`的作用范围是HttpSession。

#### HttpSessionActivationListener

在分布式环境下，会用多个容器来进行负载均衡，有可能需要将session保存起来，在容器之间传递。例如当一个容器内存不足时，会把很少用到的对象转存到其它容器上。这时候，容器就会通知所有`HttpSessionActivationListener`接口的实现类。该接口有如下两个方法：

```java
public interface HttpSessionActivationListener extends EventListener {
    void sessionWillPassivate(HttpSessionEvent var1);

    void sessionDidActivate(HttpSessionEvent var1);
}
```

当HttpSession将要失效时，`sessionWillPassivate`方法会被调用。

当HttpSession被转移到其它容器之后，`sessionDidActivate`方法会被调用。

这两个方法中，容器都会 将一个`HttpSessionEvent`方法传递到方法里，可以从这个对象中获取HttpSession。

#### HttpSessionAttributeListener

`HttpSessionAttributeListener`接口和`ServletContextAttributeListener`类似，只不过它响应的是HttpSession范围属性的添加、删除和替换。

#### HttpSessionBindingListener

当有属性绑定或者解绑到HttpSession上时，`HttpSessionBindingListener`监听器会被调用。如果对HttpSession属性的绑定和解绑动作感兴趣，就可以实现这个接口来监听。例如可以在HttpSession属性绑定时更新状态，或者在属性解绑时释放资源。

#### HttpSessionAttributeListener和HttpSessionBindingListener的区别

> 参考：[https://www.cnblogs.com/Neil223/p/5221154.html](https://www.cnblogs.com/Neil223/p/5221154.html)

1. 只有实现了`HttpSessionBindingListener`的类，在和session绑定、解除绑定时触发其事件。
2. 实现了`HttpSessionAttributeListener`后，任何对象（不论其是否实现了`HttpSessionAttributeListener`)在变化时均触发对应的事件。

#### HttpSessionBindingListener和HttpSessionActivationListener

> HttpSessionBindingListener和HttpSessionActivationListener又叫对象感知监听器
>
> 参考：[https://blog.csdn.net/qq_39218765/article/details/80346106](https://blog.csdn.net/qq_39218765/article/details/80346106)

- HttpSessionBindingListener监听 
  ⑴在需要监听的实体类实现HttpSessionBindingListener接口 
  ⑵重写valueBound()方法，这方法是在当该实体类被放到Session中时，触发该方法 
  ⑶重写valueUnbound()方法，这方法是在当该实体类从Session中被移除时，触发该方法 

- HttpSessionActivationListener监听 

  ⑴在需要监听的实体类实现HttpSessionActivationListener接口 

  ⑵重写sessionWillPassivate()方法，这方法是在当该实体类被序列化时，触发该方法 

  ⑶重写sessionDidActivate()方法，这方法是在当该实体类被反序列化时，触发该方法

### ServletRequest监听

> ServletRequest(监听request)，ServletRequest范围的监听器接口有三个：`ServletRequestListener`,`ServletRequestAttributeListener`,`AsyncListener`，其中`AsyncListener`是web3.0中新增的，用来配合Servlet和过滤器执行异步操作的。

#### ServletRequestListener

`ServletRequestListener`监听器会对ServletRequest的创建和销毁事件进行响应。容器会通过一个池来存放并重复利用多个ServletRequest，ServletRequest的创建是从容器池里被分配出来的时刻开始，而它的销毁时刻是放回容器池里的时间。`ServletRequestListener`和`ServletContextListener`类似有两个方法，两个方法的定义如下：

```java
void requestDestroyed(ServletRequestEvent var1);
```

```java
void requestInitialized(ServletRequestEvent var1);
```

当一个ServletRequest创建（从容器池里取出）时，`requestInitialized`会被调用，当ServletRequest销毁（被容器回收）时，`requestDestroyed`会被调用。

这两个方法都会接收到一个`ServletRequestEvent`对象，可以通过使用这个对象的`getServletContext`方法来获取`ServletContext`对象，也可以通过`getServletRequest`方法来获取`ServletRequest`对象。`ServletRequestEvent`接口的定义如下：

```java
public class ServletRequestEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private final transient ServletRequest request;

    public ServletRequestEvent(ServletContext sc, ServletRequest request) {
        super(sc);
        this.request = request;
    }

    public ServletRequest getServletRequest() {
        return this.request;
    }

    public ServletContext getServletContext() {
        return (ServletContext)super.getSource();
    }
}
```

#### ServletRequestAttributeListener

> 它和`ServletContextAttributeListener`或`HttpSessionAttributeListener`类似，只不过它监听的是ServletRequest范围属性的添加、删除和替换，对应提供的三个方法是`attributeAdded`,`attributeRemoved`,`attributeReplaced`。

`ServletRequestAttributeListener`接口中的三个方法都可以获得一个继承自`ServletRequestEvent`的`ServletRequestAttributeEvent`对象。

## 三大组件的部署描述符（web.xml）

在Servlet 3.0中虽然可以通过注解的方式部署不太复杂的应用程序，但有些时候仍然需要部署描述符，比如：需要传递初始参数给ServletContext，指定多个过滤器的调用顺序，更改会话超时设置，限制资源访问，配置用户身份验证方式等。

官方文档：[http://www.oracle.com/webfolder/technetwork/jsc/xml/ns/javaee/web-common_4_0.xsd](http://www.oracle.com/webfolder/technetwork/jsc/xml/ns/javaee/web-common_4_0.xsd)，（下载后可以用visual studio 打开可以看到目录）官方文档中详细说明了web.xml中的各种元素的配置规则。

参考：[http://www.cnblogs.com/hafiz/p/5715523.html](http://www.cnblogs.com/hafiz/p/5715523.html)

## 异步处理

> Web3.0引入的新功能，可以使用异步的Servlet和过滤器，以及异步监听器。

### 异步Servlet和过滤器

编写异步Servlet和过滤器需要添加`asyncSupport`属性（无论是注解方式还是xml方式），然后通过调用`ServletRequest`的`startAsync`方法来启动一个新线程以实现异步处理的功能，最后需要在Runnable中需要调用`asyncContext.complete();`或者`asyncContext.dispatch();`方法来完成任务例如下：

```java
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)     throws ServletException, IOException {
            final AsyncContext asyncContext = request.startAsync();
            asyncContext.setTimeout(5000);
            
            asyncContext.start(new Runnable() {
                @Override
                public void run() {
                    //do something...
                    
                    
                    
                    asyncContext.complete();
                    //or .. asyncContext.dispatch();
                }
            });
    }
```

### 异步的监听器

异步的监听器，则需要监听类实现`AsyncListener`接口，并且**必须**（不能用注解方式或web.xml方式注册）通过调用`addListener`方法为`AsyncContext`（调用`ServletRequest`的`startAsync`方法会返回一个`AsyncContext`对象）手动注册一个`AsyncListener`监听器，用于接收所需要的事件。如下：

```java
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)     throws ServletException, IOException {
            final AsyncContext asyncContext = request.startAsync();
            asyncContext.setTimeout(5000);
            
            asyncContext.addListener(new MyAsyncListener());//MyAsyncListener是一个实现了AsyncListener接口的类
    }
```



------

以上内容参考：

1. [https://blog.csdn.net/xiaojie119120/article/details/73274759](https://blog.csdn.net/xiaojie119120/article/details/73274759)

2. 《Servlet、JSP和Spring MVC初学指南》

