# JavaWeb其它基础

## Servlet与线程安全

**Servlet在多线程下其本身并不是线程安全的，多线程并发的读写会导致数据不同步的问题。**。

如果在类中定义成员变量，而在service中根据不同的线程对该成员变量进行更改，那么在并发的时候就会引起错误。最好是在方法中，定义局部变量，而不是类变量或者对象的成员变量。由于方法中的局部变量是在栈中，彼此各自都拥有独立的运行空间而不会互相干扰，因此才做到线程安全。

## 转发(Forward)和重定向(Redirect)的区别

**转发是服务器行为，重定向是客户端行为。**

**转发（Forword）** 通过`RequestDispatcher`对象的`forward（HttpServletRequest request,HttpServletResponse response）`方法实现的。`RequestDispatcher`可以通过`HttpServletRequest` 的`getRequestDispatcher()`方法获得。

例如下面的代码就是servlet中跳转到login.jsp页面。

```java
request.getRequestDispatcher("login.jsp").forward(request, response);
```

jsp中的实现方式forward跳转：

```jsp
<jsp:forward page="index.htm"/>
```

**重定向（Redirect）** 是利用服务器返回的状态吗来实现的。客户端浏览器请求服务器的时候，服务器会返回一个状态码。服务器通过`HttpServletRequestResponse`的`setStatus(int status)`方法设置状态码。如果服务器返回301或者302，则浏览器会到新的网址重新请求该资源。

实现重定向的方法：

```java
 response.sendRedirect("/index.jsp"); 
```

或者

```java
response.setHeader("Location","/index.jsp"); 
```

- **从地址栏显示来说**

forward：服务器获取跳转页面内容传给用户，**用户地址栏不变**

redirect：是服务器向用户发送转向的地址，**地址栏变成新的地址**

forward是服务器请求资源,服务器直接访问目标地址的URL,把那个URL的响应内容读取过来,然后把这些内容再发给浏览器.浏览器根本不知道服务器发送的内容从哪里来的,所以它的地址栏还是原来的地址.；

redirect是服务端根据逻辑,发送一个状态码,告诉浏览器重新去请求那个地址.所以地址栏显示的是新的URL。

- **从数据共享来说**

forward:转发页面和转发到的页面**可以共享**request里面的数据。

 redirect:**不能共享数**据.

- **从运用地方来说**

forward:一般用于用户登陆的时候,根据角色转发到相应的模块。

redirect:一般用于用户注销登陆时返回主页面和跳转到其它的网站等。

- **从效率来说**

forward:高。 redirect:低。

## 自动刷新(Refresh)

自动刷新不仅可以实现一段时间之后自动跳转到另一个页面，还可以实现一段时间之后自动刷新本页面。Servlet中通过`HttpServletResponse`对象设置Header属性实现自动刷新。例如：

```java
Response.setHeader("Refresh","5;URL=http://localhost:8080/servlet/example.htm");
```

其中5为时间，单位为秒。URL指定就是要跳转的页面（如果设置自己的路径，就会实现每过一秒自动刷新本页面一次）