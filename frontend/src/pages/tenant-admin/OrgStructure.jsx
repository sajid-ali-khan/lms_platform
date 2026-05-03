import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getOrgStructures, getOrgUnitsTree, getOrgUnitDetails } from "../../api";

function buildTree(flatNodes) {
  const map = {};
  const roots = [];
  flatNodes.forEach((n) => { map[n.id] = { ...n, children: [] }; });
  flatNodes.forEach((n) => {
    if (n.parentId && map[n.parentId]) {
      map[n.parentId].children.push(map[n.id]);
    } else if (!n.parentId) {
      roots.push(map[n.id]);
    }
  });
  return roots;
}

function TreeNode({ node, depth = 0, levelNames, onSelect, selectedId }) {
  const [open, setOpen] = useState(depth === 0);
  const hasChildren = node.children && node.children.length > 0;
  const isSelected = node.id === selectedId;

  return (
    <div style={{ marginLeft: depth * 20 }}>
      <div
        className="d-flex align-items-center gap-1 py-1 px-2"
        onClick={() => { if (hasChildren) setOpen(!open); onSelect(node); }}
        style={{
          cursor: "pointer",
          borderRadius: "5px",
          background: isSelected ? "var(--enterprise-primary)" : "transparent",
          color: isSelected ? "#fff" : "var(--enterprise-text)",
          fontSize: "0.875rem",
          userSelect: "none",
        }}
      >
        <span style={{ width: "16px", display: "inline-block", textAlign: "center", fontSize: "0.7rem", opacity: hasChildren ? 1 : 0 }}>
          {hasChildren ? (open ? "▼" : "▶") : ""}
        </span>
        <span
          style={{
            display: "inline-block",
            width: "6px",
            height: "6px",
            borderRadius: "50%",
            marginRight: "6px",
            background: isSelected ? "#fff" : "var(--enterprise-primary)",
            flexShrink: 0,
          }}
        />
        <span>{node.name}</span>
        {levelNames && levelNames[node.level] && (
          <span style={{ marginLeft: "auto", fontSize: "0.7rem", opacity: 0.6, fontStyle: "italic" }}>
            {levelNames[node.level]}
          </span>
        )}
      </div>
      {open &&
        hasChildren &&
        node.children.map((child) => (
          <TreeNode key={child.id} node={child} depth={depth + 1} levelNames={levelNames} onSelect={onSelect} selectedId={selectedId} />
        ))}
    </div>
  );
}

export default function OrgStructure() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const tenantId = user?.tenantId;

  const [structures, setStructures] = useState([]);
  const [selectedStructureId, setSelectedStructureId] = useState("");
  const [treeNodes, setTreeNodes] = useState([]);
  const [selectedNode, setSelectedNode] = useState(null);
  const [loadingStructures, setLoadingStructures] = useState(true);
  const [loadingTree, setLoadingTree] = useState(false);
  const [structuresError, setStructuresError] = useState("");
  const [nodeDetails, setNodeDetails] = useState(null);
  const [loadingDetails, setLoadingDetails] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  useEffect(() => {
    if (!tenantId) return;
    getOrgStructures(tenantId)
      .then((data) => {
        setStructures(data);
        if (data.length > 0) setSelectedStructureId(String(data[0].id));
      })
      .catch(() => setStructuresError("Failed to load structures"))
      .finally(() => setLoadingStructures(false));
  }, [tenantId]);

  useEffect(() => {
    if (!selectedStructureId || !tenantId) return;
    setLoadingTree(true);
    setSelectedNode(null);
    setNodeDetails(null);
    getOrgUnitsTree(tenantId, selectedStructureId)
      .then((flat) => setTreeNodes(buildTree(flat)))
      .catch(() => setTreeNodes([]))
      .finally(() => setLoadingTree(false));
  }, [selectedStructureId, tenantId]);

  const handleNodeSelect = (node) => {
    setSelectedNode(node);
    setNodeDetails(null);
    setSearchQuery("");
    setCurrentPage(1);
    if (!node || !tenantId) return;

    setLoadingDetails(true);
    getOrgUnitDetails(tenantId, node.id)
      .then((data) => setNodeDetails(data))
      .catch(() => setNodeDetails(null))
      .finally(() => setLoadingDetails(false));
  };

  const selectedStructure = structures.find((s) => String(s.id) === selectedStructureId);

  // Build level name map: { [levelIndex]: typeName }
  const levelNames = {};
  if (selectedStructure?.structure) {
    selectedStructure.structure.forEach((lvl, i) => { levelNames[i] = typeof lvl === "string" ? lvl : lvl.name; });
  }

  return (
    <div>
      <div className="page-header">
        <h1 className="page-header-title">Organization Structure</h1>
        <div className="d-flex gap-2">
          <button onClick={() => navigate("/admin/organization/create")} className="btn-enterprise-ghost">
            + Create Structure
          </button>
          {selectedStructureId && (
            <button onClick={() => navigate("/admin/organization/update")} className="btn-enterprise-primary">
              Add Units
            </button>
          )}
        </div>
      </div>

      {structuresError && <div className="alert-enterprise-danger mb-3">{structuresError}</div>}

      {loadingStructures ? (
        <div className="d-flex align-items-center" style={{ color: "var(--enterprise-muted)", gap: "0.5rem" }}>
          <div className="loading-enterprise"></div> Loading structures…
        </div>
      ) : structures.length === 0 ? (
        <div className="empty-state-enterprise">
          <p>No organization structures found.</p>
          <button onClick={() => navigate("/admin/organization/create")} className="btn-enterprise-primary">
            Create your first structure
          </button>
        </div>
      ) : (
        <>
          {/* Structure selector */}
          <div className="mb-4" style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
            <label className="form-label-enterprise mb-0">Structure:</label>
            <select
              className="form-control-enterprise"
              style={{ minWidth: "220px", width: "auto" }}
              value={selectedStructureId}
              onChange={(e) => setSelectedStructureId(e.target.value)}
            >
              {structures.map((s) => (
                <option key={s.id} value={String(s.id)}>{s.name}</option>
              ))}
            </select>
          </div>

          <div className="row">
            {/* Tree Panel */}
            <div className="col-md-6 mb-3">
              <div className="card-enterprise" style={{ height: "420px", display: "flex", flexDirection: "column" }}>
                <div className="card-enterprise-header">
                  <span style={{ fontWeight: 600 }}>{selectedStructure?.name || "Tree"}</span>
                  {selectedStructure?.structure && (
                    <span style={{ fontSize: "0.75rem", color: "var(--enterprise-muted)" }}>
                      {Array.isArray(selectedStructure.structure)
                        ? selectedStructure.structure.map((s) => (typeof s === "string" ? s : s.name)).join(" → ")
                        : ""}
                    </span>
                  )}
                </div>
                <div className="card-enterprise-body" style={{ flex: 1, overflowY: "auto" }}>
                  {loadingTree ? (
                    <div className="d-flex align-items-center" style={{ color: "var(--enterprise-muted)", gap: "0.5rem" }}>
                      <div className="loading-enterprise"></div> Loading…
                    </div>
                  ) : treeNodes.length === 0 ? (
                    <div className="empty-state-enterprise" style={{ padding: "1rem 0" }}>
                      <p style={{ margin: 0 }}>No units added yet.</p>
                    </div>
                  ) : (
                    treeNodes.map((root) => (
                      <TreeNode
                        key={root.id}
                        node={root}
                        depth={0}
                        levelNames={levelNames}
                        onSelect={handleNodeSelect}
                        selectedId={selectedNode?.id}
                      />
                    ))
                  )}
                </div>
              </div>
            </div>

            {/* Details Panel */}
            <div className="col-md-6">
              <div className="card-enterprise" style={{ minHeight: "420px" }}>
                <div className="card-enterprise-header">
                  <span style={{ fontWeight: 600 }}>Details</span>
                </div>
                <div className="card-enterprise-body">
                  {!selectedNode ? (
                    <div className="empty-state-enterprise" style={{ padding: "1rem 0" }}>
                      <p style={{ margin: 0 }}>Select a node to view details.</p>
                    </div>
                  ) : loadingDetails ? (
                    <div className="d-flex align-items-center" style={{ color: "var(--enterprise-muted)", gap: "0.5rem" }}>
                      <div className="loading-enterprise"></div> Loading node details…
                    </div>
                  ) : (
                    <div>
                      {/* Grid for basics + metrics */}
                      <div className="row g-3 mb-4">
                        <div className="col-sm-6">
                          <dl className="mb-0" style={{ fontSize: "0.875rem" }}>
                            <dt style={{ color: "var(--enterprise-muted)", fontWeight: 500, marginBottom: "0.1rem" }}>Name</dt>
                            <dd style={{ marginBottom: "0.75rem", fontWeight: 600 }}>{selectedNode.name}</dd>

                            <dt style={{ color: "var(--enterprise-muted)", fontWeight: 500, marginBottom: "0.1rem" }}>Level</dt>
                            <dd style={{ marginBottom: "0.75rem" }}>
                              {levelNames[selectedNode.level]
                                ? `${selectedNode.level} — ${levelNames[selectedNode.level]}`
                                : selectedNode.level}
                            </dd>

                            <dt style={{ color: "var(--enterprise-muted)", fontWeight: 500, marginBottom: "0.1rem" }}>ID</dt>
                            <dd style={{ fontFamily: "monospace", fontSize: "0.75rem", color: "var(--enterprise-muted)", wordBreak: "break-all" }}>{selectedNode.id}</dd>
                          </dl>
                        </div>
                        <div className="col-sm-6 d-flex flex-column gap-2">
                          <div className="p-2 border rounded" style={{ backgroundColor: "#f8f9fa", border: "1px solid #dee2e6" }}>
                            <div style={{ color: "var(--enterprise-muted)", fontSize: "0.75rem", fontWeight: 500 }}>USERS</div>
                            <div style={{ fontSize: "1.25rem", fontWeight: 700 }}>{nodeDetails?.userCount ?? 0}</div>
                          </div>
                          <div className="p-2 border rounded" style={{ backgroundColor: "#f8f9fa", border: "1px solid #dee2e6" }}>
                            <div style={{ color: "var(--enterprise-muted)", fontSize: "0.75rem", fontWeight: 500 }}>COURSES</div>
                            <div style={{ fontSize: "1.25rem", fontWeight: 700 }}>{nodeDetails?.courseCount ?? 0}</div>
                          </div>
                          <div className="p-2 border rounded" style={{ backgroundColor: "#f8f9fa", border: "1px solid #dee2e6" }}>
                            <div style={{ color: "var(--enterprise-muted)", fontSize: "0.75rem", fontWeight: 500 }}>FACULTY</div>
                            <div style={{ fontSize: "1.25rem", fontWeight: 700 }}>{nodeDetails?.facultyCount ?? 0}</div>
                          </div>
                        </div>
                      </div>

                      {/* Search & Course List */}
                      <div className="mt-4 pt-3 border-top">
                        <div className="d-flex align-items-center justify-content-between mb-3 gap-2">
                          <h5 style={{ fontSize: "0.95rem", fontWeight: 600, margin: 0 }}>Courses in this unit</h5>
                          <input
                            type="text"
                            placeholder="Search courses..."
                            className="form-control-enterprise"
                            style={{ maxWidth: "200px", padding: "0.3rem 0.6rem", fontSize: "0.85rem" }}
                            value={searchQuery}
                            onChange={(e) => { setSearchQuery(e.target.value); setCurrentPage(1); }}
                          />
                        </div>

                        {/* Filtered course list */}
                        {(() => {
                          const courses = nodeDetails?.courses || [];
                          const filteredCourses = courses.filter((c) =>
                            c.title?.toLowerCase().includes(searchQuery.toLowerCase())
                          );
                          const totalPages = Math.ceil(filteredCourses.length / pageSize) || 1;
                          const currentCourses = filteredCourses.slice(
                            (currentPage - 1) * pageSize,
                            currentPage * pageSize
                          );

                          if (filteredCourses.length === 0) {
                            return (
                              <div style={{ color: "var(--enterprise-muted)", fontSize: "0.85rem", fontStyle: "italic" }}>
                                {courses.length === 0 ? "No courses in this unit." : "No courses match the search query."}
                              </div>
                            );
                          }

                          return (
                            <div>
                              <div className="table-responsive">
                                <table className="table table-sm table-bordered mb-2" style={{ fontSize: "0.825rem" }}>
                                  <thead className="table-light">
                                    <tr>
                                      <th>Title</th>
                                      <th style={{ width: "90px" }}>Status</th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    {currentCourses.map((c) => (
                                      <tr key={c.id}>
                                        <td>{c.title}</td>
                                        <td>
                                          <span className="badge bg-secondary" style={{ fontSize: "0.7rem" }}>
                                            {c.status}
                                          </span>
                                        </td>
                                      </tr>
                                    ))}
                                  </tbody>
                                </table>
                              </div>

                              {totalPages > 1 && (
                                <div className="d-flex justify-content-between align-items-center mt-2">
                                  <button
                                    className="btn btn-outline-secondary btn-sm py-1 px-2"
                                    style={{ fontSize: "0.75rem" }}
                                    disabled={currentPage === 1}
                                    onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
                                  >
                                    Previous
                                  </button>
                                  <span style={{ fontSize: "0.75rem", color: "var(--enterprise-muted)" }}>
                                    Page {currentPage} of {totalPages}
                                  </span>
                                  <button
                                    className="btn btn-outline-secondary btn-sm py-1 px-2"
                                    style={{ fontSize: "0.75rem" }}
                                    disabled={currentPage === totalPages}
                                    onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
                                  >
                                    Next
                                  </button>
                                </div>
                              )}
                            </div>
                          );
                        })()}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
