# SQL 语法与面试题

Java 工程中的数据库常常只是实体的持久化层：按主键查询、插入、更新、删除都可以交给 ORM。但报表、统计和面试题经常需要把多个表的数据关联起来，按不同粒度汇总、排序，或分析时间序列。本章从单表查询出发，逐步学习这些能力，并用统一数据练习。

> **运行环境：MySQL 8.0**，使用 CTE 和窗口函数；MySQL 5.7 不能直接运行这两类语法。其他数据库的日期函数、分页语法可能不同。金额使用 `DECIMAL`，日期以自然日计算。执行建表和修改语句请使用测试库。

## 一、准备练习数据

下面的表专门用于本章，和原题网站上的表结构不完全相同；题目按来源的考点改编。为展示边界情况，数据包含空部门、无订单用户、并列薪资、同一天多次登录和无员工部门。

```sql
CREATE TABLE departments (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    dept_id INT NULL,
    manager_id INT NULL,
    salary DECIMAL(10, 2) NULL,
    hire_date DATE NOT NULL
);

CREATE TABLE orders (
    id INT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE login_logs (
    id INT PRIMARY KEY,
    user_id INT NOT NULL,
    login_at DATETIME NOT NULL
);

INSERT INTO departments VALUES
    (1, '研发'), (2, '销售'), (3, '运营'), (4, '财务');
INSERT INTO employees VALUES
    (1, '安', 1, NULL, 12000, '2020-01-01'),
    (2, '白', 1, 1,     9000, '2021-03-01'),
    (3, '陈', 1, 1,     9000, '2022-06-01'),
    (4, '杜', 2, NULL, 10000, '2021-03-01'),
    (5, '方', 2, 4,     8000, '2023-01-01'),
    (6, '高', NULL, NULL, NULL, '2024-01-01'),
    (7, '何', 3, NULL,  7000, '2022-06-01');
INSERT INTO orders VALUES
    (101, 1, 100, 'paid',      '2024-01-01 10:00:00'),
    (102, 1, 200, 'paid',      '2024-01-02 10:00:00'),
    (103, 2,  50, 'cancelled', '2024-01-02 11:00:00'),
    (104, 2, 120, 'paid',      '2024-01-03 10:00:00'),
    (105, 4,  80, 'paid',      '2024-01-03 11:00:00'),
    (106, 4,  20, 'paid',      '2024-01-03 12:00:00');
INSERT INTO login_logs VALUES
    (1, 1, '2024-01-01 08:00:00'), (2, 1, '2024-01-01 12:00:00'),
    (3, 1, '2024-01-02 08:00:00'), (4, 1, '2024-01-03 08:00:00'),
    (5, 1, '2024-01-05 08:00:00'), (6, 2, '2024-01-01 08:00:00'),
    (7, 2, '2024-01-03 08:00:00'), (8, 4, '2024-01-02 08:00:00'),
    (9, 4, '2024-01-03 08:00:00');
```

练习时把 `employees.id` 看作用户 ID；订单和登录记录通过 `user_id` 关联员工。本例刻意不添加外键约束以突出查询。实际业务应根据数据模型决定是否添加外键、唯一约束和索引。

## 二、基础查询：先明确结果的粒度

写 SQL 前先回答：**结果中的一行代表什么？** 是一位员工、一个部门、一笔订单，还是某个用户的某一天？每次连接、分组都要重新检查行数和粒度。

### 2.1 过滤、排序与分页

```sql
SELECT id, name, salary
FROM employees
WHERE dept_id = 1 AND salary >= 9000
ORDER BY salary DESC, id ASC
LIMIT 2 OFFSET 0;
```

- `WHERE` 在分组前过滤行；`AND`、`OR` 混用时使用括号明确优先级。
- `DISTINCT` 对所选列的**组合**去重；`ORDER BY` 指定结果顺序，分页时应加唯一的次级排序键。
- `LIMIT 2 OFFSET 0` 表示跳过 0 行取 2 行；它按行分页，不代表“前两个不同的工资”。
- `BETWEEN a AND b` 两端都包含。对于 `DATETIME`，按天查询更推荐半开区间：`created_at >= '2024-01-03' AND created_at < '2024-01-04'`。
- 空值用 `IS NULL` / `IS NOT NULL` 判断，不能写 `= NULL`。`COALESCE(salary, 0)` 可提供显示值，但不应随意把“未知工资”当作零参与统计。

### 2.2 逻辑处理顺序

便于理解的逻辑顺序是：`FROM / JOIN ... ON` → `WHERE` → `GROUP BY` → `HAVING` → 窗口函数和 `SELECT` → `DISTINCT` → `ORDER BY` → `LIMIT`。这是理解语义的模型，并非数据库必须采用的物理执行顺序。窗口函数结果不能直接用于同一层的 `WHERE`，需要外层查询或 CTE 过滤。

### 2.3 条件表达式和集合

```sql
SELECT id, name,
       CASE WHEN salary >= 10000 THEN '高'
            WHEN salary IS NULL THEN '未知'
            ELSE '普通' END AS salary_level
FROM employees;

SELECT id FROM employees WHERE dept_id = 1
UNION ALL
SELECT id FROM employees WHERE dept_id = 2;
```

`CASE` 可用于分类和条件聚合。`UNION ALL` 保留所有行；`UNION` 会对整个结果去重，两边的列数和相应列类型需要兼容。

## 三、连接：从单表走向多表

### 3.1 INNER JOIN 和 LEFT JOIN

```sql
-- 只保留有匹配部门的员工
SELECT e.id, e.name, d.name AS dept_name
FROM employees AS e
INNER JOIN departments AS d ON d.id = e.dept_id;

-- 保留所有员工，没有部门时右侧列为 NULL
SELECT e.id, e.name, d.name AS dept_name
FROM employees AS e
LEFT JOIN departments AS d ON d.id = e.dept_id;
```

`INNER JOIN` 只保留匹配行；`LEFT JOIN` 至少保留左表每一行，右表无匹配时用 `NULL` 补齐。`RIGHT JOIN` 对称，但通常可以交换表的位置改写为更易读的 `LEFT JOIN`。MySQL 没有原生 `FULL OUTER JOIN`，可用两个方向的外连接并配合集合运算模拟，注意去重语义。

**连接不等于一对一。** 一个员工有 2 笔订单时，连接后会出现 2 行；多张一对多表同时连接会产生乘积。比如员工 1 有 2 笔订单、4 条登录记录，直接连接后得到 8 行，直接 `SUM(o.amount)` 会重复计数。应先分别按用户聚合，再连接聚合结果；不要用 `SUM(DISTINCT amount)` 掩盖问题，因为不同订单可能金额相同。

### 3.2 ON 与 WHERE 的区别

```sql
-- 所有员工都保留，只连接已支付订单
SELECT e.id, o.id AS order_id
FROM employees e
LEFT JOIN orders o ON o.user_id = e.id AND o.status = 'paid';

-- 无已支付订单的员工被 WHERE 排除
SELECT e.id, o.id AS order_id
FROM employees e
LEFT JOIN orders o ON o.user_id = e.id
WHERE o.status = 'paid';
```

`ON` 决定哪些右表行算匹配；`WHERE` 在连接后过滤结果，第二条查询会过滤右侧为 `NULL` 的行。自连接则是给同一张表起两个别名，例如把员工与其经理关联起来。

## 四、聚合、子查询和窗口函数

### 4.1 GROUP BY、HAVING 与条件聚合

```sql
SELECT dept_id, COUNT(*) AS people, AVG(salary) AS avg_salary
FROM employees
WHERE dept_id IS NOT NULL
GROUP BY dept_id
HAVING COUNT(*) >= 2;

SELECT user_id,
       SUM(CASE WHEN status = 'paid' THEN amount ELSE 0 END) AS paid_amount,
       COUNT(CASE WHEN status = 'paid' THEN 1 END) AS paid_count
FROM orders
GROUP BY user_id;
```

`GROUP BY` 把多行压成每组一行；`HAVING` 在聚合后过滤组。`COUNT(*)` 统计行，`COUNT(column)` 统计非空值，`COUNT(DISTINCT column)` 统计不同的非空值；`SUM`、`AVG` 等通常忽略 `NULL`。外连接统计右表记录时用 `COUNT(o.id)`，不用 `COUNT(*)`，否则无订单的员工也会被算成 1。

在默认启用 `ONLY_FULL_GROUP_BY` 的 MySQL 中，非聚合列必须满足分组规则。`SELECT dept_id, name, MAX(salary) ... GROUP BY dept_id` 不能可靠地取得最高工资员工，应使用窗口函数或先求最大值再连接。

### 4.2 子查询、EXISTS 和 CTE

```sql
-- 存在至少一笔已支付订单：不会因多个匹配订单重复员工
SELECT e.id, e.name
FROM employees e
WHERE EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = e.id AND o.status = 'paid'
);

-- 公共表表达式：给中间结果命名，便于分步推导
WITH paid AS (
    SELECT user_id, SUM(amount) AS total
    FROM orders WHERE status = 'paid' GROUP BY user_id
)
SELECT e.id, e.name, COALESCE(p.total, 0) AS total
FROM employees e LEFT JOIN paid p ON p.user_id = e.id;
```

`NOT EXISTS` 适合查“不存在关联记录”。`NOT IN` 的子查询如果包含 `NULL`，可能使比较结果为未知，导致结果出乎预期；除非能保证非空并理解其语义，否则优先考虑 `NOT EXISTS`。

### 4.3 窗口函数：计算但不压缩行

```sql
SELECT id, name, dept_id, salary,
       ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC, id) AS rn,
       RANK()       OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk,
       DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS drnk
FROM employees
WHERE dept_id IS NOT NULL AND salary IS NOT NULL;
```

`PARTITION BY` 指定窗口分组，窗口中的 `ORDER BY` 指定组内顺序；它不保证最终结果的输出顺序。对工资 `12000, 9000, 9000, 8000`：`ROW_NUMBER` 是 `1,2,3,4`；`RANK` 是 `1,2,2,4`；`DENSE_RANK` 是 `1,2,2,3`。要求“前 N 人”可用 `ROW_NUMBER` 并加唯一排序键；要求“前 N 个不同工资，包含并列”用 `DENSE_RANK`，不要把 ID 加入其工资排名的排序键，否则并列会被拆开。

`LAG` / `LEAD` 可取组内前一行 / 后一行。窗口累计求和建议明确窗口范围，例如 `SUM(amount) OVER (PARTITION BY user_id ORDER BY created_at, id ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)`，并用唯一键保证同时间记录的顺序。

## 五、循序渐进：从需求到 SQL

### 第一步：先写清楚筛选和输出

需求：查询 2024 年 1 月 3 日支付的订单。结果一行是一笔订单。

```sql
SELECT id, user_id, amount
FROM orders
WHERE status = 'paid'
  AND created_at >= '2024-01-03' AND created_at < '2024-01-04'
ORDER BY id;
```

结果订单 ID 为 `104、105、106`。半开区间不会漏掉当日 23:59:59 之后的微秒，也便于索引使用。

### 第二步：关联并保留没有匹配记录的人

需求：列出每个员工及部门名，没有部门也要展示。选择员工为左表，使用 `LEFT JOIN`；员工 6 的部门名为 `NULL`。详见下方 A1。

### 第三步：先确定分组粒度，再计算指标

需求：列出每个部门及员工数，包括空部门。结果一行是一个部门，所以以部门为左表、连接员工、按部门 ID 和名称分组，使用 `COUNT(e.id)`。详见 B1。部门 4 的员工数应为 0。

### 第四步：区分排序名次和结果行数

需求：每个部门的最高工资员工，包括并列者。先按部门和工资使用 `DENSE_RANK`，再在外层过滤排名为 1；如题目改成“只取一人”，应改用 `ROW_NUMBER` 和明确的平局规则。详见 C2。

### 第五步：将时间序列转成可比较的日期集合

需求：求连续登录至少 3 天的用户。先对 `(user_id, DATE(login_at))` 去重，再为每个用户的日期编号。连续日期减去连续编号后得到相同的分组键，最后分组计数。详见 D2；不先去重会把同日多次登录误算为连续天数。

## 六、经典面试题分类与参考答案

以下答案均针对第一节的示例表；题意经过改写，在线评测时要以原题表名、字段、排序和输出格式为准。

### A. 查询与连接

#### A1. 所有员工及其部门，包含未分配部门的员工

参考：[牛客 SQL 练习：包含未分配部门员工](https://www.nowcoder.com/discuss/353148510633664512)。

```sql
SELECT e.id, e.name, d.name AS dept_name
FROM employees e LEFT JOIN departments d ON d.id = e.dept_id
ORDER BY e.id;
```

共 7 行；员工 6 的 `dept_name` 为 `NULL`。一个员工如果关联多个部门，结果会有多行，取决于实际表结构。

#### A2. 查询没有已支付订单的员工

```sql
SELECT e.id, e.name
FROM employees e
WHERE NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = e.id AND o.status = 'paid'
)
ORDER BY e.id;
```

结果员工 ID：`3、5、6、7`。员工 2 虽有取消订单，也有已支付订单，因此不在结果中。若需求是“没有任何订单”，应去掉状态条件。

#### A3. 查询员工与其经理姓名

```sql
SELECT e.id, e.name, m.name AS manager_name
FROM employees e
LEFT JOIN employees m ON m.id = e.manager_id
ORDER BY e.id;
```

这是自连接；员工 2、3 的经理是员工 1，员工 5 的经理是员工 4。无经理时保留员工并显示 `NULL`。

### B. 分组与聚合

#### B1. 查询每个部门的员工数，包含空部门

```sql
SELECT d.id, d.name, COUNT(e.id) AS employee_count
FROM departments d LEFT JOIN employees e ON e.dept_id = d.id
GROUP BY d.id, d.name
ORDER BY d.id;
```

结果依次为 `3、2、1、0`。未分配部门的员工不计入任何部门；若需要该类员工，应另行定义统计口径。

#### B2. 统计已支付订单至少两笔的用户和总金额

参考：[牛客 SQL 练习：分组统计与 HAVING](https://www.nowcoder.com/discuss/353148510633664512)。

```sql
SELECT user_id, COUNT(*) AS order_count, SUM(amount) AS total_amount
FROM orders
WHERE status = 'paid'
GROUP BY user_id
HAVING COUNT(*) >= 2
ORDER BY user_id;
```

结果：用户 1 为 `2 笔、300`；用户 4 为 `2 笔、100`。过滤单笔订单状态在 `WHERE`，过滤聚合后的用户在 `HAVING`。

#### B3. 每个员工的已支付金额及登录自然日数

```sql
WITH paid AS (
    SELECT user_id, SUM(amount) AS total_amount
    FROM orders WHERE status = 'paid' GROUP BY user_id
), login_days AS (
    SELECT user_id, COUNT(DISTINCT DATE(login_at)) AS days
    FROM login_logs GROUP BY user_id
)
SELECT e.id, COALESCE(p.total_amount, 0) AS total_amount,
       COALESCE(l.days, 0) AS login_days
FROM employees e
LEFT JOIN paid p ON p.user_id = e.id
LEFT JOIN login_days l ON l.user_id = e.id
ORDER BY e.id;
```

员工 1 的金额是 `300`、登录天数是 `4`。先按用户分别聚合，避免订单与登录记录相乘。

### C. 子查询与排名

#### C1. 查询第二高的不同工资，不存在时返回 NULL

参考：[力扣 176：第二高的薪水](https://leetcode.cn/problems/second-highest-salary/)；本题适配 `employees` 表。

```sql
SELECT MAX(salary) AS second_highest_salary
FROM employees
WHERE salary < (SELECT MAX(salary) FROM employees);
```

结果是 `10000`。最高工资可能有多人；比较的是不同工资而非第二行。仅有一个不同工资或没有非空工资时，外层聚合仍返回一行 `NULL`。

#### C2. 每个部门最高工资的所有员工，包含并列

参考：[牛客 SQL 练习：各部门最高工资](https://www.nowcoder.com/discuss/353157192910053376)。

```sql
WITH ranked AS (
    SELECT e.*,
           DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS salary_rank
    FROM employees e
    WHERE dept_id IS NOT NULL AND salary IS NOT NULL
)
SELECT dept_id, id, name, salary
FROM ranked WHERE salary_rank = 1
ORDER BY dept_id, id;
```

结果员工 ID：`1、4、7`。空部门和无工资员工不参与；若最高工资并列，所有并列员工都会返回。

#### C3. 每个部门前两个不同工资的所有员工

参考：[力扣 185：部门工资前三高的所有员工](https://leetcode.cn/problems/department-top-three-salaries/)；这里改为前两个不同工资。

```sql
WITH ranked AS (
    SELECT e.*,
           DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS salary_rank
    FROM employees e
    WHERE dept_id IS NOT NULL AND salary IS NOT NULL
)
SELECT dept_id, id, name, salary
FROM ranked WHERE salary_rank <= 2
ORDER BY dept_id, salary DESC, id;
```

研发部门返回员工 `1、2、3`，因为 2 和 3 的工资并列第二。把条件改成 `<= 3` 即为前三个不同工资。

#### C4. 每个用户最新的一笔订单，同一时间取 ID 最大者

```sql
WITH ranked AS (
    SELECT o.*,
           ROW_NUMBER() OVER (
               PARTITION BY user_id ORDER BY created_at DESC, id DESC
           ) AS rn
    FROM orders o
)
SELECT user_id, id, amount, status, created_at
FROM ranked WHERE rn = 1
ORDER BY user_id;
```

结果订单 ID：`102、104、106`。本题包含所有状态；如果只要已支付订单，应在 CTE 内先过滤状态，再排名。

### D. 日期、留存与连续问题

#### D1. 首次登录用户的次日留存率

参考：[牛客：每个人最近的登录日期（三）](https://www.nowcoder.com/questionTerminal/16d41af206cd4066a06a3a0aa585ad3d)。本题口径明确为：有登录记录的用户中，首次登录的次日也登录的用户比例。

```sql
WITH days AS (
    SELECT DISTINCT user_id, DATE(login_at) AS login_date FROM login_logs
), first_day AS (
    SELECT user_id, MIN(login_date) AS first_date FROM days GROUP BY user_id
)
SELECT ROUND(AVG(CASE WHEN d.user_id IS NOT NULL THEN 1.0 ELSE 0.0 END), 3) AS retention
FROM first_day f
LEFT JOIN days d ON d.user_id = f.user_id
                AND d.login_date = DATE_ADD(f.first_date, INTERVAL 1 DAY);
```

用户 1、4 次日登录，用户 2 没有；结果为 `0.667`。分母是有登录记录的 3 个用户，无日志时 `AVG` 返回 `NULL`；按注册用户或按某日新增用户计算时分母需要另行定义。

#### D2. 找出连续登录至少 3 个自然日的用户

参考：[牛客：查询连续登录的用户](https://www.nowcoder.com/practice/9944210610ec417e94140ac09512a3f5)以及[连续登录问题](https://www.nowcoder.com/discuss/430111575618478080)。

```sql
WITH days AS (
    SELECT DISTINCT user_id, DATE(login_at) AS login_date FROM login_logs
), numbered AS (
    SELECT user_id, login_date,
           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_date) AS rn
    FROM days
), islands AS (
    SELECT user_id, login_date,
           DATE_SUB(login_date, INTERVAL rn DAY) AS grp
    FROM numbered
)
SELECT DISTINCT user_id
FROM islands
GROUP BY user_id, grp
HAVING COUNT(*) >= 3
ORDER BY user_id;
```

结果是用户 `1`，连续段为 1 月 1 日至 3 日。连续超过 3 天也只返回该用户一次；多个不同连续段也通过最外层 `DISTINCT` 合并。

#### D3. 每个用户的最长连续登录天数

```sql
WITH days AS (
    SELECT DISTINCT user_id, DATE(login_at) AS login_date FROM login_logs
), numbered AS (
    SELECT user_id, login_date,
           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_date) AS rn
    FROM days
), islands AS (
    SELECT user_id, DATE_SUB(login_date, INTERVAL rn DAY) AS grp
    FROM numbered
), streaks AS (
    SELECT user_id, grp, COUNT(*) AS days
    FROM islands GROUP BY user_id, grp
)
SELECT user_id, MAX(days) AS max_consecutive_days
FROM streaks GROUP BY user_id ORDER BY user_id;
```

结果：用户 1 为 `3`，用户 2 为 `1`，用户 4 为 `2`。无登录记录的员工不在结果中；若需要展示，应以员工表为左表连接结果并用 `COALESCE` 补 0。

## 七、Java 工程实践中的注意点

1. **明确返回类型与粒度。** 聚合或多表查询往往对应 DTO / 投影，而不是完整实体；避免按一对多结果直接映射成一对一对象。
2. **使用参数化查询。** 通过 JDBC `PreparedStatement`、MyBatis `#{}` 等绑定值，避免拼接用户输入；动态表名、列名或排序方向通常不能用值占位符，应使用受控白名单。
3. **分页要有稳定排序。** 常用 `ORDER BY created_at DESC, id DESC`；大偏移分页可能扫描大量记录，可考虑基于最后一条记录的游标分页，同时明确并发写入下的一致性要求。
4. **检查执行计划。** 使用 `EXPLAIN`，根据查询条件、连接键和真实数据分布评估索引。例如已支付订单按用户汇总可评估 `(status, user_id)`；具体索引顺序和是否覆盖要结合工作负载验证。避免把某种索引当作所有查询的通用答案。
5. **写操作与事务。** 更新、删除先核对 `WHERE` 和影响行数，多步一致性操作使用事务；并发更新需要结合隔离级别、锁或乐观锁设计。索引和事务的更多内容可参阅[数据库](db.md)。
6. **时间和金额口径。** 示例按数据库保存时间的自然日统计；跨时区业务应先确定业务时区，尤其是 `TIMESTAMP` 的会话时区转换。金额使用定点数，避免浮点误差。

## 八、学习与练习顺序

1. 独立完成 A1～A3，画出连接前后的行数。
2. 完成 B1～B3，检查空部门、取消订单和一对多相乘。
3. 完成 C1～C4，分别尝试并列工资、只有一个工资和相同下单时间。
4. 完成 D1～D3，增加同日重复登录、跨月连续登录和中断后再次连续登录的数据。
5. 修改每题条件并先手算结果，再执行 SQL；最后比较原题的数据模型和评测要求。

## 九、参考资料

- [MySQL 8.0：JOIN 语法](https://dev.mysql.com/doc/refman/8.0/en/join.html)
- [MySQL 8.0：聚合函数](https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html)
- [MySQL 8.0：窗口函数概念与语法](https://dev.mysql.com/doc/refman/8.0/en/window-functions-usage.html)
- [牛客：SQL 练习题汇总](https://www.nowcoder.com/discuss/353148510633664512)
- [牛客：连续登录问题](https://www.nowcoder.com/discuss/430111575618478080)
- [力扣：第二高的薪水](https://leetcode.cn/problems/second-highest-salary/)、[部门工资前三高的所有员工](https://leetcode.cn/problems/department-top-three-salaries/)
