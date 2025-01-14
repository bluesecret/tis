### 服务接口文档
---
- Knife4j
  - 服务启动后，Knife4j的文档入口地址 [http://localhost:8082/doc.html#/plus](http://localhost:8082/doc.html#/plus)

### 服务启动环境依赖
---

执行docker-compose up -d 命令启动下面依赖的服务。
执行docker-compose down 命令停止下面服务。

- Redis
  - 版本：4以上版本
  - 端口: 6379
  - 推荐客户端工具 [AnotherRedisDesktopManager](https://github.com/qishibo/AnotherRedisDesktopManager)
- Minio
  - 版本：8.4.5
  - 控制台URL：需要配置Nginx，将请求导入到我们缺省设置的19000端口，之后可通过浏览器操作minio。
  - 缺省用户名密码：admin/admin123456
