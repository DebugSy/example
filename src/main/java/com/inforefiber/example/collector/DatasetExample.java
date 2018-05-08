package com.inforefiber.example.collector;

import com.inforefiner.europa.bean.Dataset;
import com.inforefiner.europa.bean.Schema;
import com.inforefiner.europa.client.MetaClient;
import com.inforefiner.europa.utils.Datasets;

/**
 * Created by DebugSy on 2018/4/28.
 */
public class DatasetExample {

	private MetaClient metaClient;

	public DatasetExample(MetaClient metaClient) {
		this.metaClient = metaClient;
	}

	/**
	 * 创建schema
	 * @return schema的id
	 */
	public String createDataset(){
		Schema schema = metaClient.getSchemaByName("example/client_create_schema");
		Dataset dataset = Datasets.dataset("example/client_create_dataset", schema, "HDFS")
				.storageConfig("path", "/tmp/shiy/data/output")
				.storageConfig("format", "csv")
				.storageConfig("escapeChar", "\\")
				.storageConfig("quoteChar", "\"")
				.storageConfig("separator", ",")
				.storageConfig("header", "false")
				.build();
		String datasetId = metaClient.createDataset(dataset);
		return datasetId;
	}

	/**
	 * 根据已有的schema，增加字段
	 */
	public void updateDataset(){
		Schema schema = metaClient.getSchemaByName("example/client_create_schema");
		Dataset dataset = metaClient.getDatasetByName("example/client_create_dataset");
		dataset.setSchema(schema.getId());
		dataset.setSchemaName(schema.getName());
		dataset.setSchemaVersion(schema.getVersion());
		metaClient.updateDataset(dataset);

	}

	public static void main(String[] args) {
		String host = "node3";
		String port = "8515";
		String username = "admin";
		String password = "123456";
		String version = "europa-3.0.0.13-20180426";
		MetaClient metaClient = new MetaClient();
		metaClient.login(host, port, username, password, version);
		DatasetExample datasetExample = new DatasetExample(metaClient);
		datasetExample.updateDataset();
	}

}
