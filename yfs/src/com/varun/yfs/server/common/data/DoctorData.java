package com.varun.yfs.server.common.data;

import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.varun.yfs.client.common.RpcStatusEnum;
import com.varun.yfs.client.index.ModelDataEnum;

public class DoctorData extends AbstractData
{
	public ModelData getModel()
	{
		ModelData model = new BaseModelData();
		model.set("data", DataUtil.getModelList(ModelDataEnum.Doctor.name()));

		model.set("configIds", Arrays.asList("name"));
		model.set("configCols", Arrays.asList("Name"));
		model.set("configType", Arrays.asList("Text"));

		return model;
	}

	@Override
	public RpcStatusEnum saveModel(ModelData model)
	{
		List<ModelData> lstModels = model.get("data");
		RpcStatusEnum status = RpcStatusEnum.SUCCESS;
		try
		{
			DataUtil.<ModelData> saveListStore(ModelDataEnum.Doctor.name(), lstModels);
		} catch (Exception ex)
		{
			status = RpcStatusEnum.FAILURE;
		}
		return status;
	}
}