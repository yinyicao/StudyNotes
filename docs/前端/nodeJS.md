> - Node.js 是一个让 JavaScript 运行在服务端的开发平台。
> - Node.js 是一个让 JavaScript 运行在浏览器之外的平台。它实现了诸如文件系统、模块、包、操作系统 API、网络通信等 Core JavaScript 没有或者不完善的功能。
> - `Node.js`和`CommonJS`之间的关系： CommonJS是一种规范，Node.js是这种规范的实现。

## 1. 安装项

- Node.js--Node.js运行环境。

- npm--包管理器（用于Node.js中包的安装、删除、发布、维护等）

- n--多版本控制器，一个环境可以安装多个版本的Node.js，n可以便捷的在各个版本之间切换

  > 多版本控制器不支持windows系统
  >
  > ```shell
  > npm install -g n
  > ```

- supervisor--自动重新加载工具，因为Node.js只有在第一次引用到某部分时才会去解析脚本文件，之后都会直接访问内存。所以当修改某个代码文件之后需要冲洗加载运行才会生效，影响开发和调试效率，使用supervisor工具可以进行自动的重新加载。

  ```shell
  npm install -g supervisor
  ```

## 2. 包和模块

**包**

- 包用于管理多个模块及其依赖关系，可以对多个模块进行封装。

- 包的根目录必须包含`package.json` 文件。`package.json` 文件是 CommonJS 规范用于描述包的文件，可以进入包的根目录下，使用`npm init`命令以交互的方式创建包说明文件。

- CommonJS 规范的 `package.json` 文件一般包含以下字段：

    ```
    name：包名。包名是唯一的，只能包含小写字母、数字和下划线。
    version：包版本号。
    description：包说明。
    keywords：关键字数组，用于搜索。
    homepage：项目主页。
    bugs：提交 bug 的地址。
    license：许可证。
    maintainers：维护者数组。
    contributors：贡献者数组。
    repositories：项目仓库托管地址数组。
    dependencies：包依赖。
    ```
    
    注：package.json 文件可以自己手动编辑，还可以通过 npm init 命令进行生成。你可以自己尝试在终端中输入 npm init 命令来生成一个包含 package.json 文件的包。直接输入 npm init --yes 跳过回答问题步骤，直接生成默认值的 package.json 文件。此外，我们在 github 上传自己项目的时候，通常是不会把 node_modules 这个文件夹传上去的（太大了），只需要有 package.json 就能通过 npm install 命令安装所有依赖。

**模块**

- 每一个模块为一个 js 文件，每一个模块中定义的全局变量和函数的作用范围也被限定在这个模块之内，只有使用 exports 对象才能传递到外部使用，如`exports.one=function(){}`表示公开函数one。
- Node.js 官方提供了很多模块，这些模块分别实现了一种功能，如操作文件及文件系统的模块 fs，构建 http 服务的模块 http，处理文件路径的模块 path 等。当然我们也可以自己编写模块。
- 使用require() 函数引入模块，require() 加载模块，以 '/' 为前缀的模块是文件的绝对路径。'./' 为前缀的模块是相对于当前 require() 的文件路径调用的。
- 核心模块定义在 Node.js 源代码的 lib/ 目录下。require() 总是会优先加载核心模块。
- 当循环调用 require() 时，一个模块可能在未完成执行时被返回。

## 3. Node.js的函数

在 JavaScript 中，一个函数可以作为另一个函数的参数。我们可以先定义一个函数，然后把这个函数作为变量在另一个函数中传递，也可以在传递参数的地方直接定义函数。Node.js 中函数的使用与 Javascript 类似。

举例：

```javascript
function sayHi(value) {
  console.log(value);
}
function execute(someFunction, value) {
  someFunction(value);
}
execute(sayHi, 'hi');
```

上面的例子我们也可以写成：

```javascript
function execute(someFunction, value) {
  someFunction(value);
}
execute(function (value) {
  console.log(value);
}, 'hi');
```

### 匿名函数

匿名函数就是没有命名的函数。语法为：

```javascript
function(){

}
```

### 箭头函数

ES6 标准新增了一种新的函数，箭头函数表达式的语法比函数表达式更短，并且没有自己的 this，arguments，super 或 new.target。这些函数表达式更适用于那些本来需要匿名函数的地方，并且它们不能用作构造函数。

语法为:

```javascript
(参数 1, 参数 2, …, 参数 N) => {函数声明}

// 相当于：(参数 1, 参数 2, …, 参数 N) => {return 表达式;}
(参数 1, 参数 2, …, 参数 N) => 表达式（单一）

// 当只有一个参数时，圆括号是可选的
(单一参数) => {函数声明}
单一参数 => {函数声明}

// 没有参数的函数应该写成一对圆括号
() => {函数声明}
```

举例：

```javascript
function(){
    console.log('hello syl');
}
// 上面的匿名函数可以用箭头函数改写为下面的
() => console.log('hello syl');
```

因为我们在 Node.js 中会经常使用到匿名函数，为了缩减代码，我们可以使用箭头函数。一般来说上面语法中的简单使用就可以了。

## 4. Node.js异步编程

- Node.js 异步编程的直接体现就是回调。回调函数在完成任务后就会被调用，Node.js 使用了大量的回调函数，Node.js 所有 API 都支持回调函数。回调函数一般作为函数的最后一个参数出现。

- 回调函数：当需要等待的I/O操作时，单线程非阻塞模式不会像普通的多线模式一样等待，而是继续执行其他的运算，当I/O操作完成之后是发起一个回调函数的事件到事件队列，等待事件循环来处理。

- 阻塞和线程：了解普通的多线程模式和单线程非阻塞模式的区别，如下图：

  <img src="_images/前端/thread.png" />

- 事件：Node.js从开始启动到程序结束始终在进行事件循环，事件循环会检查事件队列中有没有未处理的事件。

  <img src="_images/前端/event.png" />

**阻塞代码实例**

先创建一个 syl.txt 的文件，里面随便输入一段文本内容，比如：hello syl!。

创建 a.js 代码为：

```javascript
var fs = require('fs');
var data = fs.readFileSync('syl.txt');
console.log(data.toString());
console.log('程序执行完毕!');
```

运行结果为：

```
hello syl!
程序执行完毕!
```

**非阻塞代码实例**

把前面 a.js 的代码改为：

```javascript
var fs = require('fs');
fs.readFile('syl.txt', function (err, data) {
  if (err) return console.error(err);
  console.log(data.toString());
});

console.log('程序执行完毕!');
```

运行结果为：

```
程序执行完毕!
hello syl!
```

从这两个实例中我们可以初步地体验到阻塞和非阻塞的不同之处。第一个实例在文件读取完后才执行完程序。第二个实例我们不需要等待文件读取完，这样就可以在读取文件时同时执行接下来的代码，大大提高了程序的性能。

## 5. Express框架

Express 是一个高度包容，快速而极简的 Node.js Web 框架，提供了一系列强大特性帮助我们创建各种 Web 应用，和丰富的 HTTP 工具。我们可以通过 Express 可以快速地搭建一个完整功能的网站。使用框架的目的就是让我们更加专注于业务，而不是底层细节。 

1)以下命令安装 Express： `npm install express`

2)第一个 Express 框架实例

我们的第一个实例是输出 “hello world”，新建 hello.js 文件，代码如下所示：

```javascript
var express = require('express');
var app = express();

app.get('/', function (req, res) {
  res.send('Hello World');
});

app.listen(8080, function () {
  console.log('服务器启动了');
});
```

执行以上代码：

```shell
node hello.js
```

### 路由

路由用于确定应用程序如何响应客户端请求，包含一个 URI（路径）和一个特定的 HTTP 请求方法（GET、POST 等）。

每个路由可以具有一个或多个处理程序函数，这些函数在路由匹配时执行。

路由定义采用以下结构：

```javascript
app.method(path, handler);
```

注：app 是 Express 的实例，method 是指 HTTP 请求方法（GET、POST 等），path 是指服务器上的路径，handler 是指路由匹配时执行的函数。

下面我们来看一下简单的例子来学习如何定义路由，新建 test.js 文件代码如下所示：

```javascript
var express = require('express');
 var app = express();

 // GET 请求
 app.get('/', function (req, res) {
   console.log('GET 请求');
   res.send('Hello，我是GET请求');
 });

 // POST 请求
 app.post('/', function (req, res) {
   console.log('POST 请求');
   res.send('Hello，我是 POST 请求');
 });

 // /index 响应 index 页面 GET 请求
 app.get('/index', function (req, res) {
   console.log('/响应index页面 GET 请求');
   res.send('Hello，我是 index 页面 GET 请求');
 });

 // 对页面 abcd, abxcd, ab123cd, 等响应 GET 请求
 app.get('/ab*cd', function (req, res) {
   console.log('/ab*cd GET 请求');
   res.send('Hello，我是正则匹配');
 });

 app.listen(8080, function () {
   console.log('服务器启动了');
 });
```

运行上述代码：

```shell
node test.js
```

对比一下我们前面用原生的 http，少了很多的判断语句，代码量也大大减少了，而这也是我们 Express 的魅力所在。我们后面将继续学习如何使用 Express 框架，其实 Express 框架之与 Node.js 就相当于 jQuery 之与 JavaScript。

### 静态文件

Express 提供了内置的中间件 express.static 来设置静态文件如：图片，CSS，JavaScript 等。比如：

```javascript
// 我们只有公开了 public 目录，才可以直接通过 /public/xx 的方式访问 public 目录中的资源
app.use('/public/', express.static('./public/'));
```

来看个完整的例子，首先新建 public 目录，在 public 目录下新建 test.txt 文，里面随便写一句话：我爱学习，身体棒棒！然后在 project 目录下新建一个 testStatic.js 的文件代码如下：

```javascript
var express = require('express');
var app = express();

app.use(express.static('public'));

app.get('/', function (req, res) {
  res.send('Hello World');
});

app.listen(8080, function () {
  console.log('服务器启动了');
});
```

执行上面的代码，文件命名为test.txt：

```shell
node testStatic
```

然后就可以直接在浏览器请求到静态文件：`http://localhost:8080/test.txt`。

大家可以自行尝试把 app.use(express.static('public')); 这行代码注释掉运行后会显示 Cannot GET /test.txt。

## 6. 使用Express搭建开发环境

### 安装Express包

安装express时增加generator参数： 

```shell
npm install -g express-generator
```
express 已经把命令行工具分离出来了； 原因：4以前版本的express带cli, 现在把cli拆成了单独的express-generator包. 原先的express运行生成的项目是 node app.js, 因为httpserver相关代码都在app.js里, 现在这部分代码移到了项目目录的bin/www下面, app.js 只保留实现app的逻辑代码, 你需要去运行那个bin/www。 只是很单纯的细化应用和包依赖的版本变更。
*安装路径在：C:\Users\\<NAME>\AppData\Roaming\npm\node_modules>*

### 创建项目

```shell
express nodedemo
```
默认使用官方推荐的jade模板。
执行完成后，根据提示安装依赖包： `cd nodedemo & npm install`

### 启动项目
```shell
 SET DEBUG=nodedemo:* & npm start
```

这里需要注意 express 4.x 无法以 node app.js 为启动方式，而是用指令 `npm start` 作为启动；
访问测试：`http://localhost:3000`

> 可以在app.js中使用`process.env.PORT = 1337`修改端口号

**注意**：生成的demo，会拦截所有路由响应`404`，需要在app.js中修改，以免造成困扰。

```javascript
app.use(function(req, res, next) {
  res.status(404);
  res.send('404: Not Found');
});
```

### 安装supervisor

supervisor 可让修改代码后不用手动重启服务器，服务器自动监控文件修改并重启，安装命令：

``` shell
npm -g install supervisor
```
启动方式为： `supervisor bin\www`
*不要进入bin里面执行 supervisor www ，这样只会"watching" bin 目录* （屏幕会打印Watching directory字样）