
> 技巧：云服务器（如阿里云的CentOS）的端口开放与过滤最好是在网页端操作**安全组规则**，并关闭服务器firewalld和iptables，否则可能出现莫名奇妙的端口访问不到。

#  iptables和firewalld

> 参考：[ **《Linux就该这么学》-第八章-Iptables与Firewalld防火墙** ]( https://www.linuxprobe.com/chapter-08.html )

firewalld是centos7里面的新的防火墙命令，它底层还是使用 iptables 对内核命令动态通信包过滤的，简单理解就是firewalld是centos7下管理iptables的新命令。

# firewalld命令

## 基本使用

启动： `systemctl start firewalld`</br>
关闭： `systemctl stop firewalld`</br>
查看状态： `systemctl status firewalld`</br>
开机禁用  ： `systemctl disable firewalld`</br>
开机启用  ： `systemctl enable firewalld`</br>

> systemctl是CentOS7的服务管理工具中主要的工具，它融合之前service和chkconfig的功能于一体

启动一个服务：`systemctl start firewalld.service`</br>
关闭一个服务：`systemctl stop firewalld.service`</br>
重启一个服务：`systemctl restart firewalld.service`</br>
显示一个服务的状态：`systemctl status firewalld.service`</br>
在开机时启用一个服务：`systemctl enable firewalld.service`</br>
在开机时禁用一个服务：`systemctl disable firewalld.service`</br>
查看服务是否开机启动：`systemctl is-enabled firewalld.service`</br>
查看已启动的服务列表：`systemctl list-unit-files|grep enabled`</br>
查看启动失败的服务列表：`systemctl --failed`</br>

## 配置firewalld-cmd

查看版本： `firewall-cmd --version`</br>
查看帮助： `firewall-cmd --help`</br>
显示状态： `firewall-cmd --state`</br>
查看所有打开的端口： `firewall-cmd --zone=public --list-ports`</br>
更新防火墙规则： `firewall-cmd --reload`</br>
查看可用的区域：`firewall-cmd --get-zones`</br>
查看当前的默认区域(关于区域的概念查看[附录](#zone))：`firewall-cmd --get-default-zone` <span id = 'list-default-zone' ></span></br>
查看当前激活的区域:  `firewall-cmd --get-active-zones`</br>
查看指定接口所属区域： `firewall-cmd --get-zone-of-interface=eth0`</br>
拒绝所有包：`firewall-cmd --panic-on`</br>
取消拒绝状态： `firewall-cmd --panic-off`</br>
查看是否拒绝： `firewall-cmd --query-panic`</br>

## 开放端口

添加：`firewall-cmd --zone=public --add-port=80/tcp --permanent`    （--permanent永久生效，没有此参数重启后失效）</br>
重新载入：`firewall-cmd --reload`</br>
查看：`firewall-cmd --zone=public --query-port=80/tcp`</br>
删除：`firewall-cmd --zone=public --remove-port=80/tcp --permanent`</br>

## 配置IP白名单

添加：`firewall-cmd --permanent --zone=trusted --add-source=173.245.48.0/20`</br>
重新载入：`firewall-cmd --reload`</br>
确认 trusted 区域是否设置正确：`firewall-cmd --zone=trusted --list-all`</br>
设置默认区域为drop([查看默认](#list-default-zone))：`firewall-cmd --set-default-zone=drop`</br>
将默认网卡 eth0 分配给 drop 区域：`firewall-cmd --permanent --zone=drop --change-interface=eth0`</br>
重新载入：`firewall-cmd --reload`</br>




------------

# iptables命令

## 基本使用

启动：`systemctl start iptables` </br>
查看运行状态：`systemctl status iptables` </br>
重启：`systemctl restart iptables.service`</br>
停止：`systemctl stop iptables.service`</br>
设置开机启动：`systemctl enable iptables.service`</br>
禁止开机启动：`systemctl disable iptables.service`</br>

## 其它

查询帮助：`iptables -h` </br>
列出（filter表）所有规则：`iptables -L -n` </br>
列出（filter表）所有规则，带编号：`iptables -L -n --line-number` </br>
列出（nat表）所有规则：`iptables -L -n -t nat` </br>
清除（filter表）中所有规则：`iptables -F` </br>
清除（nat表）中所有规则：`iptables -F -t nat` </br>
保存配置（保存配置后必须重启iptables）：`service iptables save` </br>
重启：`systemctl restart iptables.service` </br>

## 开放端口

禁止192.168.1.3 IP地址的所有类型数据接入：`iptables -A INPUT ! -s 192.168.1.3 -j DROP`</br>
开放80端口：`iptables -A INPUT -p tcp --dport 80 -j ACCEPT` </br>
开放22-80范围的端口：`iptables -I INPUT -p tcp --dport 22:80 -j ACCEPT`</br>
不允许80端口流出：`iptables -I OUTPUT -p tcp --dport 80 -j DROP`</br>



# 附录1-区域(zone) 

<span id="zone"></span>区域(zone)基本上是一组规则，它们决定了允许哪些流量，具体取决于你对计算机所连接的网络的信任程度。为网络接口分配了一个区域，以指示防火墙应允许的行为。
Firewalld 一般已经默认内置了 **9 个区域(zone)**，大部分情况下，这些已经足够使用，按从最不信任到最受信任的顺序为：

- `drop`：最低信任级别。所有传入的连接都将被丢弃而不会回复，并且只能进行传出连接。
- `block`：与上述类似，但不是简单地删除连接，而是使用 icmp-host-prohibitedor 和 icmp6-adm-prohibited 消息拒绝传入的请求。
- `public`：表示不信任的公共网络。您不信任其他计算机，但可能会视情况允许选择的传入连接。默认情况下，此区域为激活状态。
- `external`：如果你使用防火墙作为网关，则为外部网络。将其配置为 NAT 转发，以便你的内部网络保持私有但可访问。
- `internal`：external 区域的另一侧，用于网关的内部。这些计算机值得信赖，并且可以使用一些其他服务。
- `dmz`：用于 DMZ (DeMilitarized Zone) 中的计算机（将无法访问网络其余部分的隔离计算机），仅允许某些传入连接。
- `work`：用于工作机。信任网络中的大多数计算机。可能还允许其他一些服务。
- `home`：家庭环境。通常，这意味着您信任其他大多数计算机，并且将接受其他一些服务。
- `trusted`：信任网络中的所有计算机。可用选项中最开放的，应谨慎使用。