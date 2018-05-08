package com.inforefiber.example.collector;

import com.inforefiner.europa.bean.*;
import com.inforefiner.europa.client.FlowClient;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by DebugSy on 2018/4/3.
 */
public class StreamTransform {

	public static void main(String[] args) throws InterruptedException {

		FlowClient client = new FlowClient();
		client.setExecutorCores(4);
		client.setExecutorMemory("2048");
		client.setDriverMemory("1024");
		client.setUseLastState(true);
		client.setIgnoreFailState(true);

		//login to the server
		String login = client.login("node2", "17510", "admin", "123456", "");
		if ("success".equalsIgnoreCase(login)){

			//1. get flow from server by flow's name and version
			FlowDesc flow = client.getFlowByNameAndVersion("default/admin/shiy/socket_example", 7);

			//2. flow parameter settings
			Properties flowConf = new Properties();
			String executionId = client.submit(flow, flowConf, "socket_example_" + new Date().getTime());

			//3. if the flow's type is dataflow,you can await for report
			FlowExecution flowExecution = client.awaitForReport(executionId);

			//4. get flow status
			Status status = flowExecution.getStatus();

			//5. get flow outputs
			List<Dataset> executionOutputs = client.getExecutionOutputs(executionId);

		} else {
			throw new RuntimeException("can't login to the server.");
		}

	}

}
