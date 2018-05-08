package com.inforefiber.example.collector;

import com.inforefiner.europa.bean.DataField;
import com.inforefiner.europa.bean.Schema;
import com.inforefiner.europa.client.MetaClient;
import com.inforefiner.europa.utils.Schemas;

import java.util.List;

/**
 * Created by DebugSy on 2018/4/28.
 */
public class SchemaExample {

	private MetaClient metaClient;

	public SchemaExample(MetaClient metaClient) {
		this.metaClient = metaClient;
	}

	/**
	 * 创建schema
	 * @return schema的id
	 */
	public String createSchema(){
		Schema schema = Schemas.schema("example/client_create_schema") //名称带目录，以/分割
				.field("id", "int")
				.field("name", "string")
				.field("age", "int")
				.build();
		String schemaId = metaClient.createSchema(schema);
		return schemaId;
	}

	/**
	 * 根据已有的schema，增加字段
	 * 注：更新schema操作是具有风险的，如果别的dataset也在使用该schema可能导致schema找不到的情况
	 */
	public void updateSchema(){
		Schema schema = metaClient.getSchemaByName("example/client_create_schema");
		List<DataField> fields = schema.getFields();

		DataField adrress = new DataField("address", "string");
		fields.add(adrress);

		metaClient.updateSchema(schema);
	}

	/**
	 * 通过名称获取schema
	 * @return
	 */
	public Schema getSchemaByName(){
		Schema schema = metaClient.getSchemaByName("example/client_create_schema");
		return schema;
	}

	public static void main(String[] args) {
		String host = "node3";
		String port = "8515";
		String username = "admin";
		String password = "123456";
		String version = "europa-3.0.0.13-20180426";
		MetaClient metaClient = new MetaClient();
		metaClient.login(host, port, username, password, version);
		SchemaExample schemaExample = new SchemaExample(metaClient);
		schemaExample.createSchema();
	}

}
