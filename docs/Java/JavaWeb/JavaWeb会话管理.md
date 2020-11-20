# 会话管理（会话跟踪技术）

## HttpSession对象

> 在所有的会话跟踪技术中，HttpSession对象是最强大和最通用的。一个用户可以有且最多有一个HttpSession,并且不会被其它用户访问到。

HttpSession对象在用户第一次访问网站的时候自动被创建。

可以通过调用```HttpServletRequest```的```getSession```方法获取该对象。

可以通过```HttpSession```的```setAttribute```方法将值放入HttpSession，同样也可以通过```getAttribute```方法取回之前放入的对象。**注意：如果放入HttpSession的值是对象类型，则该对象必须实现java.io.Serializable，否则Servlet容器视图序列化的时候会失败并报错，因为Servlet容器认为必要时会将存入的对象放入文件或数据库中，尤其在内存不够的时候。**

每个HttpSession有会话过期时间，可以调用```getMaxInactiveInterval```方法来查看会话多久过期。可以调用```setMaxInactiveInterval```方法来单独对某个HttpSession设定其超时时间，设置为0，则该HttpSession永不过期。通过调用```invalidate```方法强制会话过期并清空保存的对象。

## Cookies

> Cookies是一个很少的信息片段，可自动地在浏览器和Web服务器间进行交互，因此cookies可存储在多个页面间传递的信息。Cookie作为HTTP header的一部分，其传输由HTTP协议控制。你可以控制cookies的有效时间。浏览器通常支持每个网站高达20个cookies。

向客户端发送Cookie

```java
Cookie c =new Cookie("name","value"); //创建Cookie 
c.setMaxAge(60*60*24); //设置最大时效，此处设置的最大时效为一天
response.addCookie(c); //把Cookie放入到HTTP响应中
```

从客户端读取Cookie

```java
Cookie[] cookies =request.getCookies(); 
if(cookies !=null){ 
   for(Cookie cookie : cookies){ 
       if("name".equals(cookie.getName())){
            //do  something
           cookie.getValue(); 
       } 
   }
 }
```

**优点:** 数据可以持久保存，不需要服务器资源，简单，基于文本的Key-Value

**缺点:** 大小受到限制；用户可以禁用Cookie功能；由于保存在本地，有一定的安全风险。

## URL重写

> 在URL中添加用户会话的信息作为请求的参数，或者将唯一的会话ID添加到URL结尾以标识一个会话。适用于信息仅在少量页面间传递，且信息本身不敏感。

它将一个或多个token添加到URL的查询字符串中，每个token通常为key=value形式，如下：

```
http:localhost:8080?key1=value1&key2=value2
```

后台就需要通过判断value值以访问不同的页面(其实页面是同一个，只是数据不同)，例如：

```java
String value1 = request.getParmeter("key1");
if("value1".equals(value1)){
    //页面数据不同,页面数据1
}else{
	//页面数据不同,页面数据2
}
```

**优点**：在Cookie被禁用的时候依然可以使用。

**缺点**：url有长度限制；某些字符，如空格、问好等需要base64编码；所有页面必须动态生成，不能用预先记录下来的URL进行访问；不适用于跨越多个页面。

## 隐藏域

> 使用隐藏域来保持状态类似于URL重写技术，但不是将值直接附加到URL上，而是放到HTML表单的隐藏域中。当表单提交时，隐藏域的值也同时提交到服务器端。

```html
<form method="post" action="...">
    <input type='hidden' name='id' value='1'/>
    //other elements

</form>
```

**优点：** Cookie被禁时可以使用

**缺点：** 使用场景有限制，所有页面必须是表单提交之后的结果；不适用于跨越多个页面。

## Cookies和Session的区别

> 参考自知乎，链接：[https://www.zhihu.com/question/19786827/answer/28752144](https://www.zhihu.com/question/19786827/answer/28752144)

- 由于HTTP协议是无状态的协议，所以服务端需要记录用户的状态时，就需要用某种机制来识具体的用户，这个机制就是Session.典型的场景比如购物车，当你点击下单按钮时，由于HTTP协议无状态，所以并不知道是哪个用户操作的，所以服务端要为特定的用户创建了特定的Session，用用于标识这个用户，并且跟踪用户，这样才知道购物车里面有几本书。这个**Session是保存在服务端的，有一个唯一标识**。在服务端保存Session的方法很多，内存、数据库、文件都有。集群的时候也要考虑Session的转移，在大型的网站，一般会有专门的Session服务器集群，用来保存用户会话，这个时候 Session 信息都是放在内存的，使用一些缓存服务比如Memcached之类的来放 Session。
- 思考一下服务端如何识别特定的客户？这个时候Cookie就登场了。每次HTTP请求的时候，客户端都会发送相应的Cookie信息到服务端。实际上大多数的应用都是用 Cookie 来实现Session跟踪的，第一次创建Session的时候，服务端会在HTTP协议中告诉客户端，**需要在 Cookie 里面记录一个Session ID**，以后每次请求把这个会话ID发送到服务器，我就知道你是谁了。有人问，**如果客户端的浏览器禁用了 Cookie** 怎么办？一般这种情况下，会使用一种叫做**URL重写的技术来进行会话跟踪**，即每次HTTP交互，URL后面都会被附加上一个诸如 sid=xxxxx 这样的参数，服务端据此来识别用户。
- Cookie其实还可以用在一些方便用户的场景下，设想你某次登陆过一个网站，下次登录的时候不想再次输入账号了，怎么办？这个信息可以写到Cookie里面，访问网站的时候，网站页面的脚本可以读取这个信息，就自动帮你把用户名给填了，能够方便一下用户。这也是Cookie名称的由来，给用户的一点甜头。

总结一下：
Session是在服务端保存的一个数据结构，用来跟踪用户的状态，这个数据可以保存在集群、数据库、文件中；
Cookie是客户端（浏览器）保存用户信息的一种机制，用来记录用户的一些信息，也是实现Session的一种方式。

也就是说Session和Cookie一般配合使用，但是就目前来说**Session不是一定依赖Cookies，可以使用HTML5中的LocalStorage实现**。