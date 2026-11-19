# 超时订单：支付关单竞争及晚到成功补偿

独立 Spring Boot 工程，包名 `lab.orderexpiry`；无其他示例模块的运行时依赖。

- `ScenarioService.java`：业务事务及难点实现。
- `Rules.java` / `RulesTest.java`：实际调用的纯业务规则与边界测试。
- `src/main/resources/schema.sql`：H2 建表和初始数据，启动会重置内存库。
- `ScenarioIT.java`：Spring + H2 事务集成、并发/重试与 HTTP 测试。

在父目录执行 `./run.ps1 -Module orderexpiry -Action verify`；启动执行 `./run.ps1 -Module orderexpiry -Action run`，使用 8080 端口，或用 `-Port 8081` 覆盖。`GET /api/scenario` 查看模块名称；业务 API 见 `ScenarioController.java`。

这是单进程可执行教学模型；跨节点、真实 MQ、Redis、第三方渠道和分库分表没有接入。模拟部分和测试边界见父目录 README。
