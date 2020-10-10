#### 1.安装vue-cli

**①** 使用npm（需要安装node环境）**全局安装webpack**，打开命令行工具输入：`npm install webpack -g`或者（`npm install -g webpack`），安装完成之后输入 `webpack -v`，如果出现相应的版本号，则说明安装成功。

**②** **全局安装vue-cli**，在cmd中输入命令:

 ```
npm install -g @vue/cli
 ```

安装完成之后输入 vue -V（注意这里是大写的“V”），出现版本号，则说明安装成功。

#### 2.用vue-cli来构建项目

**①** 我首先在D盘新建一个文件夹（yyc_vue）作为项目存放地，然后使用命令行cd进入到项目目录输入：

```kotlin
vue init webpack baoge
```

baoge是自定义的项目名称，命令执行之后，会在当前目录生成一个以该名称命名的项目文件夹。

输入命令后，会跳出几个选项让你回答，自己设置就好了。

**②** 配置完成后，可以看到目录下多出了一个项目文件夹baoge，然后cd进入这个文件夹：
 **安装依赖**：

```undefined
npm install
```

 ( 如果安装速度太慢。可以安装淘宝镜像，打开命令行工具，输入：

```
npm install -g cnpm --registry=https://registry.npm.taobao.org
```
 然后使用`cnpm`来安装 )

npm install ：安装所有的模块，如果是安装具体的哪个模块，在install 后面输入模块的名字即可。而只输入install就会按照项目的根目录下的package.json文件中依赖的模块安装（这个文件里面是不允许有任何注释的），每个使用npm管理的项目都有这个文件，是npm操作的入口文件。因为是初始项目，还没有任何模块，所以我用npm install 安装所有的模块。安装完成后，目录中会多出来一个node_modules文件夹，这里放的就是所有依赖的模块。

#### 3.启动项目

```undefined
npm run dev
```

 如果浏览器打开之后，没有加载出页面，有可能是本地的 8080 端口被占用，需要修改一下配置文件 config里的index.js

还有，如果本地调试项目时，建议将build 里的`assetsPublicPath`的路径前缀修改为 ' ./ '（开始是 ' / '），因为打包之后，外部引入 js 和 css 文件时，如果路径以 ' / ' 开头，在本地是无法找到对应文件的（服务器上没问题）。所以**如果需要在本地打开打包后的文件**，就得修改文件路径。
 我的端口没有被占用，直接成功（服务启动成功后浏览器会默认打开一个“欢迎页面”）：


**注意：在进行vue页面调试时，一定要去谷歌商店下载一个vue-tool扩展程序。**

#### 4.vue-cli的webpack配置分析

- 从`package.json`中可以看到开发和生产环境的入口。
- 可以看到dev中的设置，**build/webpack.dev.conf.js**，该文件是开发环境中webpack的配置入口。
- 在webpack.dev.conf.js中出现**webpack.base.conf.js**，这个文件是开发环境和生产环境，甚至测试环境，这些环境的公共webpack配置。可以说，这个文件相当重要。
- 还有**config/index.js 、build/utils.js  、build/build.js**等，具体请看这篇介绍：
   [https://segmentfault.com/a/1190000008644830](https://link.jianshu.com?t=https%3A%2F%2Fsegmentfault.com%2Fa%2F1190000008644830) 

#### 5.打包上线

注意，自己的项目文件都需要放到 src 文件夹下。
 在项目开发完成之后，可以输入 `npm run build` 来进行打包工作。

```undefined
npm run build
```

另：

1. npm 开启了npm run dev以后怎么退出或关闭？
`ctrl+c`
1. --save-dev
`自动把模块和版本号添加到模块配置文件package.json中的依赖里devdependencies部分`
1. --save-dev 与 --save 的区别
`--save     安装包信息将加入到dependencies（生产阶段的依赖）`
`--save-dev 安装包信息将加入到devDependencies（开发阶段的依赖），所以开发阶段一般使用它`

打包完成后，会生成 dist 文件夹，如果已经修改了文件路径，可以直接打开本地文件查看。
项目上线时，只需要将 dist 文件夹放到服务器就行了。

