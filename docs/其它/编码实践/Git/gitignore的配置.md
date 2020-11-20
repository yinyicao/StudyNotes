# .gitignore的配置

1. 首先在与.git文件夹同级目录创建.gitignore文件

   ```shell
   admin@DESKTOP-ABC MINGW64 /e/workspace/blog_yyc (master)
   $ touch .gitignore
   ```

2. 编辑文件，加入需要忽略的文件和文件夹，规则如下：

   ```shell
   # 此为注释 – 将被 Git 忽略
    
   *.a       # 忽略所有 .a 结尾的文件
   !lib.a    # 但 lib.a 除外
   /TODO     # 仅仅忽略项目根目录下的 TODO 文件，不包括 subdir/TODO
   build/    # 忽略 build/ 目录下的所有文件
   doc/*.txt # 会忽略 doc/notes.txt 但不包括 doc/server/arch.txt
   ```

   **注意：**如果你是新加的，这里需要注意的是.gitignore只能作用于没有被track的文件，也就是工作区的文件，对于add，commit操作后的文件是没有作用的，这个时候需要先把本地的缓存删除，在去提交，就可以实现忽略整个仓库文件了。

   ```shell
   git rm -r --cached .
   git add .
   git commit -m 'update'
   ```