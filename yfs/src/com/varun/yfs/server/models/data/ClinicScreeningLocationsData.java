package com.varun.yfs.server.models.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.varun.yfs.client.common.RpcStatusEnum;
import com.varun.yfs.dto.CityDTO;
import com.varun.yfs.dto.ClinicDTO;
import com.varun.yfs.dto.PermissionTypeEnum;
import com.varun.yfs.dto.UserDTO;

public class ClinicScreeningLocationsData extends AbstractData
{
	private static final Logger LOGGER = Logger.getLogger(ClinicScreeningLocationsData.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public ModelData getModel(UserDTO userDto)
	{
		LOGGER.debug("Attempting data load");
		
		List<ModelData> nodes = new ArrayList<ModelData>();

		List<String> lstClinicPermissions = userDto.getClinicWithPermission(PermissionTypeEnum.READ);

		List<String> clinicsWithRead = null;
		if (userDto.isAdmin())
			clinicsWithRead = (List<String>) DataUtil.executeQuery("select clinicName from Clinic where deleted = 'N'");
		else
			clinicsWithRead = lstClinicPermissions;
		

		ModelData model = new BaseModelData();
		model.set("data", nodes);
		
		model.set("permissions", userDto.getClinicPermissionsMap());

		List<CityDTO> rootNodes = DataUtil.<CityDTO> getModelList("City");
		for (CityDTO cityDTO : rootNodes)
		{
			ModelData rootNode = new BaseModelData();
			rootNode.set("name", cityDTO.getName());
			rootNode.set("icon", "");
			nodes.add(rootNode);

			List<ModelData> chapterNodes = new ArrayList<ModelData>();
			rootNode.set("children", chapterNodes);

			List<ClinicDTO> lstScrDet = DataUtil.getClinics("City", "id", String.valueOf(cityDTO.getId()));
			for (ClinicDTO clinicDTO : lstScrDet)
			{
				String clinicName = clinicDTO.toString();

				if (!clinicsWithRead.contains(clinicName))
					continue;

				ModelData scrNode = new BaseModelData();
				scrNode.set("name", clinicName);
				scrNode.set("id", String.valueOf(clinicDTO.getId()));
				scrNode.set("icon", "screeningIndividual");
				chapterNodes.add(scrNode);
			}
		}
		LOGGER.debug("Data load complete.");
		return model;
	}

	@Override
	public RpcStatusEnum saveModel(ModelData model)
	{
		return RpcStatusEnum.SUCCESS;
	}
}
