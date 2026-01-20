# Quick Reference - Identity Fix Changes

## API Endpoint Changes

### 1. Get Courses by Tenant
**Before:**
```
GET /api/courses/tenants/acme_university
```

**After:**
```
GET /api/courses/tenants/550e8400-e29b-41d4-a716-446655440000
```

---

### 2. Get Org Units (Query Parameters)
**Before:**
```
GET /api/tenants/{tenantId}/org-units?structureName=Academic&typeName=Department
```

**After:**
```
GET /api/tenants/{tenantId}/org-units?structureId=550e8400-e29b-41d4-a716-446655440000&typeId=660e8400-e29b-41d4-a716-446655440001
```

---

### 3. Get Org Units Tree
**Before:**
```
GET /api/tenants/{tenantId}/org-units/structure/Academic/tree
```

**After:**
```
GET /api/tenants/{tenantId}/org-units/structure/550e8400-e29b-41d4-a716-446655440000/tree
```

---

## JWT Response Change

### Login Response
**Before:**
```json
{
  "accessToken": "eyJhbGc...",
  "type": "Bearer",
  "username": "admin@example.com",
  "role": "ADMIN",
  "refreshToken": "550e8400-...",
  "tenantId": "acme_university"
}
```

**After:**
```json
{
  "accessToken": "eyJhbGc...",
  "type": "Bearer",
  "username": "admin@example.com",
  "role": "ADMIN",
  "refreshToken": "550e8400-...",
  "tenantId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## How to Get UUIDs

### 1. From List Responses
All list endpoints return objects with `id` field (UUID):

```json
{
  "550e8400-e29b-41d4-a716-446655440000": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Computer Science",
    ...
  }
}
```

Use the `id` field for subsequent API calls.

### 2. From Login Response
After login, store the `tenantId` from JWT response for future API calls.

### 3. From Creation Responses
When creating entities, the response includes the generated UUID:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "New Course",
  ...
}
```

---

## Migration Checklist for Frontend

- [ ] Update login flow to use `tenantId` UUID from response
- [ ] Update course listing to use `/api/courses/tenants/{tenantId}`
- [ ] Update org unit queries to use `structureId` and `typeId`
- [ ] Update org unit tree endpoint to use `structureId`
- [ ] Store entity UUIDs from API responses
- [ ] Remove any hardcoded entity names from API calls
- [ ] Update form submissions to use UUIDs for foreign keys

---

## Backwards Compatibility

⚠️ **Breaking Changes** - Old endpoints will not work:

1. ❌ Using tenant name in course endpoint
2. ❌ Using structure/type names in org unit queries
3. ❌ Expecting tenant name in JWT response

**No backwards compatibility** - all clients must update to UUID-based approach.

---

## Example Workflow

### Creating an Org Unit
1. **Get Org Structures**: `GET /api/tenants/{tenantId}/org-structures`
   ```json
   Response: {
     "550e8400-...": { "id": "550e8400-...", "name": "Academic", ... }
   }
   ```

2. **Get Org Structure Details**: `GET /api/tenants/{tenantId}/org-structures/detailed`
   ```json
   Response includes orgUnitTypes with their UUIDs
   ```

3. **Create Org Unit**: `POST /api/tenants/{tenantId}/org-units`
   ```json
   {
     "name": "Computer Science Dept",
     "orgUnitTypeId": "660e8400-...",  // Use UUID from step 2
     "parentOrgUnitId": "770e8400-...", // Use UUID if applicable
     "attributes": {}
   }
   ```

4. **Query Org Units**: `GET /api/tenants/{tenantId}/org-units?structureId=550e8400-...&typeId=660e8400-...`

---

## Testing Examples

### Using cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"password"}'

# Save tenantId from response

# Get courses
curl http://localhost:8080/api/courses/tenants/{tenantId}

# Get org units
curl "http://localhost:8080/api/tenants/{tenantId}/org-units?structureId={structureId}&typeId={typeId}"
```

---

## Common Errors After Migration

### Error: 400 Bad Request - "Invalid UUID string"
**Cause:** Still passing entity name instead of UUID
**Fix:** Use the UUID from the entity's `id` field

### Error: 404 Not Found - "Tenant not found"
**Cause:** Using tenant name instead of tenant ID
**Fix:** Use `tenantId` from login JWT response

### Error: 404 Not Found - "Org Structure not found"
**Cause:** Using structure name instead of structure ID
**Fix:** First query org structures to get IDs, then use those IDs

---

## Support

All entities now use UUID as primary identifier:
- ✅ User ID
- ✅ Tenant ID
- ✅ Course ID
- ✅ Org Structure ID
- ✅ Org Unit Type ID
- ✅ Org Unit ID
- ✅ Module ID
- ✅ Lesson ID
- ✅ All other entities

**Rule:** If it's an entity, use its UUID for identification.

