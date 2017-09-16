我们在使用git clone 或其他命令的时候，有时候会遇到这类问题，如图：
fatal: Could not read from remote repository.

Please make sure you have the correct access rights
and the repository exists.
fatal: Could not read from remote
 repository.Please make sure you
 have the correct access rights and the repository exists.

出现这个问题是因为没有在github账号添加SSH key
解决方法如下：
1.在终端输入。
ssh-keygen -t rsa -C "username" (注：username为你git上的用户名)
如果执行成功。返回
Generating public/private rsa key pair.
Enter file in which to save the key (/Users/username/.ssh/id_rsa):
Generating public/private rsa key pair.
Enter file in which to save the key (/Users/username/.ssh/id_rsa): 
首先，说明一下，这里的username是你电脑上的用户名
然后，在这里就是设置存储地址了.我们直接按回车，会出现一下两种情况的一种：
（1）如果正常运行的话，会出现
Enter passphrase (empty for no passphrase):
然后我们直接回车
（2）有的时候我们可能会出现
/Users/your username/.ssh/id_rsa already exists.
Overwrite (y/n)?
这说明你已经设置了存储地址，我们输入“y”覆盖
Overwrite (y/n)? y
回车


上面的任意两种情况之后，会出现
Enter same passphrase again: 
再次回车，这时候你会看见：
Your identification has been saved in /Users/username/.ssh/id_rsa.
Your public key has been saved in /Users/username/.ssh/id_rsa.pub.
The key fingerprint is:
58:42:8b:58:ad:4b:b5:b9:6d:79:bf:8c:f9:e2:2b:ed 
username
The key's randomart image is:
+--[ RSA 2048]----+
|    ...          |
|   o oo.         |
|  . .ooo.        |
|    o o+         |
|   . ..oS.       |
|    . . + .      |
|       . o .     |
|        . o+.    |
|         +E++.   |
+-----------------+

这说明SSH key就已经生成了。文件目录就是：/Users/username/.ssh/id_rsa.pub.
我们执行cat命令查看文件的内容：
cat /User/username/.ssh/id_rsa.pub
这时候会看见：
ssh-rsa AAAAB3NzaC1yc2。。。。。。。。。
后面的内容我省略了
(说明：ssh-rsa 后面的内容这就是你的SSH keys)
把显示出来的SSH
 keys直接添加到github账户设置里边的SSH keys

最后再执行git clone命令就可以了