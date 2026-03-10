## 博客介绍

<p align=center>
  <a href="http://www.shiyit.com" style="border-radius: 50%;width: 100px;height: 100px">
    <img src="logo.ico" alt="Shang博客" >
  </a>
</p>

<p align="center">
   <a target="_blank" href="https://github.com/X1192176811/blog">
      <img src="https://img.shields.io/hexpm/l/plug.svg"/>
      <img src="https://img.shields.io/badge/JDK-1.8+-green.svg"/>
      <img src="https://img.shields.io/badge/springboot-2.4.1.RELEASE-green"/>
      <img src="https://img.shields.io/badge/vue-2.5.17-green"/>
      <img src="https://img.shields.io/badge/mysql-5.5.0-green"/>
      <img src="https://img.shields.io/badge/mybatis--plus-3.4.0-green"/>
      <img src="https://img.shields.io/badge/redis-6.0.5-green"/>
      <img src="https://img.shields.io/badge/elasticsearch-7.9.2-green"/>
   </a>
</p>

[在线地址](#在线地址) | [目录结构](#目录结构) | [项目特点](#项目特点) | [技术介绍](#技术介绍) | [运行环境](#运行环境)


## 在线地址

**项目链接：** [Shang博客](http://www.shiyit.com)

## 站点演示地址
**后台链接：** [Shang博客后台管理系统](https://admin.shiyit.com/#/login)

账号:test 密码:123456

**Gitee地址：** [https  ://gitee.com/quequnlong/shiyi-blog](https://gitee.com/quequnlong/shiyi-blog)

您的star是我坚持的动力，感谢大家的支持，欢迎提交pr共同改进项目。

## 目录结构

前端项目blog-web为前台，blog-admin为后台。

后端项目位于blog下。

SQL文件位于根目录下的 mj-blog.sql

可直接导入该项目于本地，修改后端配置文件中的数据库等连接信息，项目中使用到的关于七牛云功能和第三方授权登录等需要自行开通。

当你克隆项目到本地后可使用账号：admin，密码：123456 进行登录

本地访问接口文档地址：http://127.0.0.1:8800/shiyi/doc.html

**ps：请先运行后端项目，再启动前端项目，前端项目配置由后端动态加载。**

```
blog
├── mojian-admin    --  后台管理系统的controller模块
├── mojian-common        --  通用模块
├── mojian-auth        --  认证模块
├── mojian-file        --  文件模块
├── mojian-quartz        --  定时任务模块
├── mojian-server        --  博客启动类模块
├── mojian-api           --  门户接口模块

```
- 

## 项目截图

**门户**
![statics/img0.png](/statics/img0.png) ![statics/img.png](/statics/img.png)


![statics/img_1.png](/statics/img_1.png) ![statics/img_2.png](/statics/img_2.png)


![statics/img_3.png](/statics/img_3.png) ![img.png](/statics/login.png)

![img.png](/statics/about.png)

**管理系统**
![statics/img_4.png](/statics/img_4.png)  ![statics/img_5.png](/statics/img_5.png)

![statics/img_6.png](/statics/img_6.png)  ![statics/img_7.png](/statics/img_7.png)

![statics/img_8.png](/statics/img_8.png)  ![statics/img_9.png](/statics/img_9.png)

![statics/img_10.png](/statics/img_10.png)  ![statics/img_11.png](/statics/img_11.png)

![statics/img_12.png](/statics/img_12.png)  ![statics/img_13.png](/statics/img_13.png)

![statics/img_14.png](/statics/img_14.png)  ![statics/img_15.png](/statics/img_15.png)

![statics/img_16.png](/statics/img_16.png)  ![statics/img_17.png](/statics/img_17.png)

![statics/img_18.png](/statics/img_18.png)  ![statics/img_19.png](/statics/img_19.png)

![statics/img_20.png](/statics/img_20.png)


## 技术介绍

**门户-前端：** vue2.0 +  Vuex + vue-router + axios + elementUi

**管理系统-前端：** vue3.0 +  pinia + vue-router + axios + element-plus + echarts

**后端：** SpringBoot + nginx + docker + sa-token + Swagger2 + MyBatisPlus + Mysql + Redis + elasticsearch

**其他：** 接入QQ、微博、码云、微信公众号等第三方登录，接入七牛云对象存储

## 运行环境

**服务器：** 腾讯云2核4G CentOS7.6

**对象存储：** 七牛云OSS

**最低配置：** 1核2G服务器（关闭ElasticSearch）

## 开发环境

|            开发工具            |           说明            |
| ----------------------------- | ------------------------- |
| IDEA                          | Java开发工具IDE            |
| VSCode                        | Vue开发工具IDE             |
| Navicat                       | MySQL远程连接工具          |
| Another Redis Desktop Manager | Redis远程连接工具          |
| finalshell                    | Linux远程连接和文件上传工具 |

|    开发环境    |  版本  |
| ------------- | ----- |
| JDK           | 1.8   |
| MySQL         | 5.5.0 |
| Redis         | 6.0.5 |
| Elasticsearch | 7.9.2 |
|            |      |
|            |      |
|            |      |shang-blog
介绍
{以下是 Gitee 平台说明，您可以替换此简介 Gitee 是 OSCHINA 推出的基于 Git 的代码托管平台（同时支持 SVN）。专为开发者提供稳定、高效、安全的云端软件开发协作平台 无论是个人、团队、或是企业，都能够用 Gitee 实现代码托管、项目管理、协作开发。企业项目请看 https://gitee.com/enterprises}

软件架构
软件架构说明

安装教程
xxxx
xxxx
xxxx
使用说明
xxxx
xxxx
xxxx
参与贡献
Fork 本仓库
新建 Feat_xxx 分支
提交代码
新建 Pull Request
特技
使用 Readme_XXX.md 来支持不同的语言，例如 Readme_en.md, Readme_zh.md
Gitee 官方博客 blog.gitee.com
你可以 https://gitee.com/explore 这个地址来了解 Gitee 上的优秀开源项目
GVP 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
Gitee 官方提供的使用手册 https://gitee.com/help
Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 https://gitee.com/gitee-stars/
