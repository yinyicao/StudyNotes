### 安装webpack与webpack-dev-server

#### 1.安装Node.js和NPM

#### 2.NPM初始化配置

**①** 新建目录`demo`，执行：

```bash
npm init
```

生成package.json文件

#### 3.局部安装webpack

```bash
npm install webpack --save-dev
```

`--save-dev`作为开发依赖安装webpack，package.json文件中多处一项`devDependencies`。

#### 4.局部安装webpack-dev-server

```bash
npm install webpack-dev-server --save-dev
```

`webpack-dev-server`提供服务，如启动一个服务器、热更新、接口代理等。

此时的package.json文件内容：

```json
{
  "name": "zhihu-daily",
  "version": "1.0.0",
  "description": "知乎日报-Vue实战",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "yicao",
  "license": "ISC",
  "devDependencies": {
    "webpack": "^5.39.0",
    "webpack-dev-server": "^3.11.2"
  }
}
```

### webpak基本概念

webpack本质上就是一个.js配置文件，这个配置文件的内容直接影响整个项目的架构，随着项目的进行，这个配置文件会逐步完善。

**重要概念：** 入口(Entry)、出口(Output)、加载器(Loaders)、插件(Plugins)。

#### 1.配置文件

在`package.json`同级目录下新建配置文件`webpack.config.js`，初始化内容：

```javascript
var config = {
    
};

module.exports = config;
```

`module.exports = config;`相当于ES6中的`export default config;`

在`package.json`文件的scripts里增加一个快速启动的webpack-dev-server服务脚本：

```json
{
 // ...
"scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "dev": "webpack-dev-server --open  --config webpack.config.js"
  },
 //...
 }
```

运行`npm run dev`就会执行`webpack-dev-server --open  --config webpack.config.js`，`--config`指向webpack-dev-server读取的配置文件路径，`--open`自动在浏览器打开页面，默认地址`127.0.0.1:8080`。可以通过`--host` 和`--port`配置IP和端口，如：

```bash
webpack-dev-server --open --host 127.0.0.2 --port 8888 --config webpack.config.js
```

当然，此时运行`npm run dev`会失败，因为这时的webpack项目是不完整的，还需要一些其它的必须配置。

#### 2.配置入口(Entry)和出口(Output)文件

webpack在配置文件中必须配置入口（Entry）和出口（Output），入口的作用是告诉webpack从哪里开始寻找依赖，并且编译，出口则用来配置编译后的文件存储位置和文件名。

在`demo`目录下新建一个空的`main.js`文件作为入口文件，然后在webpack.config.js中进行入口和出口配置：

```javascript
var path = require('path');

var config = {
    entry: {
        main: './main' // 入口，表示从main.js开始工作
    },
    output: {
        path: path.join(__dirname,'./dist'), // 存放打包后文件的输出目录，必填
        publicPath: '/dist/', // 指定资源文件引用的目录
        filename: 'main.js' // 指定输出文件名称
    }    
};

module.exports = config;
```

#### 3.SPA入口与启动

在`demo`目录下，新建一个`index.html`作为SPA的入口：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My webpack App</title>
</head>
<body>
    <div id="app">
        Hello World.
    </div>
    <script type="text/javascript" src="/dist/main.js"></script>
</body>
</html>
```

**启动**

这时执行命令`npm run dev`，就会自动在浏览器打开页面了。如果报错`Cannot find module 'webpack-cli/bin/config-yargs'`，请参考[Error: Cannot find module 'webpack-cli/bin/config-yargs' · Issue #1948 · webpack/webpack-cli (github.com)](https://github.com/webpack/webpack-cli/issues/1948)

#### 4.webpack的加载器(Loaders)

在webpack中，每一个文件都是一个模块，比如.css、.js、.html、.jpg、.less等。对于不同的模块，需要不同的加载器（Loaders）来处理。通过安装不同的加载器可以对各种后缀名的文件进行处理，比如现在要写一些CSS样式，就要用到style-loader和css-loader。

```bash
npm install css-loader --save-dev
npm install style-loader --save-dev
```

安装完成后，在webpack.config.js文件里配置Loader，增加对.css文件的处理：

```javascript
var config = {
	// ...
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    'css-loader'
                ]
            }
        ]
    }    
};
```

在module对象的rules属性中可以指定一系列的loaders，每一个loaders都必须包含test和use两个选项。这段配置表示编译过程中遇到require()或import语句导入一个后缀名为.css的文件时，先将它通过css-loader转换，再通过style-loader转换，然后继续打包。use选项的值可以是数组或字符串，如果是数组，它的编译顺序就是从后往前。

新建`style.css`文件，并在main.js中导入：

```css
/** style.css */
#app{
    font-size: 24px;
    color: #f50;
}
```

```javascript
// main.js文件
import './style.css'
```

但是，如果我们只是通过这样在项目中import一个css文件编译后可以发现，这样是通过JavaScript动态创建`<style>`标签来引入样式，这就意味着项目中的css代码会被直接编译到页面中，如果样式太多会严重影响性能（占用体积大、无法缓存样式表）。我们希望通过`<link>`的形式加载它。这时需要用到webpack的插件。

#### 5.webpack的插件(Plugins)

webpack的插件很强大且可定制。这里我们使用一个`extract-text-webpack-plugin`的插件来把散落在各地的css提取出来，并为每一个页面生成一个.css文件，最终在html里通过`<link>`的形式加载它。

```bash
npm install extract-text-webpack-plugin --save-dev
```

然后在配置文件中导入插件，并改写loader的配置：

```javascript
// 导入插件
var ExtractTextPlugin = require('extract-text-webpack-plugin');

var config = {
    // ...
    module:{
        rules: [
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    use: 'css-loader',
                    fallback: 'style-loader'
                })
            }
        ]
    },
    plugins: [
        // 重命名提取后的css文件
        new ExtractTextPlugin("main.css")
    ]    
};
```

webpack4以上版本extract-text-webpack-plugin不支持（当前extract-text-webpack-plugin最新版V3.0.2），使用mini-css-extract-plugin替代：[mini-css-extract-plugin - npm (npmjs.com)](https://www.npmjs.com/package/mini-css-extract-plugin)

安装`mini-css-extract-plugin`，如果要生产打包需要`html-webpack-plugin`插件[html-webpack-plugin - npm (npmjs.com)](https://www.npmjs.com/package/html-webpack-plugin)配合使用：

```bash
npm install mini-css-extract-plugin --save-dev
npm install html-webpack-plugin --save-dev
```

配置文件，`style-loader`不可以和`mini-css-extract-plugin`同时使用：

```javascript
var path = require('path');
// 导入插件
var miniCssExtractPlugin  = require('mini-css-extract-plugin');
var htmlWebpackPlugin = require('html-webpack-plugin')

var config = {
    entry: {
        main: './main' // 入口，表示从main.js开始工作
    },
    output: {
        path: path.join(__dirname,'./dist'), // 存放打包后文件的输出目录，必填
        publicPath: '/dist/', // 指定资源文件引用的目录
        filename: 'main.js' // 指定输出文件名称
    },
    plugins: [
        new htmlWebpackPlugin(),
        new miniCssExtractPlugin({
            // 指定每个输出CSS文件的名称
            filename: 'css-[name].css' 
        })
    ] ,
    module:{
        rules: [
            {
                test: /\.css$/,
                use: [miniCssExtractPlugin.loader, 'css-loader'],
            }
        ]
    }   
};

module.exports = config;
```

在`package.json`中配置一个scripts：`"build": "webpack --mode production"`然后执行`npm run build`就可以生成dist目录。

