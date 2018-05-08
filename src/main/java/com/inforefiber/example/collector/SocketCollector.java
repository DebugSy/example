package com.inforefiber.example.collector;

import com.inforefiner.europa.client.DataInportClient;
import com.inforefiner.europa.entity.FieldMapping;
import com.inforefiner.europa.entity.SyncTask;
import com.inforefiner.europa.rpc.dto.task.source.DataSource;
import com.inforefiner.europa.rpc.dto.task.store.DataStore;
import com.inforefiner.europa.utils.DataSources;
import com.inforefiner.europa.utils.DataStores;
import com.inforefiner.europa.utils.FieldMappings;
import com.inforefiner.europa.utils.SyncTasks;

import java.util.List;

/**
 *
 * Created by DebugSy on 2018/4/3.
 */
public class SocketCollector {

	public static void main(String[] args) {
		DataInportClient client = new DataInportClient();
		//login to the server
		client.login("node2", "8514", "admin", "123456", "europa-3.0.0.49-20180330");

		//1. get syncTask by jobId
		SyncTask syncTask = client.getCollectorTask("3eb54b38-617c-4b0e-9975-3ead779c3774");

		//or use code to create syncTask
		//operateType -- 0: 原样, 1: 抽取, 2: 分割, 3: 过滤
		DataSource dataSource = DataSources.dataSource().operateType(4).socketDataSource("node3", 9083, "TCP",
				"{\"a1\": \"(?<id>(\\\\d+)),(?<name>(\\\\w+))\",\"a2\": \"(?<id>(\\\\d+)),(?<age>(\\\\d+))\"}",
				"05e134fb-0c91-4a85-b8da-cee69e6cc9ec", "shiy_socket_schema").build();

		DataStore dataStore = DataStores.dataStore().kafkaDataStore("node1:2181,node2:2181,node3:2181/linkoop33/kafka333",
				"node3:9092","test11", "0.10", ",").build();

		List<FieldMapping> fieldMappings = FieldMappings.fieldMapping()
				.fieldMapping("id", "int", "id", "int")
				.fieldMapping("name", "string", "name", "string")
				.fieldMapping("age", "int", "age", "int")
				.fieldMapping("_match_key_", "string", "_match_key_", "string")
				.build();

		SyncTask task = SyncTasks.syncTask("shiy_client_create_for_test_socket", dataSource, dataStore, fieldMappings).build();

		//2. create a syncTask
		syncTask.setName(syncTask.getName().concat("_copy_by_client"));
		String syncJobId = client.createSyncJob(syncTask);//client.createSyncJob(task)
		syncTask.setJobId(syncJobId);

		//3. start a syncJob
		client.startSyncJob(syncJobId);

		//4. stop a syncJob
		client.stopSyncJob(syncJobId);

		//5. modify a syncJob		! note:	The modify operation will stop and delete the old job, and then create a new job
		String jobId = client.modifySocketCollectorTask(syncTask, "(?<id>(\\d+))<(?<name>(\\w+))>(?<age>(\\d+))");
		client.startSyncJob(jobId);
		client.stopSyncJob(jobId);

		//6. delete a syncJob
		client.deleteSyncJob(jobId);
	}



}
