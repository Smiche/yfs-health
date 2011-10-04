package com.varun.yfs.server.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "town")
public class Town implements Serializable
{
	private static final long serialVersionUID = 3791392517651758123L;
	@Id
	@GeneratedValue
	@Column(name = "townId")
	private long id;
	
	@Column(nullable = false)
	private String townName;
	
	@Column(nullable = false)
	private String deleted;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "stateId",  nullable = false, updatable = true, insertable = true)
	@Fetch(FetchMode.SELECT)
	private State state;
	
//	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "town")
//	@Fetch(FetchMode.SELECT)
//	private List<Users> users;

	public Town()
	{
		setDeleted("N");
	}

	public Town(String name)
	{
		setName(name);
		setDeleted("N");
	}

	
	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	
	public String getName()
	{
		return townName;
	}

	public void setName(String name)
	{
		this.townName = name;
	}

	
	public String getDeleted()
	{
		return deleted;
	}

	public void setDeleted(String deleted)
	{
		this.deleted = deleted;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}
	

//	public void setUsers(List<Users> users)
//	{
//		this.users = users;
//	}
//
//	public List<Users> getUsers()
//	{
//		return users;
//	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Town other = (Town) obj;
		if (!townName.equalsIgnoreCase(other.townName))
			return false;
		return true;
	}
}
