
> 技巧：云服务器（如阿里云的CentOS）的端口开放与过滤最好是在网页端操作**安全组规则**，并关闭服务器firewalld和iptables，否则可能出现莫名奇妙的端口访问不到。

#  iptables和firewalld

> 参考：[ **《Linux就该这么学》-第八章-Iptables与Firewalld防火墙** ]( https://www.linuxprobe.com/chapter-08.html )

firewalld是centos7里面的新的防火墙命令，它底层还是使用 iptables 对内核命令动态通信包过滤的，简单理解就是firewalld是centos7下管理iptables的新命令。

# firewalld命令

## 基本使用

启动： `systemctl start firewalld`
关闭： `systemctl stop firewalld`
查看状态： `systemctl status firewalld`
开机禁用  ： `systemctl disable firewalld`
开机启用  ： `systemctl enable firewalld`

> systemctl是CentOS7的服务管理工具中主要的工具，它融合之前service和chkconfig的功能于一体

启动一个服务：`systemctl start firewalld.service`
关闭一个服务：`systemctl stop firewalld.service`
重启一个服务：`systemctl restart firewalld.service`
显示一个服务的状态：`systemctl status firewalld.service`
在开机时启用一个服务：`systemctl enable firewalld.service`
在开机时禁用一个服务：`systemctl disable firewalld.service`
查看服务是否开机启动：`systemctl is-enabled firewalld.service`
查看已启动的服务列表：`systemctl list-unit-files|grep enabled`
查看启动失败的服务列表：`systemctl --failed`

## 配置firewalld-cmd

查看版本： `firewall-cmd --version`
查看帮助： `firewall-cmd --help`
显示状态： `firewall-cmd --state`
查看所有打开的端口： `firewall-cmd --zone=public --list-ports`
更新防火墙规则： `firewall-cmd --reload`
查看区域信息:  `firewall-cmd --get-active-zones`
查看指定接口所属区域： `firewall-cmd --get-zone-of-interface=eth0`
拒绝所有包：`firewall-cmd --panic-on`
取消拒绝状态： `firewall-cmd --panic-off`
查看是否拒绝： `firewall-cmd --query-panic`

## 开放端口

添加：`firewall-cmd --zone=public --add-port=80/tcp --permanent`    （--permanent永久生效，没有此参数重启后失效）
重新载入：`firewall-cmd --reload`
查看：`firewall-cmd --zone=public --query-port=80/tcp`
删除：`firewall-cmd --zone=public --remove-port=80/tcp --permanent`

------------

# iptables命令

## 基本使用

启动：`systemctl start iptables` 
查看运行状态：`systemctl status iptables` 
重启：`systemctl restart iptables.service`
停止：`systemctl stop iptables.service`
设置开机启动：`systemctl enable iptables.service`
禁止开机启动：`systemctl disable iptables.service`

## 其它

查询帮助：`iptables -h` 
列出（filter表）所有规则：`iptables -L -n` 
列出（filter表）所有规则，带编号：`iptables -L -n --line-number` 
列出（nat表）所有规则：`iptables -L -n -t nat` 
清除（filter表）中所有规则：`iptables -F` 
清除（nat表）中所有规则：`iptables -F -t nat` 
保存配置（保存配置后必须重启iptables）：`service iptables save` 
重启：`systemctl restart iptables.service` 

## 开放端口

禁止192.168.1.3 IP地址的所有类型数据接入：`iptables -A INPUT ! -s 192.168.1.3 -j DROP`
开放80端口：`iptables -A INPUT -p tcp --dport 80 -j ACCEPT` 
开放22-80范围的端口：`iptables -I INPUT -p tcp --dport 22:80 -j ACCEPT`
不允许80端口流出：`iptables -I OUTPUT -p tcp --dport 80 -j DROP`

