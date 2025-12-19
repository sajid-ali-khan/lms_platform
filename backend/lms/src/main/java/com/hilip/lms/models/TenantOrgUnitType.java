package com.hilip.lms.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tenant_org_unit_types")
public class TenantOrgUnitType {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "tenantOrgUnitType", cascade = CascadeType.REMOVE)
	private List<OrgUnitType> orgUnitTypes = new ArrayList<>();
}