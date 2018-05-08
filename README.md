# example说明
这个example是socket数据源的示例

# 流程配置
## 1. 配置前端采集器
1. 在"数据集成" -> "资源目录" -> "数据源" 中新建"Socket数据源"
2. "数据集成" -> "数据导入"中新建导入任务,数据源选择上面配置的socket数据源，其他正常配置

## 2. 配置stream transform flow
1. 配置stream flow的处理流程（无特殊要求）

# 如何调用
## collector调用
1. collector的调用需要先登录
2. 登陆完成后，可以根据jobId获取任务SyncTask
3. 详见：SocketCollector.java

## server调用
1. server的调用需要先登录
2. 登陆完成后，需要根据提交的flow type设置运行参数 
3. 然后可以设置并运行flow
4. 详见：StreamTransform.java

note:
src/libs下是工程所有的依赖包
