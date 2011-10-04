package com.varun.yfs.dto;

import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ScreeningDetailDTO extends BaseModelData
{
	private static final long serialVersionUID = 7504451639139702860L;

	private long id;
	private String deleted;
	private CountryDTO country;
	private StateDTO state;
	private CityDTO city;
	private TownDTO town;
	private VillageDTO village;
	private ChapterNameDTO chapterName;
	private String address;
	private LocalityDTO locality;
	private String screeningDate;
	private ProcessTypeDTO processType;
	private String contactInformation;
	private TypeOfLocationDTO typeOfLocation;
	private Set<VolunteerDTO> setVolunteers;
	private Set<DoctorDTO> setDoctors;
	private Set<PatientDetailDTO> setPatientDetails;

	public ScreeningDetailDTO()
	{
		setDeleted("N");
	}

	public ScreeningDetailDTO(String name)
	{
		setDeleted("N");
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		set("id",id);
		this.id = id;
	}

	public String getDeleted()
	{
		return deleted;
	}

	public void setDeleted(String deleted)
	{
		set("deleted",deleted);
		this.deleted = deleted;
	}

	public CountryDTO getCountry()
	{
		return country;
	}

	public void setCountry(CountryDTO country)
	{
		set("country",country);
		this.country = country;
	}

	public StateDTO getState()
	{
		return state;
	}

	public void setState(StateDTO state)
	{
		set("state",state);
		this.state = state;
	}

	public CityDTO getCity()
	{
		return city;
	}

	public void setCity(CityDTO city)
	{
		set("city",city);
		this.city = city;
	}

	public TownDTO getTown()
	{
		return town;
	}

	public void setTown(TownDTO town)
	{
		set("town",town);
		this.town = town;
	}

	public VillageDTO getVillage()
	{
		return village;
	}

	public void setVillage(VillageDTO village)
	{
		set("village",village);
		this.village = village;
	}

	public ChapterNameDTO getChapterName()
	{
		return chapterName;
	}

	public void setChapterName(ChapterNameDTO chapterName)
	{
		set("chapterName",chapterName);
		this.chapterName = chapterName;
	}

	public LocalityDTO getLocality()
	{
		return locality;
	}

	public void setLocality(LocalityDTO locality)
	{
		set("locality",locality);
		this.locality = locality;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		set("address",address);
		this.address = address;
	}

	public String getScreeningDate()
	{
		return screeningDate;
	}

	public void setScreeningDate(String screeningDate)
	{
		set("screeningDate",screeningDate);
		this.screeningDate = screeningDate;
	}

	public ProcessTypeDTO getProcessType()
	{
		return processType;
	}

	public void setProcessType(ProcessTypeDTO processType)
	{
		set("processType",processType);
		this.processType = processType;
	}

	public TypeOfLocationDTO getTypeOfLocation()
	{
		return typeOfLocation;
	}

	public void setTypeOfLocation(TypeOfLocationDTO typeOfLocation)
	{
		set("typeOfLocation",typeOfLocation);
		this.typeOfLocation = typeOfLocation;
	}

	public Set<VolunteerDTO> getSetVolunteers()
	{
		return setVolunteers;
	}

	public void setSetVolunteers(Set<VolunteerDTO> setVolunteers)
	{
		set("volunteers",setVolunteers);
		this.setVolunteers = setVolunteers;
	}

	public Set<DoctorDTO> getSetDoctors()
	{
		return setDoctors;
	}

	public void setSetDoctors(Set<DoctorDTO> setDoctors)
	{
		set("doctors",setDoctors);
		this.setDoctors = setDoctors;
	}

	public String getContactInformation()
	{
		return contactInformation;
	}

	public void setContactInformation(String contactInformation)
	{
		set("contactInformation",contactInformation);
		this.contactInformation = contactInformation;
	}

	public void setSetPatientDetails(Set<PatientDetailDTO> setPatientDetails)
	{
		set("patientDetails",setPatientDetails);
		this.setPatientDetails = setPatientDetails;
	}

	public Set<PatientDetailDTO> getSetPatientDetails()
	{
		return setPatientDetails;
	}

}