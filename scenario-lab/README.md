# Java 场景设计实验室

这是与笔记对应的 Maven 多模块项目。每个子目录有自己的 Spring Boot 启动类、业务包、HTTP 接口、H2 SQL 和测试，也可以单独导入 IDE。没有模块间运行时依赖，父 POM 只统一依赖及构建配置。技术栈为 **JDK 17 + 本机缓存中的 Spring Boot 3.3.2 + Spring JDBC + H2 + JUnit 5**。

## 快速开始

在仓库根目录执行：

```powershell
# 全部单元测试 + 全部 Spring/H2 集成测试
.\scenario-lab\run.ps1 -Action verify -Offline
# 单独验证秒杀
.\scenario-lab\run.ps1 -Module seckill -Action verify -Offline
# 启动一个独立服务；不同模块同时运行时指定不同端口
.\scenario-lab\run.ps1 -Module seckill -Action run -Port 8080 -Offline
Invoke-RestMethod 'http://localhost:8080/api/scenario'
Invoke-RestMethod -Method Post 'http://localhost:8080/api/buy?user=101'
```

脚本只在当前进程切换 JAVA_HOME，退出后恢复，不修改系统设置。默认 JDK 路径为本机 `D:\DevKit\Java\jdk-17.0.6+10`，其他机器通过 `-JdkHome` 指定。去掉 `-Offline` 可下载缺失依赖。IDE 中选择 JDK 17，导入父 `pom.xml`，运行对应模块的 `Application`。应用默认随机端口（可看启动日志），脚本 run 模式默认固定为 8080。`Ctrl+C` 停止。

也可以自行设置 JAVA_HOME 后运行标准命令：

```text
mvn -f scenario-lab/pom.xml verify
mvn -f scenario-lab/seckill/pom.xml spring-boot:run
java -jar scenario-lab/seckill/target/seckill-1.0.0.jar --server.port=8080
```

## 场景及实验

| 模块 / 包 | 实现与测试重点 | 关键 SQL |
| --- | --- | --- |
| feed / lab.feed | 普通作者写收件箱、大账号读取时聚合；复合游标；解除好友后过滤陈旧索引 | post、friendship、inbox |
| seckill / lab.seckill | 24 个并发请求抢 5 件；同用户并发一单；插单后故障回滚扣库 | stock、orders、唯一键及条件更新 |
| payment / lab.payment | 并发重复回调只记账一次；故障回滚流水与 outbox；并发退款总额不超支付额 | payment、ledger、outbox、refund |
| qrlogin / lab.qrlogin | 创建、扫码、确认、一次性兑换；浏览器秘密哈希绑定、账号绑定及到期边界 | scene |
| shortlink / lab.shortlink | 可注入短码生成器，确定性演示碰撞重试、重试上限、过期和危险协议 | short_link |
| orderexpiry / lab.orderexpiry | 并发支付与关单；库存释放事件唯一；已关闭后支付成功产生一次退款任务 | orders、release_event、refund_task |
| redpacket / lab.redpacket | 并发领取金额守恒、同人一次；固定中奖分支验证奖品防超发 | packet、claim、prize、draw_result |
| im / lab.im | 相同客户端消息重复发送返回原序号；并发消息序号连续；按游标补拉 | conversation、message |
| ranking / lab.ranking | 业务事件唯一去重；绝对分数与版本更新读模型，拒绝乱序旧事件 | score_event、score、rank_view |
| scheduler / lab.scheduler | 并发领取仅一人持有租约；过期重领增加 token；目标资源拒绝旧 token | job_run、protected_resource |
| cache / lab.cache | 确定性复现旧快照；持久版本栅栏拒绝旧读回填；并发回填与更新 | product、cache_entry |

每个模块的 `src/main/resources/schema.sql` 可独立查看表和约束，包含可演示的初始数据。测试每次执行 SQL 清空并重建该模块的内存库，避免测试互相污染；应用重启数据消失。不要对外部数据库执行其中的 `DROP ALL OBJECTS`。

## 如何阅读测试

`RulesTest` 是不启动 Spring 的纯规则单元测试；`ScenarioIT` 使用真实 Spring 上下文、JDBC 事务和 H2，另通过 MockMvc 验证 HTTP 控制器可用。`test` 只运行单元测试，**要执行并发和数据库实验必须运行 verify**。报告分别位于各模块 `target/surefire-reports` 和 `target/failsafe-reports`。

并发实验用起跑门控制同时放行，每个 Future 有超时且异常会传回测试线程；没有用 sleep 猜测时序。故障测试在事务中注入异常，检查事务后的业务不变量。传入时间参数而非等待真实时钟，可以稳定测试到期和租约竞争。

## 简化边界

这些实验实现的是单机可复现的关键机制，不是完整生产系统：

- Feed 以 H2 查询模拟读扩散和收件箱，发布与扩散处于同一事务；没有异步任务、媒体服务和分片。权限示例只有好友关系与删除状态。
- 秒杀实现数据库最终防线，没有 Redis 预留、MQ 或跨系统补偿，不能据此测得分布式秒杀吞吐。
- 支付把回调当作已经过渠道验签的内部调用；outbox 只演示本地原子落地，没有投递器和真实渠道。退款示例是额度预占，没有渠道退款状态机。
- 登录 API 接受显式 user 参数来模拟已登录手机，不做真正认证；场景 ID、浏览器秘密由演示调用者提供。兑换只演示一次性状态消费，没有发放会话。
- 短链接协议校验不等同于完整 SSRF 防御或反钓鱼；代码不抓取目标网页。
- 超时关单使用传入时间触发；未实现真实延迟队列、渠道关单协议、扫描任务及退款执行。
- 红包是确定性均分以突出守恒，抽奖只演示中奖分支与库存控制，不提供随机公平性和真实账户转账。
- IM 没有 WebSocket、推送网关和多设备 ACK；实现发送持久化、序号和拉取。
- 排行榜与缓存使用 H2 表模拟读模型；缓存版本栅栏与商品同库事务，真实 Redis/DB 间原子性不成立。栅栏过期或丢失的分布式恢复没有实现。
- 调度实验模拟一个到期实例和受控资源，旧 token 只有在新 token 已被资源接受后才会被拒绝；token 不替代业务幂等，也没有 cron 扫描与补跑器。
- H2 的并发和锁语义不能替代目标 MySQL/PostgreSQL 验证；多节点故障、跨机房和实际分库分表需要独立环境验证。

业务接口以演示为目的，ID、金额等输入校验与错误码并未形成生产 API 契约。完整设计权衡参见笔记 [场景设计面试](../doc/java/场景设计面试.md)。
