# RESTful API

> REST，即Representational State Transfer。
>
> RESTful 是目前最流行的 API 设计规范，用于 Web 数据接口的设计。

## RESTful架构

（1）每一个URI代表一种资源；

（2）客户端和服务器之间，传递这种资源的某种表现层；

（3）客户端通过四个HTTP动词，对服务器端资源进行操作，实现"表现层状态转化"。

## URL设计

动词+宾语：
&emsp;&emsp;动词通常就是五种 HTTP 方法，对应 CRUD 操作。

```
GET：读取（Read）
POST：新建（Create）
PUT：更新（Update）
PATCH：更新（Update），通常是部分更新
DELETE：删除（Delete）
```
&emsp;&emsp;宾语就是 API 的 URL，是 HTTP 动词作用的对象。它应该是复数名词，不能是动词。比如，`/articles`这个 URL 就是正确的，而诸如`/getAllCars`，`/deleteAllRedCars`等 URL 不是名词，所以都是错误的。

## 状态码

> 客户端的每一次请求，服务器都必须给出回应。回应包括 HTTP 状态码和数据两部分。

### 2xx状态码

200状态码表示操作成功，但是不同的方法可以返回更精确的状态码。

```
GET: 200 OK
POST: 201 Created //生成了新的资源
PUT: 200 OK
PATCH: 200 OK
DELETE: 204 No Content //资源已经不存在
```
此外，202 Accepted状态码表示服务器已经收到请求，但还未进行处理，会在未来再处理，通常用于异步操作。

### 3xx状态码

API 用不到301状态码（永久重定向）和302状态码（暂时重定向，307也是这个含义），因为它们可以由应用级别返回，浏览器会直接跳转，API 级别可以不考虑这两种情况。

API 用到的3xx状态码，主要是`303 See Other`，表示参考另一个 URL。它与302和307的含义一样，也是"暂时重定向"，区别在于302和307用于GET请求，而303用于POST、PUT和DELETE请求。收到303以后，浏览器不会自动跳转，而会让用户自己决定下一步怎么办。下面是一个例子。

```
HTTP/1.1 303 See Other
Location: /api/orders/12345
```

### 4xx 状态码


4xx状态码表示客户端错误，主要有下面几种。

```
400 Bad Request：服务器不理解客户端的请求，未做任何处理。
401 Unauthorized：用户未提供身份验证凭据，或者没有通过身份验证。
403 Forbidden：用户通过了身份验证，但是不具有访问资源所需的权限。
404 Not Found：所请求的资源不存在，或不可用。
405 Method Not Allowed：用户已经通过身份验证，但是所用的 HTTP 方法不在他的权限之内。
410 Gone：所请求的资源已从这个地址转移，不再可用。
415 Unsupported Media Type：客户端要求的返回格式不支持。比如，API 只能返回 JSON 格式，但是客户端要求返回 XML 格式。
422 Unprocessable Entity ：客户端上传的附件无法处理，导致请求失败。
429 Too Many Requests：客户端的请求次数超过限额。
```

### 5xx 状态码

5xx状态码表示服务端错误。一般来说，API 不会向用户透露服务器的详细信息，所以只要两个状态码就够了。

```
500 Internal Server Error：客户端请求有效，服务器处理时发生了意外。
503 Service Unavailable：服务器无法处理请求，一般用于网站维护状态。
```
## 幂等性
> 参考：https://www.cnblogs.com/weidagang2046/archive/2011/06/04/idempotence.html
> HTTP方法的幂等性是指一次和多次请求某一个资源应该具有同样的副作用。 
一个幂等的操作典型如：把编号为5的记录的A字段设置为0，这种操作不管执行多少次都是幂等的。
一个非幂等的操作典型如：把编号为5的记录的A字段增加1，这种操作显然就不是幂等的。

- HTTP GET 方法：**幂等**。用于获取资源，不应有副作用（并不是指每次GET的结果相同），所以是幂等的。幂等性指的是作用于结果而非资源本身。怎么理解呢？这个 HTTP GET 方法可能会每次得到不同的返回内容，但并不影响资源。
- HTTP DELETE 方法：**幂等**。调用一次和N次对资源产生的副作用是相同的，即都是删除资源。 比如：`DELETE /article/4231`，调用一次和N次对系统产生的副作用是相同的，即删掉id为`4231`的帖子 。因此，调用者可以多次调用或刷新页面而不必担心引起错误。
- HTTP PUT 方法：**幂等**。PUT所对应的URI是要创建或更新的资源本身。比如：`PUT /articles/4231`的语义是创建或更新ID为`4231`的帖子。对同一URI进行多次PUT的副作用和一次PUT是相同的；因此，PUT方法具有幂等性。
- HTTP POST 方法：**非幂等**。调用多次，都将产生新的资源。POST所对应的URI并非创建的资源本身，而是资源的接收者。比如：`POST /articles`的语义是在`/articles`下创建一篇帖子，HTTP响应中应包含帖子的创建状态以及帖子的URI。两次相同的POST请求会在服务器端创建两份资源，它们具有不同的URI；所以，POST方法不具备幂等性。
- HTTP PATCH 方法：**非幂等**。PATCH 提供的实体则需要根据程序或其它协议的定义，解析后在服务器上执行，以此来修改服务器上的资源。换句话说，PATCH 请求是会执行某个程序的，如果重复提交，程序可能执行多次，对服务器上的资源就可能造成额外的影响，所以它是非幂等的。

## 参考

1. [阮一峰的网络日志-理解RESTful架构](http://www.ruanyifeng.com/blog/2011/09/restful.html)
2. [阮一峰的网络日志-RESTful API 最佳实践](http://www.ruanyifeng.com/blog/2018/10/restful-api-best-practices.html)