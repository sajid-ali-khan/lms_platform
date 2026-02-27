import axiosInstance from './axiosInstance';

export function getOrgStructuresBasic(tenantId) {
  return axiosInstance.get(`/api/tenants/${tenantId}/org-structures`);
}

export function getOrgStructuresDetailed(tenantId) {
  return axiosInstance.get(`/api/tenants/${tenantId}/org-structures/detailed`);
}

export function createOrgStructure(tenantId, data) {
  return axiosInstance.post(`/api/tenants/${tenantId}/org-structures`, data);
}

export function createOrgUnit(tenantId, data) {
  return axiosInstance.post(`/api/tenants/${tenantId}/org-units`, data);
}

export function getOrgUnitsTree(tenantId, structureId) {
  return axiosInstance.get(`/api/tenants/${tenantId}/org-units/structure/${structureId}/tree`);
}

export function getOrgUnits(tenantId, structureId, typeId, parentUnitId) {
  const params = {};
  if (structureId !== undefined && structureId !== null) params.structureId = structureId;
  if (typeId !== undefined && typeId !== null) params.typeId = typeId;
  if (parentUnitId !== undefined && parentUnitId !== null) params.parentUnitId = parentUnitId;
  return axiosInstance.get(`/api/tenants/${tenantId}/org-units`, { params });
}
