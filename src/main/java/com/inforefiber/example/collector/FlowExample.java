package com.inforefiber.example.collector;

import com.inforefiner.europa.bean.*;
import com.inforefiner.europa.client.FlowClient;
import com.inforefiner.europa.client.MetaClient;
import com.inforefiner.europa.utils.Flows;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DebugSy on 2018/4/28.
 */
public class FlowExample {

	private FlowClient flowClient;

	private MetaClient metaClient;

	public FlowExample(FlowClient flowClient, MetaClient metaClient) {
		this.flowClient = flowClient;
		this.metaClient = metaClient;
	}

	/***
	 * 创建flow
	 * 创建flow需要已经配置好的schema和dataset
	 * @return
	 */
	public FlowDesc createFlow(){
		Schema schema = metaClient.getSchemaByName("example/client_create_schema");
		Dataset dataset = metaClient.getDatasetByName("example/client_create_dataset");

		FlowDesc flow = Flows.dataflow("example/client_create_dataflow")
				//source step
				.step(Flows.step("source", "source_0", "source")
						.config("schema", schema.getName())
						.config("id", dataset.getId())
						.config("dataset", dataset.getName())
						.outputWith("output", schema.getFields())
						.build())
				.step(Flows.step("filter", "filter_0", "大于50岁")
						.inputWith("input", schema.getFields())
						.config("condition", "age>50")
						.outputWith("output", schema.getFields())
						.build())
				.step(Flows.step("sink", "sink_0", "大于22岁人员信息")
						.inputWith("input", schema.getFields())
						.config("schema", schema.getName())
						.config("dataset", "example/client_test_output")
						.config("type", "HDFS")
						.config("format", "csv")
						.config("path", "/tmp/shiy/data/output")
						.config("quoteChar", "\"")
						.config("escapeChar", "\\")
						.config("separator", ",")
						.config("mode", "overwrite")
						.outputWith("output", schema.getFields())
						.build())
				.connect("source_0", "filter_0")
				.connect("filter_0", "sink_0")
				.build();
		FlowDesc flowDesc = flowClient.create(flow);
		return flowDesc;
	}

	/**
	 * 通过名称和版本号获取Fow
	 * @param name flow的名称
	 * @param version flow的版本号
	 * @return
	 */
	public FlowDesc getFlow(String name, int version){
		FlowDesc flow = flowClient.getFlowByNameAndVersion(name, version);
		return flow;
	}

	/**
	 * 更新flow
	 * 该示例只展示更新flow中所有step的输入和输出
	 * @param flowDesc
	 */
	public void updateFlow(FlowDesc flowDesc){

		List<DataField> fields = new ArrayList<DataField>();
		DataField id = new DataField("id", "int");
		DataField name = new DataField("name", "string");
		DataField age = new DataField("age", "int");
//		DataField address = new DataField("address", "string");
		fields.add(id);
		fields.add(name);
		fields.add(age);
//		fields.add(address);

		//更新每个step的输入和输出
		List<StepDesc> steps = flowDesc.getSteps();
		for (StepDesc step : steps){
			List<ConfigObject> inputConfigurations = step.getInputConfigurations();
			if (inputConfigurations != null){
				for (ConfigObject configObject : inputConfigurations){
					configObject.withConfig("fields", fields);
				}
			}

			List<ConfigObject> outputConfigurations = step.getOutputConfigurations();
			if (outputConfigurations != null){
				for (ConfigObject configObject : inputConfigurations){
					configObject.withConfig("fields", fields);
				}
			}
		}
	}

	/**
	 * 提交一个任务
	 * @param flowDesc
	 * @return 返回一个执行id - executionId
	 */
	public String submitFlow(FlowDesc flowDesc){
		String executionId = flowClient.submit(flowDesc, null, "example_client_submit_flow_" + System.currentTimeMillis());
		return executionId;
	}

	/**
	 * 等待flow的执行完毕，返回通知
	 * @param executionId
	 * @return 返回flow的执行
	 */
	public FlowExecution awaitReport(String executionId){
		try {
			FlowExecution flowExecution = flowClient.awaitForReport(executionId);
			return flowExecution;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据返回信息获取flow的输出
	 * @param flowExecution
	 * @return 返回输出的dataset列表
	 */
	public List<Dataset> getFlowOutput(FlowExecution flowExecution){
		List<Dataset> executionOutputs = flowClient.getExecutionOutputs(flowExecution.getId());
		return executionOutputs;
	}

	/**
	 * 删除flow,会将flow的信息以及历史信息一并删除
	 * @param flowId
	 */
	public void deleteFlow(String flowId){
		flowClient.delete(flowId);
	}

	public static void main(String[] args) {
		String host = "node3";
		String port = "8515";
		String username = "admin";
		String password = "123456";
		String version = "europa-3.0.0.13-20180426";
		FlowClient flowClient = new FlowClient(3000);
		flowClient.login(host, port, username, password, version);
		MetaClient metaClient = new MetaClient(3000);
		metaClient.login(host, port, username, password, version);
		FlowExample flowExample = new FlowExample(flowClient, metaClient);
		FlowDesc flowDesc = flowExample.getFlow("example/client_create_dataflow", 1);
		String executionId = flowExample.submitFlow(flowDesc);
		FlowExecution flowExecution = flowExample.awaitReport(executionId);
		List<Dataset> flowOutput = flowExample.getFlowOutput(flowExecution);
		for (Dataset dataset : flowOutput){
			System.out.println(dataset.getName());
		}
	}

}
